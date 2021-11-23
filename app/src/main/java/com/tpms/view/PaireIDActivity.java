package com.tpms.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.std.dev.TpmsDataSrc;
import com.mcal.tmps.R;
import com.mcal.tmps.TpmsApplication;
import com.tpms.modle.PaireIDOkEvent;
import com.tpms.modle.QueryIDOkEvent;
import com.tpms.utils.Log;

import de.greenrobot.event.EventBus;

public class PaireIDActivity extends Activity {
    TpmsApplication app = null;
    @ViewInject(R.id.btn_paire_canel)
    Button btn_paire_canel;
    @ViewInject(R.id.btn_paire_start)
    Button btn_paire_start;
    TpmsDataSrc datasrc = null;
    @ViewInject(R.id.ib_left_back_id)
    ImageButton ib_left_back_id;
    @ViewInject(R.id.ib_left_front_id)
    ImageButton ib_left_front_id;
    @ViewInject(R.id.ib_right_back_id)
    ImageButton ib_right_back_id;
    @ViewInject(R.id.ib_right_front_id)
    ImageButton ib_right_front_id;
    View mImgBtn;
    int mTimeOut = 120;
    @ViewInject(R.id.progressBar1)
    ProgressBar progressBar1;
    Handler timeOut;
    @ViewInject(R.id.tires_container)
    LinearLayout tires_container;
    @ViewInject(R.id.tv_left_back_id)
    TextView tv_left_back_id;
    @ViewInject(R.id.tv_left_front_id)
    TextView tv_left_front_id;
    @ViewInject(R.id.tv_right_back_id)
    TextView tv_right_back_id;
    @ViewInject(R.id.tv_right_front_id)
    TextView tv_right_front_id;
    @ViewInject(R.id.tv_sptires_id)
    TextView tv_sptires_id;
    @ViewInject(R.id.tv_title_state)
    TextView tv_title_state;
    private final String TAG = "PaireIDActivity";
    Runnable timeOutCnt = new Runnable() {
        public void run() {
            if (mTimeOut <= 0) {
                timeOut.removeCallbacks(timeOutCnt);
                PaireIDActivity paireIDActivity = PaireIDActivity.this;
                paireIDActivity.btn_paire_canel(paireIDActivity.btn_paire_canel);
                btn_paire_start.setVisibility(View.GONE);
                tv_title_state.setText(R.string.dianjikaishianniujintupeidui);
                return;
            }
            TextView textView = tv_title_state;
            StringBuilder sb = new StringBuilder();
            sb.append(PaireIDActivity.this.getString(R.string.zhengzaipeidui));
            PaireIDActivity paireIDActivity2 = PaireIDActivity.this;
            int i = paireIDActivity2.mTimeOut;
            paireIDActivity2.mTimeOut = i - 1;
            sb.append(i);
            textView.setText(sb.toString());
            timeOut.postDelayed(timeOutCnt, 1000);
        }
    };

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_paire_id);
        ViewUtils.inject(this);
        EventBus.getDefault().register(this);
        TpmsApplication tpmsApplication = (TpmsApplication) getApplication();
        this.app = tpmsApplication;
        this.datasrc = tpmsApplication.getDataSrc();
        this.app.getTpms().querySensorID();
        this.btn_paire_start.setVisibility(View.INVISIBLE);
        this.btn_paire_canel.setVisibility(View.GONE);
        this.timeOut = new Handler();
        if (!this.app.getTpms().getSparetireEnable()) {
            this.tv_sptires_id.setVisibility(View.INVISIBLE);
        } else {
            this.tv_sptires_id.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.ib_left_front_id})
    public void ib_left_front_id(View view) {
        setSelectButton(view);
    }

    @OnClick({R.id.ib_right_front_id})
    public void ib_right_front_id(View view) {
        setSelectButton(view);
    }

    @OnClick({R.id.ib_right_back_id})
    public void ib_right_back_id(View view) {
        setSelectButton(view);
    }

    @OnClick({R.id.ib_left_back_id})
    public void ib_left_back_id(View view) {
        setSelectButton(view);
    }

    @OnClick({R.id.tv_sptires_id})
    public void tv_sptires_id(View view) {
        Log.i(this.TAG, "tv_sptires_id");
        setSelectButton(view);
    }

    private void setSelectButton(View view) {
        if (this.btn_paire_canel.getVisibility() == View.VISIBLE) {
            btn_paire_canel(this.btn_paire_canel);
            return;
        }
        View view2 = this.mImgBtn;
        if (view2 != null) {
            view2.getBackground().setLevel(0);
        }
        this.mImgBtn = view;
        view.getBackground().setLevel(1);
        this.btn_paire_start.setVisibility(View.VISIBLE);
        this.btn_paire_canel.setVisibility(View.GONE);
        this.tv_title_state.setText(getString(R.string.dianjikaishianniujintupeidui));
    }

    @OnClick({R.id.btn_paire_canel})
    public void btn_paire_canel(View view) {
        this.btn_paire_start.setVisibility(View.INVISIBLE);
        this.btn_paire_canel.setVisibility(View.GONE);
        Log.i(this.TAG, "btn_paire_canel");
        this.tv_title_state.setText(R.string.qinxuanzeyaopeiduideluntai);
        this.progressBar1.setVisibility(View.GONE);
        this.app.getTpms().stopPaire();
        this.timeOut.removeCallbacks(this.timeOutCnt);
        View view2 = this.mImgBtn;
        if (view2 != null) {
            view2.getBackground().setLevel(0);
        }
        this.tires_container.setVisibility(View.GONE);
    }

    @OnClick({R.id.btn_paire_start})
    public void btn_paire_start(View view) {
        View view2 = this.mImgBtn;
        if (view2 != null) {
            int id = view2.getId();
            if (id != R.id.tv_sptires_id) {
                switch (id) {
                    case R.id.ib_left_back_id:
                        this.app.getTpms().paireBackLeft();
                        break;
                    case R.id.ib_left_front_id:
                        this.app.getTpms().paireFrontLeft();
                        break;
                    case R.id.ib_right_back_id:
                        this.app.getTpms().paireBackRight();
                        break;
                    case R.id.ib_right_front_id:
                        this.app.getTpms().paireFrontRight();
                        break;
                }
            } else {
                this.app.getTpms().paireSpTired();
            }
            this.btn_paire_canel.setVisibility(View.VISIBLE);
            this.btn_paire_start.setVisibility(View.GONE);
            TextView textView = this.tv_title_state;
            textView.setText(getString(R.string.zhengzaipeidui) + mTimeOut);
            this.progressBar1.setVisibility(View.VISIBLE);
            this.mTimeOut = 120;
            this.timeOut.postDelayed(this.timeOutCnt, 1000);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        Log.i(this.TAG, "onDestroy onDestroy");
        EventBus.getDefault().unregister(this);
        this.app.getTpms().stopPaire();
    }

    public void onStop() {
        Log.i(this.TAG, "onStop onStop");
        this.app.getTpms().stopPaire();
        super.onStop();
    }

    public void onEventMainThread(PaireIDOkEvent paireIDOkEvent) {
        Log.w(this.TAG, "收到了配对也就是学习到了ID:" + paireIDOkEvent.tires + ";mac:" + paireIDOkEvent.mID);
        if (paireIDOkEvent.tires == 1) {
            this.ib_left_front_id.getBackground().setLevel(0);
        } else if (paireIDOkEvent.tires == 2) {
            this.ib_right_front_id.getBackground().setLevel(0);
        } else if (paireIDOkEvent.tires == 3) {
            this.ib_right_back_id.getBackground().setLevel(0);
        } else if (paireIDOkEvent.tires == 0) {
            this.ib_left_back_id.getBackground().setLevel(0);
        } else if (paireIDOkEvent.tires == 5) {
            this.tv_sptires_id.getBackground().setLevel(0);
        }
        this.app.getTpms().querySensorID();
        View view = this.mImgBtn;
        if (view != null) {
            view.getBackground().setLevel(2);
        }
        if (this.btn_paire_canel.getVisibility() != View.GONE) {
            Toast.makeText(this, getString(R.string.xuexichenggong), Toast.LENGTH_LONG).show();
        }
        btn_paire_canel(this.btn_paire_canel);
    }

    @SuppressLint("SetTextI18n")
    public void onEventMainThread(QueryIDOkEvent queryIDOkEvent) {
        Log.i(this.TAG, "收到了查寻ID:" + queryIDOkEvent.tires + ";mac:" + queryIDOkEvent.mID);
        if (queryIDOkEvent.tires == 1) {
            Log.i("test", "查到 左前id:" + queryIDOkEvent.mID);
            TextView textView = this.tv_left_front_id;
            textView.setText("ID:" + queryIDOkEvent.mID);
            this.ib_left_front_id.getBackground().setLevel(0);
        } else if (queryIDOkEvent.tires == 2) {
            Log.i("test", "查到 右前id:" + queryIDOkEvent.mID);
            TextView textView2 = this.tv_right_front_id;
            textView2.setText("ID:" + queryIDOkEvent.mID);
            this.ib_right_front_id.getBackground().setLevel(0);
        } else if (queryIDOkEvent.tires == 3) {
            Log.i("test", "查到 右后id:" + queryIDOkEvent.mID);
            TextView textView3 = this.tv_right_back_id;
            textView3.setText("ID:" + queryIDOkEvent.mID);
            this.ib_right_back_id.getBackground().setLevel(0);
        } else if (queryIDOkEvent.tires == 0) {
            Log.i("test", "查到 左后id:" + queryIDOkEvent.mID);
            TextView textView4 = this.tv_left_back_id;
            textView4.setText("ID:" + queryIDOkEvent.mID);
            this.ib_left_back_id.getBackground().setLevel(0);
        } else if (queryIDOkEvent.tires == 5) {
            Log.i("test", "查到 备胎id:" + queryIDOkEvent.mID);
            TextView textView5 = this.tv_sptires_id;
            textView5.setText("ID:" + queryIDOkEvent.mID);
            this.tv_sptires_id.getBackground().setLevel(0);
        }
    }

    @OnClick({R.id.back_ui})
    public void back_ui(View view) {
        finish();
    }

    @OnClick({R.id.view_plane})
    public void view_plane(View view) {
        Log.i(this.TAG, "遮罩层，屏蔽下层的点击事件");
    }
}
