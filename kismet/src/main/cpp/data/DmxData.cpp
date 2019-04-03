//
// Created by michelle on 2019/4/3.
//

#include "data/DmxData.h"

void DmxData::allocPkt() {
    pkt = av_packet_alloc();
}

void DmxData::freePkt() {
    av_packet_free(&pkt);
}

AVPacket *DmxData::getPkt() {
    return pkt;
}

void DmxData::setDmxPktType(dmx_packet_t type) {
    dtype = type;
}

dmx_packet_t DmxData::getDmxPktType() {
    return dtype;
}