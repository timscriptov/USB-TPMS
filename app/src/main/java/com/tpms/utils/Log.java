package com.tpms.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Log {
    private static final char DEBUG = 'd';
    private static final char ERROR = 'e';
    private static final char INFO = 'i';
    private static final String TAG = "LogToFile";
    private static final char VERBOSE = 'v';
    private static final char WARN = 'w';
    private static final Date date = new Date();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
    public static boolean enableLog = true;
    public static String fileName = "";
    public static boolean mLogFileEnable = false;
    static BufferedWriter bw = null;
    static FileOutputStream fos = null;
    private static String logPath;

    public static void init(Context context) {
        logPath = getFilePath(context) + "/Logs";
        createLogFile();
    }

    private static String getFilePath(Context context) {
        Environment.isExternalStorageEmulated();
        if (Environment.isExternalStorageRemovable()) {
            return context.getExternalFilesDir(null).getPath();
        }
        return context.getExternalFilesDir("").getPath();
    }

    public static void setLogToFile(boolean z) {
        mLogFileEnable = z;
    }

    public static void v(String str, String str2) {
        if (enableLog) {
            android.util.Log.v(str, str2);
            writeToFile(VERBOSE, str, str2);
        }
    }

    public static void d(String str, String str2) {
        if (enableLog) {
            android.util.Log.d(str, str2);
            writeToFile(DEBUG, str, str2);
        }
    }

    public static void i(String str, String str2) {
        if (enableLog) {
            android.util.Log.i(str, str2);
            writeToFile(INFO, str, str2);
        }
    }

    public static void w(String str, String str2) {
        if (enableLog) {
            android.util.Log.w(str, str2);
            writeToFile(WARN, str, str2);
        }
    }

    public static void e(String str, String str2) {
        android.util.Log.e(str, str2);
        writeToFile(ERROR, str, str2);
    }

    private static synchronized void writeToFile(char c, String str, String str2) {
        synchronized (Log.class) {
            if (logPath == null) {
                android.util.Log.e(TAG, "logPath == null ，未初始化LogToFile");
            } else if (mLogFileEnable) {
                new DirSizeLimitUtil(logPath, 2097152.0d).sizeProc();
                String str3 = dateFormat.format(date) + " " + c + " " + str + " " + str2 + "\n";
                File file = new File(logPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File file2 = new File(fileName);
                if (bw == null || file2.length() > 102400) {
                    if (bw != null) {
                        try {
                            bw.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        bw = null;
                    }
                    createLogFile();
                }
                try {
                    bw.write(str3);
                    bw.flush();
                } catch (Exception e2) {
                    e2.printStackTrace();
                    try {
                        if (bw != null) {
                            bw.close();
                            bw = null;
                        }
                    } catch (IOException unused) {
                        e2.printStackTrace();
                    }
                }
            }
        }
    }

    private static void createLogFile() {
        File file = new File(logPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        fileName = logPath + "/log_" + dateFormat.format(new Date()) + ".log";
        try {
            fos = new FileOutputStream(fileName, true);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
