package com.tpms.encode;

import com.mcal.tmps.TpmsApplication;
import com.tpms.utils.Log;
import com.tpms.utils.SLOG;

public class PackBufferFrameEn {
    String TAG = PackBufferFrameEn.class.getSimpleName();
    TpmsApplication theApp;

    public PackBufferFrameEn(TpmsApplication tpmsApplication) {
        this.theApp = tpmsApplication;
    }

    public void send(byte[] bArr) {
        bArr[bArr.length - 1] = calcCC(bArr);
        SLOG.LogByteArr(this.TAG + "write", bArr, bArr.length);
        this.theApp.getDataSrc().writeData(bArr);
    }

    public byte calcCC(byte[] bArr) {
        int abs = Math.abs((int) bArr[3]);
        byte b = bArr[3];
        byte b2 = 0;
        for (int i = 0; i < abs - 1; i++) {
            b2 = (byte) (b2 + bArr[i]);
        }
        String str = this.TAG;
        Log.i(str, "cc:" + ((int) b2));
        return b2;
    }
}
