package com.tpms.widget;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public abstract class Toast2 {
    public static final int LENGTH_LONG = 1;
    public static final int LENGTH_SHORT = 0;
    private static final Handler handler = new Handler();
    private static Toast toast;
    private static final Runnable run = new Runnable() {
        public void run() {
            Toast2.toast.cancel();
        }
    };

    private static void toast(Context context, CharSequence charSequence, int i) {
        handler.removeCallbacks(run);
        if (i == 0) {
            i = 1000;
        } else if (i == 1) {
            i = 3000;
        }
        Toast toast2 = toast;
        if (toast2 != null) {
            toast2.setText(charSequence);
        } else {
            toast = Toast.makeText(context, charSequence, i);
        }
        handler.postDelayed(run, (long) i);
        toast.show();
    }

    public static void show(Context context, CharSequence charSequence, int i) throws NullPointerException {
        if (context != null) {
            if (i < 0) {
                i = 0;
            }
            toast(context, charSequence, i);
            return;
        }
        throw new NullPointerException("The ctx is null!");
    }

    public static void show(Context context, int i, int i2) throws NullPointerException {
        if (context != null) {
            if (i2 < 0) {
                i2 = 0;
            }
            toast(context, context.getResources().getString(i), i2);
            return;
        }
        throw new NullPointerException("The ctx is null!");
    }
}
