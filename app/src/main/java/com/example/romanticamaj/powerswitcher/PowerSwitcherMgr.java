package com.example.romanticamaj.powerswitcher;

import android.util.Log;

import com.google.android.things.pio.Gpio;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by romanticamaj on 2017/3/28.
 */

public class PowerSwitcherMgr {
    private static final String TAG = PowerSwitcherMgr.class.getName();

    private HashMap<String, Gpio> mNameGpioMap;

    PowerSwitcherMgr() {
        mNameGpioMap = new HashMap<>();
    }

    public void addPort(final String strPort, final Gpio gpio) {
        try {
            gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            gpio.setActiveType(Gpio.ACTIVE_HIGH);

            mNameGpioMap.put(strPort, gpio);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeState(final String strPort, boolean blState) {
        Gpio gpio = mNameGpioMap.get(strPort);

        if (null == gpio) {
            Log.i(TAG, "Failed to find port: " + strPort);
            return;
        }

        try {
            gpio.setValue(blState);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
