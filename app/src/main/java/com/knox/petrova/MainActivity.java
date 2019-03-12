package com.knox.petrova;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.knox.kismet.NativeWrapper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvText = findViewById(R.id.tv_test);
        tvText.setText(NativeWrapper.stringFromJNI());
    }
}
