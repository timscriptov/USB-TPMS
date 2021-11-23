package com.mcal.tmps;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;

import com.std.dev.TpmsDataSrc;
import com.std.dev.TpmsDataSrcUsb;
import com.tpms.biz.Tpms;
import com.tpms.biz.Tpms3;
import com.tpms.utils.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TpmsApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public String TAG = TpmsApplication.class.getSimpleName();
    TpmsDataSrc datasrc = null;
    BKReceiver mReceive = null;
    private Service mAppService;
    private Tpms tpms;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
                UsbDevice usbDevice = intent.getParcelableExtra("device");
                if (usbDevice != null) {
                    String deviceName = usbDevice.getDeviceName();
                    int deviceId = usbDevice.getDeviceId();
                    Log.i(TAG, "==================================name:" + deviceName + ";did:" + deviceId);
                    if (TpmsApplication.this.datasrc == null) {
                        Log.i(TAG, "datasrc==null");
                    } else if (deviceName.equals(datasrc.getDevName())) {
                        Log.i(TAG, "kill safe");
                        Process.myPid();
                        TpmsApplication.this.stopTpms();
                    }
                }
            } else if (action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                Log.e(TAG, " ACTION_USB_ACCESSORY_ATTACHED usb 插入");
                startTpms();
            }
        }
    };

    public TpmsApplication() {
        Log.i(TAG, "BTApplication tid:" + Thread.currentThread().getId());
    }

    public static Context getContext() {
        if (context == null) {
            context = new TpmsApplication();
        }
        return context;
    }

    private static String getProcessName(int i) {
        Throwable th;
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/" + i + "/cmdline"));
            try {
                String readLine = bufferedReader.readLine();
                if (!TextUtils.isEmpty(readLine)) {
                    readLine = readLine.trim();
                }
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return readLine;
            } catch (Throwable th2) {
                th = th2;
                try {
                    th.printStackTrace();
                    return null;
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }
        } catch (Throwable th3) {
            th = th3;
            bufferedReader = null;
            th.printStackTrace();
            return null;
        }
    }

    public void attachService(Service service) {
        this.mAppService = service;
    }

    public Service getTpmsServices() {
        return this.mAppService;
    }

    public TpmsDataSrc getDataSrc() {
        return this.datasrc;
    }

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        if (!isMainPid()) {
            android.util.Log.i(this.TAG, "onCreate is two process");
            return;
        }
        android.util.Log.i(this.TAG, "onCreate is one process");
        Log.init(this);
        Log.setLogToFile(false);
        String str = this.TAG;
        Log.i(str, "App is onCreate tid:" + Thread.currentThread().getId());
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(new Intent(this, TpmsService.class));
        } else {
            startService(new Intent(this, TpmsService.class));
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        registerReceiver(this.mReceiver, intentFilter);
        startTpms();
    }

    private boolean isMainPid() {
        String packageName = getPackageName();
        String processName = getProcessName(Process.myPid());
        return processName == null || processName.equals(packageName);
    }

    public void onTerminate() {
        super.onTerminate();
        String str = this.TAG;
        Log.i(str, "App is onTerminate tid:" + Thread.currentThread().getId());
    }

    public Tpms getTpms() {
        return this.tpms;
    }

    public void startTpms() {
        Log.i(this.TAG, "startTpms");
        if (this.datasrc == null) {
            TpmsDataSrcUsb tpmsDataSrcUsb = new TpmsDataSrcUsb(this);
            this.datasrc = tpmsDataSrcUsb;
            tpmsDataSrcUsb.init();
            Tpms3 tpms3 = new Tpms3(this);
            this.tpms = tpms3;
            tpms3.initCodes();
            this.tpms.init();
            this.datasrc.setBufferFrame(this.tpms.getDecode().getPackBufferFrame());
        }
        this.datasrc.start();
        this.tpms.initShakeHand();
    }

    public void stopTpms() {
        Log.i(this.TAG, "stopTpms");
        TpmsDataSrc tpmsDataSrc = this.datasrc;
        if (tpmsDataSrc != null) {
            tpmsDataSrc.stop();
        }
        Tpms tpms2 = this.tpms;
        if (tpms2 != null) {
            tpms2.unintShakeHand();
        }
    }
}
