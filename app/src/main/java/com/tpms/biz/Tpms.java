package com.tpms.biz;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.syt.tmps.ModelManager;
import com.syt.tmps.R;
import com.syt.tmps.TpmsApplication;
import com.syt.tmps.data.Preferences;
import com.syt.tmps.data.UMErrorCode;
import com.syt.tmps.data.UmengConst;
import com.tpms.decode.FrameDecode;
import com.tpms.encode.FrameEncode;
import com.tpms.modle.AlarmAgrs;
import com.tpms.modle.AlarmCntrol;
import com.tpms.modle.PaireIDOkEvent;
import com.tpms.modle.QueryIDOkEvent;
import com.tpms.modle.ShakeHands;
import com.tpms.modle.TiresState;
import com.tpms.modle.TiresStateEvent;
import com.tpms.utils.Log;
import com.tpms.utils.SoundPoolCtrl;
import com.tpms.utils.SoundPoolCtrl2;
import com.tpms.view.TpmsMainActivity;
import com.tpms.widget.CDialog2;
import com.tpms.widget.ClickToast;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class Tpms {
    private static final String BOOT_COMPLATE = "android.intent.action.BOOT_COMPLETED";
    protected SharedPreferences mPreferences;
    protected WaringUI mUI;
    String TAG = "Tpms";
    ModelManager Util = null;
    TpmsApplication app = null;
    AlertDialog dlg;
    Runnable getTpmsState = new Runnable() {
        public void run() {
            new Handler().postDelayed(Tpms.this.getTpmsState, 3000);
        }
    };
    AlarmAgrs mAlarmAgrs = new AlarmAgrs();
    TiresState mBackLeft = new TiresState();
    TiresState mBackRight = new TiresState();
    CDialog2 mErrorDlg;
    ClickToast mErrorToast = null;
    TiresState mFrontLeft = new TiresState();
    TiresState mFrontRight = new TiresState();
    Handler mHeader;
    int mHiPressStamp = 310;
    int mHiTempStamp = 75;
    boolean mIsInit = false;
    boolean mIsPairedId = false;
    boolean mIsSeedAckOk = true;
    int mLowPressStamp = 180;
    int mNotificationState = -1;
    SoundPoolCtrl mSoundPoolCtrl;
    TiresState mSpareTire = new TiresState();
    CDialog2 mTimedlg;
    String mWenduDanwei = "℃";
    String mYaliDanwei = "Bar";
    int mZhuDongBaojin = 1;
    boolean mbForeground = false;
    FrameDecode mdecode = null;
    FrameEncode mencode = null;
    NotificationManager notificationManager;

    public Tpms(TpmsApplication tpmsApplication) {
        this.mPreferences = tpmsApplication.getSharedPreferences("setting", 0);
        initData();
        EventBus.getDefault().register(this);
        this.app = tpmsApplication;
        this.notificationManager = (NotificationManager) tpmsApplication.getSystemService(Context.NOTIFICATION_SERVICE);
        this.mSoundPoolCtrl = new SoundPoolCtrl2(tpmsApplication.getApplicationContext());
    }

    public void HeartbeatEventAck() {
    }

    public void exchangeLeftBackRightBack() {
    }

    public void exchangeLeftFrontLeftBack() {
    }

    public void exchangeLeftFrontRightBack() {
    }

    public void exchangeLeftFrontRightFront() {
    }

    public void exchangeRightFrontLeftBack() {
    }

    public void exchangeRightFrontRightBack() {
    }

    public void exchange_sp_bl() {
    }

    public void exchange_sp_br() {
    }

    public void exchange_sp_fl() {
    }

    public void exchange_sp_fr() {
    }

    public void initShakeHand() {
    }

    public void paireSpTired() {
    }

    public void querySensorID() {
    }

    public int setHiPressDef() {
        return 0;
    }

    public int setHiTempDef() {
        return 0;
    }

    public int setLowPressDef() {
        return 0;
    }

    public void unintShakeHand() {
    }

    private void initData() {
        this.mHiTempStamp = getHiTemp();
        this.mHiPressStamp = getHiPress();
        this.mLowPressStamp = getLowPress();
    }

    public FrameDecode getDecode() {
        return this.mdecode;
    }

    public AlarmAgrs getAlarmAgrs() {
        return this.mAlarmAgrs;
    }

    public void initCodes() {
        FrameEncode frameEncode = new FrameEncode(this.app);
        this.mencode = frameEncode;
        frameEncode.init(this.app);
        FrameDecode frameDecode = new FrameDecode();
        this.mdecode = frameDecode;
        frameDecode.init(this.app);
    }

    public void init() {
        Log.i(this.TAG, "握手");
        if (!this.mIsInit) {
            shakeHand();
            queryConfig();
            this.mIsInit = true;
        }
    }

    public void unInit() {
        if (this.mIsInit) {
            StopSound("");
            closeFloatWindow();
            clearAlarmCntrol();
            EventBus.getDefault().unregister(this);
            this.mIsInit = false;
        }
    }

    private void clearAlarmCntrol() {
        this.mFrontLeft.mAlarmCntrols = new HashMap<>();
        this.mFrontRight.mAlarmCntrols = new HashMap<>();
        this.mBackLeft.mAlarmCntrols = new HashMap<>();
        this.mBackRight.mAlarmCntrols = new HashMap<>();
        this.mSpareTire.mAlarmCntrols = new HashMap<>();
    }

    public void closeFloatWindow() {
        ClickToast clickToast = this.mErrorToast;
        if (clickToast != null) {
            clickToast.hideCustomToast();
            this.mErrorToast = null;
        }
        CDialog2 cDialog2 = this.mTimedlg;
        if (cDialog2 != null) {
            cDialog2.hideCustomToast();
            this.mTimedlg = null;
        }
    }

    public void shakeHand() {
        Log.i(this.TAG, "shakeHand 握手");
        this.mencode.send(new byte[]{-86, -79, -95, 7, 17, 0, 20});
    }

    public void queryVersion() {
        Log.i(this.TAG, "查协议版本号");
        this.mencode.send(new byte[]{-86, -79, -95, 7, -127, 0, -124});
    }

    public void queryConfig() {
        this.mencode.send(new byte[]{-86, -79, -95, 7, 33, 0, 36});
    }

    public void delayMs(int i) {
        try {
            Thread.sleep((long) i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int setWendu(int i) {
        Log.i(this.TAG, "设置最高温度阀值");
        this.mencode.send(new byte[]{-86, -79, -95, 8, 49, 2, (byte) i, -124});
        return i;
    }

    public int getHiTemp() {
        return this.mPreferences.getInt("mHiTempStamp", 75);
    }

    public int addHiTemp() {
        Log.i(this.TAG, "设置最高温度阀值");
        int i = this.mHiTempStamp + 1;
        this.mHiTempStamp = i;
        if (i > 100) {
            this.mHiTempStamp = 100;
        }
        this.mPreferences.edit().putInt("mHiTempStamp", this.mHiTempStamp).apply();
        return this.mHiTempStamp;
    }

    public int decHiTemp() {
        Log.i(this.TAG, "设置最高温度阀值");
        int i = this.mHiTempStamp - 1;
        this.mHiTempStamp = i;
        if (i < 50) {
            this.mHiTempStamp = 50;
        }
        this.mPreferences.edit().putInt("mHiTempStamp", this.mHiTempStamp).apply();
        return this.mHiTempStamp;
    }

    public String getWenduDanwei() {
        return this.mPreferences.getString("mWenduDanwei", "℃");
    }

    public String setNextWenduDanwei() {
        if (this.mWenduDanwei.equals("℃")) {
            this.mWenduDanwei = "℉";
        } else {
            this.mWenduDanwei = "℃";
        }
        this.mPreferences.edit().putString("mWenduDanwei", this.mWenduDanwei).apply();
        return this.mWenduDanwei;
    }

    public int setGaoya(int i) {
        int i2 = i / 10;
        Log.i(this.TAG, "设置最高压力阀值");
        this.mencode.send(new byte[]{-86, -79, -95, 8, 49, 0, (byte) i2, -124});
        return i2 * 10;
    }

    public int setDiya(int i) {
        int i2 = i / 10;
        Log.i(this.TAG, "设置最高压力阀值");
        this.mencode.send(new byte[]{-86, -79, -95, 8, 49, 1, (byte) i2, -124});
        return i2 * 10;
    }

    public String getYaliDanwei() {
        return this.mPreferences.getString("mYaliDanwei", this.mYaliDanwei);
    }

    public String setNextYaliDanwei() {
        if (this.mYaliDanwei.equals("Bar")) {
            this.mYaliDanwei = "Psi";
        } else if (this.mYaliDanwei.equals("Psi")) {
            this.mYaliDanwei = "Kpa";
        } else {
            this.mYaliDanwei = "Bar";
        }
        this.mPreferences.edit().putString("mYaliDanwei", this.mYaliDanwei).apply();
        return this.mYaliDanwei;
    }

    public String setPreYaliDanwei() {
        if (this.mYaliDanwei.equals("Bar")) {
            this.mYaliDanwei = "Kpa";
        } else if (this.mYaliDanwei.equals("Psi")) {
            this.mYaliDanwei = "Bar";
        } else {
            this.mYaliDanwei = "Psi";
        }
        this.mPreferences.edit().putString("mYaliDanwei", this.mYaliDanwei).apply();
        return this.mYaliDanwei;
    }

    public int getZhuDongBaojin() {
        return this.mZhuDongBaojin;
    }

    public void setZhuDongBaojin(int i) {
        this.mZhuDongBaojin = i;
    }

    public void queryBackLeft() {
        Log.i(this.TAG, "查左后轮ID");
        this.mencode.send(new byte[]{-86, -79, -95, 7, 65, 0, -124});
    }

    public void queryBackRight() {
        Log.i(this.TAG, "查右后轮ID");
        this.mencode.send(new byte[]{-86, -79, -95, 7, 65, 3, -124});
    }

    public void queryFrontLeft() {
        Log.i(this.TAG, "查前左轮ID");
        this.mencode.send(new byte[]{-86, -79, -95, 7, 65, 1, -124});
    }

    public void queryFrontRight() {
        Log.i(this.TAG, "查前右ID");
        this.mencode.send(new byte[]{-86, -79, -95, 7, 65, 2, -124});
    }

    public void paireBackLeft() {
        Log.i(this.TAG, "配对左后轮ID");
        this.mencode.send(new byte[]{-86, -79, -95, 7, 97, 0, -124});
    }

    public void paireBackRight() {
        Log.i(this.TAG, "配对右后轮ID");
        this.mencode.send(new byte[]{-86, -79, -95, 7, 97, 3, -124});
    }

    public void paireFrontLeft() {
        Log.i(this.TAG, "配对前左轮ID");
        this.mencode.send(new byte[]{-86, -79, -95, 7, 97, 1, -124});
    }

    public void paireFrontRight() {
        Log.i(this.TAG, "配对前右ID");
        this.mencode.send(new byte[]{-86, -79, -95, 7, 97, 2, -124});
    }

    public void stopPaire() {
        Log.i(this.TAG, "stopPaire");
    }

    public TiresState getFrontLeftState() {
        return this.mFrontLeft;
    }

    public TiresState getFrontRightState() {
        return this.mFrontRight;
    }

    public TiresState getBackLeftState() {
        return this.mBackLeft;
    }

    public TiresState getBackRightState() {
        return this.mBackRight;
    }

    public TiresState getSpareTire() {
        return this.mSpareTire;
    }

    public void onEventMainThread(AlarmAgrs alarmAgrs) {
        this.mAlarmAgrs = alarmAgrs;
    }

    public void onEventMainThread(PaireIDOkEvent paireIDOkEvent) {
        if (paireIDOkEvent.tires == 1) {
            this.mFrontLeft.TiresID = paireIDOkEvent.mID;
        } else if (paireIDOkEvent.tires == 2) {
            this.mFrontRight.TiresID = paireIDOkEvent.mID;
        } else if (paireIDOkEvent.tires == 3) {
            this.mBackRight.TiresID = paireIDOkEvent.mID;
        } else if (paireIDOkEvent.tires == 0) {
            this.mBackLeft.TiresID = paireIDOkEvent.mID;
        } else if (paireIDOkEvent.tires == 5) {
            this.mSpareTire.TiresID = paireIDOkEvent.mID;
        }
    }

    public void onEventMainThread(QueryIDOkEvent queryIDOkEvent) {
        if (queryIDOkEvent.tires == 1) {
            this.mFrontLeft.TiresID = queryIDOkEvent.mID;
        } else if (queryIDOkEvent.tires == 2) {
            this.mFrontRight.TiresID = queryIDOkEvent.mID;
        } else if (queryIDOkEvent.tires == 3) {
            this.mBackRight.TiresID = queryIDOkEvent.mID;
        } else if (queryIDOkEvent.tires == 0) {
            this.mBackLeft.TiresID = queryIDOkEvent.mID;
        } else if (queryIDOkEvent.tires == 5) {
            this.mSpareTire.TiresID = queryIDOkEvent.mID;
        }
    }

    public void onEventMainThread(TiresStateEvent tiresStateEvent) {
        String str;
        if (tiresStateEvent.tires == 1 && this.mFrontLeft != null) {
            tiresStateEvent.mState.TiresID = this.mFrontLeft.TiresID;
        } else if (tiresStateEvent.tires == 2 && this.mFrontRight != null) {
            tiresStateEvent.mState.TiresID = this.mFrontRight.TiresID;
        } else if (tiresStateEvent.tires == 3 && this.mBackRight != null) {
            tiresStateEvent.mState.TiresID = this.mBackRight.TiresID;
        } else if (tiresStateEvent.tires == 0 && this.mBackLeft != null) {
            tiresStateEvent.mState.TiresID = this.mBackLeft.TiresID;
        } else if (tiresStateEvent.tires == 5 && this.mSpareTire != null) {
            tiresStateEvent.mState.TiresID = this.mSpareTire.TiresID;
        }
        if (tiresStateEvent.tires == 1) {
            this.mFrontLeft = tiresStateEvent.mState;
            str = "您的前左轮";
        } else if (tiresStateEvent.tires == 2) {
            this.mFrontRight = tiresStateEvent.mState;
            str = "您的前右轮";
        } else if (tiresStateEvent.tires == 3) {
            this.mBackRight = tiresStateEvent.mState;
            str = "您的后右轮";
        } else if (tiresStateEvent.tires == 0) {
            this.mBackLeft = tiresStateEvent.mState;
            str = "您的后左轮";
        } else if (tiresStateEvent.tires == 5) {
            this.mSpareTire = tiresStateEvent.mState;
            str = "您的备胎";
        } else {
            str = "您的";
        }
        if (!tiresStateEvent.mState.error.isEmpty() && getZhuDongBaojin() != 0) {
            showAlarmDialog((str + "请检查!") + tiresStateEvent.mState.error);
        }
    }

    public void showAlarmDialog(String str) {
        this.dlg = new AlertDialog.Builder(this.app).setTitle("系统提示").setMessage(str).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Tpms.this.dlg.dismiss();
            }
        }).show();
    }

    private void queryAllState() {
        this.mencode.send(new byte[]{-86, -79, -95, 7, 113, 0, 4});
        this.mencode.send(new byte[]{-86, -79, -95, 7, 113, 1, 4});
        this.mencode.send(new byte[]{-86, -79, -95, 7, 113, 2, 4});
        this.mencode.send(new byte[]{-86, -79, -95, 7, 113, 3, 4});
    }

    public void onEventMainThread(ShakeHands shakeHands) {
        if (shakeHands.mShakeHandOK == 0) {
            queryBackLeft();
            queryBackRight();
            queryConfig();
            queryFrontLeft();
            queryFrontRight();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Tpms.this.queryAllState();
                }
            }, 300);
            return;
        }
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Tpms.this.shakeHand();
            }
        }, 3000);
    }

    public boolean getShowUiEnable() {
        return this.mPreferences.getBoolean("ShowUiEnable", true);
    }

    public void setShowUiEnable(boolean z) {
        this.mPreferences.edit().putBoolean("ShowUiEnable", z).apply();
    }

    public boolean getSoundWarringEnable() {
        boolean z = this.mPreferences.getBoolean("SoundWarringEnable", true);
        String str = this.TAG;
        Log.i(str, "getSoundWarringEnable:" + z);
        return z;
    }

    public void setSoundWarringEnable(boolean z) {
        if (!z) {
            StopSound("");
        }
        this.mPreferences.edit().putBoolean("SoundWarringEnable", z).apply();
    }

    public boolean getBettaWarringEnable() {
        return this.mPreferences.getBoolean("BettaWarringEnable", true);
    }

    public void setBettaWarringEnable(boolean z) {
        this.mPreferences.edit().putBoolean("BettaWarringEnable", z).apply();
        if (!z && getSoundGuid().contains("电压过低")) {
            StopSound("");
        }
    }

    public boolean getConnectWarringEnable() {
        return this.mPreferences.getBoolean("ConnectWarringEnable", true);
    }

    public void setConnectWarringEnable(boolean z) {
        this.mPreferences.edit().putBoolean("ConnectWarringEnable", z).apply();
        if (!z && getSoundGuid().contains("连接异常")) {
            StopSound("");
        }
    }

    public boolean getSparetireEnable() {
        return this.mPreferences.getBoolean("SparetireEnable", false);
    }

    public void setSparetireEnable(boolean z) {
        this.mPreferences.edit().putBoolean("SparetireEnable", z).apply();
    }

    public int getHiPress() {
        return this.mPreferences.getInt("mHiPressStamp", 310);
    }

    public int getLowPress() {
        return this.mPreferences.getInt("mLowPressStamp", 180);
    }

    public String getPressString(int i) {
        String yaliDanwei = getYaliDanwei();
        DecimalFormat decimalFormat = new DecimalFormat("######0.00");
        if (yaliDanwei.equals("Kpa")) {
            return Preferences.getKpa() + i + "";
        } else if (yaliDanwei.equals("Bar")) {
            return decimalFormat.format((double) (((float) i) / Preferences.getBar()));
        } else {
            return yaliDanwei.equals("Psi") ? new DecimalFormat("######0.0").format((double) (((float) i) / 6.895f)) : UmengConst.N;
        }
    }

    public String getTempString(int i) {
        String wenduDanwei = getWenduDanwei();
        DecimalFormat decimalFormat = new DecimalFormat("######0.00");
        if (wenduDanwei.equals("℃")) {
            return "" + i;
        }
        Double.isNaN((double) i);
        return decimalFormat.format(((double) i * 1.8d) + 32.0d);
    }

    public int addHiPressStamp() {
        Log.i(this.TAG, "设置最高高压阀值add");
        return this.mHiPressStamp;
    }

    public int decHiPressStamp() {
        Log.i(this.TAG, "设置最高高压阀值dec");
        return this.mHiPressStamp;
    }

    public int addLowPressStamp() {
        Log.i(this.TAG, "设置最低低压度阀值add");
        return this.mLowPressStamp;
    }

    public int decLowPressStamp() {
        Log.i(this.TAG, "设置最低低压度阀值dec");
        return this.mLowPressStamp;
    }

    public void playerSound(String str) {
        if (isDevCheckOk()) {
            this.mSoundPoolCtrl.player(str);
        }
    }

    public void StopSound(String str) {
        this.mSoundPoolCtrl.stop(str);
    }

    public String getSoundGuid() {
        return this.mSoundPoolCtrl.getSoundGuid();
    }

    public void setForeground(boolean z) {
        this.mbForeground = z;
    }

    public boolean isRunningForeground() {
        return this.mbForeground;
    }

    public void resetAll() {
        this.mPreferences.edit().clear().apply();
        clearAlarmCntrol();
        this.mHiTempStamp = 75;
        this.mHiPressStamp = 310;
        this.mLowPressStamp = 180;
        Log.i(this.TAG, "reset tpms");
        this.mencode.reset_dev();
        //Util.Sleep(100L);
        this.mencode.reset_dev();
    }

    public void showNormalNotifMsg() {
        Log.i(this.TAG, "showNormalNotifMsg mNotificationState:" + this.mNotificationState);
        if (this.mNotificationState != 1) {
            try {
                this.notificationManager.cancelAll();
                Log.i(this.TAG, "showNormalNotifMsg cancelAll");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= 26) {
                this.notificationManager.createNotificationChannel(new NotificationChannel("com.dfz.tpms", "tpms", 4));
            }
            Notification build = new NotificationCompat.Builder(this.app, "com.dfz.tpms").setContentTitle(this.app.getString(R.string.zhuangtailantaiya)).setContentText(this.app.getString(R.string.zhuangtailantaiyazhengchang)).setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.ic_notif_ok).setLargeIcon(BitmapFactory.decodeResource(this.app.getResources(), R.drawable.ic_notif_ok)).setContentIntent(PendingIntent.getActivity(this.app, UMErrorCode.E_UM_BE_DEFLATE_FAILED, new Intent(this.app, TpmsMainActivity.class), 0)).build();
            build.flags |= 2;
            try {
                this.app.getTpmsServices().startForeground(UMErrorCode.E_UM_BE_DEFLATE_FAILED, build);
            } catch (Exception unused) {
                this.notificationManager.notify(UMErrorCode.E_UM_BE_DEFLATE_FAILED, build);
            }
            this.mNotificationState = 1;
            Log.i(this.TAG, "lyc cur showNormalNotifMsg");
        }
    }

    public void showErrorNotifMsg() {
        String str = this.TAG;
        Log.i(str, "showErrorNotifMsg mNotificationState:" + this.mNotificationState);
        if (!isDevCheckOk()) {
            Log.i(this.TAG, "showErrorNotifMsg !isDevCheckOk()");
        } else {
            showErrorNotifMsg2();
        }
    }

    public void showErrorNotifMsg2() {
        if (this.mNotificationState != 0) {
            try {
                this.notificationManager.cancelAll();
                Log.i(this.TAG, "showErrorNotifMsg2 cancelAll");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= 26) {
                this.notificationManager.createNotificationChannel(new NotificationChannel("com.dfz.tpms", "tpms", 4));
            }
            Notification build = new NotificationCompat.Builder(this.app, "com.dfz.tpms").setContentTitle(this.app.getString(R.string.zhuangtailantaiya)).setContentText(this.app.getString(R.string.ztltaiyayichang)).setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.ic_notif_error).setLargeIcon(BitmapFactory.decodeResource(this.app.getResources(), R.drawable.ic_notif_ok)).setContentIntent(PendingIntent.getActivity(this.app, UMErrorCode.E_UM_BE_DEFLATE_FAILED, new Intent(this.app, TpmsMainActivity.class), 0)).build();
            build.flags |= 2;
            try {
                this.app.getTpmsServices().startForeground(UMErrorCode.E_UM_BE_DEFLATE_FAILED, build);
            } catch (Exception e2) {
                e2.printStackTrace();
                this.notificationManager.notify(UMErrorCode.E_UM_BE_DEFLATE_FAILED, build);
            }
            Log.i(this.TAG, "lyc cur showErrorNotifMsg2");
            this.mNotificationState = 0;
        }
    }

    public boolean isAllOk() {
        return isokTires(this.mFrontLeft.mAlarmCntrols) && isokTires(this.mFrontRight.mAlarmCntrols) && isokTires(this.mBackLeft.mAlarmCntrols) && isokTires(this.mBackRight.mAlarmCntrols) && isokTires(this.mSpareTire.mAlarmCntrols);
    }

    private boolean isokTires(Map<String, AlarmCntrol> map) {
        for (AlarmCntrol alarmCntrol : map.values()) {
            if (!TextUtils.isEmpty(alarmCntrol.mError)) {
                return false;
            }
            String str = this.TAG;
            Log.i(str, "isokTires ? ac.mError:" + alarmCntrol.mError);
        }
        return true;
    }

    public boolean isDevCheckOk() {
        String str = this.TAG;
        Log.i(str, "isDevCheckOk:" + this.mIsSeedAckOk);
        return this.mIsSeedAckOk;
    }
}
