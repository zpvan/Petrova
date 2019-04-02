//
// Created by michelle on 2019/4/2.
//

#include "player/VoData.h"

#include "util/KisLog.h"

#define TAG_LOG "VoData"

VoData *VoData::create(AVFrame *frame) {
    return (new VoData())->init(frame);
}

void VoData::free() {
    // delete datas;
}

VoData *VoData::init(AVFrame *frame) {
    data = (unsigned char *) frame;

    size = (frame->linesize[0] + frame->linesize[1] + frame->linesize[2]) * frame->height;
    width = frame->width;
    height = frame->height;

    format = frame->format;
    KLOGE(TAG_LOG, "data format is %d, size=%d", frame->format, sizeof(datas));
    memcpy(datas, frame->data, sizeof(datas));
    return this;
}

int VoData::getFormat() {
    return format;
}

unsigned char **VoData::getFrameData() {
    return datas;
}

int VoData::getWidth() {
    return width;
}

int VoData::getHeight() {
    return height;
}