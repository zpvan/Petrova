//
// Created by michelle on 2019/4/2.
//

#ifndef PETROVA_GLSHADER_H
#define PETROVA_GLSHADER_H

extern "C" {
#include "libavutil/pixfmt.h"
};

enum FRAGMENT_SHADER_TYPE {
    FS_TYPE_UNKNOWN = -1,
    FS_TYPE_YUV420P = 0,  // Y 4  U 1  V 1
    FS_TYPE_NV12 = 1,     // Y 4  UV 1
    FS_TYPE_NV21 = 2,     // Y 4  VU 1
};

class GLShader {
public:
    static FRAGMENT_SHADER_TYPE pixelFmt2ShaderType(AVPixelFormat pixelFormat);
    bool init(FRAGMENT_SHADER_TYPE type);
    FRAGMENT_SHADER_TYPE getFragmentShaderType();
    //获取材质并映射到内存
    void GetTexture(unsigned int index, int width, int height, unsigned char *buf, bool isa = false);
    void draw();

private:
    FRAGMENT_SHADER_TYPE ftype = FS_TYPE_UNKNOWN;
    unsigned int vsh;
    unsigned int fsh;
    unsigned int program;
    unsigned int texts[100] = {0};
};


#endif //PETROVA_GLSHADER_H
