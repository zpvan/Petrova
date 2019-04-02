//
// Created by michelle on 2019/4/1.
//

#ifndef PETROVA_GLWINDOW_H
#define PETROVA_GLWINDOW_H

#include "player/GLTexture.h"
#include "data/VoData.h"

#include <EGL/egl.h>

class GLWindow {
public:
    static GLWindow *create(void *win);
    void render(VoData *voData);

private:
    GLWindow() {};
    bool init(void *win);
    void flip();

    EGLDisplay display = EGL_NO_DISPLAY;
    EGLSurface surface = EGL_NO_SURFACE;
    EGLContext context = EGL_NO_CONTEXT;

    GLTexture *glTexture = nullptr;
    void *win = nullptr;
};


#endif //PETROVA_GLWINDOW_H
