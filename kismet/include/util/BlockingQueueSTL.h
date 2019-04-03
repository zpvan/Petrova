//
// Created by michelle on 2019/4/3.
//

#ifndef PETROVA_KISLIST_H
#define PETROVA_KISLIST_H

#include <list>
#include <mutex>
#include <condition_variable>

template <typename T> class BlockingQueueSTL {
private:
    std::list<T> list;
    std::mutex mutex;
    std::condition_variable empty_cond;
    std::condition_variable full_cond;
    int capacity;

    bool is_full_locked() {
        return capacity == list.size();
    }

    bool is_empty_locked() {
        return 0 == list.size();
    }

public:
    BlockingQueueSTL(int capacity) {
        this->capacity = capacity;
    }

    T get_font() {
        std::unique_lock<std::mutex> locker(mutex);

        while (is_empty_locked()) {
            empty_cond.wait(locker);
        }
        T t = list.front();
        list.pop_front();

        locker.unlock();
        full_cond.notify_all();
        return t;
    }

    void push_back(T t) {
        std::unique_lock<std::mutex> locker(mutex);

        while (is_full_locked()) {
            full_cond.wait(locker);
        }

        list.push_back(t);

        locker.unlock();

        empty_cond.notify_all();
    }
};


#endif //PETROVA_KISLIST_H
