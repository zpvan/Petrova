package com.example.aes;

import android.util.Base64;
import android.util.Log;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class JavaxAES {
    public static String cKey = "qglHs5p4xtvU8RO2";
    private static String civ = "XUfqi3bWThv5wsd2";
    public static final String SECRYPT_ALGORITHM = "AES/GCM/NoPadding";

    private static final String TAG = "AES";

    /*
     * 加密用的Key 可以用26个字母和数字组成，最好不要用保留字符，虽然不会错，至于怎么裁决，个人看情况而定
     * 此处使用AES-128-CBC加密模式，key需要为16位。
     */
    public static void main(String[] args) throws Exception {
        // 需要加密的字串
        String cSrc = "你好";
        Log.d(TAG,"原字符串: "+cSrc);
        // 加密
        String enString = Encrypt(cSrc, cKey);
        Log.d(TAG,"加密后的字串 个数: " + enString.length() + ", 是: " + enString);
        // 解密
        String DeString = Decrypt(enString, cKey);
        Log.d(TAG,"解密后的字串是: " + DeString);
    }


    //加密
    public static String  Encrypt(String message, String sKey) throws Exception {
        if (sKey == null) {
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            return null;
        }
        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance(SECRYPT_ALGORITHM);//"算法/模式/补码方式"
        byte[] bytes = civ.getBytes();
        GCMParameterSpec ivSpec = new GCMParameterSpec(128, bytes);//GCM向量iv
        //byte[] bytes = cipher.getIV();
        //IvParameterSpec iv = new IvParameterSpec(bytes);//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(message.getBytes());
        return Base64.encodeToString(encrypted, Base64.DEFAULT);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }

    // 解密
    public static String Decrypt(String sSrc, String sKey) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance(SECRYPT_ALGORITHM);
            byte[] bytes = civ.getBytes();
            GCMParameterSpec ivSpec = new GCMParameterSpec(128, bytes);//GCM向量iv
            //IvParameterSpec iv = new IvParameterSpec(civ.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
            byte[] encrypted1 = Base64.decode(sSrc, Base64.DEFAULT);//先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            return originalString;
        } catch (Throwable e) {
            return null;
        }
    }

    //----------------------------------------------------------------------------------------------
    public static final String AES_GCM_PARAMS = "AES/GCM/NoPadding";

    private byte[] mKsc = ("0123456789ABCDEF").getBytes();
    private byte[] mQC = ("01234567").getBytes();
    private byte[] mQS = ("01234567").getBytes();
    private long mT = 1100;
    private byte[] mKaes = new byte[16];
    private byte[] mIV = new byte[12];
    private byte[] mAAD = new byte[20];

    public void init() {

    }

    public byte[] encrypt(byte[] pt) {
        byte[] ct = null;
        try {
            Mac sha384_HMAC = Mac.getInstance("HmacSHA384");

            SecretKeySpec secret_key = new SecretKeySpec(mKsc, "HmacSHA384");

            sha384_HMAC.init(secret_key);

            byte[] data = new byte[mQC.length + mQS.length + 8];
            System.arraycopy(mQC, 0, data, 0, mQC.length);
            System.arraycopy(mQS, 0, data, mQC.length, mQS.length);
            System.arraycopy(long2Bytes(mT), 0, data, mQC.length + mQS.length, 8);
            byte[] aesGcmParams = sha384_HMAC.doFinal(data);

            mKaes = Arrays.copyOfRange(aesGcmParams, 0, 16);
            mIV = Arrays.copyOfRange(aesGcmParams, 16, 28);
            mAAD = Arrays.copyOfRange(aesGcmParams, 28, 48);


            Cipher cipher = Cipher.getInstance(AES_GCM_PARAMS);
            GCMParameterSpec ivSpec = new GCMParameterSpec(128, mIV);
            SecretKeySpec skeySpec = new SecretKeySpec(mKaes, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
            cipher.updateAAD(mAAD);
            ct = cipher.doFinal(pt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ct;
    }

    public byte[] decrypt(byte[] ct) {
        byte[] pt = null;

        try {
            Mac sha384_HMAC = Mac.getInstance("HmacSHA384");

            SecretKeySpec secret_key = new SecretKeySpec(mKsc, "HmacSHA384");

            sha384_HMAC.init(secret_key);

            byte[] data = new byte[mQC.length + mQS.length + 8];
            System.arraycopy(mQC, 0, data, 0, mQC.length);
            System.arraycopy(mQS, 0, data, mQC.length, mQS.length);
            System.arraycopy(long2Bytes(mT), 0, data, mQC.length + mQS.length, 8);
            byte[] aesGcmParams = sha384_HMAC.doFinal(data);

            mKaes = Arrays.copyOfRange(aesGcmParams, 0, 16);
            mIV = Arrays.copyOfRange(aesGcmParams, 16, 28);
            mAAD = Arrays.copyOfRange(aesGcmParams, 28, 48);


            Cipher cipher = Cipher.getInstance(AES_GCM_PARAMS);
            GCMParameterSpec ivSpec = new GCMParameterSpec(128, mIV);
            SecretKeySpec skeySpec = new SecretKeySpec(mKaes, "AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
            cipher.updateAAD(mAAD);
            pt = cipher.doFinal(ct);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pt;
    }

    //----------------------------------------------------------------------------------------------
    public static String bytes2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (byte b : bytes) {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            sb.append(tmp);
        }
        return sb.toString();
    }

    public static byte[] int2Bytes(int num) {
        byte[] byteNum = new byte[4];
        for (int ix = 0; ix < 4; ++ix) {
            int offset = 32 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }

    public static int bytes2Int(byte[] byteNum) {
        int num = 0;
        for (int ix = 0; ix < 4; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }

    public static byte int2OneByte(int num) {
        return (byte) (num & 0x000000ff);
    }

    public static int oneByte2Int(byte byteNum) {
        return byteNum > 0 ? byteNum : (128 + (128 + byteNum));
    }

    public static byte[] long2Bytes(long num) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }

    public static long bytes2Long(byte[] byteNum) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }
}
