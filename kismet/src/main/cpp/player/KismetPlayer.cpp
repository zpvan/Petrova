//
// Created by michelle on 2019/3/22.
//

#include "player/KismetPlayer.h"

#include <unistd.h>
#include <player/PlayerData.h>

#include "util/KisLog.h"
#include "util/KisThd.h"

#define TAG_LOG "KismetPlayerCpp"

static const char *THREAD_NAME = "PLY";

#define TEST_FLOW 0

#if TEST_FLOW
extern "C" {
    #include <libavcodec/avcodec.h>
    #include <libavformat/avformat.h>
    #include <libswscale/swscale.h>
};

AVFormatContext *pFormatCtx = NULL;
AVCodecContext *pCodecCtx = NULL;
AVCodec *pCodec = NULL;
AVFrame *pFrame = NULL;
AVFrame *pFrameRGB = NULL;
struct SwsContext *img_convert_ctx = NULL;

int videoStream = -1;
uint8_t *buffer;
AVFrame *frame = nullptr;
static int count = 0;

static unsigned sws_flags = SWS_BICUBIC;
#endif

void *static_run(void *data) {
    // 设置线程名称
    setCurrentThreadname(THREAD_NAME);
    KismetPlayer *player = (KismetPlayer *)data;
    if (nullptr != player) {
        player->threadRun();
    }
    // 不要漏了返回值, 会发生crash
    KLOGE(TAG_LOG, "static_run Out %s", getCurrentThreadname());
    return nullptr;
}

// PUBLIC
void KismetPlayer::init() {
    // 创建线程ply-thread
    // TODO 创建线程有两种方法, 一种是POSIX thread, 一种是C++ 11 thread
    if (pthread_create(&inner_thread, nullptr, static_run, this)) {
        KLOGE(TAG_LOG, "pthread_create %s failed!", THREAD_NAME);
    } else {
        pthread_mutex_init(&msg_list_mutex, nullptr);
    }
}

void KismetPlayer::setDataSource(const char *path) {
    KLOGE(TAG_LOG, "setDataSource %s", path);
    pushMsg2List(PLAYER_CMD_SET_DATA_SOURCE, (void *)path);
}

void KismetPlayer::setDisplay(void *surface) {
    KLOGE(TAG_LOG, "setDisplay %p", surface);
    pushMsg2List(PLAYER_CMD_SET_DISPLAY, surface);
}

void KismetPlayer::prepare() {
    KLOGE(TAG_LOG, "prepare");
    pushMsg2List(PLAYER_CMD_PREPARE, nullptr);
}

void KismetPlayer::prepareAsync() {
    KLOGE(TAG_LOG, "prepareAsync");
    pushMsg2List(PLAYER_CMD_PREPARE, nullptr);
}

void KismetPlayer::start() {
    KLOGE(TAG_LOG, "start");
    pushMsg2List(PLAYER_CMD_START, nullptr);
}

void KismetPlayer::stop() {
    KLOGE(TAG_LOG, "stop");
    pushMsg2List(PLAYER_CMD_STOP, nullptr);
}

void KismetPlayer::pause() {
    KLOGE(TAG_LOG, "pause");
    pushMsg2List(PLAYER_CMD_PAUSE, nullptr);
}

void KismetPlayer::reset() {
    KLOGE(TAG_LOG, "reset");
    pushMsg2List(PLAYER_CMD_RESET, nullptr);
}

void KismetPlayer::release() {
    KLOGE(TAG_LOG, "release");
    pushMsg2List(PLAYER_CMD_RELEASE, nullptr);
    // TODO C++ 11 thread
    if (0 != inner_thread) {
        pthread_join(inner_thread, nullptr);
        inner_thread = 0;
        pthread_mutex_destroy(&msg_list_mutex);
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
            case PLAYER_CMD_SET_DATA_SOURCE:
            {
                innerSetDataSource((const char *)msg.data);
                break;
            }

            case PLAYER_CMD_SET_DISPLAY:
            {
                innerSetDisplay(msg.data);
                break;
            }

            case PLAYER_CMD_PREPARE:
            {
                innerPrepare();
                break;
            }

            case PLAYER_CMD_START:
            {
                innerStart();
                break;
            }

            case PLAYER_CMD_PAUSE:
            {
                innerPause();
                break;
            }

            case PLAYER_CMD_STOP:
            {
                innerStop();
                break;
            }

            case PLAYER_CMD_RESET:
            {
                innerReset();
                break;
            }

            case PLAYER_CMD_RELEASE:
            {
                innerRelease();
                is_exit = true;
                break;
            }
        }
    }
}

// PRIVATE
void KismetPlayer::pushMsg2List(player_cmd_t cmd, void *data) {
    pthread_mutex_lock(&msg_list_mutex);
    player_msg_t msg;
    msg.cmd = cmd;
    msg.data = data;
    msg_list.push_back(msg);
    pthread_mutex_unlock(&msg_list_mutex);
}

void KismetPlayer::innerSetDataSource(const char *url) {
#if TEST_FLOW
    // 注册所有的文件格式和编解码器的库
    av_register_all();

    // 打开多媒体文件
    avformat_open_input(&pFormatCtx, url, NULL, NULL);
#endif

    ffDemuxer = FFDemuxer::open(url);
}

void KismetPlayer::innerSetDisplay(void *surface) {

}

void KismetPlayer::innerPrepare() {
#if TEST_FLOW
    // 解析流讯息, 耗时
    avformat_find_stream_info(pFormatCtx, NULL);

    // 找到视频流
//    int i;
//    for (i = 0; i < pFormatCtx->nb_streams; i++)
//    {
//        if (pFormatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO)
//        {
//            videoStream = i;
//            break;
//        }
//    }
    videoStream = av_find_best_stream(pFormatCtx, AVMEDIA_TYPE_VIDEO, -1, -1, NULL, 0);
    KLOGE(TAG_LOG, "best video %d", videoStream);

    // 获取视频编解码器上下文
//    AVCodecContext *pCodecCtx = NULL;
//    pCodecCtx = pFormatCtx->streams[videoStream]->codec;
    AVCodecParameters *vCodecPar = pFormatCtx->streams[videoStream]->codecpar;

    // 找到对应的视频解码器
    AVCodec *pCodec = NULL;
    pCodec = avcodec_find_decoder(vCodecPar->codec_id);

    // 打开解码器
    pCodecCtx = avcodec_alloc_context3(pCodec);
    avcodec_parameters_to_context(pCodecCtx, vCodecPar);
    pCodecCtx->thread_count = 8;
//    avcodec_open2(pCodecCtx, pCodec, NULL);
    if (avcodec_open2(pCodecCtx, NULL, NULL) < 0) {
        KLOGE(TAG_LOG, "avcodec_open2 failed");
    }

    // 分配视频帧内存空间
    pFrame = av_frame_alloc();
#endif

    ffDemuxer->parse();
    int bvi = ffDemuxer->getBestVideoIndex();
    KLOGE(TAG_LOG, "best video %d", bvi);
    ffDecoder = FFDecoder::openVideoDecoder(ffDemuxer);
}

void KismetPlayer::innerStart() {
    KLOGE(TAG_LOG, "innerStart In");
#if TEST_FLOW
//    while (av_read_frame(pFormatCtx, &packet) >= 0) {
//        // 解码视频帧
//        KLOGE(TAG_LOG, "read frame index=%d", packet.stream_index);
//        if (packet.stream_index == videoStream) {
//            int frameFinished;
//            avcodec_decode_video2(pCodecCtx, pFrame, &frameFinished, &packet);
//            if (frameFinished) {
//                // saveIt()
//                KLOGE(TAG_LOG, "saveIt");
//            }
//        }
//    }
    int res = 0;
    do {
        AVPacket *pkt = av_packet_alloc();
        res = av_read_frame(pFormatCtx, pkt);
        // KLOGE(TAG_LOG, "av_read_frame res=%d, idx=%d", res, pkt->stream_index);
        if (pkt->stream_index == videoStream) {
            // 解码视频帧
            avcodec_send_packet(pCodecCtx, pkt);
            if (!frame) {
                frame = av_frame_alloc();
            }
            int res1 = -1;
            do {
                res1 = avcodec_receive_frame(pCodecCtx, frame);
                KLOGE(TAG_LOG, "get frame count=%d", ++count);
            } while (res1 == 0);
        }
        av_packet_free(&pkt);
    } while (res >= 0);
#endif

    int count = 0;
    // TODO thread
    while (1) {
        AVPacket *pkt = ffDemuxer->read();
        if (nullptr == pkt) {
            break;
        }
        ffDecoder->push(pkt);
        AVFrame *frame = nullptr;
        do {
            ffDecoder->free(frame);
            frame = ffDecoder->get();
            KLOGE(TAG_LOG, "get frame count=%d", ++count);
        } while (nullptr != frame);
        ffDemuxer->free(pkt);
    }
    KLOGE(TAG_LOG, "innerStart Out");
}

void KismetPlayer::innerStop() {
#if TEST_FLOW
    // 释放packet, 它是在av_read_frame里边分配内存packet.data
    // av_free_packet(&packet);
    if (frame) {
        av_frame_free(&frame);
    }
#endif
}

void KismetPlayer::innerPause() {

}

void KismetPlayer::innerReset() {

}

void KismetPlayer::innerRelease() {
#if TEST_FLOW
    // 释放RGB image
    av_free(buffer);
    av_free(pFrameRGB);

    // 释放yuv image
    av_free(pFrame);

    // 关闭codec
    avcodec_close(pCodecCtx);

    // 关闭视频文件
    avformat_close_input(&pFormatCtx);

    // 释放sws
    sws_freeContext(img_convert_ctx);
#endif
}

void KismetPlayer::saveIt() {
#if 0
    // 配置sw scale, 用来将原本的数据格式转换成rgb24的
    struct SwsContext *img_convert_ctx = NULL;
    img_convert_ctx = sws_getCachedContext(img_convert_ctx, pFrame->width, pFrame->height, pFrame->format, pFrame->width, pFrame->height, AV_PIX_FMT_RGB24, sws_flags, NULL, NULL, NULL);

    // 分配RGB的视频帧内存空间
    pFrameRGB = av_frame_alloc();

    // 申请放置原始数据的内存空间
    uint8_t *buffer;
    int numBytes;
    numBytes = avpicture_get_size(AV_PIX_FMT_RGB24, pCodecCtx->width, pCodecCtx->height);
    buffer = (uint8_t *)av_malloc(numBytes * sizeof(uint8_t));

    // 把帧和新申请的内存结合起来
    avpicture_fill((AVPicture *)pFrameRGB, buffer, AV_PIX_FMT_RGB24, pCodecCtx->width, pCodecCtx->height);

    // 转换格式
    sws_scale(img_convert_ctx, pFrame->data, pFrame->linesize, 0, pCodecCtx->height, pFrameRGB->data, pFrameRGB->linesize);

    // 写ppm文件
    AVFrame *pFrame = pFrameRGB
    int iFrame = 1;
    int width = pCodecCtx->width;
    int height = pCodecCtx->height;

    FILE *pFile;
    char szFilename[32];
    int y;

    // 打开文件
    sprintf(szFilename, "frame%d.ppm", iFrame);
    pFile = fopen(szFilename, "wb");
    if (pFile == NULL)
    {
        printf("open file failed!\n");
        return;
    }

    // 写文件头
    fprintf(pFile, "P6\n%d %d\n255\n", width, height);

    // 写像素数据
    for (y = 0; y < height; ++y)
    {
        fwrite(pFrame->data[0] + y * pFrame->linesize[0], 1, width * 3, pFile);
    }

    // 关闭文件
    fclose(pFile);
#endif
}