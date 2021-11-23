package com.tpms.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.mcal.tmps.R;

public class GetSreenInfoActivity extends Activity {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_screen);
        final EditText editText = findViewById(R.id.main_et_width_px);
        final EditText editText2 = findViewById(R.id.main_et_height_px);
        final EditText editText3 = findViewById(R.id.main_et_density);
        final EditText editText4 = findViewById(R.id.main_et_density_dpi);
        final EditText editText5 = findViewById(R.id.main_et_width_dip);
        final EditText editText6 = findViewById(R.id.main_et_height_dip);
        findViewById(R.id.main_btn).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            public void onClick(View view) {
                WindowManager windowManager = GetSreenInfoActivity.this.getWindowManager();
                int width = windowManager.getDefaultDisplay().getWidth();
                int height = windowManager.getDefaultDisplay().getHeight();
                editText.setText(width + "");
                editText2.setText(height + "");
                float f = getResources().getDisplayMetrics().density;
                editText3.setText(f + "");
                editText4.setText(((float) getResources().getDisplayMetrics().densityDpi) + "");
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
