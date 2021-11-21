package com.cz.usbserial.driver;

public class UsbSerialRuntimeException extends RuntimeException {
    public UsbSerialRuntimeException() {
    }

    public UsbSerialRuntimeException(String str, Throwable th) {
        super(str, th);
    }

    public UsbSerialRuntimeException(String str) {
        super(str);
    }

    public UsbSerialRuntimeException(Throwable th) {
        super(th);
    }
}
