package com.syt.tmps;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.syt.tmps.utils.BitmapUtils;
import com.tpms.view.TpmsMainActivity;

public class TpmsService extends Service {
    private static final String NOTIFI_CLICK_ACTION = "com.syt.tpms.action.NOTIFI_CLICK_ACTION";
    private static final int SERVICE_NOTIFICATION_ID = 112;
    private static final String TAG = "TpmsService";
    Runnable getCurentWindow = new Runnable() {
        public void run() {
            try {
                @SuppressLint("WrongConstant") ComponentName componentName = ((ActivityManager) TpmsService.this.getSystemService("activity")).getRunningTasks(1).get(0).topActivity;
                Log.d("TestService", "pkg:" + componentName.getPackageName());
                Log.d("TestService", "cls:" + componentName.getClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Handler().postDelayed(TpmsService.this.getCurentWindow, 2000);
        }
    };

    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
        ((TpmsApplication) getApplication()).attachService(this);
        startForeground(112, getForegroundNotification());
    }

    @SuppressLint("WrongConstant")
    private Notification getForegroundNotification() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(new NotificationChannel("com.dfz.tpms", "tpms", 4));
        }
        return new NotificationCompat.Builder(this, "com.dfz.tpms")
                .setContentTitle(getString(R.string.zhuangtailantaiya))
                .setContentText(getString(R.string.zhuangtailantaiyazhengchang))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.outline_error_ok_24)
                .setLargeIcon(BitmapUtils.getBitmapFromVectorDrawable(this, R.drawable.outline_error_ok_24))
                .setContentIntent(PendingIntent.getActivity(this, 112, new Intent(this, TpmsMainActivity.class), 0))
                .build();
    }

    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return null;
    }

    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    public void onStart(Intent intent, int i) {
        super.onStart(intent, i);
    }
}
