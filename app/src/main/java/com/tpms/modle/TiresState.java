package com.tpms.modle;

import java.util.HashMap;
import java.util.Map;

public class TiresState {
    public int AirPressure;
    public boolean Leakage = false;
    public boolean LowPower = false;
    public boolean NoSignal = false;
    public int Temperature;
    public String TiresID = "";
    public String error = "";
    public Map<String, AlarmCntrol> mAlarmCntrols = new HashMap<>();
}
