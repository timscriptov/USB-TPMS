package com.std.dev;

import com.syt.tmps.TpmsApplication;
import com.tpms.decode.PackBufferFrame;

public class TpmsDataSrc {
    public static final int MESSAGE_DATA_PROC = 1;
    protected PackBufferFrame BufferFrame = null;
    boolean DEBUG = true;
    private final String TAG = "TpmsDataSrc";
    protected TpmsApplication theapp;

    public String getDevName() {
        return "";
    }

    public void init() {
    }

    public void start() {
    }

    public void stop() {
    }

    public void writeData(byte[] bArr) {
    }

    public TpmsDataSrc(TpmsApplication tpmsApplication) {
        this.theapp = tpmsApplication;
    }

    public void setBufferFrame(PackBufferFrame packBufferFrame) {
        this.BufferFrame = packBufferFrame;
    }

    public void testAddBuf(byte[] bArr) {
        this.BufferFrame.addBuffer(bArr, bArr.length);
    }
}
