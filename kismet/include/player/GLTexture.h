//
// Created by michelle on 2019/4/2.
//

#ifndef PETROVA_GLTEXTURE_H
#define PETROVA_GLTEXTURE_H

#include "player/GLShader.h"

class GLTexture {
public:
    void init(int colorFmt);
    void draw(unsigned char *data[], int width, int height);

private:
    GLShader glShader;
};


#endif //PETROVA_GLTEXTURE_H
