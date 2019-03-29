package com.knox.kismet;

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class KismetActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private static final String TAG = "KismetActivity";

    private static final String MEDIA_FILE = "/mnt/sdcard/Download/valor-01-01.mp4";
    private PlayerProxy mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpView();
        // setContentView(R.layout.activity_kismet);
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
//        File mediaFile = new File(MEDIA_FILE);
        File mediaFile = new File("/mnt/sdcard/DCIM/Camera/1548569015270.jpg");
        if (mediaFile.exists()) {
            Log.e(TAG, "surfaceCreated: mediaFile(exists=" + mediaFile.exists() + " read=" +
                    mediaFile.canRead() + " write=" + mediaFile.canWrite() + " execute=" + mediaFile.canExecute() + ")");
        }
        mPlayer.setDataSource(MEDIA_FILE);
//        mPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/Download/valor-01-01.mp4");
        mPlayer.setDisplay(holder);
        mPlayer.prepare();
        mPlayer.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "surfaceChanged: HolderCallback");
        if (null != mPlayer) {
            mPlayer.setDisplay(holder);
        }
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
        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.getHolder().addCallback(this);
        glSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                Log.e(TAG, "onSurfaceCreated: GLSurfaceView.Renderer");
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                Log.e(TAG, "onSurfaceChanged: GLSurfaceView.Renderer");
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            }
        });
        TextView textView = new TextView(this);
        textView.setText(TAG);
        textView.setTextColor(Color.RED);
        frameLayout.addView(glSurfaceView);
        frameLayout.addView(textView);
        setContentView(frameLayout);
    }
}
