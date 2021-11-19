package com.tpms.utils;

public class SLOG {
    private static final boolean DEBUG = true;
    private static final String TAG = "SLOG";
    private static final boolean WARNING = true;

    public static void d(String str) {
        Log.d(TAG, str);
    }

    public static void d(String str, String str2) {
        Log.d(str, str2);
    }

    public static void w(String str) {
        Log.w(TAG, str);
    }

    public static void e(String str) {
        Log.e(TAG, str);
    }

    public static void e(String str, String str2) {
        Log.e(str, str2);
    }

    public static void e(String str, Exception exc, String str2) {
        StackTraceElement[] stackTrace = exc.getStackTrace();
        if (!(stackTrace == null || stackTrace.length == 0)) {
            for (int i = 0; i < stackTrace.length; i++) {
                if (stackTrace[i].getFileName() != null && stackTrace[i].getFileName().equals(str2)) {
                    Log.e(TAG, stackTrace[i].getFileName() + ":" + stackTrace[i].getLineNumber());
                }
            }
        }
        Log.e(TAG, str);
    }

    public static void i(String str) {
        Log.i(TAG, str);
    }

    public static void i(String str, String str2) {
        Log.i(str, str2);
    }

    public static String byteToHexString(byte b) {
        StringBuilder sb = new StringBuilder("");
        String hexString = Integer.toHexString(b & 255);
        if (hexString.length() < 2) {
            sb.append(0);
        }
        sb.append(hexString);
        return sb.toString();
    }

    public static String LogByteArr(String str, byte[] bArr, int i) {
        StringBuilder stringBuffer = new StringBuilder();
        String str2 = null;
        for (int i2 = 0; i2 < i; i2++) {
            try {
                str2 = byteToHexString(bArr[i2]);
                stringBuffer.append(" " + str2);
            } catch (Exception unused) {
                return str2;
            }
        }
        String stringBuffer2 = stringBuffer.toString();
        Log.i(str, "len:" + i + ";data:" + stringBuffer2);
        return stringBuffer2;
    }
}
