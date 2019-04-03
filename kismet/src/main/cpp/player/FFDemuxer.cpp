//
// Created by michelle on 2019/4/1.
//

#include "player/FFDemuxer.h"

#include "util/KisLog.h"
#include "util/KisThd.h"

#include <unistd.h>

#define TAG_LOG "FFDemuxer"

#define THREAD_NAME "DMX"

static void *static_run(void *data) {
    // 设置线程名称
    setCurrentThreadname(THREAD_NAME);
    FFDemuxer *demuxer = (FFDemuxer *)data;
    if (nullptr != demuxer) {
        demuxer->threadRun();
    }
    // 不要漏了返回值, 会发生crash
    KLOGE(TAG_LOG, "static_run Out %s", getCurrentThreadname());
    return nullptr;
}

FFDemuxer *FFDemuxer::open(const char *url) {
    FFDemuxer *ffDemuxer = new FFDemuxer();
    // 注册所有的文件格式和编解码器的库
    av_register_all();

    // 打开多媒体文件
    if (0 == avformat_open_input(&(ffDemuxer->avFmtCtx), url, NULL, NULL)) {
        return ffDemuxer;
    } else {
        return nullptr;
    }
}

bool FFDemuxer::parse() {
    // 解析流讯息, 耗时
    return avformat_find_stream_info(avFmtCtx, NULL) >= 0;
}

int FFDemuxer::getBestVideoIndex() {
    if (-1 == bestVideoIndex) {
        bestVideoIndex = av_find_best_stream(avFmtCtx, AVMEDIA_TYPE_VIDEO, -1, -1, NULL, 0);
    }
    return bestVideoIndex;
}

int FFDemuxer::getBestAudioIndex() {
    if (-1 == bestAudioIndex) {
        bestAudioIndex = av_find_best_stream(avFmtCtx, AVMEDIA_TYPE_AUDIO, -1, -1, NULL, 0);
    }
    return bestAudioIndex;
}

DmxData &FFDemuxer::read() {
    DmxData dmxData;
    dmxData.allocPkt();
    AVPacket *pkt = dmxData.getPkt();
    if (av_read_frame(avFmtCtx, pkt) < 0) {
        dmxData.freePkt();
        dmxData.setDmxPktType(DMX_PACKET_ERROR);
    } else {
        if (-1 != bestVideoIndex && pkt->stream_index == bestVideoIndex) {
            dmxData.setDmxPktType(DMX_PACKET_VIDEO);
        }
    }
    return dmxData;
}

void FFDemuxer::free(DmxData dmxData) {
    dmxData.freePkt();
}

AVFormatContext *FFDemuxer::getAVFormatContext() {
    return avFmtCtx;
}

void FFDemuxer::attachVideoQueue(BlockingQueueSTL<DmxData> *video_queue) {
    this->video_queue = video_queue;
}

void FFDemuxer::start() {
    KLOGE(TAG_LOG, "start In");
    if (pthread_create(&inner_thread, nullptr, static_run, this)) {
        KLOGE(TAG_LOG, "pthread_create %s failed!", THREAD_NAME);
        return;
    }
    status = DMX_STATUS_RUNNING;
}

void FFDemuxer::threadRun() {
    bool is_exit = false;
    while (!is_exit) {
        switch (status) {
            case DMX_STATUS_IDLE:
                usleep(300); //300ms
                break;

            case DMX_STATUS_RUNNING:
            {
                DmxData data = read();
                // error case
                if (data.getDmxPktType() == DMX_PACKET_ERROR) {
                    status = DMX_STATUS_STOP;
                }
                // video case
                if (data.getDmxPktType() == DMX_PACKET_VIDEO && nullptr != video_queue) {
                    video_queue->push_back(data);
                    break;

                }
                data.freePkt();
                break;
            }


            case DMX_STATUS_STOP:
                is_exit = true;
                break;
        }
    }
}

void FFDemuxer::attachAudioQueue(BlockingQueueSTL<DmxData> *audio_queue) {
    this->audio_queue = audio_queue;
}

void FFDemuxer::stop() {
    status = DMX_STATUS_STOP;
}