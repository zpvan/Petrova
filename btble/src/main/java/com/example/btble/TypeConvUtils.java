package com.example.btble;

public class TypeConvUtils {

    private static final String TAG = "TypeConvUtil";

    public static byte[] int2LeBytes(int value) {
        int temp = value;
        byte[] bs = new byte[4];
        for (int i = 0; i < bs.length; i++) {
            bs[i] = Integer.valueOf(temp & 0xFF).byteValue();//将最低位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
        return bs;
    }

    public static byte[] int2BeBytes(int value) {
        int temp = value;
        byte[] bs = new byte[4];
        for (int i = 0; i < bs.length; i++) {
            bs[bs.length - 1 - i] = Integer.valueOf(temp & 0xFF).byteValue();//将最低位保存在最高位
            temp = temp >> 8; // 向右移8位
        }
        return bs;
    }

    public static byte[] intTo16bits(int value) {
        byte[] b = new byte[2];
        int temp = value;
        for (int i = 0; i < 2; i++) {
            b[2 - 1 - i] = Integer.valueOf(temp & 0xFF).byteValue();
            temp >>= 8;
        }
        return b;
    }

    public static byte[] uint32ToBeBytes(long number) {
        long temp = number;
        byte[] b = new byte[4];
        for (int i = 0; i < b.length; i++) {
            b[b.length - 1 - i] = Long.valueOf(temp & 0xFF).byteValue();
            temp >>= 8;
        }
        return b;
    }

    public static byte intTo8bits(int value) {
        return Integer.valueOf(value & 0xFF).byteValue();
    }

    // TODO log hexprint byte[] --------------------------------------------------------------------

    public static String printByHex(byte[] buffer) {
        String h = "";

        for (int i = 0; i < buffer.length; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            h = h + " " + temp;
        }
        return h;
    }

    public static String printByChar(byte[] buffer, int length) {
        String h = "";

        for (int i = 0; i < length; i++) {
            char temp = (char) buffer[i];
            h = h + temp;
        }
        return h;
    }

    // TODO byte[] -> basicDataType ----------------------------------------------------------------

    public static int leBytes2Int(byte[] bs, int offset) {
        int value;
        value = (int) ((bs[offset] & 0xFF)
                | ((bs[offset + 1] & 0xFF) << 8)
                | ((bs[offset + 2] & 0xFF) << 16)
                | ((bs[offset + 3] & 0xFF) << 24));
        return value;
    }

    public static int beBytes2Int(byte[] bs, int offset) {
        int value;
        value = (int) ((bs[offset] & 0xFF) << 24
                | ((bs[offset + 1] & 0xFF) << 16)
                | ((bs[offset + 2] & 0xFF) << 8)
                | ((bs[offset + 3] & 0xFF)));
        return value;
    }

    public static long beBytes2Uint32(byte[] bs, int offset) {
        if (bs.length <= offset + 3)
            return 0;

        long value;
        value = ((bs[offset] & 0xFFL) << 24
                | ((bs[offset + 1] & 0xFFL) << 16)
                | ((bs[offset + 2] & 0xFFL) << 8)
                | ((bs[offset + 3] & 0xFFL)));
        return value;
    }

    public static byte beBytes2Byte(byte[] bs, int byte_offset, int bit_offset, int bit_length) {
        // 0 <= bit_offset <= 7
        // 1 <= bit_length <= 8
        if (bit_offset < 0 || bit_offset > 7 || bit_length < 1 || bit_length > 8) {
            return 0;
        }
        if ((bs.length <= byte_offset + ((bit_offset + bit_length - 1) < 8 ? 0 : 1))) {
            return 0;
        }

        int base = 0x80;
        base >>>= bit_offset;

        int value = 0;
        if (bit_offset + bit_length > 8) {
        } else {
            for (int i = 0; i < bit_length; i++) {
                base |= (base >>> i);
            }
            value = bs[byte_offset] & base;
            value >>>= (8 - bit_offset - bit_length);
        }
        return (byte) value;
    }

    public static int beBytes2Int(byte[] bs, int offset, int length) {
        if (bs.length <= offset + length - 1) {
            return 0;
        }
        int value = 0;
        for (int i = 0; i < length; i++) {
            value |= (int) ((bs[offset + i] & 0xFF) << (8 * (length - 1 - i)));
        }
        return value;
    }

    public static String bytes2String(byte[] bs, int offset, int length) {
        int index = 0;
        while (index < length) {
            if (bs[offset + index] == 0x00)
                break;
            index++;
        }
        return (index > 0) ? new String(bs, offset, index) : "0";
    }
    
}
