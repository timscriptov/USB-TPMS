package com.tpms.widget;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mcal.tmps.R;

public class ClickToast {
    private boolean closeBtnEvent = false;
    private String guid = "";
    private Context mContext;
    private WindowManager.LayoutParams mParams;
    private int mStartX;
    private int mStartY;
    private Button mTvAddress;
    private View mView;
    private WindowManager mWM;

    public static ClickToast makeToast(Context context, View view, String str) {
        ClickToast clickToast = new ClickToast();
        clickToast.initToast(context, view, str);
        return clickToast;
    }

    public static ClickToast makeToast(Context context, int i, String str) {
        ClickToast clickToast = new ClickToast();
        clickToast.closeBtnEvent = true;
        clickToast.initToast(context, LayoutInflater.from(context).inflate(i, (ViewGroup) null), str);
        return clickToast;
    }

    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String str) {
        this.guid = str;
    }

    public void initToast(Context context, View view, String str) {
        this.mContext = context;
        this.mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        this.mParams = layoutParams;
        layoutParams.height = -2;
        this.mParams.width = -1;
        this.mParams.x = 0;
        this.mParams.y = 0;
        this.mParams.format = -3;
        if (Build.VERSION.SDK_INT >= 23) {
            this.mParams.type = 2038;
        } else {
            this.mParams.type = 2003;
        }
        this.mParams.flags = 136;
        this.mParams.gravity = 49;
        this.mView = view;
        if (this.closeBtnEvent) {
            view.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ClickToast.this.hideCustomToast();
                }
            });
        }
        ((TextView) this.mView.findViewById(R.id.txt_view)).setText(str);
        this.mView.setLayoutParams(this.mParams);
        this.mView.setOnTouchListener(new View.OnTouchListener() {
            int lastX;
            int lastY;
            int paramX;
            int paramY;

            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.lastX = (int) motionEvent.getRawX();
                    this.lastY = (int) motionEvent.getRawY();
                    this.paramX = ClickToast.this.mParams.x;
                    this.paramY = ClickToast.this.mParams.y;
                    return true;
                } else if (action != 2) {
                    return true;
                } else {
                    int rawX = ((int) motionEvent.getRawX()) - this.lastX;
                    int rawY = ((int) motionEvent.getRawY()) - this.lastY;
                    ClickToast.this.mParams.x = this.paramX + rawX;
                    ClickToast.this.mParams.y = this.paramY + rawY;
                    ClickToast.this.mWM.updateViewLayout(ClickToast.this.mView, ClickToast.this.mParams);
                    return true;
                }
            }
        });
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
