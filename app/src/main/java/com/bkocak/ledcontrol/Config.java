package com.bkocak.ledcontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Burak on 25/08/16.
 */

public class Config {
    private boolean emulatorMode=false;
    private static String macAddress = "";
    private static String masterKey="1234";
    private static Context ctx;


    Config(){

    }
    public boolean isEmulatorMode() {
        return emulatorMode;
    }

    public static String getMacAddress() {
        return macAddress;
    }

    public static void setMacAddress(String macAddress) {
        Config.macAddress = macAddress;
    }

    public static String getMasterKey() {
        return masterKey;
    }
    public static Context getCtx() {
        return ctx;
    }

    public static void setCtx(Context ctx) {
        Config.ctx = ctx;
    }

}
