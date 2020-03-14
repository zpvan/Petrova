package com.example.aes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AES-MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            JavaxAES aes = new JavaxAES();
            //                       17    18    19    20    21    22    23
            byte[] pt = new byte[] {0x11, 0x23, 0x34, 0x45, 0x56, 0x11, 0x22};
            Log.e(TAG, "pt: " + JavaxAES.bytes2hex(pt));
            byte[] ct = aes.encrypt(pt);
            // TODO ct的长度跟pt的长度强相关, ct.length = pt.length + 16
            Log.e(TAG, "ct size: " + ct.length + ", content: " + JavaxAES.bytes2hex(ct));
            byte[] pt2 = aes.decrypt(ct);
            Log.e(TAG, "pt2: " + JavaxAES.bytes2hex(pt));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
