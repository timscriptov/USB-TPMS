package com.cz.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;

abstract class CommonUsbSerialDriver implements UsbSerialDriver {
    public static final int DEFAULT_READ_BUFFER_SIZE = 16384;
    public static final int DEFAULT_WRITE_BUFFER_SIZE = 16384;
    protected final UsbDeviceConnection mConnection;
    protected final UsbDevice mDevice;
    protected byte[] mReadBuffer;
    protected final Object mReadBufferLock = new Object();
    protected byte[] mWriteBuffer;
    protected final Object mWriteBufferLock = new Object();

    public CommonUsbSerialDriver(UsbDevice usbDevice, UsbDeviceConnection usbDeviceConnection) {
        this.mDevice = usbDevice;
        this.mConnection = usbDeviceConnection;
        this.mReadBuffer = new byte[16384];
        this.mWriteBuffer = new byte[16384];
    }

    @Override // com.cz.usbserial.driver.UsbSerialDriver
    public final UsbDevice getDevice() {
        return this.mDevice;
    }

    public final void setReadBufferSize(int i) {
        synchronized (this.mReadBufferLock) {
            if (i != this.mReadBuffer.length) {
                this.mReadBuffer = new byte[i];
            }
        }
    }

    public final void setWriteBufferSize(int i) {
        synchronized (this.mWriteBufferLock) {
            if (i != this.mWriteBuffer.length) {
                this.mWriteBuffer = new byte[i];
            }
        }
    }
}
