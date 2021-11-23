package com.mcal.tmps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BKReceiver extends BroadcastReceiver {
    private static final String BOOT_COMPLATE = "android.intent.action.BOOT_COMPLETED";
    private static final String MSG_SYS_STD_TPMS_DATA_TEST_RECV = "MSG_SYS_STD_TPMS_DATA_TEST_RECV";
    private static final String TAG = "BKReceiver";
    ModelManager Util = null;
    TpmsApplication app = null;
    private SharedPreferences mPreferences;

    private static byte[] hexStringToBytes(String str) {
        if (str == null || str.equals("")) {
            Log.e(TAG, "hexString == null || hexString.equals()");
            return null;
        }
        String upperCase = str.replace("0x", "").replace("+", "").replace(" ", "").toUpperCase();
        int length = upperCase.length() / 2;
        char[] charArray = upperCase.toCharArray();
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

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        this.Util = new ModelManager();
        Log.i(TAG, "onReceive act:" + action);
        if (this.app == null) {
            this.app = (TpmsApplication) context.getApplicationContext();
        }
        if (intent.getAction().equals(BOOT_COMPLATE) || intent.getAction().equals("android.intent.action.USER_PRESENT") || intent.getAction().equals("android.intent.action.SCREEN_ON")) {
            this.app.startTpms();
        } else if (intent.getAction().equals(MSG_SYS_STD_TPMS_DATA_TEST_RECV)) {
            Log.i(TAG, "接受广播MSG_SYS_STD_CANBUS_DATA_TEST_RECV");
            this.app.datasrc.testAddBuf(hexStringToBytes(intent.getStringExtra("data")));
        } else if (!intent.getAction().equals("android.intent.action.ACC_EVENT")) {
        } else {
            if (intent.getIntExtra("android.intent.extra.ACC_STATE", -1) == 0) {
                this.app.stopTpms();
            } else {
                this.app.startTpms();
            }
        }
    }

    private void delayMs(int i) {
        try {
            Thread.sleep((long) i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
