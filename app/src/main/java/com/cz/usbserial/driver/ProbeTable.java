package com.cz.usbserial.driver;

import android.util.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProbeTable {
    private final Map<Pair<Integer, Integer>, Class<? extends UsbSerialDriver>> mProbeTable = new LinkedHashMap<>();

    public ProbeTable addProduct(int i, int i2, Class<? extends UsbSerialDriver> cls) {
        this.mProbeTable.put(Pair.create(Integer.valueOf(i), Integer.valueOf(i2)), cls);
        return this;
    }

    public ProbeTable addDriver(Class<? extends UsbSerialDriver> driverClass) {
        try {
            try {
                for (Map.Entry<Integer, int[]> entry : ((Map<Integer, int[]>) driverClass.getMethod("getSupportedDevices", new Class[0]).invoke(null, new Object[0])).entrySet()) {
                    int intValue = entry.getKey().intValue();
                    for (int i : entry.getValue()) {
                        addProduct(intValue, i, driverClass);
                    }
                }
                return this;
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } catch (SecurityException | NoSuchMethodException e4) {
            throw new RuntimeException(e4);
        }
    }

    public Class<? extends UsbSerialDriver> findDriver(int i, int i2) {
        return this.mProbeTable.get(Pair.create(Integer.valueOf(i), Integer.valueOf(i2)));
    }
}
