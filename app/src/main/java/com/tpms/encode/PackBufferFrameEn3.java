package com.tpms.encode;

import com.mcal.tmps.TpmsApplication;
import com.tpms.utils.Log;
import com.tpms.utils.SLOG;

public class PackBufferFrameEn3 extends PackBufferFrameEn {
    String TAG = PackBufferFrameEn3.class.getSimpleName();

    public PackBufferFrameEn3(TpmsApplication tpmsApplication) {
        super(tpmsApplication);
    }

    @Override
    public byte calcCC(byte[] bArr) {
        byte b = bArr[2];
        byte b2 = bArr[0];
        for (int i = 1; i < b - 1; i++) {
            b2 = (byte) (b2 ^ bArr[i]);
        }
        String str = this.TAG;
        Log.i(str, "cc:" + SLOG.byteToHexString(b2));
        return b2;
    }
}
