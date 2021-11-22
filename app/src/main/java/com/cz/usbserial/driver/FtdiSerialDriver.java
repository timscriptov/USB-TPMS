package com.cz.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FtdiSerialDriver implements UsbSerialDriver {
    private final UsbDevice mDevice;
    private final UsbSerialPort mPort;

    public FtdiSerialDriver(UsbDevice usbDevice) {
        this.mDevice = usbDevice;
        this.mPort = new FtdiSerialPort(usbDevice, 0);
    }

    public static Map<Integer, int[]> getSupportedDevices() {
        LinkedHashMap<Integer, int[]> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put(1027, new int[]{24577, 24597});
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

    public enum DeviceType {
        TYPE_BM,
        TYPE_AM,
        TYPE_2232C,
        TYPE_R,
        TYPE_2232H,
        TYPE_4232H
    }

    private class FtdiSerialPort extends CommonUsbSerialPort {
        public static final int FTDI_DEVICE_IN_REQTYPE = 192;
        public static final int FTDI_DEVICE_OUT_REQTYPE = 64;
        public static final int USB_ENDPOINT_IN = 128;
        public static final int USB_ENDPOINT_OUT = 0;
        public static final int USB_READ_TIMEOUT_MILLIS = 5000;
        public static final int USB_RECIP_DEVICE = 0;
        public static final int USB_RECIP_ENDPOINT = 2;
        public static final int USB_RECIP_INTERFACE = 1;
        public static final int USB_RECIP_OTHER = 3;
        public static final int USB_TYPE_CLASS = 0;
        public static final int USB_TYPE_RESERVED = 0;
        public static final int USB_TYPE_STANDARD = 0;
        public static final int USB_TYPE_VENDOR = 0;
        public static final int USB_WRITE_TIMEOUT_MILLIS = 5000;
        private static final boolean ENABLE_ASYNC_READS = false;
        private static final int MODEM_STATUS_HEADER_LENGTH = 2;
        private static final int SIO_MODEM_CTRL_REQUEST = 1;
        private static final int SIO_RESET_PURGE_RX = 1;
        private static final int SIO_RESET_PURGE_TX = 2;
        private static final int SIO_RESET_REQUEST = 0;
        private static final int SIO_RESET_SIO = 0;
        private static final int SIO_SET_BAUD_RATE_REQUEST = 3;
        private static final int SIO_SET_DATA_REQUEST = 4;
        private static final int SIO_SET_FLOW_CTRL_REQUEST = 2;
        private final String TAG = FtdiSerialDriver.class.getSimpleName();
        private int mInterface = 0;
        private int mMaxPacketSize = 64;
        private DeviceType mType;

        public FtdiSerialPort(UsbDevice usbDevice, int i) {
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
            return false;
        }

        @Override
        public void setDTR(boolean z) throws IOException {
        }

        @Override
        public boolean getRI() throws IOException {
            return false;
        }

        @Override
        public boolean getRTS() throws IOException {
            return false;
        }

        @Override
        public void setRTS(boolean z) throws IOException {
        }

        @Override
        public UsbSerialDriver getDriver() {
            return FtdiSerialDriver.this;
        }

        private int filterStatusBytes(byte[] bArr, byte[] bArr2, int i, int i2) {
            int i3 = i % i2;
            int i4 = 0;
            int i5 = (i / i2) + (i3 == 0 ? 0 : 1);
            while (i4 < i5) {
                int i6 = i4 == i5 + -1 ? i3 - 2 : i2 - 2;
                if (i6 > 0) {
                    System.arraycopy(bArr, (i4 * i2) + 2, bArr2, (i2 - 2) * i4, i6);
                }
                i4++;
            }
            return i - (i5 * 2);
        }

        public void reset() throws IOException {
            int controlTransfer = this.mConnection.controlTransfer(64, 0, 0, 0, null, 0, 5000);
            if (controlTransfer == 0) {
                this.mType = DeviceType.TYPE_R;
                return;
            }
            throw new IOException("Reset failed: result=" + controlTransfer);
        }

        @Override
        public void open(UsbDeviceConnection usbDeviceConnection) throws IOException {
            if (this.mConnection == null) {
                this.mConnection = usbDeviceConnection;
                for (int i = 0; i < this.mDevice.getInterfaceCount(); i++) {
                    try {
                        if (usbDeviceConnection.claimInterface(this.mDevice.getInterface(i), true)) {
                            Log.d(this.TAG, "claimInterface " + i + " SUCCESS");
                        } else {
                            throw new IOException("Error claiming interface " + i);
                        }
                    } catch (Throwable th) {
                        close();
                        this.mConnection = null;
                        throw th;
                    }
                }
                reset();
                return;
            }
            throw new IOException("Already open");
        }

        @Override
        public void close() throws IOException {
            if (this.mConnection != null) {
                try {
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
            int filterStatusBytes;
            UsbEndpoint endpoint = this.mDevice.getInterface(0).getEndpoint(0);
            synchronized (this.mReadBufferLock) {
                int bulkTransfer = this.mConnection.bulkTransfer(endpoint, this.mReadBuffer, Math.min(bArr.length, this.mReadBuffer.length), i);
                if (bulkTransfer >= 2) {
                    filterStatusBytes = filterStatusBytes(this.mReadBuffer, bArr, bulkTransfer, endpoint.getMaxPacketSize());
                } else {
                    throw new IOException("Expected at least 2 bytes");
                }
            }
            return filterStatusBytes;
        }

        @Override
        public int write(byte[] bArr, int i) throws IOException {
            int min;
            byte[] bArr2;
            int bulkTransfer;
            UsbEndpoint endpoint = this.mDevice.getInterface(0).getEndpoint(1);
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
                    bulkTransfer = this.mConnection.bulkTransfer(endpoint, bArr2, min, i);
                }
                if (bulkTransfer > 0) {
                    i2 += bulkTransfer;
                } else {
                    throw new IOException("Error writing " + min + " bytes at offset " + i2 + " length=" + bArr.length);
                }
            }
            return i2;
        }

        private int setBaudRate(int i) throws IOException {
            long[] convertBaudrate = convertBaudrate(i);
            long j = convertBaudrate[0];
            long j2 = convertBaudrate[1];
            int controlTransfer = this.mConnection.controlTransfer(64, 3, (int) convertBaudrate[2], (int) j2, null, 0, 5000);
            if (controlTransfer == 0) {
                return (int) j;
            }
            throw new IOException("Setting baudrate failed: result=" + controlTransfer);
        }

        @Override
        public void setParameters(int i, int i2, int i3, int i4) throws IOException {
            int i5;
            int i6;
            setBaudRate(i);
            if (i4 == 0) {
                i5 = i2 | 0;
            } else if (i4 == 1) {
                i5 = i2 | 256;
            } else if (i4 == 2) {
                i5 = i2 | 512;
            } else if (i4 == 3) {
                i5 = i2 | 768;
            } else if (i4 == 4) {
                i5 = i2 | 1024;
            } else {
                throw new IllegalArgumentException("Unknown parity value: " + i4);
            }
            if (i3 == 1) {
                i6 = i5 | 0;
            } else if (i3 == 2) {
                i6 = i5 | 4096;
            } else if (i3 == 3) {
                i6 = i5 | 2048;
            } else {
                throw new IllegalArgumentException("Unknown stopBits value: " + i3);
            }
            int controlTransfer = this.mConnection.controlTransfer(64, 4, i6, 0, null, 0, 5000);
            if (controlTransfer != 0) {
                throw new IOException("Setting parameters failed: result=" + controlTransfer);
            }
        }

        private long[] convertBaudrate(int i) {
            int i2 = 24000000 / i;
            int i3 = 8;
            int[] iArr = new int[8];
            iArr[1] = 3;
            int i4 = 2;
            iArr[2] = 2;
            iArr[3] = 4;
            iArr[4] = 1;
            iArr[5] = 5;
            iArr[6] = 6;
            iArr[7] = 7;
            int i5 = 0;
            int i6 = 0;
            int i7 = 0;
            int i8 = 0;
            while (true) {
                if (i5 >= i4) {
                    break;
                }
                int i9 = i2 + i5;
                if (i9 <= i3) {
                    i9 = 8;
                } else if (this.mType != DeviceType.TYPE_AM && i9 < 12) {
                    i9 = 12;
                } else if (i2 < 16) {
                    i9 = 16;
                } else if (this.mType != DeviceType.TYPE_AM && i9 > 131071) {
                    i9 = 131071;
                }
                int i10 = ((i9 / 2) + 24000000) / i9;
                int i11 = i10 < i ? i - i10 : i10 - i;
                if (i5 != 0 && i11 >= i6) {
                    i11 = i6;
                    i9 = i7;
                    i10 = i8;
                } else if (i11 == 0) {
                    i8 = i10;
                    i7 = i9;
                    break;
                }
                i5++;
                i8 = i10;
                i6 = i11;
                i7 = i9;
                i3 = 8;
                i4 = 2;
            }
            long j = (long) ((i7 >> 3) | (iArr[i7 & 7] << 14));
            if (j == 1) {
                j = 0;
            } else if (j == 16385) {
                j = 1;
            }
            return new long[]{(long) i8, (this.mType == DeviceType.TYPE_2232C || this.mType == DeviceType.TYPE_2232H || this.mType == DeviceType.TYPE_4232H) ? ((j >> 8) & 65535 & 65280) | 0 : (j >> 16) & 65535, j & 65535};
        }

        @Override
        public boolean purgeHwBuffers(boolean z, boolean z2) throws IOException {
            int controlTransfer;
            int controlTransfer2;
            if (z && (controlTransfer2 = this.mConnection.controlTransfer(64, 0, 1, 0, null, 0, 5000)) != 0) {
                throw new IOException("Flushing RX failed: result=" + controlTransfer2);
            } else if (!z2 || (controlTransfer = this.mConnection.controlTransfer(64, 0, 2, 0, null, 0, 5000)) == 0) {
                return true;
            } else {
                throw new IOException("Flushing RX failed: result=" + controlTransfer);
            }
        }
    }
}
