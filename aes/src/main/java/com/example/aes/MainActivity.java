package com.example.aes;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AES-MAIN";
    private              long   mReadAllRate;
    private              long   mReadRate;
    private              long   mEncryptRate;
    private              long   mDecryptRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView viewById = findViewById(R.id.tv_text);
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.runOnPermissionGranted(MainActivity.this, new Runnable() {
                    @Override
                    public void run() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mReadAllRate = 0;
                                    mReadRate = 0;
                                    mEncryptRate = 0;
                                    mDecryptRate = 0;
                                    int TIMES = 100;
                                    for (int i = 0; i < TIMES; i++) {
                                        doSomething(i);
                                    }
                                    Log.e(TAG, "readAllRate: " + (mReadAllRate / TIMES) + " B/s\n"
                                            + "readRate: " + (mReadRate / TIMES) + " B/s\n"
                                            + "encryptRate: " + (mEncryptRate / TIMES) + " B/s\n"
                                            + "decryptRate: " + (mDecryptRate / TIMES) + " B/s\n");
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (InvalidKeyException e) {
                                    e.printStackTrace();
                                } catch (ShortBufferException e) {
                                    e.printStackTrace();
                                } catch (IllegalBlockSizeException e) {
                                    e.printStackTrace();
                                } catch (BadPaddingException e) {
                                    e.printStackTrace();
                                } catch (InvalidAlgorithmParameterException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "can't have permision", Toast.LENGTH_LONG).show();
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });

        //try {
        //    JavaxAES aes = new JavaxAES();
        //    //                       17    18    19    20    21    22    23
        //    byte[] pt = new byte[] {0x11, 0x23, 0x34, 0x45, 0x56, 0x11, 0x22};
        //    Log.e(TAG, "pt: " + JavaxAES.bytes2hex(pt));
        //    byte[] ct = aes.encrypt(pt);
        //    // TODO ct的长度跟pt的长度强相关, ct.length = pt.length + 16
        //    Log.e(TAG, "ct size: " + ct.length + ", content: " + JavaxAES.bytes2hex(ct));
        //    byte[] pt2 = aes.decrypt(ct);
        //    Log.e(TAG, "pt2: " + JavaxAES.bytes2hex(pt));
        //
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}


    }

    private static final int    READ_SIZE       = 60 * 1024;
    private static final int    ENCRYPT_PADDING = 100;
    private              byte[] mReadBuf        = new byte[READ_SIZE + ENCRYPT_PADDING];
    private              int    mTotalSize      = 0;

    private void doSomething(int index) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, InvalidKeyException {
        String testFile = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Download/"
                + "teststream/"
                + "android-ndk-r16b-darwin-x86_64.zip";
        File file = new File(testFile);
        if (!file.canRead()) {
            Log.e(TAG, file.getAbsolutePath() + " can't read");
            return;
        }
        AndroidAES encryptAES = AndroidAES.create();
        AndroidAES decryptAES = AndroidAES.create();

        Log.e(TAG, "do Something (" + index + ")");
        FileInputStream fileInputStream = new FileInputStream(file);
        mTotalSize = 0;
        int read = 0;
        long encryptStartTimeMillis = 0;
        long encryptTimeMillis = 1;
        long decryptStartTimeMillis = 0;
        long decryptTimeMillis = 1;
        long readStartTimeMillis = 0;
        long readTimeMillis = 1;
        long startTimeMillis = System.currentTimeMillis();
        do {
            readStartTimeMillis = System.currentTimeMillis();
            read = fileInputStream.read(mReadBuf, 0, READ_SIZE);
            readTimeMillis += (System.currentTimeMillis() - readStartTimeMillis);
            mTotalSize += read;

            encryptStartTimeMillis = System.currentTimeMillis();
            int ct = encryptAES.encrypt(mReadBuf, 0, READ_SIZE);
            encryptTimeMillis += (System.currentTimeMillis() - encryptStartTimeMillis);

            decryptStartTimeMillis = System.currentTimeMillis();
            int pt = decryptAES.decrypt(mReadBuf, 0, ct);
            decryptTimeMillis += (System.currentTimeMillis() - decryptStartTimeMillis);
        } while (read == READ_SIZE);
        long costTimeMillis = System.currentTimeMillis() - startTimeMillis;


        //read = fileInputStream.read(mReadBuf, 0, READ_SIZE);
        //Log.e(TAG, "before-encrypt, md5: " + AndroidAES.MD5(mReadBuf, 0, READ_SIZE));
        //try {
        //    int ct = encryptAES.encrypt(mReadBuf, 0, READ_SIZE);
        //    Log.e(TAG, "after-encrypt, plainLength: " + read + ", chipherLength: " + (ct - 8));
        //    int pt = decryptAES.decrypt(mReadBuf, 0, ct);
        //    Log.e(TAG, "after-decrypt, plainLength: " + pt + ", md5: " + AndroidAES.MD5(mReadBuf, 0, pt));
        //} catch (InvalidAlgorithmParameterException e) {
        //    e.printStackTrace();
        //} catch (InvalidKeyException e) {
        //    e.printStackTrace();
        //} catch (BadPaddingException e) {
        //    e.printStackTrace();
        //} catch (IllegalBlockSizeException e) {
        //    e.printStackTrace();
        //} catch (ShortBufferException e) {
        //    e.printStackTrace();
        //}


        fileInputStream.close();
        mReadAllRate += (mTotalSize * 1000) / costTimeMillis;
        mReadRate += (mTotalSize * 1000) / readTimeMillis;
        mEncryptRate += (mTotalSize * 1000) / encryptTimeMillis;
        mDecryptRate += (mTotalSize * 1000) / decryptTimeMillis;

    }
}
