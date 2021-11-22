package com.cz.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class UsbSerialProber {
    private final ProbeTable mProbeTable;

    public UsbSerialProber(ProbeTable probeTable) {
        this.mProbeTable = probeTable;
    }

    public static UsbSerialProber getDefaultProber() {
        return new UsbSerialProber(getDefaultProbeTable());
    }

    public static ProbeTable getDefaultProbeTable() {
        ProbeTable probeTable = new ProbeTable();
        probeTable.addDriver(FtdiSerialDriver.class);
        probeTable.addDriver(Ch34xSerialDriver.class);
        probeTable.addDriver(Cp21xxSerialDriver.class);
        probeTable.addDriver(ProlificSerialDriver.class);
        return probeTable;
    }

    public List<UsbSerialDriver> findAllDrivers(UsbManager usbManager) {
        ArrayList<UsbSerialDriver> arrayList = new ArrayList<>();
        for (UsbDevice usbDevice : usbManager.getDeviceList().values()) {
            UsbSerialDriver probeDevice = probeDevice(usbDevice);
            if (probeDevice != null) {
                arrayList.add(probeDevice);
            }
        }
        return arrayList;
    }

    public UsbSerialDriver probeDevice(UsbDevice usbDevice) {
        Class<? extends UsbSerialDriver> findDriver = this.mProbeTable.findDriver(usbDevice.getVendorId(), usbDevice.getProductId());
        if (findDriver == null) {
            return null;
        }
        try {
            return findDriver.getConstructor(UsbDevice.class).newInstance(usbDevice);
        } catch (NoSuchMethodException | IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
