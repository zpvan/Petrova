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
    void innerSetDataSource(const char *url);
    void innerSetDisplay(void *surface);
    void innerPrepare();
    void innerStart();
    void innerStop();
    void innerPause();
    void innerReset();
    void innerRelease();

    void saveIt();

    void pushMsg2List(player_cmd_t cmd, void *data);
    void wait4PrepareDone();

    pthread_t inner_thread;
    pthread_mutex_t msg_list_mutex;
    std::list<player_msg_t> msg_list;

};


#endif //PETROVA_KISMETPLAYER_H
