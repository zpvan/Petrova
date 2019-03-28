//
// Created by michelle on 2019/3/22.
//

#ifndef PETROVA_KISMETPLAYER_H
#define PETROVA_KISMETPLAYER_H

#include <pthread.h>
#include <list>

#include "player/PlayerData.h"

class KismetPlayer {

public:
    void init();

    void setDataSource(const char *path);
    void setDisplay(void *surface);
    void prepare();
    void prepareAsync();
    void start();
    void stop();
    void pause();
    void reset();
    void release();

    void threadRun();

private:
    void innerSetDataSource(const char *path);
    void innerSetDisplay(void *surface);
    void innerPrepare();
    void innerPrepareAsync();
    void innerStart();
    void innerStop();

    pthread_t inner_thread;
    std::list<player_msg_t> msg_list;
};


#endif //PETROVA_KISMETPLAYER_H
