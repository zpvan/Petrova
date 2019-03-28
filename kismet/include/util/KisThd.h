//
// Created by knox on 27/3/2019.
//

#ifndef PETROVA_THREAD_H
#define PETROVA_THREAD_H

#include <sys/prctl.h>

static char* getCurrentThreadname() {
    static char tname[16];
    prctl(PR_GET_NAME, tname);
    return tname;
}

static void setCurrentThreadname(const char *tname) {
    prctl(PR_SET_NAME, tname);
}

#endif //PETROVA_THREAD_H
