package com.tpms.utils;

import com.umeng.commonsdk.proguard.ap;

import java.io.PrintStream;

public class FormatTransfer {
    public static byte[] toHH(int i) {
        byte[] bArr = new byte[4];
        bArr[3] = (byte) (i & 255);
        bArr[2] = (byte) ((i >> 8) & 255);
        bArr[1] = (byte) ((i >> 16) & 255);
        bArr[0] = (byte) ((i >> 24) & 255);
        return bArr;
    }

    public static byte[] toHH(short s) {
        byte[] bArr = new byte[2];
        bArr[1] = (byte) (s & 255);
        bArr[0] = (byte) ((s >> 8) & 255);
        return bArr;
    }

    public static byte[] toLH(int i) {
        return new byte[]{(byte) (i & 255), (byte) ((i >> 8) & 255), (byte) ((i >> 16) & 255), (byte) ((i >> 24) & 255)};
    }

    public static byte[] toLH(short s) {
        return new byte[]{(byte) (s & 255), (byte) ((s >> 8) & 255)};
    }

    public static byte[] toLH(float f) {
        return toLH(Float.floatToRawIntBits(f));
    }

    public static byte[] toHH(float f) {
        return toHH(Float.floatToRawIntBits(f));
    }

    public static byte[] stringToBytes(String str, int i) {
        while (str.getBytes().length < i) {
            str = str + " ";
        }
        return str.getBytes();
    }

    public static String bytesToString(byte[] bArr) {
        StringBuilder stringBuffer = new StringBuilder("");
        for (byte b : bArr) {
            stringBuffer.append((char) (b & 255));
        }
        return stringBuffer.toString();
    }

    public static byte[] stringToBytes(String str) {
        return str.getBytes();
    }

    public static int hBytesToInt(byte[] bArr) {
        byte b;
        byte b2;
        int i = 0;
        for (int i2 = 0; i2 < 3; i2++) {
            if (bArr[i2] >= 0) {
                b2 = bArr[i2];
            } else {
                i += 256;
                b2 = bArr[i2];
            }
            i = (i + b2) * 256;
        }
        if (bArr[3] >= 0) {
            b = bArr[3];
        } else {
            i += 256;
            b = bArr[3];
        }
        return i + b;
    }

    public static int lBytesToInt(byte[] bArr) {
        byte b;
        byte b2;
        int i = 0;
        for (int i2 = 0; i2 < 3; i2++) {
            int i3 = 3 - i2;
            if (bArr[i3] >= 0) {
                b2 = bArr[i3];
            } else {
                i += 256;
                b2 = bArr[i3];
            }
            i = (i + b2) * 256;
        }
        if (bArr[0] >= 0) {
            b = bArr[0];
        } else {
            i += 256;
            b = bArr[0];
        }
        return i + b;
    }

    public static short hBytesToShort(byte[] bArr) {
        int i;
        byte b;
        if (bArr[0] >= 0) {
            i = bArr[0] + 0;
        } else {
            i = 256 + bArr[0];
        }
        int i2 = i * 256;
        if (bArr[1] >= 0) {
            b = bArr[1];
        } else {
            i2 += 256;
            b = bArr[1];
        }
        return (short) (i2 + b);
    }

    public static short lBytesToShort(byte[] bArr) {
        int i;
        byte b;
        if (bArr[1] >= 0) {
            i = bArr[1] + 0;
        } else {
            i = bArr[1] + ap.a;
        }
        int i2 = i * 256;
        if (bArr[0] >= 0) {
            b = bArr[0];
        } else {
            i2 += 256;
            b = bArr[0];
        }
        return (short) (i2 + b);
    }

    public static float hBytesToFloat(byte[] bArr) {
        new Float(0.0d);
        return Float.intBitsToFloat((bArr[3] & 255) | ((((((bArr[0] & 255) << 8) | (bArr[1] & 255)) << 8) | (bArr[2] & 255)) << 8));
    }

    public static float lBytesToFloat(byte[] bArr) {
        new Float(0.0d);
        return Float.intBitsToFloat((bArr[0] & 255) | ((((((bArr[3] & 255) << 8) | (bArr[2] & 255)) << 8) | (bArr[1] & 255)) << 8));
    }

    public static byte[] bytesReverseOrder(byte[] bArr) {
        int length = bArr.length;
        byte[] bArr2 = new byte[length];
        for (int i = 0; i < length; i++) {
            bArr2[(length - i) - 1] = bArr[i];
        }
        return bArr2;
    }

    public static void printBytes(byte[] bArr) {
        int length = bArr.length;
        for (int i = 0; i < length; i++) {
            PrintStream printStream = System.out;
            printStream.print(bArr + " ");
        }
        System.out.println("");
    }

    public static void logBytes(byte[] bArr) {
        String str = "";
        for (int i = 0; i < bArr.length; i++) {
            str = str + bArr + " ";
        }
    }

    public static int reverseInt(int i) {
        return hBytesToInt(toLH(i));
    }

    public static short reverseShort(short s) {
        return hBytesToShort(toLH(s));
    }

    public static float reverseFloat(float f) {
        return hBytesToFloat(toLH(f));
    }
}
