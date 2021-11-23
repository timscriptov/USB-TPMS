package com.tpms.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mcal.tmps.R;

public class CDialog extends Dialog {
    LayoutInflater inflater;
    View mView;

    public CDialog(Context context, View view) {
        super(context, R.style.DialogStyle);
        this.inflater = LayoutInflater.from(context);
        initView(view);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    public CDialog(Context context, int i) {
        this(context, (View) null);
        LayoutInflater from = LayoutInflater.from(context);
        this.inflater = from;
        initView(from.inflate(i, (ViewGroup) null));
    }

    private void initView(View view) {
        if (view != null) {
            this.mView = view;
            View findViewById = view.findViewById(R.id.close_btn);
            setContentView(view);
            if (findViewById != null) {
                findViewById.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        CDialog.this.dismiss();
                    }
                });
            }
        }
    }

    public void show(String str) {
        ((TextView) this.mView.findViewById(R.id.txt_view)).setText(str);
        super.show();
    }
}
