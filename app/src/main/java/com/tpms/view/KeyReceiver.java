package com.tpms.view;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.tpms.utils.Util;

public class KeyReceiver extends BroadcastReceiver {
    private static final String BOOT_COMPLATE = "android.intent.action.BOOT_COMPLETED";
    private final int mScaFb = -1;
    Util Util = null;
    private boolean aux_acc = false;
    private SharedPreferences mPreferences;

    public void onReceive(Context context, Intent intent) {
        intent.getAction();
        this.Util = new Util();
        if (intent.getAction().equals(BOOT_COMPLATE)) {
            System.out.println("======AUX收到开机广播");
            getPreferenceValue(context);
            if (this.aux_acc) {
                System.out.println("=====true 断电时在播放");
                context.getSharedPreferences("aux_pref", mScaFb).edit().putBoolean("LaunchAccOff", true).apply();
                ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1).get(0).topActivity.getPackageName().equals(context.getPackageName());
                savePreferenceValue(context);
                return;
            }
            System.out.println("======断电时AUX没有播放！！！");
        }
    }

    private void getPreferenceValue(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("aux_pref", mScaFb);
        this.mPreferences = sharedPreferences;
        this.aux_acc = sharedPreferences.getBoolean("aux_goplay", false);
    }

    private void savePreferenceValue(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("aux_pref", mScaFb);
        this.mPreferences = sharedPreferences;
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean("aux_goplay", false);
        edit.apply();
    }

    private void delayMs(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
