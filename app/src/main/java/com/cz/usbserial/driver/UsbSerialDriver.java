package com.cz.usbserial.driver;

import android.hardware.usb.UsbDevice;

import java.util.List;

public interface UsbSerialDriver {
    UsbDevice getDevice();

    List<UsbSerialPort> getPorts();
}
