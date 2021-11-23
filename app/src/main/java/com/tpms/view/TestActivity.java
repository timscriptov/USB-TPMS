package com.tpms.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.std.dev.TpmsDataSrc;
import com.mcal.tmps.R;
import com.mcal.tmps.TpmsApplication;
import com.tpms.utils.Log;
import com.tpms.utils.SoundPoolCtrl;
import com.tpms.widget.CDialog;
import com.tpms.widget.ClickToast;
import com.tpms.widget.PAlertDialog;

public class TestActivity extends Activity {
    private final BroadcastReceiver filterReceiver = new BroadcastReceiver() {
        /* class com.tpms.view.TestActivity.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            String stringExtra;
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction()) && (stringExtra = intent.getStringExtra("reason")) != null) {
                stringExtra.equals("homekey");
            }
        }
    };
    private final String TAG = "MainActivity";
    TpmsApplication app = null;
    ClickToast ctotast = null;
    TpmsDataSrc datasrc = null;
    AudioManager mAudioManager;
    int mNotificationState = -1;
    SoundPoolCtrl mSound;
    CDialog mdlg;
    NotificationManager notificationManager;
    boolean player = true;
    int playret = 0;
    CDialog resetDlg;
    float speed = 0.3f;
    @ViewInject(R.id.tv_screen_info)
    TextView tv_screen_info;
    int volindex = 0;
    private SoundPool soundPool;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main_test);
        ViewUtils.inject(this);
        TpmsApplication tpmsApplication = (TpmsApplication) getApplication();
        this.app = tpmsApplication;
        this.datasrc = tpmsApplication.getDataSrc();
        this.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        this.mSound = new SoundPoolCtrl(this);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.hand_work) {
            this.app.getTpms().shakeHand();
        } else if (view.getId() == R.id.query) {
            this.app.getTpms().querySensorID();
        } else if (view.getId() == R.id.query_front_left) {
            this.app.getTpms().queryFrontLeft();
        } else if (view.getId() == R.id.query_two_back) {
            this.app.getTpms().queryBackLeft();
            this.app.getTpms().queryBackRight();
        }
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

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4) {
            return super.onKeyDown(i, keyEvent);
        }
        finish();
        onDestroy();
        return true;
    }

    @OnClick({R.id.btn_time_select})
    public void btn_time_select(View view) {
        showTimeDialog();
    }

    @OnClick({R.id.btn_enter_apk})
    public void btn_enter_apk(View view) {
        startActivity(new Intent(this, TpmsMainActivity.class));
    }

    @OnClick({R.id.btn_click_toast})
    public void btn_click_toast(View view) {
        this.ctotast = new ClickToast();
        View inflate = LayoutInflater.from(getApplicationContext()).inflate(R.layout.click_error_toast, null);
        inflate.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ctotast.hideCustomToast();
                showTimeDialog();
            }
        });
        this.ctotast.initToast(getApplicationContext(), inflate, "测试");
        this.ctotast.show();
    }

    private void showTimeDialog() {
        View inflate = LayoutInflater.from(getApplicationContext()).inflate(R.layout.time_dialog, null);
        this.mdlg = new CDialog(this, inflate);
        ((RadioGroup) inflate.findViewById(R.id.time_select)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.i(TAG, "showTimeDialog...:" + i);
                mdlg.dismiss();
                ctotast = null;
            }
        });
        this.mdlg.show();
    }

    private void showTimeDialog_x() {
        final String[] strArr = {"10分钟内", "20分钟内", "30分钟内", "熄火前不再提示"};
        new AlertDialog.Builder(this).setTitle("此轮胎相同警告不再提示").setSingleChoiceItems(strArr, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(TestActivity.this, strArr[i], Toast.LENGTH_LONG).show();
            }
        }).create().show();
    }

    @OnClick({R.id.btn_exchangeing})
    public void btn_exchangeing(View view) {
        Log.i(this.TAG, "time:" + (System.currentTimeMillis() / 1000));
        PAlertDialog.showDiolg(this, "");
    }

    @OnClick({R.id.btn_exchange_failed})
    public void btn_exchange_failed(View view) {
        new CDialog(this, (int) R.layout.confirm_dialog).show();
    }

    @OnClick({R.id.btn_reset_data})
    public void btn_reset_data(View view) {
        View inflate = getLayoutInflater().inflate(R.layout.reset_dialog, (ViewGroup) null);
        CDialog cDialog = new CDialog(this, inflate);
        this.resetDlg = cDialog;
        cDialog.show();
        inflate.findViewById(R.id.close_btn_ok).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                TestActivity.this.resetDlg.dismiss();
                Toast.makeText(TestActivity.this, "点击了关闭", Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick({R.id.btn_open_usb})
    public void btn_open_usb(View view) {
        this.app.getDataSrc().start();
    }

    @OnClick({R.id.btn_close_usb})
    public void btn_close_usb(View view) {
        this.app.getDataSrc().stop();
    }

    @OnClick({R.id.btn_get_px})
    public void btn_get_px(View view) {
        this.tv_screen_info.setText("");
    }

    @OnClick({R.id.btn_notif_ok})
    public void btn_notif_ok(View view) {
        this.app.getTpms().showNormalNotifMsg();
    }

    @OnClick({R.id.btn_notif_error})
    public void btn_notif_error(View view) {
        app.getTpms().showErrorNotifMsg2();
    }

    public void showNormalNotifMsg() {
        Log.i(this.TAG, "showNormalNotifMsg mNotificationState:" + this.mNotificationState);
        if (this.mNotificationState != 1) {
            this.notificationManager.cancel(1);
            new Notification(R.drawable.ic_notif_ok, "胎压", System.currentTimeMillis()).flags |= 2;
        }
    }

    public void showErrorNotifMsg() {
        Log.i(this.TAG, "showErrorNotifMsg mNotificationState:" + this.mNotificationState);
        if (this.mNotificationState != 0) {
            this.notificationManager.cancel(1);
            new Notification(R.drawable.ic_notif_error, "胎压", System.currentTimeMillis()).flags |= 2;
            PendingIntent.getActivity(this.app, 0, new Intent(this.app, TpmsMainActivity.class), 0);
        }
    }

    @OnClick({R.id.btn_play_sound})
    public void btn_play_sound(View view) {
        this.mSound.player("1");
    }

    @OnClick({R.id.btn_stop_sound})
    public void btn_stop_sound(View view) {
        this.mSound.stop("1");
    }

    @OnClick({R.id.btn_stop_data})
    public void btn_stop_data(View view) {
        this.app.stopTpms();
    }

    @OnClick({R.id.btn_start_data})
    public void btn_start_data(View view) {
        this.app.startTpms();
    }

    @OnClick({R.id.btn_error})
    public void btn_error(View view) {
        int i = 1 / 0;
    }
}
