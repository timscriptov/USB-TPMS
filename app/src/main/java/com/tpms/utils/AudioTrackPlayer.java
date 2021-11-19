package com.tpms.utils;

import android.media.AudioTrack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioTrackPlayer {
    private byte[] audioData;
    private AudioTrack audioTrack = new AudioTrack(3, 44100, 12, 2, AudioTrack.getMinBufferSize(44100, 12, 2), 0);

    public AudioTrackPlayer() {
        releaseAudioTrack();
    }

    private void releaseAudioTrack() {
        AudioTrack audioTrack2 = this.audioTrack;
        if (audioTrack2 != null) {
            audioTrack2.stop();
            this.audioTrack.release();
            this.audioTrack = null;
        }
    }

    public boolean isPlaying() {
        return this.audioTrack.getPlayState() == 3;
    }

    public void load(InputStream inputStream) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(264848);
            while (true) {
                int read = inputStream.read();
                if (read == -1) {
                    break;
                }
                byteArrayOutputStream.write(read);
            }
            this.audioData = byteArrayOutputStream.toByteArray();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable th) {
            //inputStream.close();
            throw th;
        }
        AudioTrack audioTrack2 = this.audioTrack;
        byte[] bArr = this.audioData;
        audioTrack2.write(bArr, 0, bArr.length);
    }

    public void start() {
        AudioTrack audioTrack2 = this.audioTrack;
        byte[] bArr = this.audioData;
        audioTrack2.write(bArr, 0, bArr.length);
        this.audioTrack.play();
    }

    public void pause() {
        this.audioTrack.pause();
    }
}
