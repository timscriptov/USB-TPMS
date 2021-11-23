package com.tpms.widget;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mcal.tmps.R;

public class PAlertDialog {
    private static final String TAG = "PAlertDialog";

    public static AlertDialog showDiolg(Context context, String str) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R.id.tv_txt_wait);
        if (textView != null && !TextUtils.isEmpty(str)) {
            textView.setText(str);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(inflate);
        builder.create();
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialogInterface) {
                Log.i(PAlertDialog.TAG, "onCancel");
            }
        });
        return builder.show();
    }

    public static ProgressDialog showProgress(Context context, String str, String str2) {
        return ProgressDialog.show(context, str, str2, true, false);
    }
}
