package com.tpms.encode;

import android.content.Context;

import com.mcal.tmps.TpmsApplication;
import com.tpms.utils.Log;

import java.util.HashMap;

public class FrameEncode {
    private static final HashMap<String, String> activityMap = new HashMap<>();
    private final String TAG = "FrameEncode";
    PackBufferFrameEn FrameEn = null;
    String mOldHintTxt = "";
    Context mctx;
    TpmsApplication theApp;

    public FrameEncode(TpmsApplication tpmsApplication) {
        this.theApp = tpmsApplication;
    }

    public void HeartbeatEventAck() {
    }

    public void SendEncryption(byte b) {
    }

    public void SendHeartbeat() {
    }

    public void exchange_sp_bl() {
    }

    public void exchange_sp_br() {
    }

    public void exchange_sp_fl() {
    }

    public void exchange_sp_fr() {
    }

    public void reset_dev() {
    }

    public void send(String str) {
    }

    public void init(Context context) {
        this.mctx = context;
        this.FrameEn = new PackBufferFrameEn(this.theApp);
    }

    public void send(byte[] bArr) {
        this.FrameEn.send(bArr);
        sleep(60);
    }

    private void sleep(long j) {
        try {
            Thread.sleep(j);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void paireBackLeft() {
        Log.i(this.TAG, "配对右左轮ID");
    }

    public void paireBackRight() {
        Log.i(this.TAG, "配对右后轮ID");
    }

    public void paireFrontLeft() {
        Log.i(this.TAG, "配对前左轮ID");
    }

    public void paireFrontRight() {
        Log.i(this.TAG, "配对前右ID");
    }

    public void paireSpTired() {
        Log.i(this.TAG, "配对备胎ID");
    }

    public void querySensorID() {
        Log.i(this.TAG, "querySensorID");
    }

    public void stopPaire() {
        Log.i(this.TAG, "stopPaire");
    }

    public void exchangeLeftFrontRightFront() {
        Log.i(this.TAG, "exchangeLeftFrontRightFront");
    }

    public void exchangeLeftFrontLeftBack() {
        Log.i(this.TAG, "exchangeLeftFrontLeftBack");
    }

    public void exchangeLeftFrontRightBack() {
        Log.i(this.TAG, "exchangeLeftFrontRightBack");
    }

    public void exchangeRightFrontLeftBack() {
        Log.i(this.TAG, "exchangeRightFrontLeftBack");
    }

    public void exchangeRightFrontRightBack() {
        Log.i(this.TAG, "exchangeRightFrontRightBack");
    }

    public void exchangeLeftBackRightBack() {
        Log.i(this.TAG, "exchangeLeftBackRightBack");
    }
}
