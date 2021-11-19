package com.tpms.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnCompoundButtonCheckedChange;
import com.std.dev.TpmsDataSrc;
import com.syt.tmps.BuildConfig;
import com.syt.tmps.R;
import com.syt.tmps.TpmsApplication;
import com.tpms.biz.Tpms;
import com.tpms.modle.TiresState;
import com.tpms.utils.Log;
import com.tpms.widget.CDialog;

public class SetDetailActivity extends Fragment {
    private final String TAG = "TpmsMainActivity";
    TpmsApplication app = null;
    @ViewInject(R.id.cb_betta_warring)
    ToggleButton cb_betta_warring;
    @ViewInject(R.id.cb_connect_warring)
    ToggleButton cb_connect_warring;
    @ViewInject(R.id.cb_showui_enable)
    ToggleButton cb_showui_enable;
    @ViewInject(R.id.cb_sound_warring)
    ToggleButton cb_sound_warring;
    @ViewInject(R.id.cb_spare_tire_enable)
    ToggleButton cb_spare_tire_enable;
    TpmsDataSrc datasrc = null;
    TiresState mBackLeft;
    TiresState mBackRight;
    TiresState mFrontLeft;
    TiresState mFrontRight;
    Tpms mTpms;
    CDialog resetDlg;
    @ViewInject(R.id.tv_hi_temp)
    TextView tv_hi_temp;
    @ViewInject(R.id.tv_hipressure)
    TextView tv_hipressure;
    @ViewInject(R.id.tv_lopressure)
    TextView tv_lopressure;
    @ViewInject(R.id.tv_pressureunit)
    TextView tv_pressureunit;
    @ViewInject(R.id.tv_tempunit)
    TextView tv_tempunit;
    @ViewInject(R.id.tv_version)
    TextView tv_version;

    @OnClick({R.id.tv_version})
    public void tv_version(View view) {
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_set, (ViewGroup) null);
        ViewUtils.inject(this, inflate);
        TpmsApplication tpmsApplication = (TpmsApplication) getActivity().getApplication();
        this.app = tpmsApplication;
        this.datasrc = tpmsApplication.getDataSrc();
        this.mTpms = this.app.getTpms();
        initView();
        Log.e("difengze.com", "SetDeailActivty onCreateView");
        return inflate;
    }

    public void initView() {
        TextView textView = this.tv_hi_temp;
        Tpms tpms = this.mTpms;
        String sb = tpms.getTempString(tpms.getHiTemp()) +
                this.mTpms.getWenduDanwei();
        textView.setText(sb);
        this.tv_tempunit.setText(this.mTpms.getWenduDanwei());
        TextView textView2 = this.tv_hipressure;
        Tpms tpms2 = this.mTpms;
        String sb2 = tpms2.getPressString(tpms2.getHiPress()) +
                this.mTpms.getYaliDanwei();
        textView2.setText(sb2);
        TextView textView3 = this.tv_lopressure;
        Tpms tpms3 = this.mTpms;
        String sb3 = tpms3.getPressString(tpms3.getLowPress()) +
                this.mTpms.getYaliDanwei();
        textView3.setText(sb3);
        this.tv_pressureunit.setText(this.mTpms.getYaliDanwei());
        this.cb_showui_enable.setChecked(this.mTpms.getShowUiEnable());
        this.cb_sound_warring.setChecked(this.mTpms.getSoundWarringEnable());
        this.cb_betta_warring.setChecked(this.mTpms.getBettaWarringEnable());
        this.cb_connect_warring.setChecked(this.mTpms.getConnectWarringEnable());
        this.cb_spare_tire_enable.setChecked(this.mTpms.getSparetireEnable());
        this.tv_version.setText(BuildConfig.VERSION_NAME);
    }

    public void onHiddenChanged(boolean z) {
        super.onHiddenChanged(z);
        Log.i(this.TAG, "onHiddenChanged :" + z);
        if (getView() == null) {
            Log.i(this.TAG, "还没有创建view");
        }
    }

    @OnCompoundButtonCheckedChange({R.id.cb_showui_enable})
    public void cb_showui_enable(CompoundButton compoundButton, boolean z) {
        this.mTpms.setShowUiEnable(z);
    }

    @OnCompoundButtonCheckedChange({R.id.cb_sound_warring})
    public void cb_sound_warring(CompoundButton compoundButton, boolean z) {
        this.mTpms.setSoundWarringEnable(z);
    }

    @OnCompoundButtonCheckedChange({R.id.cb_betta_warring})
    public void cb_betta_warring(CompoundButton compoundButton, boolean z) {
        this.mTpms.setBettaWarringEnable(z);
    }

    @OnCompoundButtonCheckedChange({R.id.cb_connect_warring})
    public void cb_connect_warring(CompoundButton compoundButton, boolean z) {
        this.mTpms.setConnectWarringEnable(z);
    }

    @OnCompoundButtonCheckedChange({R.id.cb_spare_tire_enable})
    public void cb_spare_tire_enable(CompoundButton compoundButton, boolean z) {
        this.mTpms.setSparetireEnable(z);
    }

    @OnClick({R.id.btn_temp_def})
    public void btn_temp_def(View view) {
        TextView textView = this.tv_hi_temp;
        Tpms tpms = this.mTpms;
        String sb = tpms.getTempString(tpms.setHiTempDef()) +
                this.mTpms.getWenduDanwei();
        textView.setText(sb);
    }

    @OnClick({R.id.btn_hipressure_def})
    public void btn_hipressure_def(View view) {
        TextView textView = this.tv_hipressure;
        Tpms tpms = this.mTpms;
        String sb = tpms.getPressString(tpms.setHiPressDef()) +
                this.mTpms.getYaliDanwei();
        textView.setText(sb);
        TextView textView2 = this.tv_lopressure;
        Tpms tpms2 = this.mTpms;
        String sb2 = tpms2.getPressString(tpms2.setLowPressDef()) +
                this.mTpms.getYaliDanwei();
        textView2.setText(sb2);
    }

    @OnClick({R.id.btn_lowpressure_def})
    public void btn_lowpressure_def(View view) {
        TextView textView = this.tv_hipressure;
        Tpms tpms = this.mTpms;
        String sb = tpms.getPressString(tpms.setHiPressDef()) +
                this.mTpms.getYaliDanwei();
        textView.setText(sb);
        TextView textView2 = this.tv_lopressure;
        Tpms tpms2 = this.mTpms;
        String sb2 = tpms2.getPressString(tpms2.setLowPressDef()) +
                this.mTpms.getYaliDanwei();
        textView2.setText(sb2);
    }

    @OnClick({R.id.btn_temp_dec})
    public void btn_temp_dec(View view) {
        TextView textView = this.tv_hi_temp;
        Tpms tpms = this.mTpms;
        String sb = tpms.getTempString(tpms.decHiTemp()) +
                this.mTpms.getWenduDanwei();
        textView.setText(sb);
    }

    @OnClick({R.id.btn_hipressure_dec})
    public void btn_hipressure_dec(View view) {
        Log.i("test", "hipressure:" + this.mTpms.getHiPress());
        TextView textView = this.tv_hipressure;
        Tpms tpms = this.mTpms;
        String sb = tpms.getPressString(tpms.decHiPressStamp()) +
                this.mTpms.getYaliDanwei();
        textView.setText(sb);
    }

    @OnClick({R.id.btn_lowpressure_dec})
    public void btn_lowpressure_dec(View view) {
        TextView textView = this.tv_lopressure;
        Tpms tpms = this.mTpms;
        String sb = tpms.getPressString(tpms.decLowPressStamp()) +
                this.mTpms.getYaliDanwei();
        textView.setText(sb);
    }

    @OnClick({R.id.btn_temp_add})
    public void btn_temp_add(View view) {
        TextView textView = this.tv_hi_temp;
        Tpms tpms = this.mTpms;
        String sb = tpms.getTempString(tpms.addHiTemp()) +
                this.mTpms.getWenduDanwei();
        textView.setText(sb);
    }

    @OnClick({R.id.btn_hipressure_add})
    public void btn_hipressure_add(View view) {
        TextView textView = this.tv_hipressure;
        Tpms tpms = this.mTpms;
        String sb = tpms.getPressString(tpms.addHiPressStamp()) +
                this.mTpms.getYaliDanwei();
        textView.setText(sb);
    }

    @OnClick({R.id.btn_lowpressure_add})
    public void btn_lowpressure_add(View view) {
        TextView textView = this.tv_lopressure;
        Tpms tpms = this.mTpms;
        String sb = tpms.getPressString(tpms.addLowPressStamp()) +
                this.mTpms.getYaliDanwei();
        textView.setText(sb);
    }

    @OnClick({R.id.btn_tempunit_dec})
    public void btn_tempunit_dec(View view) {
        this.tv_tempunit.setText(this.mTpms.setNextWenduDanwei());
        TextView textView = this.tv_hi_temp;
        Tpms tpms = this.mTpms;
        String sb = tpms.getTempString(tpms.getHiTemp()) +
                this.mTpms.getWenduDanwei();
        textView.setText(sb);
    }

    @OnClick({R.id.btn_tempunit_add})
    public void btn_tempunit_add(View view) {
        this.tv_tempunit.setText(this.mTpms.setNextWenduDanwei());
        TextView textView = this.tv_hi_temp;
        Tpms tpms = this.mTpms;
        String sb = tpms.getTempString(tpms.getHiTemp()) +
                this.mTpms.getWenduDanwei();
        textView.setText(sb);
    }

    @OnClick({R.id.btn_pressureunit_dec})
    public void btn_pressureunit_dec(View view) {
        this.tv_pressureunit.setText(this.mTpms.setPreYaliDanwei());
        TextView textView = this.tv_hipressure;
        Tpms tpms = this.mTpms;
        String sb = tpms.getPressString(tpms.getHiPress()) +
                this.mTpms.getYaliDanwei();
        textView.setText(sb);
        TextView textView2 = this.tv_lopressure;
        Tpms tpms2 = this.mTpms;
        String sb2 = tpms2.getPressString(tpms2.getLowPress()) +
                this.mTpms.getYaliDanwei();
        textView2.setText(sb2);
    }

    @OnClick({R.id.btn_pressureunit_add})
    public void btn_pressureunit_add(View view) {
        this.tv_pressureunit.setText(this.mTpms.setNextYaliDanwei());
        TextView textView = this.tv_hipressure;
        Tpms tpms = this.mTpms;
        String sb = tpms.getPressString(tpms.getHiPress()) +
                this.mTpms.getYaliDanwei();
        textView.setText(sb);
        TextView textView2 = this.tv_lopressure;
        Tpms tpms2 = this.mTpms;
        String sb2 = tpms2.getPressString(tpms2.getLowPress()) +
                this.mTpms.getYaliDanwei();
        textView2.setText(sb2);
    }

    @OnClick({R.id.tv_reset_all})
    public void tv_reset_all(View view) {
        View inflate = getActivity().getLayoutInflater().inflate(R.layout.reset_dialog, (ViewGroup) null);
        CDialog cDialog = new CDialog(getActivity(), inflate);
        this.resetDlg = cDialog;
        cDialog.show();
        inflate.findViewById(R.id.close_btn_ok).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.i(SetDetailActivity.this.TAG, "点击了确定 恢复所有");
                SetDetailActivity.this.resetDlg.dismiss();
                SetDetailActivity.this.mTpms.resetAll();
                SetDetailActivity.this.initView();
            }
        });
    }
}
