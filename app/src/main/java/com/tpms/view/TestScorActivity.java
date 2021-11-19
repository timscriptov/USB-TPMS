package com.tpms.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.std.dev.TpmsDataSrc;
import com.syt.tmps.R;
import com.syt.tmps.TpmsApplication;

public class TestScorActivity extends Activity {
    private final String TAG = "MainActivity";
    TpmsApplication app = null;
    TpmsDataSrc datasrc = null;
    boolean player = true;

    public void onClick(View view) {
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.fragment_set);
        TpmsApplication tpmsApplication = (TpmsApplication) getApplication();
        this.app = tpmsApplication;
        this.datasrc = tpmsApplication.getDataSrc();
    }

    private void sleep() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
