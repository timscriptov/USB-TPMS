package com.tpms.view;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.std.dev.TpmsDataSrc;
import com.syt.tmps.R;
import com.syt.tmps.TpmsApplication;
import com.tpms.biz.Tpms;
import com.tpms.modle.TiresExchangeEvent;
import com.tpms.utils.Log;
import com.tpms.widget.CDialog;
import com.tpms.widget.PAlertDialog;

import de.greenrobot.event.EventBus;

public class SetExchangeActivity extends Fragment {
    private final BroadcastReceiver filterReceiver = new BroadcastReceiver() {
        /* class com.tpms.view.SetExchangeActivity.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            String stringExtra;
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction()) && (stringExtra = intent.getStringExtra("reason")) != null) {
                stringExtra.equals("homekey");
            }
        }
    };
    private final String TAG = "SetExchangeActivity";
    TpmsApplication app = null;
    @ViewInject(R.id.btn_cannel_exchange)
    Button btn_cannel_exchange;
    @ViewInject(R.id.btn_sp_bl)
    Button btn_sp_bl;
    @ViewInject(R.id.btn_sp_br)
    Button btn_sp_br;
    @ViewInject(R.id.btn_sp_fl)
    Button btn_sp_fl;
    @ViewInject(R.id.btn_sp_fr)
    Button btn_sp_fr;
    @ViewInject(R.id.btn_start_exchange)
    Button btn_start_exchange;
    TpmsDataSrc datasrc = null;
    @ViewInject(R.id.iv_exchange)
    ImageView iv_exchange;
    CDialog mExChangeFailed;
    Toast mExChangeOk;
    Handler mFailedHander;
    AlertDialog mPDlg;
    Button mSelectBtn;
    Tpms mTpms;
    @ViewInject(R.id.tv_exchange_hint)
    TextView tv_exchange_hint;
    Runnable mFailedAble = new Runnable() {
        public void run() {
            SetExchangeActivity.this.mExChangeFailed.show();
            if (SetExchangeActivity.this.mPDlg != null) {
                SetExchangeActivity.this.mPDlg.dismiss();
                SetExchangeActivity.this.mPDlg = null;
            }
            SetExchangeActivity setExchangeActivity = SetExchangeActivity.this;
            setExchangeActivity.btn_cannel_exchange(setExchangeActivity.btn_cannel_exchange);
        }
    };

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_exchange2, (ViewGroup) null);
        ViewUtils.inject(this, inflate);
        this.app = (TpmsApplication) getActivity().getApplication();
        EventBus.getDefault().register(this);
        this.datasrc = this.app.getDataSrc();
        this.mTpms = this.app.getTpms();
        this.btn_cannel_exchange.setVisibility(8);
        this.mExChangeOk = Toast.makeText(getActivity(), getString(R.string.jiaohuanchenggong), 2000);
        this.mExChangeFailed = new CDialog(getActivity(), (int) R.layout.confirm_dialog);
        this.mFailedHander = new Handler();
        showLeftTires(!this.app.getTpms().getSparetireEnable());
        return inflate;
    }

    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    public void onHiddenChanged(boolean z) {
        super.onHiddenChanged(z);
        Log.i(this.TAG, "onHiddenChanged :" + z);
        if (getView() == null) {
            Log.i(this.TAG, "还没有创建view");
        } else if (z) {
            this.mFailedHander.removeCallbacks(this.mFailedAble);
        } else {
            showLeftTires(!this.app.getTpms().getSparetireEnable());
        }
    }

    private void showLeftTires(boolean z) {
        if (z) {
            this.btn_sp_fl.setVisibility(4);
            this.btn_sp_fr.setVisibility(4);
            this.btn_sp_bl.setVisibility(4);
            this.btn_sp_br.setVisibility(4);
            this.iv_exchange.setImageLevel(0);
            btn_cannel_exchange(null);
            return;
        }
        this.btn_sp_fl.setVisibility(0);
        this.btn_sp_fr.setVisibility(0);
        this.btn_sp_bl.setVisibility(0);
        this.btn_sp_br.setVisibility(0);
        btn_cannel_exchange(null);
        this.mSelectBtn = null;
    }

    @OnClick({R.id.back_exchange})
    public void back_exchange(View view) {
        setPress(view);
        this.iv_exchange.getBackground().setLevel(1);
    }

    @OnClick({R.id.deputy_cross})
    public void deputy_cross(View view) {
        setPress(view);
        this.iv_exchange.getBackground().setLevel(2);
    }

    @OnClick({R.id.deputy_master})
    public void deputy_master(View view) {
        setPress(view);
        this.iv_exchange.getBackground().setLevel(5);
    }

    @OnClick({R.id.deputy_updown})
    public void deputy_updown(View view) {
        setPress(view);
        this.iv_exchange.getBackground().setLevel(6);
    }

    @OnClick({R.id.master_cross})
    public void master_cross(View view) {
        setPress(view);
        this.iv_exchange.getBackground().setLevel(4);
    }

    @OnClick({R.id.mater_updown})
    public void mater_updown(View view) {
        setPress(view);
        this.iv_exchange.getBackground().setLevel(3);
    }

    @OnClick({R.id.btn_sp_fl})
    public void btn_sp_fl(View view) {
        setPress(view);
        this.iv_exchange.getBackground().setLevel(9);
    }

    @OnClick({R.id.btn_sp_fr})
    public void btn_sp_fr(View view) {
        setPress(view);
        this.iv_exchange.getBackground().setLevel(10);
    }

    @OnClick({R.id.btn_sp_bl})
    public void btn_sp_bl(View view) {
        setPress(view);
        this.iv_exchange.getBackground().setLevel(7);
    }

    @OnClick({R.id.btn_sp_br})
    public void btn_sp_br(View view) {
        setPress(view);
        this.iv_exchange.getBackground().setLevel(8);
    }

    @OnClick({R.id.btn_cannel_exchange})
    public void btn_cannel_exchange(View view) {
        setUnPress();
        if (this.mTpms.getSparetireEnable()) {
            this.iv_exchange.setBackgroundResource(R.drawable.exchange_sptires_level);
        } else {
            this.iv_exchange.setBackgroundResource(R.drawable.exchange_nosptires_level);
        }
        this.iv_exchange.getBackground().setLevel(0);
        this.tv_exchange_hint.setText("");
        this.mFailedHander.removeCallbacks(this.mFailedAble);
    }

    @OnClick({R.id.btn_start_exchange})
    public void btn_start_exchange(View view) {
        Button button = this.mSelectBtn;
        if (button != null) {
            switch (button.getId()) {
                case R.id.back_exchange:
                    this.mTpms.exchangeLeftBackRightBack();
                    break;
                case R.id.btn_sp_bl:
                    this.mTpms.exchange_sp_bl();
                    break;
                case R.id.btn_sp_br:
                    this.mTpms.exchange_sp_br();
                    break;
                case R.id.btn_sp_fl:
                    this.mTpms.exchange_sp_fl();
                    break;
                case R.id.btn_sp_fr:
                    this.mTpms.exchange_sp_fr();
                    break;
                case R.id.deputy_cross:
                    this.mTpms.exchangeRightFrontLeftBack();
                    break;
                case R.id.deputy_master:
                    this.mTpms.exchangeLeftFrontRightFront();
                    break;
                case R.id.deputy_updown:
                    this.mTpms.exchangeRightFrontRightBack();
                    break;
                case R.id.master_cross:
                    this.mTpms.exchangeLeftFrontRightBack();
                    break;
                case R.id.mater_updown:
                    this.mTpms.exchangeLeftFrontLeftBack();
                    break;
            }
            this.tv_exchange_hint.setText(this.mSelectBtn.getContentDescription());
            this.mPDlg = PAlertDialog.showDiolg(getActivity(), "");
            this.mFailedHander.postDelayed(this.mFailedAble, 2000);
        }
    }

    private void setPress(View view) {
        Button button = this.mSelectBtn;
        if (button != null) {
            button.getBackground().setLevel(0);
        }
        Button button2 = (Button) view;
        this.mSelectBtn = button2;
        button2.getBackground().setLevel(1);
        this.tv_exchange_hint.setText(this.mSelectBtn.getContentDescription());
    }

    private void setUnPress() {
        Button button = this.mSelectBtn;
        if (button != null) {
            button.getBackground().setLevel(0);
        }
        this.mSelectBtn = null;
    }

    public void onEventMainThread(TiresExchangeEvent tiresExchangeEvent) {
        Log.i(this.TAG, "交换成功 ev:" + tiresExchangeEvent.EventName);
        if (!tiresExchangeEvent.EventName.equals("左前右前") && !tiresExchangeEvent.EventName.equals("左前左后") && !tiresExchangeEvent.EventName.equals("左前右后") && !tiresExchangeEvent.EventName.equals("右前左后") && !tiresExchangeEvent.EventName.equals("右前右后")) {
            tiresExchangeEvent.EventName.equals("左后右后");
        }
        AlertDialog alertDialog = this.mPDlg;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mPDlg = null;
        }
        this.mExChangeOk.show();
        btn_cannel_exchange(this.btn_cannel_exchange);
    }

    public void onStop() {
        this.mFailedHander.removeCallbacks(this.mFailedAble);
        super.onStop();
    }

    public void onDestroy() {
        this.mFailedHander.removeCallbacks(this.mFailedAble);
        super.onDestroy();
    }

    public void setUserVisibleHint(boolean z) {
        super.setUserVisibleHint(z);
        Log.i(this.TAG, "setUserVisibleHint:" + z);
    }
}
