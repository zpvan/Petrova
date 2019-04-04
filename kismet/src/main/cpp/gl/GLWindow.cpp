//
// Created by michelle on 2019/4/1.
//

#include "gl/GLWindow.h"

#include "util/KisLog.h"
#include "util/KisThd.h"

#include <android/native_window.h>
#include <unistd.h>

#define TAG_LOG "GLWindow"

#define THREAD_NAME "VO"

static void *static_run(void *data) {
    // 设置线程名称
    setCurrentThreadname(THREAD_NAME);
    GLWindow *renderer = (GLWindow *)data;
    if (nullptr != renderer) {
        renderer->threadRun();
    }
    // 不要漏了返回值, 会发生crash
    KLOGE(TAG_LOG, "static_run Out %s", getCurrentThreadname());
    return nullptr;
}

GLWindow *GLWindow::create(void *win) {

    GLWindow *glWindow = new GLWindow();
    glWindow->win = win;
    return glWindow;
}

void GLWindow::render(VoData *voData) {
    if (nullptr == glTexture) {
        init(win);
        glTexture = new GLTexture();
        glTexture->init(voData->getFormat());
    }
    // 画图
    // hex_dump(TAG_LOG, voData->getFrameData()[0], 16);
    glTexture->draw(voData->getFrameData(), voData->getWidth(), voData->getHeight());

    // 显示
    flip();
}

bool GLWindow::init(void *win) {
    this->win = win;
    ANativeWindow *nwin = (ANativeWindow *) win;

    //初始化EGL

    //1 获取EGLDisplay对象, 显示设备
    display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (EGL_NO_DISPLAY == display) {
        KLOGE(TAG_LOG, "eglGetDisplay failed");
        return false;
    }
    // KLOGE(TAG_LOG, "eglGetDisplay success");

    //2 初始化Display
    if (EGL_TRUE != eglInitialize(display, 0, 0)) {
        KLOGE(TAG_LOG, "eglInitialize failed");
        return false;
    }
    // KLOGE(TAG_LOG, "eglInitialize success");

    //3 获取配置
    EGLConfig config = 0;
    EGLint configNum = 0;
    EGLint configSpec[] = {
            EGL_RED_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_BLUE_SIZE, 8,
            EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
            EGL_NONE
    };
    if (EGL_TRUE != eglChooseConfig(display, configSpec, &config, 1, &configNum)) {
        KLOGE(TAG_LOG, "eglChooseConfig failed");
        return false;
    }
    // KLOGE(TAG_LOG, "eglChooseConfig success");

    //4 创建surface
    surface = eglCreateWindowSurface(display, config, nwin, NULL);
    if (surface == EGL_NO_SURFACE) {
        KLOGE(TAG_LOG, "eglCreateWindowSurface failed nwin=%p", nwin);
        return false;
    }
    // KLOGE(TAG_LOG, "eglCreateWindowSurface success");

    //5 创建并打开EGL上下文
    const EGLint ctxAttr[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL_NONE
    };
    context = eglCreateContext(display, config, EGL_NO_CONTEXT, ctxAttr);
    if (config == EGL_NO_CONTEXT) {
        KLOGE(TAG_LOG, "eglCreateContext failed");
        return false;
    }

    //6 上下文切换
    if (EGL_TRUE != eglMakeCurrent(display, surface, surface, context)) {
        KLOGE(TAG_LOG, "eglMakeCurrent failed");
        return false;
    }
    // KLOGE(TAG_LOG, "EGL Init Success");
    return true;
}

void GLWindow::flip() {
    if (display == EGL_NO_DISPLAY || surface == EGL_NO_SURFACE) {
        return;
    }
    eglSwapBuffers(display, surface);
}

void GLWindow::attachInputQueue(BlockingQueueSTL<VoData> *queue) {
    vo_queue = queue;
}

void GLWindow::start() {
    // KLOGE(TAG_LOG, "start");
    if (start_thread()) {
        status = WIN_STATUS_RUNNING;
    }
}

void GLWindow::threadRun() {
    bool is_exit = false;
    while (!is_exit) {
        switch (status) {
            case WIN_STATUS_IDLE:
                usleep(16); //16ms
                break;

            case WIN_STATUS_RUNNING:
            {
                VoData voData = vo_queue->get_font();
                render(&voData);
                voData.free();
                break;
            }

            case WIN_STATUS_STOP:
                is_exit = true;
                break;
        }
    }
}

void GLWindow::stop() {
    // KLOGE(TAG_LOG, "stop");
    status = WIN_STATUS_STOP;
    stop_thread();
}

// Extends RunnablePsx
void GLWindow::thread_loop() {
    switch (status) {
        case WIN_STATUS_IDLE:
            usleep(16); //16ms
            break;

        case WIN_STATUS_RUNNING:
        {
            VoData voData = vo_queue->get_font();
            render(&voData);
            voData.free();
            break;
        }

        case WIN_STATUS_STOP:
            break;
    }
}

const char *GLWindow::get_thread_name() {
    return THREAD_NAME;
}