//
// Created by michelle on 2019/4/2.
//

#include "player/GLTexture.h"

void GLTexture::init(int colorFmt) {
    glShader.init(GLShader::pixelFmt2ShaderType((AVPixelFormat) colorFmt));
}

void GLTexture::draw(unsigned char *data[], int width, int height) {
    glShader.GetTexture(0, width, height, data[0]);         // Y
    switch (glShader.getFragmentShaderType()) {
        case FS_TYPE_YUV420P:
            glShader.GetTexture(1, width / 2, height / 2, data[1]); // U
            glShader.GetTexture(2, width / 2, height / 2, data[2]); // V
            break;

        case FS_TYPE_NV12:
        case FS_TYPE_NV21:
            glShader.GetTexture(1, width / 2, height / 2, data[1], true); // UV
            break;

        default:
            break;
    }
    glShader.draw();
}