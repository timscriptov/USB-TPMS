package com.tpms.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    private static final CrashHandler INSTANCE = new CrashHandler();
    private static Thread s_td = null;
    @SuppressLint("SimpleDateFormat")
    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private final Map<String, String> infos = new HashMap<>();
    MailSenderInfo mailInfo = null;
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public static void logFileProcSync(String str, String str2, Context context) {
        if (str2 != null) {
            boolean z = true;
            try {
                Log.i(TAG, "logFileProc===" + str2);
            } catch (Exception e) {
                e.printStackTrace();
                z = false;
            }
            if (z) {
                new File(str2).delete();
            }
        }
        new DirSizeLimitUtil(context.getExternalFilesDir("").getPath(), 2097152.0d).sizeProc();
    }

    public static void logFileProc(final String str, final String str2, final Context context) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                CrashHandler.logFileProcSync(str, str2, context);
            }
        });
        s_td = thread;
        thread.start();
    }

    public static void _sendLogToMail(String str) {
        new Thread(new Runnable() {
            public void run() {
            }
        }).start();
    }

    public void initDir() {
    }

    public void sendLogToMail(String str) {
    }

    public void init(Context context) {
        this.mContext = context;
        this.mailInfo = new MailSenderInfo();
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void uncaughtException(Thread thread, Throwable th) {
        if (handleException(th) || this.mDefaultHandler == null) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            Log.e(TAG, "error uncaughtException 1");
            Process.killProcess(Process.myPid());
            System.exit(1);
            return;
        }
        Log.e(TAG, "error uncaughtException 0");
        this.mDefaultHandler.uncaughtException(thread, th);
    }

    private boolean handleException(Throwable th) {
        if (th == null) {
            return false;
        }
        new Thread() {
            public void run() {
                Looper.prepare();
                Toast.makeText(CrashHandler.this.mContext, "很抱歉,程序出现异常,即将退出.", 1).show();
                Looper.loop();
            }
        }.start();
        collectDeviceInfo(this.mContext);
        String saveCrashInfo2File = saveCrashInfo2File(th);
        Log.i(TAG, "log file name:" + saveCrashInfo2File);
        return true;
    }

    public void collectDeviceInfo(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 1);
            if (packageInfo != null) {
                String str = packageInfo.versionName == null ? "null" : packageInfo.versionName;
                this.infos.put("packageName", context.getPackageName());
                this.infos.put("versionName", str);
                this.infos.put("versionCode", packageInfo.versionCode + "");
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] declaredFields = Build.class.getDeclaredFields();
        for (Field field : declaredFields) {
            try {
                field.setAccessible(true);
                this.infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e2) {
                Log.e(TAG, "an error occured when collect crash info", e2);
            }
        }
    }

    private String saveCrashInfo2File(Throwable th) {
        StringBuilder stringBuffer = new StringBuilder();
        for (Map.Entry<String, String> entry : this.infos.entrySet()) {
            stringBuffer.append(entry.getKey() + "=" + entry.getValue() + "\n");
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        th.printStackTrace(printWriter);
        for (Throwable cause = th.getCause(); cause != null; cause = cause.getCause()) {
            cause.printStackTrace(printWriter);
        }
        printWriter.close();
        stringBuffer.append(stringWriter.toString());
        Log.i(TAG, "write file in");
        Log.e(TAG, stringBuffer.toString());
        try {
            long currentTimeMillis = System.currentTimeMillis();
            String str = "crash-" + this.formatter.format(new Date()) + "-" + currentTimeMillis + ".log";
            String path = this.mContext.getExternalFilesDir("").getPath();
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(path + str);
            fileOutputStream.write(stringBuffer.toString().getBytes());
            fileOutputStream.close();
            return str;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
            new DirSizeLimitUtil(this.mContext.getExternalFilesDir("").getPath(), 2097152.0d).sizeProc();
            return null;
        }
    }

    public static class MailSenderInfo {
        private final String content = "请解决附件bug";
        private final String fromAddress = "chang19test@126.com";
        private final String mailServerHost = "smtp.126.com";
        private final String mailServerPort = "25";
        private final String password = "19ufoufo19";
        private final String subject = "HCF-BUG";
        private final String toAddress = "chang19test@126.com";
        private final String userName = "chang19test@126.com";
        private final boolean validate = true;
        private String[] attachFileNames;

        public MailSenderInfo() {
        }
    }
}
