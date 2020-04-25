package com.example.aes;

import android.util.Log;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AndroidAES {

    /**
     * AES-128-GCM-NoPadding
     * 2020-04-26 02:12:22.014 8632-8762/com.example.aes E/AES-MAIN: readAllRate: 535524 B/s
     *     readRate: 4801508 B/s
     *     encryptRate: 1218930 B/s
     *     decryptRate: 1201804 B/s
     */

    /**
     * AES-256-GCM-NoPadding
     * 2020-04-26 07:52:06.388 32361-32724/com.example.aes E/AES-MAIN: readAllRate: 514062 B/s
     *     readRate: 4915805 B/s
     *     encryptRate: 1156137 B/s
     *     decryptRate: 1148213 B/s
     */

    /**
     * AES-GCM-NoPadding key128
     * 2020-04-26 02:01:34.753 8138-8283/com.example.aes E/AES-MAIN: readAllRate: 536041 B/s
     *     readRate: 4732857 B/s
     *     encryptRate: 1208428 B/s
     *     decryptRate: 1219100 B/s
     */

    /**
     * AES-GCM-NoPadding key256
     * 2020-04-26 01:53:33.423 7729-7851/com.example.aes E/AES-MAIN: readAllRate: 511981 B/s
     *     readRate: 4824125 B/s
     *     encryptRate: 1151645 B/s
     *     decryptRate: 1146407 B/s
     */

    private static final String TAG = "AndroidAES";

    private static final String AES_ALGORITHM  = "AES";
    private static final String AES_GCM_PARAMS = "AES/GCM/NoPadding";
    private static final String HMAC_SHA_512   = "HmacSHA512";
    private static final int    T_LENGTH       = 8;

    private static final int AUTH_TAG_LENGTH_BITS = 128;
    private static final int IV_OFFSET            = 0;
    private static final int IV_LENGTH            = 12;
    private static final int AAD_OFFSET           = 12;
    private static final int AAD_LENGTH           = 20;
    private static final int KAES_OFFSET          = 32;
    public static final  int KAES_128_LENGTH      = 16;
    public static final  int KAES_256_LENGTH      = 32;

    private       Cipher mCipher;
    private       Mac    mHmacSha512;
    // 16byte + 8byte + 8byte + 8byte
    private       byte[] mHmacShaData;
    private       long   mT = 0;

    public static AndroidAES create() {
        try {
            return new AndroidAES();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int encrypt(byte[] plain, int offset, int length) throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        mT++;
        System.arraycopy(long2Bytes(mT), 0, mHmacShaData, mHmacShaData.length
                - T_LENGTH, T_LENGTH);
        byte[] aesGcmParams = mHmacSha512.doFinal(mHmacShaData);

        GCMParameterSpec ivSpec = new GCMParameterSpec(AUTH_TAG_LENGTH_BITS, aesGcmParams, IV_OFFSET, IV_LENGTH);
        SecretKeySpec secretKeySpec = new SecretKeySpec(aesGcmParams, KAES_OFFSET, KAES_256_LENGTH, AES_ALGORITHM);
        mCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
        mCipher.updateAAD(aesGcmParams, AAD_OFFSET, AAD_LENGTH);

        int ctLength = mCipher.doFinal(plain, offset, length, plain, offset + T_LENGTH);
        if (ctLength <= 0) {
            Log.e(TAG, "cipher encrypt error");
            return -1;
        }
        System.arraycopy(long2Bytes(mT), 0, plain, offset, T_LENGTH);
        ctLength += T_LENGTH;
        return ctLength;
    }

    public int decrypt(byte[] chiper, int offset, int length) throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        long t = bytes2Long(chiper, offset);
        if (t < mT) {
            return -1;
        }
        mT = t;
        System.arraycopy(chiper, offset, mHmacShaData, mHmacShaData.length - T_LENGTH, T_LENGTH);
        byte[] aesGcmParams = mHmacSha512.doFinal(mHmacShaData);

        GCMParameterSpec ivSpec = new GCMParameterSpec(AUTH_TAG_LENGTH_BITS, aesGcmParams, IV_OFFSET, IV_LENGTH);
        SecretKeySpec secretKeySpec = new SecretKeySpec(aesGcmParams, KAES_OFFSET, KAES_256_LENGTH, AES_ALGORITHM);
        mCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
        mCipher.updateAAD(aesGcmParams, AAD_OFFSET, AAD_LENGTH);

        int ptLength = mCipher.doFinal(chiper, offset + T_LENGTH, length - T_LENGTH, chiper, offset);
        return ptLength;
    }

    private AndroidAES() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {

        byte[] challenge = "QWERTYASDFGHZXCV".getBytes();
        mHmacShaData = new byte[challenge.length + T_LENGTH];
        System.arraycopy(challenge, 0, mHmacShaData, 0, challenge.length);

        byte[] key = "QWSDCVMLPOKJIUHG".getBytes();
        mHmacSha512 = Mac.getInstance(HMAC_SHA_512);
        mHmacSha512.init(new SecretKeySpec(key, HMAC_SHA_512));

        mCipher = Cipher.getInstance(AES_GCM_PARAMS);
    }

    private static byte[] long2Bytes(long num) {
        byte[] bytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            int shift = 8 - ((i + 1) << 3);
            bytes[i] = (byte) ((num >> shift) & 0xff);
        }
        return bytes;
    }

    private static long bytes2Long(byte[] input, int offset) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            int shift = (7 - i) << 3;
            value |= ((long) 0xff << shift) & ((long) input[offset + 1] << shift);
        }
        return value;
    }

    public static String MD5(byte[] data, int offset, int length) {
        byte[] src = new byte[length];
        System.arraycopy(data, offset, src, 0, length);
        try {
            byte[] md5s = MessageDigest.getInstance("md5").digest(src);
            if (md5s.length == 0) {
                return null;
            }
            return bytes2hex(md5s);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String bytes2hex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        String tmp = null;
        for (byte b : bytes) {
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            builder.append(tmp).append(" ");
        }
        return builder.toString();
    }
}
