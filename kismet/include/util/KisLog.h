//
// Created by michelle on 2019/3/22.
//

#ifndef PETROVA_KISLOG_H
#define PETROVA_KISLOG_H

#ifdef ANDROID

#include <android/log.h>

#define KLOGD(TAG, ...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define KLOGE(TAG, ...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

#else

#define KLOGD(TAG, ...) printf(TAG, __VA_ARGS__)
#define KLOGE(TAG, ...) printf(TAG, __VA_ARGS__)

#endif

#include <stdlib.h>
#include <string.h>

static void hex_dump(const char *tag, const unsigned char *buf, const int num)
{
    int dump_size = num * 3 * sizeof(unsigned char);
    char *dump = (char *)malloc(dump_size);
    memset(dump, 0, dump_size);
    int i;
    for(i = 0; i < num; i++) {
        sprintf(dump + i * 3, "%02X ", buf[i]);
    }
    KLOGE(tag, "hex_dump %s", dump);
    free(dump);
    return;
}

#endif //PETROVA_KISLOG_H
