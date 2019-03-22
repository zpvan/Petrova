package com.knox.kismet.player;

import android.view.SurfaceHolder;

/**
 * @author Knox.Tsang
 * @time 2019/3/22  9:08
 * @desc ${TODD}
 */

public interface BasePlayer {
    void setDataSource (String path);
    void setDisplay (SurfaceHolder sh);
    void prepare ();
    void prepareAsync ();
    void start ();
    void stop ();
    void pause ();
    void reset ();
    void release ();
}
