//
// Created by michelle on 2019/4/1.
//

#ifndef PETROVA_GLWINDOW_H
#define PETROVA_GLWINDOW_H

#include "GLTexture.h"

#include "common/RunnablePsx.h"
#include "data/VoData.h"
#include "util/BlockingQueueSTL.h"


#include <EGL/egl.h>

volatile typedef enum {
    WIN_STATUS_IDLE,
    WIN_STATUS_RUNNING,
    WIN_STATUS_STOP,
} win_status_t;

class GLWindow : public RunnablePsx {
public:
    static GLWindow *create(void *win);
    void render(VoData *voData);
    void attachInputQueue(BlockingQueueSTL<VoData> *queue);
    void start();
    void threadRun();
    void stop();

protected:
    virtual void thread_loop();
    virtual const char* get_thread_name();

private:
    GLWindow() {};
    bool init(void *win);
    void flip();

    EGLDisplay display = EGL_NO_DISPLAY;
    EGLSurface surface = EGL_NO_SURFACE;
    EGLContext context = EGL_NO_CONTEXT;

    GLTexture *glTexture = nullptr;
    void *win = nullptr;

    pthread_t inner_thread;

    BlockingQueueSTL<VoData> *vo_queue;

    volatile win_status_t status = WIN_STATUS_IDLE;
};


#endif //PETROVA_GLWINDOW_H
