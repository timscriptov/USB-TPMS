package com.tpms.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.text.TextUtils;

import com.syt.tmps.R;

public class SoundPoolCtrl {
    protected static int playerCount;
    private final SoundPool soundPool;
    protected Context mcont;
    String TAG = "SoundPoolCtrl";
    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int i) {
            if (i == -2) {
                Log.i(SoundPoolCtrl.this.TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
            } else if (i == -3) {
                String str = SoundPoolCtrl.this.TAG;
                Log.d(str, "有应用申请了短焦点 我压低声音  AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:" + i);
            } else if (i == 1) {
                Log.d(SoundPoolCtrl.this.TAG, "AUDIOFOCUS_GAIN");
            } else if (i == -1) {
                Log.d(SoundPoolCtrl.this.TAG, "AUDIOFOCUS_LOSS");
            } else if (i == 1) {
                Log.d(SoundPoolCtrl.this.TAG, "永久获取媒体焦点（播放音乐）现在没有播放 AUDIOFOCUS_REQUEST_GRANTED");
            } else {
                String str2 = SoundPoolCtrl.this.TAG;
                Log.i(str2, "focusChange:" + i);
            }
        }
    };
    boolean isPlayer = false;
    AudioManager mAudioM = null;
    String mGuid = "";
    private int playerId = 0;

    public SoundPoolCtrl(Context context) {
        this.mcont = context;
        SoundPool soundPool2 = new SoundPool(10, 3, 100);
        this.soundPool = soundPool2;
        soundPool2.load(context, R.raw.alarm, 1);
        this.mAudioM = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void player(String str) {
        String str2 = this.TAG;
        Log.i(str2, "player isPlayer:" + this.isPlayer + ";guid:" + str);
        if (!this.isPlayer) {
            this.playerId = this.soundPool.play(1, 15.0f, 15.0f, 1, -1, 1.0f);
            this.mGuid = str;
            this.isPlayer = true;
        }
    }

    public String getSoundGuid() {
        return this.mGuid;
    }

    public void stop(String str) {
        String str2 = this.TAG;
        Log.i(str2, "stop isPlayer:" + this.isPlayer + ";guid:" + str);
        if (this.isPlayer) {
            if (TextUtils.isEmpty(str) || str.equals(this.mGuid)) {
                try {
                    this.soundPool.stop(this.playerId);
                    this.playerId = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.isPlayer = false;
                this.mGuid = "";
            }
        }
    }
}
