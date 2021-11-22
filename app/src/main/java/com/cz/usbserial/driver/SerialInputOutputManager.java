package com.cz.usbserial.driver;

import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SerialInputOutputManager implements Runnable {
    private static final int BUFSIZ = 4096;
    private static final boolean DEBUG = true;
    private static final int READ_WAIT_MILLIS = 200;
    private static final String TAG = SerialInputOutputManager.class.getSimpleName();
    private final UsbSerialPort mDriver;
    private final ByteBuffer mReadBuffer;
    private final ByteBuffer mWriteBuffer;
    private Listener mListener;
    private State mState;

    public SerialInputOutputManager(UsbSerialPort usbSerialPort) {
        this(usbSerialPort, null);
    }

    public SerialInputOutputManager(UsbSerialPort usbSerialPort, Listener listener) {
        this.mReadBuffer = ByteBuffer.allocate(4096);
        this.mWriteBuffer = ByteBuffer.allocate(4096);
        this.mState = State.STOPPED;
        this.mDriver = usbSerialPort;
        this.mListener = listener;
    }

    public synchronized Listener getListener() {
        return this.mListener;
    }

    public synchronized void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void writeAsync(byte[] bArr) {
        synchronized (this.mWriteBuffer) {
            this.mWriteBuffer.put(bArr);
        }
    }

    public synchronized void stop() {
        if (getState() == State.RUNNING) {
            Log.i(TAG, "Stop requested");
            this.mState = State.STOPPING;
        }
    }

    private synchronized State getState() {
        return this.mState;
    }

    public void run() {
        synchronized (this) {
            if (getState() == State.STOPPED) {
                this.mState = State.RUNNING;
            } else {
                throw new IllegalStateException("Already running.");
            }
        }
        Log.i(TAG, "Running ..");
        while (getState() == State.RUNNING) {
            try {
                step();
            } catch (Exception e) {
                try {
                    Log.w(TAG, "Run ending due to exception: " + e.getMessage(), e);
                    Listener listener = getListener();
                    if (listener != null) {
                        listener.onRunError(e);
                    }
                    synchronized (this) {
                        this.mState = State.STOPPED;
                        Log.i(TAG, "Stopped.");
                        return;
                    }
                } catch (Throwable th) {
                    synchronized (this) {
                        this.mState = State.STOPPED;
                        Log.i(TAG, "Stopped.");
                        throw th;
                    }
                }
            }
        }
        Log.i(TAG, "Stopping mState=" + getState());
        synchronized (this) {
            this.mState = State.STOPPED;
            Log.i(TAG, "Stopped.");
        }
    }

    private void step() throws IOException {
        int position;
        int read = this.mDriver.read(this.mReadBuffer.array(), 200);
        if (read > 0) {
            Log.d(TAG, "Read data len=" + read);
            Listener listener = getListener();
            if (listener != null) {
                byte[] bArr = new byte[read];
                this.mReadBuffer.get(bArr, 0, read);
                listener.onNewData(bArr);
            }
            this.mReadBuffer.clear();
        }
        byte[] bArr2 = null;
        synchronized (this.mWriteBuffer) {
            position = this.mWriteBuffer.position();
            if (position > 0) {
                bArr2 = new byte[position];
                this.mWriteBuffer.rewind();
                this.mWriteBuffer.get(bArr2, 0, position);
                this.mWriteBuffer.clear();
            }
        }
        if (bArr2 != null) {
            Log.d(TAG, "Writing data len=" + position);
            this.mDriver.write(bArr2, 200);
        }
    }

    public enum State {
        STOPPED,
        RUNNING,
        STOPPING
    }

    public interface Listener {
        void onNewData(byte[] bArr);

        void onRunError(Exception exc);
    }
}
