package com.cz.usbserial.driver;

import android.hardware.usb.UsbDeviceConnection;
import java.io.IOException;

public interface UsbSerialPort {
    public static final int DATABITS_5 = 5;
    public static final int DATABITS_6 = 6;
    public static final int DATABITS_7 = 7;
    public static final int DATABITS_8 = 8;
    public static final int FLOWCONTROL_NONE = 0;
    public static final int FLOWCONTROL_RTSCTS_IN = 1;
    public static final int FLOWCONTROL_RTSCTS_OUT = 2;
    public static final int FLOWCONTROL_XONXOFF_IN = 4;
    public static final int FLOWCONTROL_XONXOFF_OUT = 8;
    public static final int PARITY_EVEN = 2;
    public static final int PARITY_MARK = 3;
    public static final int PARITY_NONE = 0;
    public static final int PARITY_ODD = 1;
    public static final int PARITY_SPACE = 4;
    public static final int STOPBITS_1 = 1;
    public static final int STOPBITS_1_5 = 3;
    public static final int STOPBITS_2 = 2;

    void close() throws IOException;

    boolean getCD() throws IOException;

    boolean getCTS() throws IOException;

    boolean getDSR() throws IOException;

    boolean getDTR() throws IOException;

    UsbSerialDriver getDriver();

    int getPortNumber();

    boolean getRI() throws IOException;

    boolean getRTS() throws IOException;

    String getSerial();

    void open(UsbDeviceConnection usbDeviceConnection) throws IOException;

    boolean purgeHwBuffers(boolean z, boolean z2) throws IOException;

    int read(byte[] bArr, int i) throws IOException;

    void setDTR(boolean z) throws IOException;

    void setParameters(int i, int i2, int i3, int i4) throws IOException;

    void setRTS(boolean z) throws IOException;

    int write(byte[] bArr, int i) throws IOException;
}
