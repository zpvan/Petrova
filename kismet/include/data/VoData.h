//
// Created by michelle on 2019/4/2.
//

#ifndef PETROVA_VODATA_H
#define PETROVA_VODATA_H

extern "C" {
#include "libavutil/frame.h"
};

class VoData {
public:
    static VoData *create(AVFrame *frame);
    VoData *init(AVFrame *frame);
    void free();
    void alloc();
    AVFrame *getFrame();

    bool isValid() {
        return valid;
    }
    void setValid(bool valid) {
        this->valid = valid;
    }

    int getFormat();
    unsigned char **getFrameData();
    int getWidth();
    int getHeight();

private:
    AVFrame *frame = nullptr;

    unsigned char *data = 0;
    unsigned char *datas[8] = {0};
    int size = 0;
    int width = 0;
    int height = 0;
    int format = 0;

    bool valid = false;
};


#endif //PETROVA_VODATA_H
