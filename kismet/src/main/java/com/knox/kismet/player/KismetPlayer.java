package com.knox.kismet.player;

import android.view.SurfaceHolder;

/**
 * @author Knox.Tsang
 * @time 2019/3/22  9:08
 * @desc ${TODD}
 */

public class KismetPlayer implements BasePlayer {

    static {
        System.loadLibrary("kismet");
        native_init();
    }

    /**
     * JNI Java层对象和C++底层对象关联，使用与"传long指针"不一样的方式
     * https://blog.csdn.net/hongdameng/article/details/80028398
     */
    private long mNativePlayer;

    public KismetPlayer() {
        native_setup();
    }

    @Override
    public void setDataSource(String path) {
        native_setDataSource(path);
    }

    @Override
    public void setDisplay(SurfaceHolder sh) {
        native_setDisplay(sh.getSurface());
    }

    @Override
    public void prepare() {
        native_prepare();
    }

    @Override
    public void prepareAsync() {
        native_prepareAsync();
    }

    @Override
    public void start() {
        native_start();
    }

    @Override
    public void stop() {
        native_stop();
    }

    @Override
    public void pause() {
        native_pause();
    }

    @Override
    public void reset() {
        native_reset();
    }

    @Override
    public void release() {
        native_release();
    }

    // native
    public static native void native_init();
    public native void native_setup();
    public native void native_setDataSource(String path);
    public native void native_setDisplay(Object surface);
    public native void native_prepare();
    public native void native_prepareAsync();
    public native void native_start();
    public native void native_stop();
    public native void native_pause();
    public native void native_reset();
    public native void native_release();
}
