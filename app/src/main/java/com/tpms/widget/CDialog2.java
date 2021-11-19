package com.tpms.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.syt.tmps.R;

public class CDialog2 {
    private boolean closeBtnEvent = false;
    private Context mContext;
    private WindowManager.LayoutParams mParams;
    private int mStartX;
    private int mStartY;
    private Button mTvAddress;
    private View mView;
    private WindowManager mWM;

    public static CDialog2 makeToast(Context context, View view, String str) {
        CDialog2 cDialog2 = new CDialog2();
        cDialog2.initToast(context, view, str);
        return cDialog2;
    }

    public static CDialog2 makeToast(Context context, int i, String str) {
        CDialog2 cDialog2 = new CDialog2();
        cDialog2.closeBtnEvent = true;
        cDialog2.initToast(context, LayoutInflater.from(context).inflate(i, (ViewGroup) null), str);
        return cDialog2;
    }

    @SuppressLint("WrongConstant")
    public void initToast(Context context, View view, String str) {
        this.mContext = context;
        this.mWM = (WindowManager) context.getSystemService("window");
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        this.mParams = layoutParams;
        layoutParams.height = -1;
        this.mParams.width = -1;
        this.mParams.format = -3;
        if (Build.VERSION.SDK_INT >= 23) {
            this.mParams.type = 2038;
        } else {
            this.mParams.type = 2003;
        }
        this.mParams.flags = 136;
        this.mView = view;
        if (this.closeBtnEvent) {
            view.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    CDialog2.this.hideCustomToast();
                }
            });
        }
        this.mView.setLayoutParams(this.mParams);
    }

    public void show() {
        this.mWM.addView(this.mView, this.mParams);
    }

    public void hideCustomToast() {
        View view = this.mView;
        if (view != null) {
            if (view.getParent() != null) {
                this.mWM.removeView(this.mView);
            }
            this.mView = null;
        }
    }
}
