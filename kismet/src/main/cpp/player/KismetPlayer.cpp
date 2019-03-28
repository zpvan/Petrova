//
// Created by michelle on 2019/3/22.
//

#include "player/KismetPlayer.h"

#include <unistd.h>

#include "util/KisLog.h"
#include "util/KisThd.h"

#define TAG_LOG "kismetplayer"

static const char *THREAD_NAME = "PLY";

void *static_run(void *data) {
    // 设置线程名称
    setCurrentThreadname(THREAD_NAME);
    KismetPlayer *player = (KismetPlayer *)data;
    if (nullptr != player) {
        player->threadRun();
    }
    // 不要漏了返回值, 会发生crash
    KLOGE(TAG_LOG, "static_run Out %s", getCurrentThreadname());
    return NULL;
}

// PUBLIC
void KismetPlayer::init() {
    // 创建线程ply-thread
    // TODO 创建线程有两种方法, 一种是POSIX thread, 一种是C++ 11 thread
    if (pthread_create(&inner_thread, NULL, static_run, this)) {
        KLOGE(TAG_LOG, "pthread_create %s failed!", THREAD_NAME);
    }
}

void KismetPlayer::setDataSource(const char *path) {
    KLOGE(TAG_LOG, "setDataSource %s", path);
    player_msg_t msg;
    msg.cmd = SETDATASOURCE;
    msg.data = (void *)path;
    msg_list.push_back(msg);
#if 0
    // 注册所有的文件格式和编解码器的库
    av_register_all();

    // 打开多媒体文件
    AVFormatContext *pFormatCtx = NULL;
    avformat_open_input(&pFormatCtx, url, NULL, NULL);
#endif
}

void KismetPlayer::setDisplay(void *surface) {
    KLOGE(TAG_LOG, "setDisplay %p", surface);
//    msg_list.push_back(2);
}

void KismetPlayer::prepare() {
    KLOGE(TAG_LOG, "prepare");
//    msg_list.push_back(3);
    KLOGE(TAG_LOG, "list size: %u", msg_list.size());
#if 0
    // 解析流讯息, 耗时
    avformat_find_stream_info(pFormatCtx, NULL);

    // 找到视频流
    int videoStream = -1;
    int i;
    for (i = 0; i < pFormatCtx->nb_streams; i++)
    {
        if (pFormatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO)
        {
            videoStream = i;
            break;
        }
    }

    // 获取视频编解码器上下文
    AVCodecContext *pCodecCtx = NULL;
    pCodecCtx = pFormatCtx->streams[videoStream]->codec;

    // 找到对应的视频解码器
    AVCodec *pCodec = NULL;
    pCodec = avcodec_find_decoder(pCodecCtx->codec_id);

    // 打开解码器
    avcodec_open2(pCodecCtx, pCodec, NULL);

    // 分配视频帧内存空间
    pFrame = av_frame_alloc();
#endif
}

void KismetPlayer::prepareAsync() {
    KLOGE(TAG_LOG, "prepareAsync");
}

void KismetPlayer::start() {
    KLOGE(TAG_LOG, "start");
//    msg_list.push_back(4);
#if 0
    // 解码视频帧
    if (packet.stream_index == videoStream)
    {
        int frameFinished;
        avcodec_decode_video2(pCodecCtx, pFrame, &frameFinished, &packet);
        if (frameFinished)
        {
            saveIt()
        }
    }
#endif
}

void KismetPlayer::stop() {
    KLOGE(TAG_LOG, "stop");
//    msg_list.push_back(5);
#if 0
    // 释放packet, 它是在av_read_frame里边分配内存packet.data
    av_free_packet(&packet);
#endif
}

void KismetPlayer::pause() {
    KLOGE(TAG_LOG, "pause");
}

void KismetPlayer::reset() {
    KLOGE(TAG_LOG, "reset");
}

void KismetPlayer::release() {
    KLOGE(TAG_LOG, "release");
//    msg_list.push_back(6);
    player_msg_t msg;
    msg.cmd = RELEASE;
    msg_list.push_back(msg);
#if 0
    releaseResource();
#endif
    // TODO C++ 11 thread
    if (0 != inner_thread) {
        pthread_join(inner_thread, NULL);
        inner_thread = 0;
    }
}

void KismetPlayer::threadRun() {
    bool is_exit = false;
    while (!is_exit) {
        if (msg_list.empty()) {
            sleep(1);
            continue;
        }
        player_msg_t msg = msg_list.front();
        msg_list.pop_front();

        switch (msg.cmd) {
            case SETDATASOURCE:
            {
                const char *url = (const char *)msg.data;
                KLOGE(TAG_LOG, "get setdatasource %s", url);
                break;
            }

            case RELEASE:
            {
                is_exit = true;
                break;
            }
        }
    }
}