//
// Created by michelle on 2019/4/1.
//

#ifndef PETROVA_FFDECODER_H
#define PETROVA_FFDECODER_H

#include "player/FFDemuxer.h"

extern "C" {
#include <libavcodec/avcodec.h>
};

class FFDecoder {
public:
    static FFDecoder *openVideoDecoder(FFDemuxer *demuxer);
    void push(AVPacket *pkt);
    AVFrame *get();
    void free(AVFrame *frame);

private:
    FFDecoder() {};

    AVCodecContext *avCodecCtx = NULL;
    int streamIndex = -1;
};


#endif //PETROVA_FFDECODER_H
