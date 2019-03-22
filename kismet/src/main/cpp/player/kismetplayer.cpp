//
// Created by michelle on 2019/3/22.
//

#include "player/kismetplayer.h"

#include "kislog.h"

#define TAG_LOG "kismetplayer"

void kismetplayer::setDataSource (const char *path) {
    KLOGE(TAG_LOG, "setDataSource %s", path);
}

void kismetplayer::setDisplay (void *surface) {
    KLOGE(TAG_LOG, "setDisplay %p", surface);
}

void kismetplayer::prepare () {
    KLOGE(TAG_LOG, "prepare");
}

void kismetplayer::prepareAsync () {
    KLOGE(TAG_LOG, "prepareAsync");
}

void kismetplayer::start () {
    KLOGE(TAG_LOG, "start");
}

void kismetplayer::stop () {
    KLOGE(TAG_LOG, "stop");
}

void kismetplayer::pause () {
    KLOGE(TAG_LOG, "pause");
}

void kismetplayer::reset () {
    KLOGE(TAG_LOG, "reset");
}

void kismetplayer::release () {
    KLOGE(TAG_LOG, "release");
}