package com.cz.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProlificSerialDriver implements UsbSerialDriver {
    private final String TAG = ProlificSerialDriver.class.getSimpleName();
    private final UsbDevice mDevice;
    private final UsbSerialPort mPort;

    public ProlificSerialDriver(UsbDevice usbDevice) {
        this.mDevice = usbDevice;
        this.mPort = new ProlificSerialPort(usbDevice, 0);
    }

    @Override // com.cz.usbserial.driver.UsbSerialDriver
    public List<UsbSerialPort> getPorts() {
        return Collections.singletonList(this.mPort);
    }

    @Override // com.cz.usbserial.driver.UsbSerialDriver
    public UsbDevice getDevice() {
        return this.mDevice;
    }

    class ProlificSerialPort extends CommonUsbSerialPort {
        private static final int CONTROL_DTR = 1;
        private static final int CONTROL_RTS = 2;
        private static final int DEVICE_TYPE_0 = 1;
        private static final int DEVICE_TYPE_1 = 2;
        private static final int DEVICE_TYPE_HX = 0;
        private static final int FLUSH_RX_REQUEST = 8;
        private static final int FLUSH_TX_REQUEST = 9;
        private static final int INTERRUPT_ENDPOINT = 129;
        private static final int PROLIFIC_CTRL_OUT_REQTYPE = 33;
        private static final int PROLIFIC_VENDOR_IN_REQTYPE = 192;
        private static final int PROLIFIC_VENDOR_OUT_REQTYPE = 64;
        private static final int PROLIFIC_VENDOR_READ_REQUEST = 1;
        private static final int PROLIFIC_VENDOR_WRITE_REQUEST = 1;
        private static final int READ_ENDPOINT = 131;
        private static final int SET_CONTROL_REQUEST = 34;
        private static final int SET_LINE_REQUEST = 32;
        private static final int STATUS_BUFFER_SIZE = 10;
        private static final int STATUS_BYTE_IDX = 8;
        private static final int STATUS_FLAG_CD = 1;
        private static final int STATUS_FLAG_CTS = 128;
        private static final int STATUS_FLAG_DSR = 2;
        private static final int STATUS_FLAG_RI = 8;
        private static final int USB_READ_TIMEOUT_MILLIS = 1000;
        private static final int USB_RECIP_INTERFACE = 1;
        private static final int USB_WRITE_TIMEOUT_MILLIS = 5000;
        private static final int WRITE_ENDPOINT = 2;
        private int mBaudRate = -1;
        private int mControlLinesValue = 0;
        private int mDataBits = -1;
        private int mDeviceType = 0;
        private UsbEndpoint mInterruptEndpoint;
        private int mParity = -1;
        private UsbEndpoint mReadEndpoint;
        private IOException mReadStatusException = null;
        private volatile Thread mReadStatusThread = null;
        private final Object mReadStatusThreadLock = new Object();
        private int mStatus = 0;
        private int mStopBits = -1;
        boolean mStopReadStatusThread = false;
        private UsbEndpoint mWriteEndpoint;

        public ProlificSerialPort(UsbDevice usbDevice, int i) {
            super(usbDevice, i);
        }

        @Override // com.cz.usbserial.driver.UsbSerialPort
        public UsbSerialDriver getDriver() {
            return ProlificSerialDriver.this;
        }

        private final byte[] inControlTransfer(int i, int i2, int i3, int i4, int i5) throws IOException {
            byte[] bArr = new byte[i5];
            int controlTransfer = this.mConnection.controlTransfer(i, i2, i3, i4, bArr, i5, 1000);
            if (controlTransfer == i5) {
                return bArr;
            }
            throw new IOException(String.format("ControlTransfer with value 0x%x failed: %d", Integer.valueOf(i3), Integer.valueOf(controlTransfer)));
        }

        private final void outControlTransfer(int i, int i2, int i3, int i4, byte[] bArr) throws IOException {
            int length = bArr == null ? 0 : bArr.length;
            int controlTransfer = this.mConnection.controlTransfer(i, i2, i3, i4, bArr, length, 5000);
            if (controlTransfer != length) {
                throw new IOException(String.format("ControlTransfer with value 0x%x failed: %d", Integer.valueOf(i3), Integer.valueOf(controlTransfer)));
            }
        }

        private final byte[] vendorIn(int i, int i2, int i3) throws IOException {
            return inControlTransfer(192, 1, i, i2, i3);
        }

        private final void vendorOut(int i, int i2, byte[] bArr) throws IOException {
            outControlTransfer(64, 1, i, i2, bArr);
        }

        private void resetDevice() throws IOException {
            purgeHwBuffers(true, true);
        }

        private final void ctrlOut(int i, int i2, int i3, byte[] bArr) throws IOException {
            outControlTransfer(33, i, i2, i3, bArr);
        }

        private void doBlackMagic() throws IOException {
            vendorIn(33924, 0, 1);
            vendorOut(1028, 0, null);
            vendorIn(33924, 0, 1);
            vendorIn(33667, 0, 1);
            vendorIn(33924, 0, 1);
            vendorOut(1028, 1, null);
            vendorIn(33924, 0, 1);
            vendorIn(33667, 0, 1);
            vendorOut(0, 1, null);
            vendorOut(1, 0, null);
            vendorOut(2, this.mDeviceType == 0 ? 68 : 36, null);
        }

        private void setControlLines(int i) throws IOException {
            ctrlOut(34, i, 0, null);
            this.mControlLinesValue = i;
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private final void readStatusThreadFunction() {
            while (!this.mStopReadStatusThread) {
                try {
                    byte[] bArr = new byte[10];
                    int bulkTransfer = this.mConnection.bulkTransfer(this.mInterruptEndpoint, bArr, 10, 500);
                    if (bulkTransfer > 0) {
                        if (bulkTransfer == 10) {
                            this.mStatus = bArr[8] & 255;
                        } else {
                            throw new IOException(String.format("Invalid CTS / DSR / CD / RI status buffer received, expected %d bytes, but received %d", 10, Integer.valueOf(bulkTransfer)));
                        }
                    }
                } catch (IOException e) {
                    this.mReadStatusException = e;
                    return;
                }
            }
        }

        private final int getStatus() throws IOException {
            if (this.mReadStatusThread == null && this.mReadStatusException == null) {
                synchronized (this.mReadStatusThreadLock) {
                    if (this.mReadStatusThread == null) {
                        byte[] bArr = new byte[10];
                        if (this.mConnection.bulkTransfer(this.mInterruptEndpoint, bArr, 10, 100) != 10) {
                            Log.w(ProlificSerialDriver.this.TAG, "Could not read initial CTS / DSR / CD / RI status");
                        } else {
                            this.mStatus = bArr[8] & 255;
                        }
                        this.mReadStatusThread = new Thread(new Runnable() {
                            /* class com.cz.usbserial.driver.ProlificSerialDriver.ProlificSerialPort.AnonymousClass1 */

                            public void run() {
                                ProlificSerialPort.this.readStatusThreadFunction();
                            }
                        });
                        this.mReadStatusThread.setDaemon(true);
                        this.mReadStatusThread.start();
                    }
                }
            }
            IOException iOException = this.mReadStatusException;
            if (iOException == null) {
                return this.mStatus;
            }
            this.mReadStatusException = null;
            throw iOException;
        }

        private final boolean testStatusFlag(int i) throws IOException {
            return (getStatus() & i) == i;
        }

        @Override // com.cz.usbserial.driver.CommonUsbSerialPort, com.cz.usbserial.driver.UsbSerialPort
        public void open(UsbDeviceConnection usbDeviceConnection) throws IOException {
            if (this.mConnection == null) {
                UsbInterface usbInterface = this.mDevice.getInterface(0);
                if (usbDeviceConnection.claimInterface(usbInterface, true)) {
                    this.mConnection = usbDeviceConnection;
                    for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                        try {
                            UsbEndpoint endpoint = usbInterface.getEndpoint(i);
                            int address = endpoint.getAddress();
                            if (address == 2) {
                                this.mWriteEndpoint = endpoint;
                            } else if (address == INTERRUPT_ENDPOINT) {
                                this.mInterruptEndpoint = endpoint;
                            } else if (address == READ_ENDPOINT) {
                                this.mReadEndpoint = endpoint;
                            }
                        } catch (Throwable th) {
                            this.mConnection = null;
                            usbDeviceConnection.releaseInterface(usbInterface);
                            throw th;
                        }
                    }
                    if (this.mDevice.getDeviceClass() == 2) {
                        this.mDeviceType = 1;
                    } else {
                        try {
                            if (((byte[]) this.mConnection.getClass().getMethod("getRawDescriptors", new Class[0]).invoke(this.mConnection, new Object[0]))[7] == 64) {
                                this.mDeviceType = 0;
                            } else {
                                if (this.mDevice.getDeviceClass() != 0) {
                                    if (this.mDevice.getDeviceClass() != 255) {
                                        Log.w(ProlificSerialDriver.this.TAG, "Could not detect PL2303 subtype, Assuming that it is a HX device");
                                        this.mDeviceType = 0;
                                    }
                                }
                                this.mDeviceType = 2;
                            }
                        } catch (NoSuchMethodException unused) {
                            Log.w(ProlificSerialDriver.this.TAG, "Method UsbDeviceConnection.getRawDescriptors, required for PL2303 subtype detection, not available! Assuming that it is a HX device");
                            this.mDeviceType = 0;
                        } catch (Exception e) {
                            Log.e(ProlificSerialDriver.this.TAG, "An unexpected exception occured while trying to detect PL2303 subtype", e);
                        }
                    }
                    setControlLines(this.mControlLinesValue);
                    resetDevice();
                    doBlackMagic();
                    return;
                }
                throw new IOException("Error claiming Prolific interface 0");
            }
            throw new IOException("Already open");
        }

        @Override // com.cz.usbserial.driver.CommonUsbSerialPort, com.cz.usbserial.driver.UsbSerialPort
        public void close() throws IOException {
            if (this.mConnection != null) {
                try {
                    this.mStopReadStatusThread = true;
                    synchronized (this.mReadStatusThreadLock) {
                        if (this.mReadStatusThread != null) {
                            try {
                                this.mReadStatusThread.join();
                            } catch (Exception e) {
                                Log.w(ProlificSerialDriver.this.TAG, "An error occured while waiting for status read thread", e);
                            }
                        }
                    }
                    resetDevice();
                    try {
                        this.mConnection.releaseInterface(this.mDevice.getInterface(0));
                    } finally {
                        this.mConnection = null;
                    }
                } catch (Throwable th) {
                    this.mConnection.releaseInterface(this.mDevice.getInterface(0));
                    throw th;
                } finally {
                    this.mConnection = null;
                }
            } else {
                throw new IOException("Already closed");
            }
        }

        @Override // com.cz.usbserial.driver.CommonUsbSerialPort, com.cz.usbserial.driver.UsbSerialPort
        public int read(byte[] bArr, int i) throws IOException {
            synchronized (this.mReadBufferLock) {
                int bulkTransfer = this.mConnection.bulkTransfer(this.mReadEndpoint, this.mReadBuffer, Math.min(bArr.length, this.mReadBuffer.length), i);
                if (bulkTransfer < 0) {
                    return 0;
                }
                System.arraycopy(this.mReadBuffer, 0, bArr, 0, bulkTransfer);
                return bulkTransfer;
            }
        }

        @Override // com.cz.usbserial.driver.CommonUsbSerialPort, com.cz.usbserial.driver.UsbSerialPort
        public int write(byte[] bArr, int i) throws IOException {
            int min;
            byte[] bArr2;
            int bulkTransfer;
            int i2 = 0;
            while (i2 < bArr.length) {
                synchronized (this.mWriteBufferLock) {
                    min = Math.min(bArr.length - i2, this.mWriteBuffer.length);
                    if (i2 == 0) {
                        bArr2 = bArr;
                    } else {
                        System.arraycopy(bArr, i2, this.mWriteBuffer, 0, min);
                        bArr2 = this.mWriteBuffer;
                    }
                    bulkTransfer = this.mConnection.bulkTransfer(this.mWriteEndpoint, bArr2, min, i);
                }
                if (bulkTransfer > 0) {
                    i2 += bulkTransfer;
                } else {
                    throw new IOException("Error writing " + min + " bytes at offset " + i2 + " length=" + bArr.length);
                }
            }
            return i2;
        }

        @Override // com.cz.usbserial.driver.CommonUsbSerialPort, com.cz.usbserial.driver.UsbSerialPort
        public void setParameters(int i, int i2, int i3, int i4) throws IOException {
            if (this.mBaudRate != i || this.mDataBits != i2 || this.mStopBits != i3 || this.mParity != i4) {
                byte[] bArr = new byte[7];
                bArr[0] = (byte) (i & 255);
                bArr[1] = (byte) ((i >> 8) & 255);
                bArr[2] = (byte) ((i >> 16) & 255);
                bArr[3] = (byte) ((i >> 24) & 255);
                if (i3 == 1) {
                    bArr[4] = 0;
                } else if (i3 == 2) {
                    bArr[4] = 2;
                } else if (i3 == 3) {
                    bArr[4] = 1;
                } else {
                    throw new IllegalArgumentException("Unknown stopBits value: " + i3);
                }
                if (i4 == 0) {
                    bArr[5] = 0;
                } else if (i4 == 1) {
                    bArr[5] = 1;
                } else if (i4 == 2) {
                    bArr[5] = 2;
                } else if (i4 == 3) {
                    bArr[5] = 3;
                } else if (i4 == 4) {
                    bArr[5] = 4;
                } else {
                    throw new IllegalArgumentException("Unknown parity value: " + i4);
                }
                bArr[6] = (byte) i2;
                ctrlOut(32, 0, 0, bArr);
                resetDevice();
                this.mBaudRate = i;
                this.mDataBits = i2;
                this.mStopBits = i3;
                this.mParity = i4;
            }
        }

        @Override // com.cz.usbserial.driver.CommonUsbSerialPort, com.cz.usbserial.driver.UsbSerialPort
        public boolean getCD() throws IOException {
            return testStatusFlag(1);
        }

        @Override // com.cz.usbserial.driver.CommonUsbSerialPort, com.cz.usbserial.driver.UsbSerialPort
        public boolean getCTS() throws IOException {
            return testStatusFlag(128);
        }

        @Override // com.cz.usbserial.driver.CommonUsbSerialPort, com.cz.usbserial.driver.UsbSerialPort
        public boolean getDSR() throws IOException {
            return testStatusFlag(2);
        }

        @Override // com.cz.usbserial.driver.CommonUsbSerialPort, com.cz.usbserial.driver.UsbSerialPort
        public boolean getDTR() throws IOException {
            return (this.mControlLinesValue & 1) == 1;
        }

        @Override // com.cz.usbserial.driver.CommonUsbSerialPort, com.cz.usbserial.driver.UsbSerialPort
        public void setDTR(boolean z) throws IOException {
            int i;
            if (z) {
                i = this.mControlLinesValue | 1;
            } else {
                i = this.mControlLinesValue & -2;
            }
            setControlLines(i);
        }

        @Override // com.cz.usbserial.driver.CommonUsbSerialPort, com.cz.usbserial.driver.UsbSerialPort
        public boolean getRI() throws IOException {
            return testStatusFlag(8);
        }

        @Override // com.cz.usbserial.driver.CommonUsbSerialPort, com.cz.usbserial.driver.UsbSerialPort
        public boolean getRTS() throws IOException {
            return (this.mControlLinesValue & 2) == 2;
        }

        @Override // com.cz.usbserial.driver.CommonUsbSerialPort, com.cz.usbserial.driver.UsbSerialPort
        public void setRTS(boolean z) throws IOException {
            int i;
            if (z) {
                i = this.mControlLinesValue | 2;
            } else {
                i = this.mControlLinesValue & -3;
            }
            setControlLines(i);
        }

        @Override // com.cz.usbserial.driver.CommonUsbSerialPort, com.cz.usbserial.driver.UsbSerialPort
        public boolean purgeHwBuffers(boolean z, boolean z2) throws IOException {
            if (z) {
                vendorOut(8, 0, null);
            }
            if (z2) {
                vendorOut(9, 0, null);
            }
            if (z || z2) {
                return true;
            }
            return false;
        }
    }

    public static Map<Integer, int[]> getSupportedDevices() {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put(1659, new int[]{8963});
        return linkedHashMap;
    }
}
