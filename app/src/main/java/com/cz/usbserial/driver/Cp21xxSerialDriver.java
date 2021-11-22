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

public class Cp21xxSerialDriver implements UsbSerialDriver {
    private static final String TAG = Cp21xxSerialDriver.class.getSimpleName();
    private final UsbDevice mDevice;
    private final UsbSerialPort mPort;

    public Cp21xxSerialDriver(UsbDevice usbDevice) {
        this.mDevice = usbDevice;
        this.mPort = new Cp21xxSerialPort(usbDevice, 0);
    }

    public static Map<Integer, int[]> getSupportedDevices() {
        LinkedHashMap<Integer, int[]> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put(4292, new int[]{60000, 60016, 60017, 60032});
        return linkedHashMap;
    }

    @Override
    public UsbDevice getDevice() {
        return this.mDevice;
    }

    @Override
    public List<UsbSerialPort> getPorts() {
        return Collections.singletonList(this.mPort);
    }

    public class Cp21xxSerialPort extends CommonUsbSerialPort {
        private static final int BAUD_RATE_GEN_FREQ = 3686400;
        private static final int CONTROL_WRITE_DTR = 256;
        private static final int CONTROL_WRITE_RTS = 512;
        private static final int DEFAULT_BAUD_RATE = 9600;
        private static final int FLUSH_READ_CODE = 10;
        private static final int FLUSH_WRITE_CODE = 5;
        private static final int MCR_ALL = 3;
        private static final int MCR_DTR = 1;
        private static final int MCR_RTS = 2;
        private static final int REQTYPE_HOST_TO_DEVICE = 65;
        private static final int SILABSER_FLUSH_REQUEST_CODE = 18;
        private static final int SILABSER_IFC_ENABLE_REQUEST_CODE = 0;
        private static final int SILABSER_SET_BAUDDIV_REQUEST_CODE = 1;
        private static final int SILABSER_SET_BAUDRATE = 30;
        private static final int SILABSER_SET_LINE_CTL_REQUEST_CODE = 3;
        private static final int SILABSER_SET_MHS_REQUEST_CODE = 7;
        private static final int UART_DISABLE = 0;
        private static final int UART_ENABLE = 1;
        private static final int USB_WRITE_TIMEOUT_MILLIS = 5000;
        private UsbEndpoint mReadEndpoint;
        private UsbEndpoint mWriteEndpoint;

        public Cp21xxSerialPort(UsbDevice usbDevice, int i) {
            super(usbDevice, i);
        }

        @Override
        public boolean getCD() throws IOException {
            return false;
        }

        @Override
        public boolean getCTS() throws IOException {
            return false;
        }

        @Override
        public boolean getDSR() throws IOException {
            return false;
        }

        @Override
        public boolean getDTR() throws IOException {
            return true;
        }

        @Override
        public void setDTR(boolean z) throws IOException {
        }

        @Override
        public int getPortNumber() {
            return super.getPortNumber();
        }

        @Override
        public boolean getRI() throws IOException {
            return false;
        }

        @Override
        public boolean getRTS() throws IOException {
            return true;
        }

        @Override
        public void setRTS(boolean z) throws IOException {
        }

        @Override
        public String getSerial() {
            return super.getSerial();
        }

        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        public UsbSerialDriver getDriver() {
            return Cp21xxSerialDriver.this;
        }

        private int setConfigSingle(int i, int i2) {
            return this.mConnection.controlTransfer(REQTYPE_HOST_TO_DEVICE, i, i2, 0, null, 0, 5000);
        }

        @Override
        public void open(UsbDeviceConnection usbDeviceConnection) throws IOException {
            if (this.mConnection == null) {
                this.mConnection = usbDeviceConnection;
                for (int i = 0; i < this.mDevice.getInterfaceCount(); i++) {
                    try {
                        if (this.mConnection.claimInterface(this.mDevice.getInterface(i), true)) {
                            Log.d(Cp21xxSerialDriver.TAG, "claimInterface " + i + " SUCCESS");
                        } else {
                            Log.d(Cp21xxSerialDriver.TAG, "claimInterface " + i + " FAIL");
                        }
                    } catch (Throwable th) {
                        try {
                            close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        throw th;
                    }
                }
                UsbInterface usbInterface = this.mDevice.getInterface(this.mDevice.getInterfaceCount() - 1);
                for (int i2 = 0; i2 < usbInterface.getEndpointCount(); i2++) {
                    UsbEndpoint endpoint = usbInterface.getEndpoint(i2);
                    if (endpoint.getType() == 2) {
                        if (endpoint.getDirection() == 128) {
                            this.mReadEndpoint = endpoint;
                        } else {
                            this.mWriteEndpoint = endpoint;
                        }
                    }
                }
                setConfigSingle(0, 1);
                setConfigSingle(7, 771);
                setConfigSingle(1, 384);
                return;
            }
            throw new IOException("Already opened.");
        }

        @Override
        public void close() throws IOException {
            if (this.mConnection != null) {
                try {
                    setConfigSingle(0, 0);
                    this.mConnection.close();
                } finally {
                    this.mConnection = null;
                }
            } else {
                throw new IOException("Already closed");
            }
        }

        @Override
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

        @Override
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
                    Log.d(Cp21xxSerialDriver.TAG, "Wrote amt=" + bulkTransfer + " attempted=" + min);
                    i2 += bulkTransfer;
                } else {
                    throw new IOException("Error writing " + min + " bytes at offset " + i2 + " length=" + bArr.length);
                }
            }
            return i2;
        }

        private void setBaudRate(int i) throws IOException {
            if (this.mConnection.controlTransfer(REQTYPE_HOST_TO_DEVICE, SILABSER_SET_BAUDRATE, 0, 0, new byte[]{(byte) (i & 255), (byte) ((i >> 8) & 255), (byte) ((i >> 16) & 255), (byte) ((i >> 24) & 255)}, 4, 5000) < 0) {
                throw new IOException("Error setting baud rate.");
            }
        }

        @Override
        public void setParameters(int i, int i2, int i3, int i4) throws IOException {
            setBaudRate(i);
            int i5 = 2048;
            if (i2 == 5) {
                i5 = 1280;
            } else if (i2 == 6) {
                i5 = 1536;
            } else if (i2 == 7) {
                i5 = 1792;
            }
            if (i4 == 1) {
                i5 |= 16;
            } else if (i4 == 2) {
                i5 |= 32;
            }
            if (i3 == 1) {
                i5 |= 0;
            } else if (i3 == 2) {
                i5 |= 2;
            }
            setConfigSingle(3, i5);
        }

        @Override
        public boolean purgeHwBuffers(boolean z, boolean z2) throws IOException {
            int i = 0;
            int i2 = z ? 10 : 0;
            if (z2) {
                i = 5;
            }
            int i3 = i2 | i;
            if (i3 == 0) {
                return true;
            }
            setConfigSingle(18, i3);
            return true;
        }
    }
}
