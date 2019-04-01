//
// Created by michelle on 2019/4/1.
//

#include "player/FFDemuxer.h"

FFDemuxer *FFDemuxer::open(const char *url) {
    FFDemuxer *ffDemuxer = new FFDemuxer();
    // 注册所有的文件格式和编解码器的库
    av_register_all();

    // 打开多媒体文件
    avformat_open_input(&(ffDemuxer->avFmtCtx), url, NULL, NULL);
    return ffDemuxer;
}

void FFDemuxer::parse() {
    // 解析流讯息, 耗时
    avformat_find_stream_info(avFmtCtx, NULL);
}

int FFDemuxer::getBestVideoIndex() {
    if (-1 == bestVideoIndex) {
        bestVideoIndex = av_find_best_stream(avFmtCtx, AVMEDIA_TYPE_VIDEO, -1, -1, NULL, 0);
    }
    return bestVideoIndex;
}

AVPacket *FFDemuxer::read() {
    AVPacket *pkt = av_packet_alloc();
    if (av_read_frame(avFmtCtx, pkt) < 0) {
        av_packet_free(&pkt);
        return nullptr;
    }
    return pkt;
}

void FFDemuxer::free(AVPacket *pkt) {
    if (nullptr != pkt) {
        av_packet_free(&pkt);
    }
}

AVFormatContext *FFDemuxer::getAVFormatContext() {
    return avFmtCtx;
}