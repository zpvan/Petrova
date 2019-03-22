//
// Created by michelle on 2019/3/22.
//

#ifndef PETROVA_KISMETPLAYER_H
#define PETROVA_KISMETPLAYER_H


class kismetplayer {

public:
    void setDataSource (const char *path);
    void setDisplay (void *surface);
    void prepare ();
    void prepareAsync ();
    void start ();
    void stop ();
    void pause ();
    void reset ();
    void release ();
};


#endif //PETROVA_KISMETPLAYER_H
