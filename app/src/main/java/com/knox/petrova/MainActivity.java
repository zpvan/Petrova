package com.knox.petrova;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

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
                startAarActivity("com.knox.petrova", "com.knox.kismet.KismetActivity");
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
}
