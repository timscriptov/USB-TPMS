package com.syt.tmps;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.tpms.view.TpmsMainActivity;

public class TpmsService extends Service {
    private static final String NOTIFI_CLICK_ACTION = "com.syt.tpms.action.NOTIFI_CLICK_ACTION";
    private static final int SERVICE_NOTIFICATION_ID = 112;
    private static final String TAG = "TpmsService";
    Runnable getCurentWindow = new Runnable() {
        public void run() {
            try {
                ComponentName componentName = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
                        .getRunningTasks(1)
                        .get(0).topActivity;
                Log.d("TestService", "pkg:" + componentName.getPackageName());
                Log.d("TestService", "cls:" + componentName.getClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Handler().postDelayed(TpmsService.this.getCurentWindow, 2000);
        }
    };

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
        ((TpmsApplication) getApplication()).attachService(this);
        startForeground(SERVICE_NOTIFICATION_ID, getForegroundNotification());
    }

    private Notification getForegroundNotification() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(new NotificationChannel("com.dfz.tpms", "tpms", NotificationManager.IMPORTANCE_HIGH));
        }
        return new NotificationCompat.Builder(this, "com.dfz.tpms")
                .setContentTitle(getString(R.string.zhuangtailantaiya)).setContentText(getString(R.string.zhuangtailantaiyazhengchang))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notif_ok)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notif_ok))
                .setContentIntent(PendingIntent.getActivity(this, SERVICE_NOTIFICATION_ID, new Intent(this, TpmsMainActivity.class), 0))
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int i) {
        super.onStart(intent, i);
    }
}
