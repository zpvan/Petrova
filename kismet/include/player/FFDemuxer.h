//
// Created by michelle on 2019/4/1.
//

#ifndef PETROVA_FFDEMUXER_H
#define PETROVA_FFDEMUXER_H

#include "data/DmxData.h"

#include "util/BlockingQueueSTL.h"

#include <pthread.h>

extern "C" {
#include <libavformat/avformat.h>
};

volatile typedef enum {
    DMX_STATUS_IDLE,
    DMX_STATUS_RUNNING,
    DMX_STATUS_STOP,
} dmx_status_t;

class FFDemuxer {
public:
    static FFDemuxer *open(const char *url);
    bool parse();
    int getBestVideoIndex();
    DmxData &read();
    void free(DmxData dmxData);
    void attachVideoQueue(BlockingQueueSTL<DmxData> *video_queue);
    void start();
    void threadRun();
    int getBestAudioIndex();
    void attachAudioQueue(BlockingQueueSTL<DmxData> *audio_queue);
    void stop();

    AVFormatContext *getAVFormatContext();

private:
    FFDemuxer() {};
    AVFormatContext *avFmtCtx = nullptr;
    int bestVideoIndex = -1;
    int bestAudioIndex = -1;
    BlockingQueueSTL<DmxData> *video_queue;
    BlockingQueueSTL<DmxData> *audio_queue;

    pthread_t inner_thread;

    volatile dmx_status_t status = DMX_STATUS_IDLE;
};


#endif //PETROVA_FFDEMUXER_H
