package com.example.romanticamaj.powerswitcher;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by romanticamaj on 2017/3/12.
 */

public class IotConnection {
    private static final String TAG = "IotConnection";

    private Handler mMessageHandler;
    private IotServer mServer;
    private Socket mSocket;
    private int mCtrlPort = -1;

    public IotConnection(Handler handler) {
        mMessageHandler = handler;
        mServer = new IotServer();
    }

    public void tearDown() {
        mServer.tearDown();
    }

    public int getControlPort() {
        return mCtrlPort;
    }

    public String getIp() {
        return mServer.getIp();
    }

    public void setControlPort(int port) {
        mCtrlPort = port;
        Log.i(TAG, "Set Server port: " + port);
    }

    public void handleReceivedMsg(String msg) {
        Log.e(TAG, "Message received: " + msg);

        Bundle messageBundle = new Bundle();
        Message message = new Message();

        messageBundle.putString("msg", msg);
        message.setData(messageBundle);

        /* Let upper level know there's a message coming in */
        mMessageHandler.sendMessage(message);
    }

    private void setSocket(Socket socket) {
        Log.d(TAG, "setSocket being called.");

        if (socket == null) {
            Log.d(TAG, "Setting a null socket.");
        }

        if (mSocket != null) {
            if (mSocket.isConnected()) {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        mSocket = socket;
    }

    private class IotServer {
        private static final String IOT_SERVER_TAG = "IotServer";

        private ServerSocket mServerSocket = null;
        private Thread mServerThread = null;
        private Thread mReceivingThread = null;

        public IotServer() {
            mServerThread = new Thread(new ServerThreadRunnable());
            mServerThread.start();
        }

        public String getIp() {
            return "UNKNOWN";
//            return mServerSocket.getInetAddress().getHostName();
        }

        public void tearDown() {
            mServerThread.interrupt();
            mReceivingThread.interrupt();

            try {
                mServerSocket.close();
            } catch (IOException ioe) {
                Log.e(TAG, "Error when closing server socket.");
            }
        }

        public void startReceiving() {
            if (mReceivingThread != null) {
                mReceivingThread.interrupt();
                mReceivingThread = null;
            }

            mReceivingThread = new Thread(new ReceivingThread());
            mReceivingThread.start();
        }

        class ServerThreadRunnable implements Runnable {
            @Override
            public void run() {
                try {
                    // Since discovery will happen via Nsd, we don't need to care which port is
                    // used. Just grab an available one and advertise it via Nsd.
                    mServerSocket = new ServerSocket(0);

                    /* Let upper level know the communication port */
                    setControlPort(mServerSocket.getLocalPort());

                    while (!Thread.currentThread().isInterrupted()) {
                        Log.d(TAG, "ServerSocket Created, awaiting connection");

                        setSocket(mServerSocket.accept());

                        Log.d(TAG, "Connected. And then start receiving work.");

                        startReceiving();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error creating ServerSocket: ", e);
                    e.printStackTrace();
                }
            }
        }

        class ReceivingThread implements Runnable {
            @Override
            public void run() {
                BufferedReader inputBufferedReader;

                try {
                    inputBufferedReader = new BufferedReader(new InputStreamReader(
                            mSocket.getInputStream()));

                    while (!Thread.currentThread().isInterrupted()) {
                        String strMessage;

                        strMessage = inputBufferedReader.readLine();
                        if (strMessage != null) {
                            Log.d(IOT_SERVER_TAG, "Read from the stream: " + strMessage);
                            handleReceivedMsg(strMessage);
                        } else {
                            Log.d(IOT_SERVER_TAG, "The nulls! The nulls!");
                            break;
                        }
                    }

                    inputBufferedReader.close();
                } catch (IOException e) {
                    Log.e(IOT_SERVER_TAG, "Server loop error: ", e);
                }
            }
        }
    }
}
