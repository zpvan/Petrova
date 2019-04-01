//
// Created by michelle on 2019/4/1.
//

#ifndef PETROVA_GLWINDOW_H
#define PETROVA_GLWINDOW_H

#include "EGL/egl.h"

class GLWindow {
public:
    static GLWindow *create(void *win);

private:
    GLWindow() {};

    EGLDisplay display = EGL_NO_DISPLAY;
    EGLSurface surface = EGL_NO_SURFACE;
    EGLContext context = EGL_NO_CONTEXT;
};


#endif //PETROVA_GLWINDOW_H
