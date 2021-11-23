package com.mcal.tmps.data;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mcal.tmps.TpmsApplication;

public class Preferences {
    private static SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(TpmsApplication.getContext());

    public Preferences() {
        preferences = PreferenceManager.getDefaultSharedPreferences(TpmsApplication.getContext());
    }

    public static float getBar() {
        return preferences.getFloat("bar", 100.0f);
    }

    public static void setBar(float flag) {
        preferences.edit().putFloat("bar", flag).apply();
    }

    public static float getPsi() {
        return preferences.getFloat("psi", 6.895f);
    }

    public static void setPsi(float flag) {
        preferences.edit().putFloat("psi", flag).apply();
    }

    public static float getKpa() {
        return preferences.getFloat("kpa", 0.0f);
    }

    public static void setKpa(float flag) {
        preferences.edit().putFloat("kpa", flag).apply();
    }
}