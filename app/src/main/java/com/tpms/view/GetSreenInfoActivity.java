package com.tpms.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.syt.tmps.R;

public class GetSreenInfoActivity extends Activity {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_screen);
        final EditText editText = (EditText) findViewById(R.id.main_et_width_px);
        final EditText editText2 = (EditText) findViewById(R.id.main_et_height_px);
        final EditText editText3 = (EditText) findViewById(R.id.main_et_density);
        final EditText editText4 = (EditText) findViewById(R.id.main_et_density_dpi);
        final EditText editText5 = (EditText) findViewById(R.id.main_et_width_dip);
        final EditText editText6 = (EditText) findViewById(R.id.main_et_height_dip);
        ((Button) findViewById(R.id.main_btn)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                WindowManager windowManager = GetSreenInfoActivity.this.getWindowManager();
                int width = windowManager.getDefaultDisplay().getWidth();
                int height = windowManager.getDefaultDisplay().getHeight();
                editText.setText(width + "");
                editText2.setText(height + "");
                float f = GetSreenInfoActivity.this.getResources().getDisplayMetrics().density;
                editText3.setText(f + "");
                editText4.setText(((float) GetSreenInfoActivity.this.getResources().getDisplayMetrics().densityDpi) + "");
                GetSreenInfoActivity getSreenInfoActivity = GetSreenInfoActivity.this;
                int pxToDip = getSreenInfoActivity.pxToDip(getSreenInfoActivity, (float) width);
                GetSreenInfoActivity getSreenInfoActivity2 = GetSreenInfoActivity.this;
                int pxToDip2 = getSreenInfoActivity2.pxToDip(getSreenInfoActivity2, (float) height);
                editText5.setText(pxToDip + "");
                editText6.setText(pxToDip2 + "");
            }
        });
    }

    private int pxToDip(Context context, float f) {
        return (int) ((f / context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public int dipToPx(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }
}
