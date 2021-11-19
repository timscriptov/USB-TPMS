package com.tpms.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.std.dev.TpmsDataSrc;
import com.syt.tmps.R;
import com.syt.tmps.TpmsApplication;
import com.tpms.biz.Tpms;
import com.tpms.modle.DeviceOpenEvent;
import com.tpms.modle.TiresState;
import com.tpms.modle.TiresStateEvent;
import com.tpms.modle.TpmsDevErrorEvent;
import com.tpms.utils.Log;
import com.tpms.widget.PAlertDialog;

import de.greenrobot.event.EventBus;

public class TpmsMainActivity extends Activity {
    private final String TAG = "difengze.com-TpmsMainActivity";
    private final BroadcastReceiver filterReceiver = new BroadcastReceiver() {
        /* class com.tpms.view.TpmsMainActivity.AnonymousClass2 */

        public void onReceive(Context context, Intent intent) {
            String stringExtra;
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction()) && (stringExtra = intent.getStringExtra("reason")) != null) {
                stringExtra.equals("homekey");
            }
        }
    };
    TpmsApplication app = null;
    @ViewInject(R.id.back_left_betta)
    TextView back_left_betta;
    @ViewInject(R.id.back_left_connect)
    TextView back_left_connect;
    @ViewInject(R.id.back_left_error)
    TextView back_left_error;
    @ViewInject(R.id.back_left_infos)
    LinearLayout back_left_infos;
    @ViewInject(R.id.back_left_pressure)
    TextView back_left_pressure;
    @ViewInject(R.id.back_left_temp)
    TextView back_left_temp;
    @ViewInject(R.id.back_right_betta)
    TextView back_right_betta;
    @ViewInject(R.id.back_right_connect)
    TextView back_right_connect;
    @ViewInject(R.id.back_right_error)
    TextView back_right_error;
    @ViewInject(R.id.back_right_infos)
    LinearLayout back_right_infos;
    @ViewInject(R.id.back_right_pressure)
    TextView back_right_pressure;
    @ViewInject(R.id.back_right_temp)
    TextView back_right_temp;
    TpmsDataSrc datasrc = null;
    @ViewInject(R.id.front_left_betta)
    TextView front_left_betta;
    @ViewInject(R.id.front_left_connect)
    TextView front_left_connect;
    @ViewInject(R.id.front_left_error)
    TextView front_left_error;
    @ViewInject(R.id.front_left_infos)
    LinearLayout front_left_infos;
    @ViewInject(R.id.front_left_pressure)
    TextView front_left_pressure;
    @ViewInject(R.id.front_left_temp)
    TextView front_left_temp;
    @ViewInject(R.id.front_right_betta)
    TextView front_right_betta;
    @ViewInject(R.id.front_right_connect)
    TextView front_right_connect;
    @ViewInject(R.id.front_right_error)
    TextView front_right_error;
    @ViewInject(R.id.front_right_infos)
    LinearLayout front_right_infos;
    @ViewInject(R.id.front_right_pressure)
    TextView front_right_pressure;
    @ViewInject(R.id.front_right_temp)
    TextView front_right_temp;
    @ViewInject(R.id.ll_sptires_contioner)
    LinearLayout ll_sptires_contioner;
    TiresState mBackLeft;
    TiresState mBackRight;
    TiresState mFrontLeft;
    TiresState mFrontRight;
    AlertDialog mPDlg;
    TiresState mSpareTire;
    Handler mSyncHandler;
    Runnable mSyncRunAble = new Runnable() {
        @SuppressLint("WrongConstant")
        public void run() {
            if (TpmsMainActivity.this.mPDlg.isShowing()) {
                TpmsMainActivity.this.mPDlg.dismiss();
                TpmsMainActivity tpmsMainActivity = TpmsMainActivity.this;
                Toast.makeText(tpmsMainActivity, tpmsMainActivity.getString(R.string.xingxiduqushibai), 4000).show();
            }
        }
    };
    Tpms mTpms;
    @ViewInject(R.id.tv_sptires_betta)
    TextView tv_sptires_betta;
    @ViewInject(R.id.tv_sptires_error)
    TextView tv_sptires_error;
    @ViewInject(R.id.tv_sptires_pressure)
    TextView tv_sptires_pressure;
    @ViewInject(R.id.tv_sptires_temp)
    TextView tv_sptires_temp;

    public void onClick(View view) {
    }

    @SuppressLint("WrongConstant")
    public void onCreate(Bundle bundle) {
        String action;
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent == null || (action = intent.getAction()) == null || !action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
            setContentView(R.layout.activity_main);
            ViewUtils.inject(this);
            EventBus.getDefault().register(this);
            TpmsApplication tpmsApplication = (TpmsApplication) getApplication();
            this.app = tpmsApplication;
            tpmsApplication.startTpms();
            this.mTpms = this.app.getTpms();
            this.datasrc = this.app.getDataSrc();
            this.mPDlg = PAlertDialog.showDiolg(this, getString(R.string.zhengzaiduquzhong));
            Handler handler = new Handler();
            this.mSyncHandler = handler;
            handler.postDelayed(this.mSyncRunAble, 14000);
            if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
                startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION"));
            }
            if (this.mTpms.isDevCheckOk()) {
                TiresState frontLeftState = this.mTpms.getFrontLeftState();
                this.mFrontLeft = frontLeftState;
                this.front_left_pressure.setText(getPressure(frontLeftState.AirPressure));
                this.front_left_error.setText(this.mFrontLeft.error);
                this.front_left_temp.setText(getTempString(this.mFrontLeft.Temperature));
                TiresState frontRightState = this.app.getTpms().getFrontRightState();
                this.mFrontRight = frontRightState;
                this.front_right_pressure.setText(getPressure(frontRightState.AirPressure));
                this.front_right_error.setText(this.mFrontRight.error);
                this.front_right_temp.setText(getTempString(this.mFrontRight.Temperature));
                TiresState backRightState = this.app.getTpms().getBackRightState();
                this.mBackRight = backRightState;
                this.back_right_pressure.setText(getPressure(backRightState.AirPressure));
                this.back_right_error.setText(this.mBackRight.error);
                this.back_right_temp.setText(getTempString(this.mBackRight.Temperature));
                TiresState backLeftState = this.app.getTpms().getBackLeftState();
                this.mBackLeft = backLeftState;
                this.back_left_pressure.setText(getPressure(backLeftState.AirPressure));
                this.back_left_error.setText(this.mBackLeft.error);
                this.back_left_temp.setText(getTempString(this.mBackLeft.Temperature));
                if (this.app.getTpms().getSparetireEnable()) {
                    this.ll_sptires_contioner.setVisibility(0);
                } else {
                    this.ll_sptires_contioner.setVisibility(4);
                }
                TiresState spareTire = this.app.getTpms().getSpareTire();
                this.mSpareTire = spareTire;
                this.tv_sptires_pressure.setText(getPressure(spareTire.AirPressure));
                this.tv_sptires_temp.setText(getTempString(this.mSpareTire.Temperature));
                return;
            }
            return;
        }
        finish();
    }

    public void onNewIntent(Intent intent) {
        String action;
        super.onNewIntent(intent);
        if (intent != null && (action = intent.getAction()) != null && action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
            finish();
        }
    }

    public void onStart() {
        this.mTpms.closeFloatWindow();
        super.onStart();
    }

    @SuppressLint("WrongConstant")
    public void onResume() {
        super.onResume();
        this.mTpms.setForeground(true);
        this.mTpms.closeFloatWindow();
        if (this.app.getTpms().getSparetireEnable()) {
            this.ll_sptires_contioner.setVisibility(0);
        } else {
            this.ll_sptires_contioner.setVisibility(4);
        }
    }

    public void onStop() {
        this.mTpms.setForeground(false);
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
        AlertDialog alertDialog = this.mPDlg;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        Handler handler = this.mSyncHandler;
        if (handler != null) {
            handler.removeCallbacks(this.mSyncRunAble);
        }
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onEventMainThread(TiresStateEvent tiresStateEvent) {
        if (this.mTpms.isDevCheckOk()) {
            this.mPDlg.dismiss();
            Log.i(this.TAG, "收到胎压装态数据");
            if (tiresStateEvent.tires == 1 && this.mFrontLeft != null) {
                tiresStateEvent.mState.TiresID = this.mFrontLeft.TiresID;
            } else if (tiresStateEvent.tires == 2 && this.mFrontRight != null) {
                tiresStateEvent.mState.TiresID = this.mFrontRight.TiresID;
            } else if (tiresStateEvent.tires == 3 && this.mBackRight != null) {
                tiresStateEvent.mState.TiresID = this.mBackRight.TiresID;
            } else if (tiresStateEvent.tires == 0 && this.mBackLeft != null) {
                tiresStateEvent.mState.TiresID = this.mBackLeft.TiresID;
            }
            int i = tiresStateEvent.tires;
            int i2 = R.drawable.connect_error_l;
            if (i == 1) {
                TiresState frontLeftState = this.app.getTpms().getFrontLeftState();
                this.mFrontLeft = frontLeftState;
                this.front_left_pressure.setText(getPressure(frontLeftState.AirPressure));
                this.front_left_temp.setText(getTempString(this.mFrontLeft.Temperature));
                TextView textView = this.front_left_connect;
                if (!tiresStateEvent.mState.NoSignal) {
                    i2 = R.drawable.connect_ok_l;
                }
                textView.setBackgroundResource(i2);
                updateView(tiresStateEvent, this.front_left_error, this.front_left_infos, this.front_left_betta);
                return;
            }
            int i3 = tiresStateEvent.tires;
            int i4 = R.drawable.connect_error_r;
            if (i3 == 2) {
                TiresState frontRightState = this.app.getTpms().getFrontRightState();
                this.mFrontRight = frontRightState;
                this.front_right_pressure.setText(getPressure(frontRightState.AirPressure));
                this.front_right_temp.setText(getTempString(this.mFrontRight.Temperature));
                TextView textView2 = this.front_right_connect;
                if (!tiresStateEvent.mState.NoSignal) {
                    i4 = R.drawable.connect_ok_r;
                }
                textView2.setBackgroundResource(i4);
                updateView(tiresStateEvent, this.front_right_error, this.front_right_infos, this.front_right_betta);
            } else if (tiresStateEvent.tires == 3) {
                TiresState backRightState = this.app.getTpms().getBackRightState();
                this.mBackRight = backRightState;
                this.back_right_pressure.setText(getPressure(backRightState.AirPressure));
                this.back_right_temp.setText(getTempString(this.mBackRight.Temperature));
                TextView textView3 = this.back_right_connect;
                if (!tiresStateEvent.mState.NoSignal) {
                    i4 = R.drawable.connect_ok_r;
                }
                textView3.setBackgroundResource(i4);
                updateView(tiresStateEvent, this.back_right_error, this.back_right_infos, this.back_right_betta);
            } else if (tiresStateEvent.tires == 0) {
                TiresState backLeftState = this.app.getTpms().getBackLeftState();
                this.mBackLeft = backLeftState;
                this.back_left_pressure.setText(getPressure(backLeftState.AirPressure));
                this.back_left_temp.setText(getTempString(this.mBackLeft.Temperature));
                TextView textView4 = this.back_left_connect;
                if (!tiresStateEvent.mState.NoSignal) {
                    i2 = R.drawable.connect_ok_l;
                }
                textView4.setBackgroundResource(i2);
                updateView(tiresStateEvent, this.back_left_error, this.back_left_infos, this.back_left_betta);
            } else if (tiresStateEvent.tires == 5) {
                this.mSpareTire = this.app.getTpms().getSpareTire();
                this.tv_sptires_pressure.setText(getPressure(tiresStateEvent.mState.AirPressure));
                this.tv_sptires_temp.setText(getTempString(tiresStateEvent.mState.Temperature));
                updateView(tiresStateEvent, this.tv_sptires_error, this.ll_sptires_contioner, this.tv_sptires_betta);
            }
        }
    }

    private void updateView(TiresStateEvent tiresStateEvent, TextView textView, LinearLayout linearLayout, TextView textView2) {
        String str;
        String str2;
        String str3;
        TiresState tiresState = tiresStateEvent.mState;
        String str4 = "";
        if (tiresState.NoSignal) {
            str2 = str4 + getString(R.string.lianjieyichang);
            str3 = "NoSignal";
        } else if (tiresState.Leakage) {
            str2 = str4 + getString(R.string.louqizhong);
            str3 = "Leakage";
        } else if (tiresState.AirPressure > this.mTpms.getHiPress()) {
            str2 = str4 + getString(R.string.taiyaguogao);
            str3 = "mHiPressStamp";
        } else if (tiresState.AirPressure < this.mTpms.getLowPress()) {
            str2 = str4 + getString(R.string.taiyaguodi);
            str3 = "mLowPressStamp";
        } else if (tiresState.Temperature > this.mTpms.getHiTemp()) {
            str2 = str4 + getString(R.string.wengduguogao);
            str3 = "mHiTempStamp";
        } else if (tiresState.LowPower) {
            str2 = str4 + getString(R.string.dianyaguodi);
            str3 = "LowPower";
        } else {
            str = str4;
            textView2.setBackgroundResource(!tiresState.LowPower ? R.drawable.bettawaring : R.drawable.bettaok);
            if (!TextUtils.isEmpty(str4)) {
                Log.i(this.TAG, "无告警");
                textView.setText(R.string.taiyazhengchang);
                linearLayout.getBackground().setLevel(1);
                return;
            }
            textView.setText(str);
            linearLayout.getBackground().setLevel(0);
            return;
        }
        str4 = str3;
        str = str2;
        textView2.setBackgroundResource(!tiresState.LowPower ? R.drawable.bettawaring : R.drawable.bettaok);
        if (!TextUtils.isEmpty(str4)) {
        }
    }

    @OnClick({R.id.btn_paire_id})
    public void btn_paire_id(View view) {
        startActivity(new Intent(this, PaireIDActivity.class));
    }

    @OnClick({R.id.btn_tpms_set})
    public void btn_tpms_set(View view) {
        startActivity(new Intent(this, SetActivity.class));
    }

    public String getPressure(int i) {
        return this.mTpms.getPressString(i) + this.mTpms.getYaliDanwei();
    }

    public String getTempString(int i) {
        return this.mTpms.getTempString(i) + this.mTpms.getWenduDanwei();
    }

    public void onEventMainThread(DeviceOpenEvent deviceOpenEvent) {
        boolean z = deviceOpenEvent.mOpen;
    }

    public void onEventMainThread(TpmsDevErrorEvent tpmsDevErrorEvent) {
        finish();
    }
}
