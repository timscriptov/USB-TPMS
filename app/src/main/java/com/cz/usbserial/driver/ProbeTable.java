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
                    int intValue = ((Integer) entry.getKey()).intValue();
                    for (int i : (int[]) entry.getValue()) {
                        addProduct(intValue, i, driverClass);
                    }
                }
                return this;
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e2) {
                throw new RuntimeException(e2);
            } catch (InvocationTargetException e3) {
                throw new RuntimeException(e3);
            }
        } catch (SecurityException e4) {
            throw new RuntimeException(e4);
        } catch (NoSuchMethodException e5) {
            throw new RuntimeException(e5);
        }
    }

    public Class<? extends UsbSerialDriver> findDriver(int i, int i2) {
        return this.mProbeTable.get(Pair.create(Integer.valueOf(i), Integer.valueOf(i2)));
    }
}
