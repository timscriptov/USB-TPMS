package com.tpms.biz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.syt.tmps.R;
import com.syt.tmps.TpmsApplication;
import com.tpms.decode.FrameDecode3;
import com.tpms.encode.FrameEncode3;
import com.tpms.modle.AlarmAgrs;
import com.tpms.modle.AlarmCntrol;
import com.tpms.modle.HeartbeatEvent;
import com.tpms.modle.TimeSeedEvent;
import com.tpms.modle.TiresState;
import com.tpms.modle.TiresStateEvent;
import com.tpms.modle.TpmsDevErrorEvent;
import com.tpms.utils.Log;
import com.tpms.view.TpmsMainActivity;
import com.tpms.widget.CDialog2;
import com.tpms.widget.ClickToast;

import de.greenrobot.event.EventBus;

public class Tpms3 extends Tpms {
    static final int MaxHiPress = 800;
    static final int MaxLowPress = 790;
    static final int MinHiPress = 10;
    static final int MinLowPress = 0;
    private static final String BOOT_COMPLATE = "android.intent.action.BOOT_COMPLETED";
    private static final BroadcastReceiver homeListenerReceiver = new BroadcastReceiver() {
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        final String SYSTEM_DIALOG_REASON_KEY = "reason";

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {
                String stringExtra = intent.getStringExtra("reason");
                Log.i("test", "reason..:" + stringExtra);
                if (stringExtra != null) {
                    stringExtra.equals("homekey");
                }
            }
        }
    };
    Runnable CheckEncryptionTime;
    String TAG;
    View.OnClickListener btn_click;
    CDialog2 mConnectErrorDlg;
    AlarmCntrol mCurrentErrCtrl;
    Handler mDataCheckHander;
    Runnable mDataCheckTimer;
    int mErrorCount;
    Runnable mHeartbeat;
    Handler mTimerCheckSeed;
    int mZhuDongBaojin;
    long startDataTime;
    byte time;

    public Tpms3(TpmsApplication tpmsApplication) {
        super(tpmsApplication);
        this.TAG = "Tpms3";
        this.mCurrentErrCtrl = null;
        this.mZhuDongBaojin = 1;
        this.time = 0;
        this.mErrorCount = 0;
        this.mTimerCheckSeed = null;
        this.startDataTime = -1;
        this.mConnectErrorDlg = null;
        this.mDataCheckTimer = new Runnable() {
            public void run() {
                long currentTimeMillis = (System.currentTimeMillis() - startDataTime) / 1000;
                Log.i(TAG, "mDataCheckTimer startDataTime:" + startDataTime + ";datTime:" + currentTimeMillis);
                if (startDataTime == -1 || currentTimeMillis <= 120) {
                    mDataCheckHander.postDelayed(mDataCheckTimer, 3000);
                    return;
                }
                showConnectErrDlg();
                showErrorNotifMsg();
                startDataTime = -1;
                mDataCheckHander.postDelayed(mDataCheckTimer, 3000);
            }
        };
        this.btn_click = new View.OnClickListener() {
            public void onClick(View view) {
                mCurrentErrCtrl.mTimeInterval = Long.parseLong((String) view.getTag());
                mCurrentErrCtrl.mTimeStamp = System.currentTimeMillis() / 1000;
                Log.i("ttimeout", "showTimeDialog...mTimeInterval:" + mCurrentErrCtrl.mTimeInterval);
                mTimedlg.hideCustomToast();
                Tpms3 tpms3 = Tpms3.this;
                tpms3.StopSound(tpms3.mCurrentErrCtrl.mErrorKey);
                mErrorToast = null;
                mTimedlg = null;
                if (Tpms3.homeListenerReceiver != null) {
                    try {
                        app.unregisterReceiver(Tpms3.homeListenerReceiver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        this.mHeartbeat = new Runnable() {
            public void run() {
                mencode.SendHeartbeat();
                mHeader.postDelayed(mHeartbeat, 1000);
            }
        };
        this.CheckEncryptionTime = new Runnable() {
            public void run() {
                if (!mIsSeedAckOk) {
                    Log.i(TAG, "CheckEncryptionTime");
                    showErrorDlg();
                }
            }
        };
        this.mErrorCount = 0;
        this.time = (byte) ((int) (System.currentTimeMillis() & 255));
    }

    @Override
    public void initCodes() {
        Log.i(this.TAG, "initCodes");
        this.mdecode = new FrameDecode3();
        this.mencode = new FrameEncode3(app);
        this.mencode.init(app);
        this.mdecode.init(app);
    }

    @Override
    public AlarmAgrs getAlarmAgrs() {
        return this.mAlarmAgrs;
    }

    @Override
    public void init() {
        if (!this.mIsInit) {
            Log.i(this.TAG, "init");
            Handler handler = new Handler();
            this.mDataCheckHander = handler;
            handler.postDelayed(this.mDataCheckTimer, 3000);
            initShakeHand();
            queryConfig();
            this.mIsInit = true;
        }
    }

    @Override
    public void initShakeHand() {
        this.mIsSeedAckOk = false;
        this.mErrorCount = 0;
        shakeHand();
        if (this.mHeader == null) {
            this.mHeader = new Handler();
        }
        this.mHeader.removeCallbacks(this.mHeartbeat);
        this.mHeader.postDelayed(this.mHeartbeat, 1000);
        this.startDataTime = System.currentTimeMillis();
    }

    @Override
    public void shakeHand() {
        Log.i(this.TAG, "shakeHand 握手,没有协议");
    }

    @Override
    public void queryVersion() {
        Log.i(this.TAG, "查协议版本号,没有协议");
    }

    @Override
    public void queryConfig() {
        Log.i(this.TAG, "queryConfig 气压上下限，温度上限,没有协议，apk实现");
    }

    @Override
    public int addHiPressStamp() {
        Log.i(this.TAG, "设置最高高压阀值add");
        this.mHiPressStamp += 10;
        if (this.mHiPressStamp > MaxHiPress) {
            this.mHiPressStamp = MaxHiPress;
        }
        this.mPreferences.edit().putInt("mHiPressStamp", this.mHiPressStamp).apply();
        return this.mHiPressStamp;
    }

    @Override
    public int decHiPressStamp() {
        Log.i(this.TAG, "设置最高高压阀值dec");
        this.mHiPressStamp -= 10;
        int max = Math.max(getLowPress() + 10, 10);
        if (this.mHiPressStamp < max) {
            this.mHiPressStamp = max;
        }
        this.mPreferences.edit().putInt("mHiPressStamp", this.mHiPressStamp).apply();
        return this.mHiPressStamp;
    }

    @Override
    public int addLowPressStamp() {
        Log.i(this.TAG, "设置最低低压度阀值add");
        this.mLowPressStamp += 10;
        int min = Math.min(getHiPress() - 10, (int) MaxLowPress);
        if (this.mLowPressStamp > min) {
            this.mLowPressStamp = min;
        }
        this.mPreferences.edit().putInt("mLowPressStamp", this.mLowPressStamp).apply();
        return this.mLowPressStamp;
    }

    @Override
    public int decLowPressStamp() {
        Log.i(this.TAG, "设置最低低压度阀值dec");
        this.mLowPressStamp -= 10;
        if (this.mLowPressStamp < 0) {
            this.mLowPressStamp = 0;
        }
        this.mPreferences.edit().putInt("mLowPressStamp", this.mLowPressStamp).apply();
        return this.mLowPressStamp;
    }

    @Override
    public int setGaoya(int i) {
        Log.w(this.TAG, "设置最高压力阀值,没有协议");
        return (i / 10) * 10;
    }

    @Override
    public int setDiya(int i) {
        Log.w(this.TAG, "设置最高压力阀值，没有协议");
        return i * 10;
    }

    @Override
    public void queryBackLeft() {
        Log.i(this.TAG, "查左后轮ID 没有协议");
    }

    @Override
    public void queryBackRight() {
        Log.i(this.TAG, "查右后轮ID  没有协议");
    }

    @Override
    public void queryFrontLeft() {
        Log.i(this.TAG, "查前左轮ID  没有协议");
    }

    @Override
    public void queryFrontRight() {
        Log.i(this.TAG, "查前右ID  没有协议");
    }

    @Override
    public void paireBackLeft() {
        this.mIsPairedId = true;
        Log.i(this.TAG, "配对左后轮ID");
        this.mencode.paireBackLeft();
    }

    @Override
    public void paireBackRight() {
        this.mIsPairedId = true;
        Log.i(this.TAG, "配对右后轮ID");
        this.mencode.paireBackRight();
    }

    @Override
    public void paireFrontLeft() {
        this.mIsPairedId = true;
        Log.i(this.TAG, "配对前左轮ID");
        this.mencode.paireFrontLeft();
    }

    @Override
    public void paireFrontRight() {
        this.mIsPairedId = true;
        Log.i(this.TAG, "配对前右ID");
        this.mencode.paireFrontRight();
    }

    @Override
    public void paireSpTired() {
        this.mIsPairedId = true;
        Log.i(this.TAG, "配对前右ID");
        this.mencode.paireSpTired();
    }

    @Override
    public void stopPaire() {
        this.mIsPairedId = false;
        Log.i(this.TAG, "stopPaire");
        this.mencode.stopPaire();
    }

    public void onEventMainThread(TimeSeedEvent timeSeedEvent) {
        if (timeSeedEvent.mSeedAck == ((byte) ((((((((this.time ^ 32) ^ 23) ^ 1) ^ -122) ^ 100) ^ 1) ^ -126) ^ 118))) {
            Log.w(this.TAG, "ack.mSeedAck==ack0");
            this.mIsSeedAckOk = true;
            this.mErrorCount = 0;
            if (this.mErrorDlg != null) {
                this.mErrorDlg.hideCustomToast();
                this.mErrorDlg = null;
                return;
            }
            return;
        }
        Log.w(this.TAG, "ack.mSeedAck!=ack0");
        int i = this.mErrorCount + 1;
        this.mErrorCount = i;
        if (i > 5) {
            showErrorDlg();
        }
    }

    @Override
    public void onEventMainThread(TiresStateEvent tiresStateEvent) {
        String str;
        Intent intent = new Intent("TPMS_APP_STATE_SEND");
        intent.putExtra("lowpower", tiresStateEvent.mState.LowPower);
        intent.putExtra("leakage", tiresStateEvent.mState.Leakage);
        intent.putExtra("nosignal", tiresStateEvent.mState.NoSignal);
        intent.putExtra("airpressure", tiresStateEvent.mState.AirPressure);
        intent.putExtra("temperature", tiresStateEvent.mState.Temperature);
        intent.putExtra("tires", tiresStateEvent.tires);
        intent.putExtra("presunit", getYaliDanwei());
        app.sendBroadcast(intent);
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
            return;
        }
        Log.i(this.TAG, "onEventMainThread(TiresStateEvent alarm)");
        sendTimeSeed();
        hideConnectErrDlg();
        this.startDataTime = System.currentTimeMillis();
        CDialog2 cDialog2 = this.mConnectErrorDlg;
        if (cDialog2 != null) {
            cDialog2.hideCustomToast();
            this.mConnectErrorDlg = null;
        } else {
            this.mDataCheckHander.removeCallbacks(this.CheckEncryptionTime);
            this.mDataCheckHander.postDelayed(this.CheckEncryptionTime, 20000);
        }
        String str2 = "";
        if (tiresStateEvent.tires == 1) {
            tiresStateEvent.mState.mAlarmCntrols = this.mFrontLeft.mAlarmCntrols;
            this.mFrontLeft = tiresStateEvent.mState;
            str = str2 + app.getResources().getString(R.string.fl_tire);
            str2 = str2 + "leftfront";
        } else if (tiresStateEvent.tires == 2) {
            tiresStateEvent.mState.mAlarmCntrols = this.mFrontRight.mAlarmCntrols;
            this.mFrontRight = tiresStateEvent.mState;
            str = str2 + app.getResources().getString(R.string.youqianluntai);
            str2 = str2 + "rightfront";
        } else if (tiresStateEvent.tires == 3) {
            tiresStateEvent.mState.mAlarmCntrols = this.mBackRight.mAlarmCntrols;
            this.mBackRight = tiresStateEvent.mState;
            str = str2 + app.getResources().getString(R.string.youhouluntai);
            str2 = str2 + "rightback";
            if (!isAllTiresOk()) {
                showErrorNotifMsg();
            } else if (!isDevCheckOk()) {
                Log.i(this.TAG, "showNormalNotifMsg but checkerror");
            } else {
                showNormalNotifMsg();
            }
        } else if (tiresStateEvent.tires == 0) {
            tiresStateEvent.mState.mAlarmCntrols = this.mBackLeft.mAlarmCntrols;
            this.mBackLeft = tiresStateEvent.mState;
            str = str2 + app.getResources().getString(R.string.rl_tire);
            str2 = str2 + "leftback";
        } else if (tiresStateEvent.tires == 5) {
            tiresStateEvent.mState.mAlarmCntrols = this.mSpareTire.mAlarmCntrols;
            this.mSpareTire = tiresStateEvent.mState;
            str = str2 + app.getResources().getString(R.string.spare_tire);
            str2 = str2 + "SpareTire";
        } else {
            str = str2;
        }
        if (getZhuDongBaojin() != 0) {
            showAlarmDialog(str2, str, tiresStateEvent);
        }
    }

    private boolean isAllTiresOk() {
        TiresState[] tiresStateArr = {this.mFrontLeft, this.mBackLeft, this.mBackRight, this.mFrontRight};
        for (int i = 0; i < 4; i++) {
            if (!thisTiresOk(tiresStateArr[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean thisTiresOk(TiresState tiresState) {
        if (!tiresState.NoSignal && !tiresState.Leakage && tiresState.AirPressure < this.mHiPressStamp && tiresState.AirPressure > this.mLowPressStamp && tiresState.Temperature < this.mHiTempStamp && !tiresState.LowPower) {
            return true;
        }
        return false;
    }

    private boolean isTimeOut(String str, long j, String str2, TiresStateEvent tiresStateEvent) {
        if (TextUtils.isEmpty(str2)) {
            return false;
        }
        AlarmCntrol alarmCntrol = tiresStateEvent.mState.mAlarmCntrols.get(str2);
        if (alarmCntrol == null) {
            Log.i("ttimeout", str + str2 + " no record is time out");
            return true;
        } else if (alarmCntrol.mTimeStamp == 0 || j - alarmCntrol.mTimeStamp > alarmCntrol.mTimeInterval) {
            Log.i("ttimeout", str + str2 + ";dat time:" + (j - alarmCntrol.mTimeStamp) + ";Cntrol.mTimeStamp:" + alarmCntrol.mTimeStamp + ";Cntrol.mTimeInterval:" + alarmCntrol.mTimeInterval + ";curTime:" + j);
            alarmCntrol.mTimeInterval = 0;
            return true;
        } else {
            Log.i("ttimeout", str + str2 + " no time out;Cntrol.mTimeInterval:" + alarmCntrol.mTimeInterval);
            return false;
        }
    }

    public void showAlarmDialog(String str, String str2, TiresStateEvent tiresStateEvent) {
        String str3 = "";
        int i = 0;
        String str4 = "";
        String str5;
        //String string;
        Log.w(this.TAG, "showAlarmDialog");
        TiresState tiresState = tiresStateEvent.mState;
        long currentTimeMillis = System.currentTimeMillis() / 1000;
        boolean z = true;
        boolean z2 = true;
        boolean z3 = true;
        boolean z4 = true;
        boolean z5 = true;
        boolean z6 = true;
        while (true) {
            if (!z || !tiresState.NoSignal) {
                if (!z2 || !tiresState.Leakage) {
                    if (!z3 || tiresState.AirPressure < this.mHiPressStamp) {
                        if (z4 && tiresState.AirPressure <= this.mLowPressStamp) {
                            str5 = "mLowPressStamp";
                            i = R.string.lianjieyichang;
                            if (isTimeOut(str2, currentTimeMillis, str5, tiresStateEvent)) {
                                //string = app.getResources().getString(R.string.low_press);
                                break;
                            }
                            z4 = false;
                        } else {
                            i = R.string.lianjieyichang;
                            if (!z5 || tiresState.Temperature < this.mHiTempStamp) {
                                if (!z6 || !tiresState.LowPower) {
                                    break;
                                } else if (!isTimeOut(str2, currentTimeMillis, "LowPower", tiresStateEvent)) {
                                    z6 = false;
                                } else if (getBettaWarringEnable()) {
                                    str3 = app.getResources().getString(R.string.low_pwr);
                                    str4 = "LowPower";
                                }
                            } else {
                                str5 = "mHiTempStamp";
                                if (isTimeOut(str2, currentTimeMillis, str5, tiresStateEvent)) {
                                    //string = app.getResources().getString(R.string.wengduguogao);
                                    break;
                                }
                                z5 = false;
                            }
                        }
                    } else {
                        str5 = "mHiPressStamp";
                        if (isTimeOut(str2, currentTimeMillis, str5, tiresStateEvent)) {
                            //string = app.getResources().getString(R.string.taiyaguogao);
                            break;
                        }
                        z3 = false;
                    }
                } else {
                    str5 = "Leakage";
                    if (isTimeOut(str2, currentTimeMillis, str5, tiresStateEvent)) {
                        //string = app.getResources().getString(R.string.leaking);
                        break;
                    }
                    z2 = false;
                }
            } else if (!isTimeOut(str2, currentTimeMillis, "NoSignal", tiresStateEvent)) {
                z = false;
            } else if (getConnectWarringEnable()) {
                str3 = app.getResources().getString(R.string.lianjieyichang);
                str4 = "NoSignal";
                i = R.string.lianjieyichang;
            } else {
                i = R.string.lianjieyichang;
            }
        }
        if (tiresState.NoSignal) {
            str3 = app.getResources().getString(i);
        }
        String str6 = str + str4;
        String str7 = str2 + str3;
        IsOkClearInTimeAndCUI(tiresState, str);
        if (TextUtils.isEmpty(str4)) {
            Log.i(this.TAG, "某一个轮胎无告警,包括没有超时");
            if (isAllOk()) {
                Log.i(this.TAG, "isAllOk");
            }
        } else if (this.mErrorToast != null) {
            Log.w(this.TAG, "已经显示了UI");
        } else {
            AlarmCntrol alarmCntrol = tiresStateEvent.mState.mAlarmCntrols.get(str4);
            this.mCurrentErrCtrl = alarmCntrol;
            if (alarmCntrol == null) {
                this.mCurrentErrCtrl = new AlarmCntrol();
                tiresStateEvent.mState.mAlarmCntrols.put(str4, this.mCurrentErrCtrl);
            }
            if (this.mCurrentErrCtrl.mTimeInterval == Long.MAX_VALUE) {
                Log.w(this.TAG, "熄火前都不报警");
                return;
            }
            if (getSoundWarringEnable()) {
                playerSound(str6);
            }
            if (!getShowUiEnable()) {
                Log.w(this.TAG, "不允许显示UI");
            } else if (this.mCurrentErrCtrl.mTimeStamp == 0 || currentTimeMillis - this.mCurrentErrCtrl.mTimeStamp > this.mCurrentErrCtrl.mTimeInterval) {
                Log.w("test", "tevent id:" + tiresStateEvent.tires + ";timestamp:" + this.mCurrentErrCtrl.mTimeStamp + ";mTimeInterval:" + this.mCurrentErrCtrl.mTimeInterval);
                this.mCurrentErrCtrl.mError = str7;
                this.mCurrentErrCtrl.mErrorKey = str6;
                if (!isRunningForeground()) {
                    showErrorToast();
                    Log.i(this.TAG, "in backround 在后台");
                    return;
                }
                Log.i(this.TAG, "in Foreground 在前台");
            }
        }
    }

    private void IsOkClearInTimeAndCUI(TiresState tiresState, String str) {
        if (!tiresState.NoSignal) {
            resetInTIme("NoSignal", tiresState);
            clearUIAndSound(str + "NoSignal");
        }
        if (!tiresState.Leakage) {
            resetInTIme("Leakage", tiresState);
            clearUIAndSound(str + "Leakage");
        }
        if (tiresState.AirPressure < this.mHiPressStamp) {
            resetInTIme("mHiPressStamp", tiresState);
            clearUIAndSound(str + "mHiPressStamp");
        }
        if (tiresState.AirPressure > this.mLowPressStamp) {
            resetInTIme("mLowPressStamp", tiresState);
            clearUIAndSound(str + "mLowPressStamp");
        }
        if (tiresState.Temperature < this.mHiTempStamp) {
            resetInTIme("mHiTempStamp", tiresState);
            clearUIAndSound(str + "mHiTempStamp");
        }
        if (!tiresState.LowPower) {
            resetInTIme("LowPower", tiresState);
            clearUIAndSound(str + "LowPower");
        }
    }

    private void clearUIAndSound(String str) {
        if (this.mErrorToast != null && this.mErrorToast.getGuid().equals(str)) {
            Log.i("testtpms", "================:" + str);
            this.mErrorToast.hideCustomToast();
            this.mErrorToast = null;
            if (this.mTimedlg != null) {
                this.mTimedlg.hideCustomToast();
                this.mTimedlg = null;
            }
        }
        StopSound(str);
    }

    private void resetInTIme(String str, TiresState tiresState) {
        AlarmCntrol alarmCntrol = tiresState.mAlarmCntrols.get(str);
        if (alarmCntrol != null) {
            alarmCntrol.mTimeInterval = 0;
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(app, TpmsMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        app.startActivity(intent);
    }

    private void showErrorToast() {
        if (isDevCheckOk()) {
            mErrorToast = new ClickToast();
            View inflate = LayoutInflater.from(app.getApplicationContext()).inflate(R.layout.click_error_toast, (ViewGroup) null);
            inflate.findViewById(R.id.relativelayout).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    startMainActivity();
                }
            });
            inflate.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (mErrorToast != null) {
                        mErrorToast.hideCustomToast();
                    }
                    showTimeDialog();
                }
            });
            mErrorToast.setGuid(mCurrentErrCtrl.mErrorKey);
            mErrorToast.initToast(app.getApplicationContext(), inflate, mCurrentErrCtrl.mError);
            mErrorToast.show();
        }
    }

    private void showErrorDlg() {
        if (this.mErrorDlg == null && !this.mIsPairedId) {
            String stackTraceString = android.util.Log.getStackTraceString(new Throwable());
            Log.i(this.TAG, "showErrorDlg");
            Log.i(this.TAG, stackTraceString);
            View inflate = LayoutInflater.from(app.getApplicationContext()).inflate(R.layout.error_dialog, (ViewGroup) null);
            this.mErrorDlg = CDialog2.makeToast(app, inflate, "");
            inflate.findViewById(R.id.btn_is_ok).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    mErrorDlg.hideCustomToast();
                    mErrorDlg = null;
                }
            });
            showErrorNotifMsg2();
            this.mErrorDlg.show();
            EventBus.getDefault().post(new TpmsDevErrorEvent(0));
        }
    }

    private void showConnectErrDlg() {
        Log.i(this.TAG, "showConnectErrDlg1");
        if (this.mConnectErrorDlg == null && !this.mIsPairedId) {
            Log.i(this.TAG, "showConnectErrDlg2");
            View inflate = LayoutInflater.from(app.getApplicationContext()).inflate(R.layout.connect_error_dialog, (ViewGroup) null);
            this.mConnectErrorDlg = CDialog2.makeToast(app, inflate, "");
            inflate.findViewById(R.id.btn_is_ok).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    mConnectErrorDlg.hideCustomToast();
                    mConnectErrorDlg = null;
                }
            });
            this.mConnectErrorDlg.show();
        }
    }

    private void hideConnectErrDlg() {
        CDialog2 cDialog2 = this.mConnectErrorDlg;
        if (cDialog2 != null) {
            cDialog2.hideCustomToast();
            this.mConnectErrorDlg = null;
        }
    }

    private void showTimeDialog() {
        View inflate = LayoutInflater.from(app.getApplicationContext()).inflate(R.layout.time_dialog, (ViewGroup) null);
        this.mTimedlg = CDialog2.makeToast(app, inflate, "");
        View findViewById = inflate.findViewById(R.id.mainbtn_0);
        View findViewById2 = inflate.findViewById(R.id.mainbtn_1);
        View findViewById3 = inflate.findViewById(R.id.mainbtn_2);
        View findViewById4 = inflate.findViewById(R.id.mainbtn_3);
        findViewById.setOnClickListener(this.btn_click);
        findViewById2.setOnClickListener(this.btn_click);
        findViewById3.setOnClickListener(this.btn_click);
        findViewById4.setOnClickListener(this.btn_click);
        inflate.findViewById(R.id.time_dialog_plane).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mTimedlg != null) {
                    mTimedlg.hideCustomToast();
                }
                mErrorToast.hideCustomToast();
                mErrorToast = null;
                mTimedlg = null;
                if (Tpms3.homeListenerReceiver != null) {
                    try {
                        app.unregisterReceiver(Tpms3.homeListenerReceiver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        app.registerReceiver(homeListenerReceiver, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        this.mTimedlg.show();
    }

    private void queryAllState() {
        Log.i(this.TAG, "queryAllState ");
    }

    public void onEventMainThread(HeartbeatEvent heartbeatEvent) {
        HeartbeatEventAck();
    }

    @Override
    public void HeartbeatEventAck() {
        this.mencode.HeartbeatEventAck();
    }

    public void Heartbeat() {
        this.mencode.HeartbeatEventAck();
    }

    @Override
    public void querySensorID() {
        this.mencode.querySensorID();
    }

    @Override
    public int setHiTempDef() {
        this.mHiTempStamp = 75;
        this.mPreferences.edit().putInt("mHiTempStamp", 75).apply();
        return this.mHiTempStamp;
    }

    @Override
    public int setHiPressDef() {
        this.mHiPressStamp = 310;
        this.mPreferences.edit().putInt("mHiPressStamp", 310).apply();
        return this.mHiPressStamp;
    }

    @Override
    public int setLowPressDef() {
        this.mLowPressStamp = 180;
        this.mPreferences.edit().putInt("mLowPressStamp", 180).apply();
        return this.mLowPressStamp;
    }

    @Override
    public void exchangeLeftFrontRightFront() {
        this.mencode.exchangeLeftFrontRightFront();
    }

    @Override
    public void exchangeLeftFrontLeftBack() {
        this.mencode.exchangeLeftFrontLeftBack();
    }

    @Override
    public void exchangeLeftFrontRightBack() {
        this.mencode.exchangeLeftFrontRightBack();
    }

    @Override
    public void exchangeRightFrontLeftBack() {
        this.mencode.exchangeRightFrontLeftBack();
    }

    @Override
    public void exchangeRightFrontRightBack() {
        this.mencode.exchangeRightFrontRightBack();
    }

    @Override
    public void exchangeLeftBackRightBack() {
        this.mencode.exchangeLeftBackRightBack();
    }

    @Override
    public void exchange_sp_fl() {
        this.mencode.exchange_sp_fl();
    }

    @Override
    public void exchange_sp_fr() {
        this.mencode.exchange_sp_fr();
    }

    @Override
    public void exchange_sp_bl() {
        this.mencode.exchange_sp_bl();
    }

    @Override
    public void exchange_sp_br() {
        this.mencode.exchange_sp_br();
    }

    @Override
    public void unInit() {
        if (this.mIsInit) {
            unintShakeHand();
            this.mDataCheckHander.removeCallbacks(this.mDataCheckTimer);
            super.unInit();
        }
    }

    @Override
    public void unintShakeHand() {
        Log.i(this.TAG, "unintShakeHand");
        this.mIsSeedAckOk = false;
        this.mHeader.removeCallbacks(this.mHeartbeat);
        this.mErrorCount = 0;
        Handler handler = this.mTimerCheckSeed;
        if (handler != null) {
            handler.removeCallbacks(this.CheckEncryptionTime);
            this.mTimerCheckSeed = null;
        }
        this.startDataTime = -1;
        showErrorNotifMsg();
    }

    public void sendTimeSeed() {
        String str = this.TAG;
        Log.i(str, "sendTimeSeed mIsSeedAckOk:" + this.mIsSeedAckOk);
        if (!this.mIsSeedAckOk) {
            this.mencode.SendEncryption(this.time);
        }
        if (this.mTimerCheckSeed == null) {
            Handler handler = new Handler();
            this.mTimerCheckSeed = handler;
            handler.postDelayed(this.CheckEncryptionTime, 600000);
        }
    }
}
