package com.example.romanticamaj.powerswitcher;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.List;

/**
 * Created by romanticamaj on 2017/3/12.
 */

public class Utils {
    private static final String TAG = "PeripheralManagerUtils";

    private void showGPIOList(PeripheralManagerService periMgr) {
        List<String> portList = periMgr.getGpioList();

        if (portList.isEmpty()) {
            Log.i(TAG, "No GPIO port available on this device.");
        } else {
            Log.i(TAG, "List of available GPIO ports: " + portList + " => " + portList.size() + " ports");
        }
    }

    private void showPWMList(PeripheralManagerService periMgr) {
        List<String> portList = periMgr.getPwmList();

        if (portList.isEmpty()) {
            Log.i(TAG, "No PWM port available on this device.");
        } else {
            Log.i(TAG, "List of available PWM ports: " + portList + " => " + portList.size() + " ports");
        }
    }

    private void showI2CList(PeripheralManagerService periMgr) {
        List<String> deviceList = periMgr.getI2cBusList();
        if (deviceList.isEmpty()) {
            Log.i(TAG, "No I2C bus available on this device.");
        } else {
            Log.i(TAG, "List of available I2C devices: " + deviceList + " => " + deviceList.size() + " devices");
        }
    }

    private void showSPIList(PeripheralManagerService periMgr) {
        List<String> deviceList = periMgr.getSpiBusList();
        if (deviceList.isEmpty()) {
            Log.i(TAG, "No SPI bus available on this device.");
        } else {
            Log.i(TAG, "List of available SPI devices: " + deviceList + " => " + deviceList.size() + " devices");
        }
    }

    private void showUARTList(PeripheralManagerService periMgr) {
        List<String> deviceList = periMgr.getUartDeviceList();
        if (deviceList.isEmpty()) {
            Log.i(TAG, "No UART port available on this device.");
        } else {
            Log.i(TAG, "List of available UART devices: " + deviceList + " => " + deviceList.size() + " devices");
        }
    }

    private GpioCallback createGpioCallback() {
        GpioCallback mGpioCallback;

        mGpioCallback = new GpioCallback() {
            @Override
            public boolean onGpioEdge(Gpio gpio) {
                // Read the active low pin state
                try {
                    if (gpio.getValue()) {
                        // Pin is LOW
                    } else {
                        // Pin is HIGH
                    }

                    Log.i(TAG, "CurrentValue: " + gpio.getValue());
                } catch (IOException e) {
                    Log.w(TAG, "Unable to access GPIO", e);
                }

                // Continue listening for more interrupts
                return true;
            }

            @Override
            public void onGpioError(Gpio gpio, int error) {
                Log.w(TAG, gpio + ": Error event " + error);
            }
        };

        return mGpioCallback;
    }
}
