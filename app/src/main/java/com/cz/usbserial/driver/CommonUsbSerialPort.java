package com.cz.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import java.io.IOException;

abstract class CommonUsbSerialPort implements UsbSerialPort {
    public static final int DEFAULT_READ_BUFFER_SIZE = 16384;
    public static final int DEFAULT_WRITE_BUFFER_SIZE = 16384;
    protected UsbDeviceConnection mConnection = null;
    protected final UsbDevice mDevice;
    protected final int mPortNumber;
    protected byte[] mReadBuffer;
    protected final Object mReadBufferLock = new Object();
    protected byte[] mWriteBuffer;
    protected final Object mWriteBufferLock = new Object();

    @Override // com.cz.usbserial.driver.UsbSerialPort
    public abstract void close() throws IOException;

    @Override // com.cz.usbserial.driver.UsbSerialPort
    public abstract boolean getCD() throws IOException;

    @Override // com.cz.usbserial.driver.UsbSerialPort
    public abstract boolean getCTS() throws IOException;

    @Override // com.cz.usbserial.driver.UsbSerialPort
    public abstract boolean getDSR() throws IOException;

    @Override // com.cz.usbserial.driver.UsbSerialPort
    public abstract boolean getDTR() throws IOException;

    @Override // com.cz.usbserial.driver.UsbSerialPort
    public abstract boolean getRI() throws IOException;

    @Override // com.cz.usbserial.driver.UsbSerialPort
    public abstract boolean getRTS() throws IOException;

    @Override // com.cz.usbserial.driver.UsbSerialPort
    public abstract void open(UsbDeviceConnection usbDeviceConnection) throws IOException;

    @Override // com.cz.usbserial.driver.UsbSerialPort
    public boolean purgeHwBuffers(boolean z, boolean z2) throws IOException {
        return !z && !z2;
    }

    @Override // com.cz.usbserial.driver.UsbSerialPort
    public abstract int read(byte[] bArr, int i) throws IOException;

    @Override // com.cz.usbserial.driver.UsbSerialPort
    public abstract void setDTR(boolean z) throws IOException;

    @Override // com.cz.usbserial.driver.UsbSerialPort
    public abstract void setParameters(int i, int i2, int i3, int i4) throws IOException;

    @Override // com.cz.usbserial.driver.UsbSerialPort
    public abstract void setRTS(boolean z) throws IOException;

    @Override // com.cz.usbserial.driver.UsbSerialPort
    public abstract int write(byte[] bArr, int i) throws IOException;

    public CommonUsbSerialPort(UsbDevice usbDevice, int i) {
        this.mDevice = usbDevice;
        this.mPortNumber = i;
        this.mReadBuffer = new byte[16384];
        this.mWriteBuffer = new byte[16384];
    }

    public String toString() {
        return String.format("<%s device_name=%s device_id=%s port_number=%s>", getClass().getSimpleName(), this.mDevice.getDeviceName(), Integer.valueOf(this.mDevice.getDeviceId()), Integer.valueOf(this.mPortNumber));
    }

    public final UsbDevice getDevice() {
        return this.mDevice;
    }

    @Override // com.cz.usbserial.driver.UsbSerialPort
    public int getPortNumber() {
        return this.mPortNumber;
    }

    @Override // com.cz.usbserial.driver.UsbSerialPort
    public String getSerial() {
        return this.mConnection.getSerial();
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
