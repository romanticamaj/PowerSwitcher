package com.example.romanticamaj.powerswitcher;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final Map<String, String> sPowerGpioMap;

    private IotDiscovery mDiscovery;
    private IotConnection mConnection;
    private Handler mMessageHandler;

    private PeripheralManagerService mPeriMgr;
    private PowerSwitcherMgr mPowerSwitcherMgr;
    private static final String SZ_GPIO_BCM21 = "BCM21";
    private static final String SZ_GPIO_BCM20 = "BCM20";
    private Gpio mGpioBCM21;
    private Gpio mGpioBCM20;

    static {
        HashMap<String, String> map = new HashMap<>();

        map.put("power1", SZ_GPIO_BCM21);
        map.put("power2", SZ_GPIO_BCM20);

        sPowerGpioMap = Collections.unmodifiableMap(map);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        initPowerSwitcher();
        initDiscoveryService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        closeGpioPort(mGpioBCM21);
        closeGpioPort(mGpioBCM20);
    }

    private void initPowerSwitcher() {
        mPeriMgr = new PeripheralManagerService();
        mPowerSwitcherMgr = new PowerSwitcherMgr();

        mGpioBCM21 = openGpioPort(SZ_GPIO_BCM21);
        mGpioBCM20 = openGpioPort(SZ_GPIO_BCM20);

        mPowerSwitcherMgr.addPort(SZ_GPIO_BCM21, mGpioBCM21);
        mPowerSwitcherMgr.addPort(SZ_GPIO_BCM20, mGpioBCM20);
    }

    private void initDiscoveryService() {
        mMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String strMsg = msg.getData().getString("msg");

                processMsg(strMsg);
            }

            private void processMsg(final String strMsg) {
                Log.i(TAG, "Received message: " + strMsg);

                /* Only for debug */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView messageTextView = (TextView) findViewById(R.id.textview_message);

                        messageTextView.append("\n" + strMsg);
                    }
                });

                StringTokenizer stringTokenizer = new StringTokenizer(strMsg);
                String strGpio = null;
                Boolean blState = null;

                while (stringTokenizer.hasMoreTokens()) {
                    String strToken = stringTokenizer.nextToken();

                    if (sPowerGpioMap.containsKey(strToken)) {
                        strGpio = sPowerGpioMap.get(strToken);
                    }

                    if (strToken.equalsIgnoreCase("on")) {
                        blState = true;
                    }
                    if (strToken.equalsIgnoreCase("off")) {
                        blState = false;
                    }
                }

                if ((null != strGpio) && (null != blState)) {
                    mPowerSwitcherMgr.changeState(strGpio, blState);
                }
            }
        };

        mConnection = new IotConnection(mMessageHandler);
        mDiscovery = new IotDiscovery(this);

        updateControlIpPort();
        advertise();
    }

    private void advertise() {
        if (mConnection.getControlPort() > -1) {
            mDiscovery.registerService(mConnection.getControlPort());
        } else {
            Log.d(TAG, "ServerSocket isn't bound.");
        }
    }

    private void updateControlIpPort() {
        TextView ipTextView = (TextView) findViewById(R.id.textview_ip);
        TextView portTextView = (TextView) findViewById(R.id.textview_port);

        Log.i(TAG, mConnection.getIp() + mConnection.getControlPort());
        ipTextView.setText(mConnection.getIp());
        portTextView.setText(String.valueOf(mConnection.getControlPort()));
    }

    private Gpio openGpioPort(final String strGpio) {
        Log.d(TAG, "Open GPIO port: " + strGpio);

        try {
            return mPeriMgr.openGpio(strGpio);
        } catch (IOException e) {
            Log.w(TAG, "Unable to access GPIO", e);
        }

        return null;
    }

    private void closeGpioPort(Gpio gpio) {
        Log.i(TAG, "Close GPIO port: " + gpio.toString());

        if (gpio != null) {
            try {
                gpio.close();
            } catch (IOException e) {
                Log.w(TAG, "Unable to close GPIO", e);
            }
        }
    }
}
