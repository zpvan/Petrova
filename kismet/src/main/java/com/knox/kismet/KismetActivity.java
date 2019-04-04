package com.knox.kismet;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

public class KismetActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private static final String TAG = "KismetActivity";

    private static final String MEDIA_FILE = "/mnt/sdcard/Download/valor-01-01.mp4";
    private PlayerProxy mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen & Landscape
        setFullscreenAndLandscape();

        // SurfaceView
        setUpView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mPlayer) {
            mPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mPlayer) {
            mPlayer.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mPlayer) {
            mPlayer.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPlayer) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated: HolderCallback");
        if (null == mPlayer) {
            mPlayer = new PlayerProxy();
        }
        mPlayer.setDataSource(MEDIA_FILE);
        mPlayer.setDisplay(holder);
        mPlayer.prepare();
        mPlayer.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "surfaceChanged: HolderCallback");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed: HolderCallback");
        if (null != mPlayer) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void setUpView() {
        FrameLayout frameLayout = new FrameLayout(this);
        SurfaceView glSurfaceView = new SurfaceView(this);
        glSurfaceView.getHolder().addCallback(this);
        // glSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
        //     @Override
        //     public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //         Log.e(TAG, "onSurfaceCreated: GLSurfaceView.Renderer");
        //     }
        //
        //     @Override
        //     public void onSurfaceChanged(GL10 gl, int width, int height) {
        //         Log.e(TAG, "onSurfaceChanged: GLSurfaceView.Renderer");
        //     }
        //
        //     @Override
        //     public void onDrawFrame(GL10 gl) {
        //         // GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //     }
        // });
        TextView textView = new TextView(this);
        textView.setText(TAG);
        textView.setTextColor(Color.RED);
        frameLayout.addView(glSurfaceView);
        frameLayout.addView(textView);
        setContentView(frameLayout);
    }

    private void setFullscreenAndLandscape() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
}
