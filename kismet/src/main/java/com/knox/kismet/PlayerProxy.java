package com.knox.kismet;

import android.view.SurfaceHolder;

import com.knox.kismet.player.BasePlayer;
import com.knox.kismet.player.KismetPlayer;

/**
 * @author Knox.Tsang
 * @time 2019/3/22  8:58
 * @desc
 * 1. 原型参考
 * https://developer.android.google.cn/reference/android/media/MediaPlayer
 *
 */

public class PlayerProxy implements BasePlayer {

    private BasePlayer mInnerPlayer;

    @Override
    public void setDataSource(String path) {
        mInnerPlayer = findBestPlayer(path);
        mInnerPlayer.setDataSource(path);
    }

    @Override
    public void setDisplay(SurfaceHolder sh) {
        mInnerPlayer.setDisplay(sh);
    }

    @Override
    public void prepare() {
        mInnerPlayer.prepare();
    }

    @Override
    public void prepareAsync() {
        mInnerPlayer.prepareAsync();
    }

    @Override
    public void start() {
        mInnerPlayer.start();
    }

    @Override
    public void stop() {
        mInnerPlayer.stop();
    }

    @Override
    public void pause() {
        mInnerPlayer.pause();
    }

    @Override
    public void reset() {
        mInnerPlayer.reset();
    }

    @Override
    public void release() {
        mInnerPlayer.release();
    }

    private BasePlayer findBestPlayer(String path) {
        // TODO
        return new KismetPlayer();
    }
}
