//
// Created by michelle on 2019/4/4.
//

#ifndef PETROVA_RUNNABLE_H
#define PETROVA_RUNNABLE_H

#include <pthread.h>

class RunnablePsx {
protected:
    bool start_thread();
    void stop_thread();

    virtual void thread_loop() = 0;
    virtual const char *get_thread_name() = 0;

private:
    static void *thread_run(void* data);

    pthread_t p_thread;
    volatile bool isCreate = false;
    volatile bool isExit = true;
};


#endif //PETROVA_RUNNABLE_H
