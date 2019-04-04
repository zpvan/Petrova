//
// Created by michelle on 2019/4/4.
//

#include "common/RunnablePsx.h"

#include "util/KisThd.h"
#include "util/KisLog.h"

#define TAG_LOG "RunnablePsx"

#define THREAD_FUNC_IN KLOGE(TAG_LOG, "%s In", __func__);
#define THREAD_FUNC_OUT KLOGE(TAG_LOG, "%s Out %s", __func__, getCurrentThreadname());

// Static
// Private
void *RunnablePsx::thread_run(void* data) {
    THREAD_FUNC_IN
    RunnablePsx *runnable = (RunnablePsx *)data;
    if (nullptr != runnable) {
        KLOGE(TAG_LOG, "pthread %s match %s", getCurrentThreadname(), runnable->get_thread_name());
        // 设置线程名称
        setCurrentThreadname(runnable->get_thread_name());
        runnable->isExit = false;
        while (!runnable->isExit) {
            runnable->thread_loop();
        }
    }
    // 不要漏了返回值, 会发生crash
    THREAD_FUNC_OUT
    return nullptr;
}

// Protected
bool RunnablePsx::start_thread() {
    if (isCreate) {
        return true;
    }
    if (pthread_create(&p_thread, nullptr, thread_run, this)) {
        KLOGE(TAG_LOG, "pthread_create %s failed!", get_thread_name());
        return false;
    }
    isCreate = true;
    return true;
}

void RunnablePsx::stop_thread() {
    if (!isCreate) {
        return;
    }
    isExit = true;
    void *join_ret;
    pthread_join(p_thread, &join_ret);
    KLOGE(TAG_LOG, "pthread_join %s result %s", getCurrentThreadname(), (char *)join_ret);
    isCreate = false;
}