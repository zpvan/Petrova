//
// Created by michelle on 2019/4/1.
//

#ifndef PETROVA_FFDEMUXER_H
#define PETROVA_FFDEMUXER_H

extern "C" {
#include <libavformat/avformat.h>
};

class FFDemuxer {
public:
    static FFDemuxer *open(const char *url);
    void parse();
    int getBestVideoIndex();
    AVPacket *read();
    void free(AVPacket *pkt);

    AVFormatContext *getAVFormatContext();

private:
    FFDemuxer() {};
    AVFormatContext *avFmtCtx = nullptr;
    int bestVideoIndex = -1;
};


#endif //PETROVA_FFDEMUXER_H
