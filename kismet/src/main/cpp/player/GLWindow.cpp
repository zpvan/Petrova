//
// Created by michelle on 2019/4/1.
//

#include "player/GLWindow.h"
#include "util/KisLog.h"

#include <android/native_window.h>

#define TAG_LOG "GLWindow"

GLWindow *GLWindow::create(void *win) {
    ANativeWindow *nwin = (ANativeWindow *) win;

    //初始化EGL
    GLWindow *glWindow = new GLWindow();
    //1 获取EGLDisplay对象, 显示设备
    glWindow->display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (EGL_NO_DISPLAY == glWindow->display) {
        KLOGE(TAG_LOG, "eglGetDisplay failed");
        return false;
    }
    KLOGE(TAG_LOG, "eglGetDisplay success");

    //2 初始化Display
    if (EGL_TRUE != eglInitialize(glWindow->display, 0, 0)) {
        KLOGE(TAG_LOG, "eglInitialize failed");
        return false;
    }
    KLOGE(TAG_LOG, "eglInitialize success");

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
    if (EGL_TRUE != eglChooseConfig(glWindow->display, configSpec, &config, 1, &configNum)) {
        KLOGE(TAG_LOG, "eglChooseConfig failed");
        return false;
    }
    KLOGE(TAG_LOG, "eglChooseConfig success");

    //4 创建surface
    glWindow->surface = eglCreateWindowSurface(glWindow->display, config, nwin, NULL);
    if (glWindow->surface == EGL_NO_SURFACE) {
        KLOGE(TAG_LOG, "eglCreateWindowSurface failed");
        return false;
    }
    KLOGE(TAG_LOG, "eglCreateWindowSurface success");

    //5 创建并打开EGL上下文
    const EGLint ctxAttr[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL_NONE
    };
    glWindow->context = eglCreateContext(glWindow->display, config, EGL_NO_CONTEXT, ctxAttr);
    if (config == EGL_NO_CONTEXT) {
        KLOGE(TAG_LOG, "eglCreateContext failed");
        return false;
    }

    //6 上下文切换
    if (EGL_TRUE != eglMakeCurrent(glWindow->display, glWindow->surface, glWindow->surface, glWindow->context)) {
        KLOGE(TAG_LOG, "eglMakeCurrent failed");
        return false;
    }
    KLOGE(TAG_LOG, "EGL Init Success");

    return glWindow;
}
