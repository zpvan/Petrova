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
    private AndroidAES          mEncryptAES;
    private AndroidAES          mDecryptAES;

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
                                    testAES(AndroidAES.KAES_128_LENGTH);

                                    testAES(AndroidAES.KAES_256_LENGTH);
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

    private void testAES(int keyLength) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, InvalidKeyException {
        mReadAllRate = 0;
        mReadRate = 0;
        mEncryptRate = 0;
        mDecryptRate = 0;
        //int TIMES = 2;
        //for (int i = 0; i < TIMES; i++) {
        //    doSomething(i, keyLength);
        //}
        //Log.e(TAG, "key bit: " + (keyLength * 8) + "\n"
        //        + "readAllRate: " + (mReadAllRate / TIMES) + " B/s\n"
        //        + "readRate: " + (mReadRate / TIMES) + " B/s\n"
        //        + "encryptRate: " + (mEncryptRate / TIMES) + " B/s\n"
        //        + "decryptRate: " + (mDecryptRate / TIMES) + " B/s\n");

        doSomething(1, keyLength);
    }

    private static final int    READ_SIZE       = 1 * 1024;
    private static final int    ENCRYPT_PADDING = 100;
    private              byte[] mReadBuf1       = new byte[READ_SIZE + ENCRYPT_PADDING];
    private              byte[] mReadBuf10       = new byte[READ_SIZE * 10 + ENCRYPT_PADDING];
    private              byte[] mReadBuf50       = new byte[READ_SIZE * 50 + ENCRYPT_PADDING];
    private              byte[] mReadBuf100       = new byte[READ_SIZE * 100 + ENCRYPT_PADDING];
    private              byte[] mReadBuf500       = new byte[READ_SIZE * 500 + ENCRYPT_PADDING];
    private              byte[] mReadBuf1000       = new byte[READ_SIZE * 1000 + ENCRYPT_PADDING];
    private              int    mTotalSize      = 0;

    private void doSomething(int index, int keyLength) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, InvalidKeyException {
        String testFile = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Download/"
                + "teststream/"
                + "android-ndk-r16b-darwin-x86_64.zip";
        File file = new File(testFile);
        if (!file.canRead()) {
            Log.e(TAG, file.getAbsolutePath() + " can't read");
            return;
        }
        mEncryptAES = AndroidAES.create(keyLength);
        mDecryptAES = AndroidAES.create(keyLength);

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
        //long startTimeMillis = System.currentTimeMillis();
        //do {
        //    readStartTimeMillis = System.currentTimeMillis();
        //    read = fileInputStream.read(mReadBuf1, 0, READ_SIZE);
        //    readTimeMillis += (System.currentTimeMillis() - readStartTimeMillis);
        //    mTotalSize += read;
        //
        //    encryptStartTimeMillis = System.currentTimeMillis();
        //    int ct = encrypt128AES.encrypt(mReadBuf1, 0, READ_SIZE);
        //    encryptTimeMillis += (System.currentTimeMillis() - encryptStartTimeMillis);
        //
        //    decryptStartTimeMillis = System.currentTimeMillis();
        //    int pt = decrypt128AES.decrypt(mReadBuf1, 0, ct);
        //    decryptTimeMillis += (System.currentTimeMillis() - decryptStartTimeMillis);
        //} while (read == READ_SIZE);
        //long costTimeMillis = System.currentTimeMillis() - startTimeMillis;


        fileInputStream.read(mReadBuf1, 0, READ_SIZE);
        fileInputStream.read(mReadBuf10, 0, READ_SIZE * 10);
        fileInputStream.read(mReadBuf50, 0, READ_SIZE * 50);
        fileInputStream.read(mReadBuf100, 0, READ_SIZE * 100);
        fileInputStream.read(mReadBuf500, 0, READ_SIZE * 500);
        fileInputStream.read(mReadBuf1000, 0, READ_SIZE * 1000);

        Log.e(TAG, "key bit: " + (keyLength * 8));

        testEncryptBuffer(mReadBuf1, READ_SIZE);
        testEncryptBuffer(mReadBuf10, READ_SIZE * 10);
        testEncryptBuffer(mReadBuf50, READ_SIZE * 50);
        testEncryptBuffer(mReadBuf100, READ_SIZE * 100);
        testEncryptBuffer(mReadBuf500, READ_SIZE * 500);
        testEncryptBuffer(mReadBuf1000, READ_SIZE * 1000);

        //Log.e(TAG, "before-encrypt, md5: " + AndroidAES.MD5(mReadBuf1, 0, READ_SIZE));
        //try {
        //    int ct = encryptAES.encrypt(mReadBuf1, 0, READ_SIZE);
        //    Log.e(TAG, "after-encrypt, plainLength: " + read + ", chipherLength: " + (ct - 8));
        //    int pt = decryptAES.decrypt(mReadBuf1, 0, ct);
        //    Log.e(TAG, "after-decrypt, plainLength: " + pt + ", md5: " + AndroidAES.MD5(mReadBuf1, 0, pt));
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
        //mReadAllRate += (mTotalSize * 1000) / costTimeMillis;
        //mReadRate += (mTotalSize * 1000) / readTimeMillis;
        //mEncryptRate += (mTotalSize * 1000) / encryptTimeMillis;
        //mDecryptRate += (mTotalSize * 1000) / decryptTimeMillis;

    }

    private void testEncryptBuffer(byte[] buf, int bufSize) throws InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        long encryptStartTimeMillis;
        long decryptStartTimeMillis;
        long encryptTimeMillis = 0;
        long decryptTimeMillis = 0;
        for (int i = 0; i < 100; i++) {
            encryptStartTimeMillis = System.currentTimeMillis();
            int ct = mEncryptAES.encrypt(buf, 0, bufSize);
            encryptTimeMillis += (System.currentTimeMillis() - encryptStartTimeMillis);

            decryptStartTimeMillis = System.currentTimeMillis();
            int pt = mDecryptAES.decrypt(buf, 0, ct);
            decryptTimeMillis += (System.currentTimeMillis() - decryptStartTimeMillis);
        }

        Log.e(TAG, "bufSize: " + bufSize + "\n"
                + "encryptRate: " + (bufSize * 10) / encryptTimeMillis + " B/s\n"
                + "decryptRate: " + (bufSize * 10) / decryptTimeMillis + " B/s\n");
    }
}
