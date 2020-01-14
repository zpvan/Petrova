package com.knox.petrova;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.knox.kismet.NativeWrapper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvText = findViewById(R.id.tv_test);
        tvText.setText(NativeWrapper.stringFromJNI());

        tvText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.runOnPermissionGranted(MainActivity.this, new Runnable() {
                    @Override
                    public void run() {
                        startAarActivity("com.knox.petrova", "com.knox.kismet.KismetActivity");
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "can't have permision", Toast.LENGTH_LONG).show();
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE);

            }
        });
    }

    /**
     * 启动存在于android library的activity
     */
    private void startAarActivity(String myPackageName, String targetActivityName) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName(myPackageName, targetActivityName));
        getApplicationContext().startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
