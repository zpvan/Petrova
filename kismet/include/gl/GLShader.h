//
// Created by michelle on 2019/4/2.
//

#ifndef PETROVA_GLSHADER_H
#define PETROVA_GLSHADER_H

extern "C" {
#include <libavutil/pixfmt.h>
};

typedef enum {
    FS_TYPE_UNKNOWN = -1,
    FS_TYPE_YUV420P,  // Y 4  U 1  V 1
    FS_TYPE_NV12,     // Y 4  UV 1
    FS_TYPE_NV21,     // Y 4  VU 1
} fragment_shader_t;

class GLShader {
public:
    static fragment_shader_t pixelFmt2ShaderType(AVPixelFormat pixelFormat);
    bool init(fragment_shader_t type);
    fragment_shader_t getFragmentShaderType();
    //获取材质并映射到内存
    void GetTexture(unsigned int index, int width, int height, unsigned char *buf, bool isa = false);
    void draw();

private:
    fragment_shader_t ftype = FS_TYPE_UNKNOWN;
    unsigned int vsh;
    unsigned int fsh;
    unsigned int program;
    unsigned int texts[100] = {0};
};


#endif //PETROVA_GLSHADER_H
