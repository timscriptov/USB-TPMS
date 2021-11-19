package com.tpms.encode;

import android.content.Context;

import com.syt.tmps.TpmsApplication;
import com.tpms.utils.Log;
import com.umeng.commonsdk.proguard.ap;

import java.util.HashMap;

public class FrameEncode3 extends FrameEncode {
    private static final HashMap<String, String> activityMap = new HashMap<>();
    private final String TAG = "FrameEncode3";
    PackBufferFrameEn3 FrameEn = null;
    String mOldHintTxt = "";

    public FrameEncode3(TpmsApplication tpmsApplication) {
        super(tpmsApplication);
    }

    @Override
    public void send(String str) {
    }

    @Override
    public void init(Context context) {
        super.init(context);
        this.FrameEn = new PackBufferFrameEn3(this.theApp);
    }

    @Override
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

    @Override
    public void HeartbeatEventAck() {
        this.FrameEn.send(new byte[]{85, -86, 6, 0, 0, 0});
    }

    @Override
    public void SendHeartbeat() {
        this.FrameEn.send(new byte[]{85, -86, 6, 25, 0, -32});
    }

    @Override
    public void SendEncryption(byte b) {
        this.FrameEn.send(new byte[]{85, -86, 6, 91, b, -32});
    }

    @Override
    public void paireBackLeft() {
        Log.i(this.TAG, "配对后左轮ID");
        this.FrameEn.send(new byte[]{85, -86, 6, 1, ap.n, 0});
    }

    @Override
    public void paireBackRight() {
        Log.i(this.TAG, "配对右后轮ID");
        this.FrameEn.send(new byte[]{85, -86, 6, 1, 17, 0});
    }

    @Override
    public void paireFrontLeft() {
        Log.i(this.TAG, "配对前左轮ID");
        this.FrameEn.send(new byte[]{85, -86, 6, 1, 0, 0});
    }

    @Override
    public void paireFrontRight() {
        Log.i(this.TAG, "配对前右ID");
        this.FrameEn.send(new byte[]{85, -86, 6, 1, 1, 0});
    }

    @Override
    public void paireSpTired() {
        Log.i(this.TAG, "配对备胎ID");
        this.FrameEn.send(new byte[]{85, -86, 6, 1, 5, 0});
    }

    @Override
    public void querySensorID() {
        Log.i(this.TAG, "querySensorID");
        this.FrameEn.send(new byte[]{85, -86, 6, 7, 0, 0});
    }

    @Override
    public void stopPaire() {
        Log.i(this.TAG, "stopPaire");
        this.FrameEn.send(new byte[]{85, -86, 6, 6, 0, 0});
    }

    @Override
    public void exchangeLeftFrontRightFront() {
        Log.i(this.TAG, "exchangeLeftFrontRightFront");
        this.FrameEn.send(new byte[]{85, -86, 7, 3, 0, 1, 0});
    }

    @Override
    public void exchangeLeftFrontLeftBack() {
        Log.i(this.TAG, "exchangeLeftFrontLeftBack");
        this.FrameEn.send(new byte[]{85, -86, 7, 3, 0, ap.n, 0});
    }

    @Override
    public void exchangeLeftFrontRightBack() {
        Log.i(this.TAG, "exchangeLeftFrontRightBack");
        this.FrameEn.send(new byte[]{85, -86, 7, 3, 0, 17, 0});
    }

    @Override
    public void exchangeRightFrontLeftBack() {
        Log.i(this.TAG, "exchangeRightFrontLeftBack");
        this.FrameEn.send(new byte[]{85, -86, 7, 3, 1, ap.n, 0});
    }

    @Override
    public void exchangeRightFrontRightBack() {
        Log.i(this.TAG, "exchangeRightFrontRightBack");
        this.FrameEn.send(new byte[]{85, -86, 7, 3, 1, 17, 0});
    }

    @Override
    public void exchangeLeftBackRightBack() {
        Log.i(this.TAG, "exchangeLeftBackRightBack");
        this.FrameEn.send(new byte[]{85, -86, 7, 3, ap.n, 17, 0});
    }

    @Override
    public void exchange_sp_fl() {
        this.FrameEn.send(new byte[]{85, -86, 7, 3, 0, 5, 0});
    }

    @Override
    public void exchange_sp_fr() {
        this.FrameEn.send(new byte[]{85, -86, 7, 3, 1, 5, 0});
    }

    @Override
    public void exchange_sp_bl() {
        this.FrameEn.send(new byte[]{85, -86, 7, 3, ap.n, 5, 0});
    }

    @Override
    public void exchange_sp_br() {
        this.FrameEn.send(new byte[]{85, -86, 7, 3, 17, 5, 0});
    }

    @Override
    public void reset_dev() {
        this.FrameEn.send(new byte[]{85, -86, 6, 88, 85, -32});
    }
}
