//
// Created by michelle on 2019/4/3.
//

#ifndef PETROVA_DMXDATA_H
#define PETROVA_DMXDATA_H

extern "C" {
#include <libavcodec/avcodec.h>
};

typedef enum {
    DMX_PACKET_ERROR = -1,
    DMX_PACKET_UNKNOWN,
    DMX_PACKET_VIDEO,
    DMX_PACKET_AUDIO,
    DMX_PACKET_SUBTITLE,
} dmx_packet_t;

class DmxData {
public:
    void allocPkt();
    void freePkt();
    AVPacket *getPkt();
    void setDmxPktType(dmx_packet_t type);
    dmx_packet_t getDmxPktType();

private:
    AVPacket *pkt = nullptr;
    dmx_packet_t dtype = DMX_PACKET_UNKNOWN;
};


#endif //PETROVA_DMXDATA_H
