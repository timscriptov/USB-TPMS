package com.tpms.decode;

import com.tpms.utils.Log;
import com.tpms.utils.SLOG;

import java.nio.ByteBuffer;
import java.util.Vector;

import de.greenrobot.event.EventBus;

public class PackBufferFrame {
    protected static final int MAX_NETPACKBUFFER_SIZE = 65536;
    private static final String TAG = "PackBufferFrame";
    protected static boolean DEBUG = false;
    protected byte[] m_pNetPackBuffer;
    protected int m_uCurBufferPosition;
    protected Object mutex;
    Vector<ByteBuffer> mRet;

    public PackBufferFrame() {
        this.m_uCurBufferPosition = 0;
        this.mutex = new Object();
        this.mRet = new Vector<>();
        this.m_pNetPackBuffer = new byte[81920];
        this.m_uCurBufferPosition = 0;
    }

    public void resetBufferPosition() {
        synchronized (this.mutex) {
            this.m_uCurBufferPosition = 0;
        }
    }

    public byte[] erase(byte[] bArr, int i, int i2) {
        if (DEBUG) {
            Log.i(TAG, "dellen:" + i2);
            SLOG.LogByteArr(TAG, bArr, i);
        }
        if (i <= i2) {
            return null;
        }
        int i3 = i - i2;
        byte[] bArr2 = new byte[i3];
        try {
            System.arraycopy(bArr, i2, bArr2, 0, i3);
            return bArr2;
        } catch (Exception e) {
            e.printStackTrace();
            return bArr2;
        }
    }

    public boolean calcCC(byte[] bArr) {
        if (sumCC(bArr) == bArr[Math.abs((int) bArr[3]) - 1]) {
            return true;
        }
        return false;
    }

    public byte sumCC(byte[] bArr) {
        int abs = Math.abs((int) bArr[3]);
        byte b = bArr[3];
        byte b2 = 0;
        for (int i = 0; i < abs - 1; i++) {
            b2 = (byte) (b2 + bArr[i]);
        }
        Log.i(TAG, "cc:0x" + SLOG.byteToHexString(b2));
        return b2;
    }

    public byte[] protocolFrameFilter2(byte[] bArr, int i) {
        if (bArr == null) {
            return null;
        }
        if (i < 4) {
            return returnNewBuf(bArr, i);
        }
        byte b = bArr[0];
        byte b2 = bArr[1];
        if (bArr[0] == -86) {
            byte b3 = bArr[3];
            if (b3 > i) {
                return returnNewBuf(bArr, i);
            }
            if (calcCC(bArr)) {
                ByteBuffer allocate = ByteBuffer.allocate(b3);
                this.mRet.add(allocate);
                allocate.put(bArr, 0, b3);
                byte[] erase = erase(bArr, i, b3);
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

    public byte[] returnNewBuf(byte[] bArr, int i) {
        byte[] bArr2 = new byte[i];
        try {
            System.arraycopy(bArr, 0, bArr2, 0, i);
            return bArr2;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addBuffer(byte[] bArr, int i) {
        synchronized (this.mutex) {
            if (bArr == null || i <= 0) {
                return false;
            }
            if (this.m_uCurBufferPosition + i > 65536) {
                this.m_uCurBufferPosition = 0;
                SLOG.e(TAG, "m_uCurBufferPosition > MAX_NETPACKBUFFER_SIZE");
            }
            System.arraycopy(bArr, 0, this.m_pNetPackBuffer, this.m_uCurBufferPosition, i);
            this.m_uCurBufferPosition += i;
            this.mRet.clear();
            byte[] protocolFrameFilter2 = protocolFrameFilter2(returnNewBuf(this.m_pNetPackBuffer, this.m_uCurBufferPosition), this.m_uCurBufferPosition);
            if (protocolFrameFilter2 != null) {
                System.arraycopy(protocolFrameFilter2, 0, this.m_pNetPackBuffer, 0, protocolFrameFilter2.length);
                this.m_uCurBufferPosition = protocolFrameFilter2.length;
            } else {
                this.m_uCurBufferPosition = 0;
            }
            for (int i2 = 0; i2 < this.mRet.size(); i2++) {
                EventBus.getDefault().post(this.mRet.get(i2));
            }
            return true;
        }
    }
}
