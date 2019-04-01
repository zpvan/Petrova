//
// Created by michelle on 2019/4/1.
//

#include "player/FFDecoder.h"
#include "util/KisLog.h"

#define TAG_LOG "FFDecoder"

FFDecoder *FFDecoder::openVideoDecoder(FFDemuxer *demuxer) {
    KLOGE(TAG_LOG, "openVideoDecoder In");
    FFDecoder *ffDecoder = new FFDecoder();

    // 获取编解码配置
    ffDecoder->streamIndex = demuxer->getBestVideoIndex();
    AVCodecParameters *vCodecPar = demuxer->getAVFormatContext()->streams[ffDecoder->streamIndex]->codecpar;

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

void FFDecoder::push(AVPacket *pkt) {
    if (pkt->stream_index == streamIndex) {
        // 解码
        avcodec_send_packet(avCodecCtx, pkt);
    }
}

AVFrame *FFDecoder::get() {
    AVFrame *frame = av_frame_alloc();
    if (0 != avcodec_receive_frame(avCodecCtx, frame)) {
        av_free(frame);
        return nullptr;
    }
    return frame;
}

void FFDecoder::free(AVFrame *frame) {
    if (nullptr != frame) {
        av_free(frame);
    }
}