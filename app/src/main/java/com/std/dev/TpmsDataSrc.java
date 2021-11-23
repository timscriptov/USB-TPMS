package com.std.dev;

import com.mcal.tmps.TpmsApplication;
import com.tpms.decode.PackBufferFrame;

public class TpmsDataSrc {
    public static final int MESSAGE_DATA_PROC = 1;
    private final String TAG = "TpmsDataSrc";
    protected PackBufferFrame BufferFrame = null;
    protected TpmsApplication theapp;
    boolean DEBUG = true;

    public TpmsDataSrc(TpmsApplication tpmsApplication) {
        this.theapp = tpmsApplication;
    }

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

    public void setBufferFrame(PackBufferFrame packBufferFrame) {
        this.BufferFrame = packBufferFrame;
    }

    public void testAddBuf(byte[] bArr) {
        this.BufferFrame.addBuffer(bArr, bArr.length);
    }
}
