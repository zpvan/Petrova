//
// Created by michelle on 2019/4/1.
//

#include "player/FFDecoder.h"

#include "util/KisLog.h"
#include "util/KisThd.h"

#include <unistd.h>

#define TAG_LOG "FFDecoder"

#define THREAD_NAME "VD"

static void *static_run(void *data) {
    // 设置线程名称
    setCurrentThreadname(THREAD_NAME);
    FFDecoder *decoder = (FFDecoder *)data;
    if (nullptr != decoder) {
        decoder->threadRun();
    }
    // 不要漏了返回值, 会发生crash
    KLOGE(TAG_LOG, "static_run Out %s", getCurrentThreadname());
    return nullptr;
}

static void hexdump(const unsigned char *buf, const int num)
{
    int i;
    printf(TAG_LOG);
    for(i = 0; i < num; i++)
    {
        printf("%02X ", buf[i]);
        if ((i+1)%8 == 0) {
            printf("\n");
        }
    }
    printf("\n");
    return;
}


FFDecoder *FFDecoder::openVideoDecoder(FFDemuxer *demuxer) {
    KLOGE(TAG_LOG, "openVideoDecoder In");
    FFDecoder *ffDecoder = new FFDecoder();

    // 获取编解码配置
    ffDecoder->video_stream_index = demuxer->getBestVideoIndex();
    AVCodecParameters *vCodecPar = demuxer->getAVFormatContext()->streams[ffDecoder->video_stream_index]->codecpar;

    // 找到对应的视频解码器
    AVCodec *vCodec = NULL;
    vCodec = avcodec_find_decoder(vCodecPar->codec_id);

    // 获取视频解码器的上下文
    ffDecoder->avCodecCtx = avcodec_alloc_context3(vCodec);
    avcodec_parameters_to_context(ffDecoder->avCodecCtx, vCodecPar);
    ffDecoder->avCodecCtx->thread_count = 8;

    // 打开解码器
    if (avcodec_open2(ffDecoder->avCodecCtx, NULL, NULL) < 0) {
        KLOGE(TAG_LOG, "avcodec_open2 failed");
    }
    KLOGE(TAG_LOG, "openVideoDecoder Out");
    return ffDecoder;
}

void FFDecoder::push(DmxData dmxData) {
//    KLOGE(TAG_LOG, " %s In", __func__);
    AVPacket *pkt = dmxData.getPkt();
    if (pkt->stream_index == video_stream_index) {
        // 解码
        hex_dump(TAG_LOG, pkt->data, 16);
        avcodec_send_packet(avCodecCtx, pkt);
//        KLOGE(TAG_LOG, "send success");
    }
//    KLOGE(TAG_LOG, " %s Out", __func__);
}

VoData &FFDecoder::get() {
//    KLOGE(TAG_LOG, " %s In", __func__);
    VoData voData;
    voData.alloc();
    AVFrame *frame = voData.getFrame();
    if (0 == avcodec_receive_frame(avCodecCtx, frame)) {
        voData.init(frame);
        hexdump(frame->data[0], 16);
        voData.setValid(true);
    } else {
        KLOGE(TAG_LOG, "receive error");
    }
//    KLOGE(TAG_LOG, " %s Out", __func__);
    return voData;
}

void FFDecoder::free(AVFrame *frame) {
    if (nullptr != frame) {
        av_free(frame);
    }
}

FFDecoder *FFDecoder::openAudioDecoder(FFDemuxer *demuxer) {
    KLOGE(TAG_LOG, "openAudioDecoder In");
    FFDecoder *ffDecoder = new FFDecoder();

    // 获取编解码配置
    ffDecoder->audio_stream_index = demuxer->getBestAudioIndex();
    AVCodecParameters *aCodecPar = demuxer->getAVFormatContext()->streams[ffDecoder->audio_stream_index]->codecpar;

    // 找到对应的音频解码器
    AVCodec *aCodec = NULL;
    aCodec = avcodec_find_decoder(aCodecPar->codec_id);

    // 获取音频解码器的上下文
    ffDecoder->avCodecCtx = avcodec_alloc_context3(aCodec);
    avcodec_parameters_to_context(ffDecoder->avCodecCtx, aCodecPar);
    ffDecoder->avCodecCtx->thread_count = 8;

    // 打开解码器
    if (avcodec_open2(ffDecoder->avCodecCtx, NULL, NULL) < 0) {
        KLOGE(TAG_LOG, "avcodec_open2 failed");
    }

    KLOGE(TAG_LOG, "openAudioDecoder Out");
    return ffDecoder;
}

void FFDecoder::attachInputQueue(BlockingQueueSTL<DmxData> *input_queue) {
    this->input_queue = input_queue;
}

void FFDecoder::attachOutputQueue(BlockingQueueSTL<VoData> *output_queue) {
    this->output_queue = output_queue;
}

void FFDecoder::start() {
    KLOGE(TAG_LOG, "start In");
    if (pthread_create(&inner_thread, nullptr, static_run, this)) {
        KLOGE(TAG_LOG, "pthread_create %s failed!", THREAD_NAME);
        return;
    }
    status = DEC_STATUS_RUNNING;
}

void FFDecoder::threadRun() {
    bool is_exit = false;
    while (!is_exit) {
        switch (status) {
            case DEC_STATUS_IDLE:
                usleep(300); //300ms
                break;

            case DEC_STATUS_RUNNING:
            {
//                KLOGE(TAG_LOG, "input_queue %p", input_queue);
                DmxData dmxData = input_queue->get_font();
                push(dmxData);
                VoData voData = get();
                if (voData.isValid()) {
//                    KLOGE(TAG_LOG, "output_queue %p", output_queue);
                    output_queue->push_back(voData);
                } else {
                    voData.free();
                }
                dmxData.freePkt();
                break;
            }

            case DEC_STATUS_STOP:
                is_exit = true;
                break;
        }
    }
}

void FFDecoder::stop() {
    status = DEC_STATUS_STOP;
}