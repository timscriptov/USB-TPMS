package com.tpms.utils;

import com.umeng.commonsdk.proguard.ap;

import java.io.ByteArrayOutputStream;

public class Util {
    private static final String hexString = "0123456789ABCDEF";

    public static String str2HexStr(String str) {
        char[] charArray = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bytes = str.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(charArray[(bytes[i] & 240) >> 4]);
            sb.append(charArray[bytes[i] & ap.m]);
        }
        return sb.toString();
    }

    public static byte[] hexStringToByte(String str) {
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        char[] charArray = str.toCharArray();
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) (toByte(charArray[i2 + 1]) | (toByte(charArray[i2]) << 4));
        }
        return bArr;
    }

    private static int toByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String bytesToHexString(byte[] bArr) {
        StringBuilder stringBuffer = new StringBuilder(bArr.length);
        for (byte b : bArr) {
            String hexString2 = Integer.toHexString(b & 255);
            if (hexString2.length() < 2) {
                stringBuffer.append(0);
            }
            stringBuffer.append(hexString2.toUpperCase());
        }
        return stringBuffer.toString();
    }

    public static String byteToUpperString(byte b) {
        return Integer.toHexString(b & 255).toUpperCase();
    }

    public static String toHexString1(byte[] bArr) {
        StringBuilder stringBuffer = new StringBuilder();
        for (byte b : bArr) {
            stringBuffer.append(toHexString1(b));
        }
        return stringBuffer.toString();
    }

    public static String toHexString1(byte b) {
        String hexString2 = Integer.toHexString(b & 255);
        if (hexString2.length() != 1) {
            return hexString2;
        }
        return "0" + hexString2;
    }

    public static String hexStr2Str(String str) {
        char[] charArray = str.toCharArray();
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) ((("0123456789ABCDEF".indexOf(charArray[i2]) * 16) + "0123456789ABCDEF".indexOf(charArray[i2 + 1])) & 255);
        }
        return new String(bArr);
    }

    public static String toStringHex(String str) {
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            try {
                bArr[i] = (byte) (Integer.parseInt(str.substring(i2, i2 + 2), 16) & 255);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            return new String(bArr, "utf-8");
        } catch (Exception e2) {
            e2.printStackTrace();
            return str;
        }
    }

    public static String encode(String str) {
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 240) >> 4));
            sb.append(hexString.charAt((bytes[i] & ap.m) >> 0));
        }
        return sb.toString();
    }

    public static String decode(String str) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(str.length() / 2);
        for (int i = 0; i < str.length(); i += 2) {
            byteArrayOutputStream.write((hexString.indexOf(str.charAt(i)) << 4) | hexString.indexOf(str.charAt(i + 1)));
        }
        return new String(byteArrayOutputStream.toByteArray());
    }

    public static byte uniteBytes(byte b, byte b2) {
        return (byte) (((byte) (Byte.decode("0x" + new String(new byte[]{b})).byteValue() << 4)) ^ Byte.decode("0x" + new String(new byte[]{b2})).byteValue());
    }

    public static byte[] HexString2Bytes(String str) {
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        byte[] bytes = str.getBytes();
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = uniteBytes(bytes[i2], bytes[i2 + 1]);
        }
        return bArr;
    }

    public static byte[] hexStringToBytes(String str) {
        if (str == null || str.equals("")) {
            return null;
        }
        int length = str.length() / 2;
        char[] charArray = str.toCharArray();
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) (charToByte(charArray[i2 + 1]) | (charToByte(charArray[i2]) << 4));
        }
        return bArr;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String hexstringTo32(String str) {
        switch (8 - str.length()) {
            case 1:
                return "0" + str;
            case 2:
                return "00" + str;
            case 3:
                return "000" + str;
            case 4:
                return "0000" + str;
            case 5:
                return "00000" + str;
            case 6:
                return "000000" + str;
            case 7:
                return "00000000" + str;
            default:
                return str;
        }
    }
}
