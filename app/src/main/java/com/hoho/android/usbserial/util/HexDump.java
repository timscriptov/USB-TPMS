package com.hoho.android.usbserial.util;

import com.syt.tmps.data.UmengConst;

public class HexDump {
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static byte[] toByteArray(byte b) {
        return new byte[]{b};
    }

    public static byte[] toByteArray(int i) {
        byte[] bArr = new byte[4];
        bArr[3] = (byte) (i & 255);
        bArr[2] = (byte) ((i >> 8) & 255);
        bArr[1] = (byte) ((i >> 16) & 255);
        bArr[0] = (byte) ((i >> 24) & 255);
        return bArr;
    }

    public static byte[] toByteArray(short s) {
        byte[] bArr = new byte[2];
        bArr[1] = (byte) (s & 255);
        bArr[0] = (byte) ((s >> 8) & 255);
        return bArr;
    }

    public static String dumpHexString(byte[] bArr) {
        return dumpHexString(bArr, 0, bArr.length);
    }

    public static String dumpHexString(byte[] bArr, int i, int i2) {
        StringBuilder sb = new StringBuilder();
        byte[] bArr2 = new byte[16];
        sb.append("\n0x");
        sb.append(toHexString(i));
        int i3 = i;
        int i4 = 0;
        while (i3 < i + i2) {
            if (i4 == 16) {
                sb.append(" ");
                for (int i5 = 0; i5 < 16; i5++) {
                    if (bArr2[i5] <= 32 || bArr2[i5] >= 126) {
                        sb.append(".");
                    } else {
                        sb.append(new String(bArr2, i5, 1));
                    }
                }
                sb.append("\n0x");
                sb.append(toHexString(i3));
                i4 = 0;
            }
            byte b = bArr[i3];
            sb.append(" ");
            sb.append(HEX_DIGITS[(b >>> 4) & 15]);
            sb.append(HEX_DIGITS[b & UmengConst.m]);
            bArr2[i4] = b;
            i3++;
            i4++;
        }
        if (i4 != 16) {
            int i6 = ((16 - i4) * 3) + 1;
            for (int i7 = 0; i7 < i6; i7++) {
                sb.append(" ");
            }
            for (int i8 = 0; i8 < i4; i8++) {
                if (bArr2[i8] <= 32 || bArr2[i8] >= 126) {
                    sb.append(".");
                } else {
                    sb.append(new String(bArr2, i8, 1));
                }
            }
        }
        return sb.toString();
    }

    public static String toHexString(byte b) {
        return toHexString(toByteArray(b));
    }

    public static String toHexString(byte[] bArr) {
        return toHexString(bArr, 0, bArr.length);
    }

    public static String toHexString(byte[] bArr, int i, int i2) {
        char[] cArr = new char[(i2 * 2)];
        int i3 = 0;
        for (int i4 = i; i4 < i + i2; i4++) {
            byte b = bArr[i4];
            int i5 = i3 + 1;
            char[] cArr2 = HEX_DIGITS;
            cArr[i3] = cArr2[(b >>> 4) & 15];
            i3 = i5 + 1;
            cArr[i5] = cArr2[b & UmengConst.m];
        }
        return new String(cArr);
    }

    public static String toHexString(int i) {
        return toHexString(toByteArray(i));
    }

    public static String toHexString(short s) {
        return toHexString(toByteArray(s));
    }

    private static int toByte(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        char c2 = 'A';
        if (c < 'A' || c > 'F') {
            c2 = 'a';
            if (c < 'a' || c > 'f') {
                throw new RuntimeException("Invalid hex char '" + c + "'");
            }
        }
        return (c - c2) + 10;
    }

    public static byte[] hexStringToByteArray(String str) {
        int length = str.length();
        byte[] bArr = new byte[(length / 2)];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((toByte(str.charAt(i)) << 4) | toByte(str.charAt(i + 1)));
        }
        return bArr;
    }
}
