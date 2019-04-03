//
// Created by michelle on 2019/4/1.
//

#ifndef PETROVA_FFDECODER_H
#define PETROVA_FFDECODER_H

#include "player/FFDemuxer.h"

#include "data/VoData.h"

volatile typedef enum {
    DEC_STATUS_IDLE,
    DEC_STATUS_RUNNING,
    DEC_STATUS_STOP,
} dec_status_t;

class FFDecoder {
public:
    static FFDecoder *openVideoDecoder(FFDemuxer *demuxer);
    static FFDecoder *openAudioDecoder(FFDemuxer *demuxer);
    void push(DmxData dmxData);
    VoData &get();
    void free(AVFrame *frame);
    void attachInputQueue(BlockingQueueSTL<DmxData> *input_queue);
    void attachOutputQueue(BlockingQueueSTL<VoData> *output_queue);
    void start();
    void threadRun();
    void stop();

private:
    FFDecoder() {};

    AVCodecContext *avCodecCtx = NULL;
    int video_stream_index = -1;
    int audio_stream_index = -1;
    BlockingQueueSTL<DmxData> *input_queue;
    BlockingQueueSTL<VoData> *output_queue;

    pthread_t inner_thread;

    volatile dec_status_t status = DEC_STATUS_IDLE;
};


#endif //PETROVA_FFDECODER_H
