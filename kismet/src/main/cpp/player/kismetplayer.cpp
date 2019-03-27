//
// Created by michelle on 2019/3/22.
//

#include "player/kismetplayer.h"

#include "kislog.h"

#define TAG_LOG "kismetplayer"

void kismetplayer::setDataSource (const char *path) {
    KLOGE(TAG_LOG, "setDataSource %s", path);
#if 0
    // 注册所有的文件格式和编解码器的库
    av_register_all();

    // 打开多媒体文件
    AVFormatContext *pFormatCtx = NULL;
    avformat_open_input(&pFormatCtx, url, NULL, NULL);
#endif
}

void kismetplayer::setDisplay (void *surface) {
    KLOGE(TAG_LOG, "setDisplay %p", surface);
}

void kismetplayer::prepare () {
    KLOGE(TAG_LOG, "prepare");
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

void kismetplayer::prepareAsync () {
    KLOGE(TAG_LOG, "prepareAsync");
}

void kismetplayer::start () {
    KLOGE(TAG_LOG, "start");
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

void kismetplayer::stop () {
    KLOGE(TAG_LOG, "stop");
#if 0
    // 释放packet, 它是在av_read_frame里边分配内存packet.data
    av_free_packet(&packet);
#endif
}

void kismetplayer::pause () {
    KLOGE(TAG_LOG, "pause");
}

void kismetplayer::reset () {
    KLOGE(TAG_LOG, "reset");
}

void kismetplayer::release () {
    KLOGE(TAG_LOG, "release");
#if 0
    releaseResource();
#endif
}