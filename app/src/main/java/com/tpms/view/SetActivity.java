package com.tpms.view;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.std.dev.TpmsDataSrc;
import com.syt.tmps.R;
import com.syt.tmps.TpmsApplication;
import com.tpms.modle.TiresState;
import com.tpms.utils.Log;

import java.util.HashMap;
import java.util.Map;

public class SetActivity extends Activity {
    private final BroadcastReceiver filterReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String stringExtra;
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction()) && (stringExtra = intent.getStringExtra("reason")) != null && stringExtra.equals("homekey")) {
                SetActivity.this.finish();
            }
        }
    };
    private final String TAG = "SetActivity";
    Map<String, Fragment> Fragments = new HashMap<>();
    TpmsApplication app = null;
    TpmsDataSrc datasrc = null;
    TiresState mBackLeft;
    TiresState mBackRight;
    TiresState mFrontLeft;
    TiresState mFrontRight;

    public void onClick(View view) {
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_set);
        ViewUtils.inject(this);
        TpmsApplication tpmsApplication = (TpmsApplication) getApplication();
        this.app = tpmsApplication;
        this.datasrc = tpmsApplication.getDataSrc();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        registerReceiver(this.filterReceiver, intentFilter);
        Log.e(TAG, "onCreate SetActivity");
        initView();
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        finish();
        super.onPause();
    }

    public void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        unregisterReceiver(this.filterReceiver);
        super.onDestroy();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    private Fragment ShowFragment(String str) {
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        try {
            Fragment fragment = this.Fragments.get(str);
            if (fragment == null) {
                fragment = (Fragment) Class.forName(str).newInstance();
                this.Fragments.put(str, fragment);
                beginTransaction.add(R.id.fragment_container, fragment);
            }
            for (Map.Entry<String, Fragment> entry : this.Fragments.entrySet()) {
                beginTransaction.hide(entry.getValue());
            }
            beginTransaction.show(fragment);
            beginTransaction.commit();
            return fragment;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initView() {
        String stringExtra = getIntent().getStringExtra("framgent");
        if (!TextUtils.isEmpty(stringExtra)) {
            ShowFragment(stringExtra);
        } else {
            ShowFragment("com.tpms.view.SetDetailActivity");
        }
        ((RadioGroup) findViewById(R.id.tablable)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                try {
                    ShowFragment((String) SetActivity.this.findViewById(i).getTag());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick({R.id.back_ui})
    public void back_ui(View view) {
        finish();
    }
}
