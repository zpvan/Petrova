package com.knox.kismet;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class KismetActivity extends AppCompatActivity {

    private static final String TAG = "KismetActivity";
    private PlayerProxy mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kismet);
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart: ");
        super.onStart();
        mPlayer = new PlayerProxy();
        mPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/Download/valor-01-01.mp4");
        mPlayer.prepare();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume: ");
        super.onResume();
        mPlayer.start();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause: ");
        super.onPause();
        mPlayer.pause();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop: ");
        super.onStop();
        mPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        super.onDestroy();
        mPlayer.release();
    }
}
