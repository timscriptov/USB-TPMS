package com.tpms.decode;

import com.tpms.utils.SLOG;

import java.nio.ByteBuffer;

public class PackBufferFrame3 extends PackBufferFrame {
    private static final String TAG = "PackBufferFrame3";

    @Override
    public boolean calcCC(byte[] bArr) {
        byte b = bArr[2];
        if (b == 0) {
            SLOG.LogByteArr("PackBufferFrame3ERR", bArr, bArr.length);
            return false;
        } else if (sumCC(bArr) == bArr[b - 1]) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public byte sumCC(byte[] bArr) {
        byte b = bArr[2];
        byte b2 = bArr[0];
        for (int i = 1; i < b - 1; i++) {
            b2 = (byte) (b2 ^ bArr[i]);
        }
        return b2;
    }

    @Override
    public byte[] protocolFrameFilter2(byte[] bArr, int i) {
        if (bArr == null) {
            return null;
        }
        if (i < 3) {
            return returnNewBuf(bArr, i);
        }
        if (bArr[0] == 85 && bArr[1] == -86) {
            byte b = bArr[2];
            if (b > i) {
                return returnNewBuf(bArr, i);
            }
            if (calcCC(bArr)) {
                ByteBuffer allocate = ByteBuffer.allocate(b);
                this.mRet.add(allocate);
                allocate.put(bArr, 0, b);
                byte[] erase = erase(bArr, i, b);
                if (erase == null) {
                    return null;
                }
                return protocolFrameFilter2(erase, erase.length);
            }
            byte[] erase2 = erase(bArr, i, 1);
            if (erase2 == null) {
                return null;
            }
            return protocolFrameFilter2(erase2, erase2.length);
        }
        byte[] erase3 = erase(bArr, i, 1);
        if (erase3 == null) {
            return null;
        }
        return protocolFrameFilter2(erase3, erase3.length);
    }
}
