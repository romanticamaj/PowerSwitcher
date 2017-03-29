package com.example.romanticamaj.powerswitcher;

/**
 * Created by romanticamaj on 2017/3/12.
 */

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

public class IotDiscovery {
    private static final String TAG = "IotDiscovery";
    private static final String SZ_SERVICE_TYPE = "_http._tcp.";
    private static final String SZ_SERVICE_NAME = "GaryIot";

    Context mContext;

    NsdManager mNsdManager;
    NsdManager.RegistrationListener mRegistrationListener;

    public IotDiscovery(Context context) {
        mContext = context;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo arg0, int arg1) {
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            }
        };
    }

    public void registerService(int serverPort) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();

        serviceInfo.setPort(serverPort);
        serviceInfo.setServiceName(SZ_SERVICE_NAME);
        serviceInfo.setServiceType(SZ_SERVICE_TYPE);

        initializeRegistrationListener();

        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

    public void tearDown() {
        mNsdManager.unregisterService(mRegistrationListener);
    }
}
