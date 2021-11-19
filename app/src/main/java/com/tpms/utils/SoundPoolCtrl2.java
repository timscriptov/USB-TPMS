package com.tpms.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;

import com.syt.tmps.R;

public class SoundPoolCtrl2 extends SoundPoolCtrl {
    String TAG = "difengze.com-SoundPoolCtrl2";
    MediaPlayer mediaPlayer;

    public SoundPoolCtrl2(Context context) {
        super(context);
        if (this.mediaPlayer == null) {
            this.mediaPlayer = MediaPlayer.create(context, (int) R.raw.alarm);
        }
    }

    @Override
    public void player(String str) {
        Log.i(this.TAG, "player isPlayer:" + this.isPlayer + ";guid:" + str);
        if (this.isPlayer) {
            playerCount++;
            if (playerCount % 10 == 0) {
                stopPlayer();
                startPlayer();
                return;
            }
            return;
        }
        startPlayer();
        this.mGuid = str;
        this.isPlayer = true;
    }

    private void startPlayer() {
        this.mediaPlayer.start();
        this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (!SoundPoolCtrl2.this.isPlayer) {
                    Log.i(SoundPoolCtrl2.this.TAG, "is over");
                    return;
                }
                SoundPoolCtrl2.this.mediaPlayer.start();
                SoundPoolCtrl2.this.mediaPlayer.setLooping(true);
            }
        });
    }

    @Override
    public void stop(String str) {
        String str2 = this.TAG;
        Log.i(str2, "stop isPlayer:" + this.isPlayer + ";guid:" + str);
        if (this.isPlayer) {
            if (TextUtils.isEmpty(str) || str.equals(this.mGuid)) {
                stopPlayer();
                this.mGuid = "";
            }
        }
    }

    private void stopPlayer() {
        this.isPlayer = false;
        try {
            this.mediaPlayer.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
