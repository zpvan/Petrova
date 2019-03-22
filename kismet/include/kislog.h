//
// Created by michelle on 2019/3/22.
//

#ifndef PETROVA_KISLOG_H
#define PETROVA_KISLOG_H

#ifdef ANDROID

#include "android/log.h"

#define KLOGD(TAG, ...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define KLOGE(TAG, ...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

#else

#define KLOGD(TAG, ...) printf(TAG, __VA_ARGS__)
#define KLOGE(TAG, ...) printf(TAG, __VA_ARGS__)

#endif

#endif //PETROVA_KISLOG_H
