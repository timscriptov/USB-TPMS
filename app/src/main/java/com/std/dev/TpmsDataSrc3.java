package com.std.dev;

import com.syt.tmps.TpmsApplication;
import com.tpms.decode.PackBufferFrame;
import com.tpms.utils.SLOG;

import java.io.File;
import java.io.IOException;

public class TpmsDataSrc3 extends TpmsDataSrc {
    private final String TAG = "TpmsDataSrc3";
    protected ReadThread mReadThread;
    Serialport mPort;

    public TpmsDataSrc3(TpmsApplication tpmsApplication) {
        super(tpmsApplication);
    }

    @Override
    public void init() {
        try {
            this.mPort = new Serialport(new File("/dev/ttyS1"), 19200, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setBufferFrame(PackBufferFrame packBufferFrame) {
        this.BufferFrame = packBufferFrame;
    }

    @Override
    public void writeData(byte[] bArr) {
        try {
            this.mPort.write(bArr, bArr.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        startReadThread();
    }

    @Override
    public void stop() {
        ReadThread readThread = this.mReadThread;
        if (readThread != null) {
            readThread.interrupt();
            this.mReadThread = null;
            Serialport serialport = this.mPort;
            if (serialport != null) {
                serialport.close();
                this.mPort = null;
            }
        }
    }

    private void startReadThread() {
        if (this.mReadThread == null) {
            try {
                ReadThread readThread = new ReadThread();
                this.mReadThread = readThread;
                readThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ReadThread extends Thread {
        private ReadThread() {
        }

        public void run() {
            super.run();
            byte[] bArr = new byte[512];
            while (!isInterrupted()) {
                try {
                    int read = mPort.read(bArr);
                    if (read == 0) {
                        try {
                            Thread.sleep(20);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        SLOG.LogByteArr(TAG + "read", bArr, read);
                        byte[] bArr2 = new byte[read];
                        System.arraycopy(bArr, 0, bArr2, 0, read);
                        BufferFrame.addBuffer(bArr2, read);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}
