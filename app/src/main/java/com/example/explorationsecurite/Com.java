package com.example.explorationsecurite;

import android.content.Context;
import android.util.Log;

public class Com implements ConnectTCP.ConnectTCPListener {

    private final ConnectTCP connectTCP;

    String TAG = "COMSSL";

    public Com(Context context) {
        connectTCP = new ConnectTCP(context);
        connectTCP.setConnectTCPListener(this);
    }

    public void establishConnection() {
        connectTCP.connect();
    }

    public void disconnect() {
        connectTCP.disconnect();
    }

    public void sendMessage(String message) {
        connectTCP.write(message);
    }

    @Override
    public void onConnected() {
        // Handle the connection success
        Log.i(TAG, "on Connected");
    }

    @Override
    public void onConnectionError(Exception e) {
        // Handle the connection error
        Log.i(TAG, "on Connection Error : " + e);
    }

    @Override
    public void onWrite(String message) {
        Log.i(TAG, "on Write : " + message);
    }

    @Override
    public void onRead(String message) {
        // Handle read success
        Log.i(TAG, "on Read : " + message);
    }

    @Override
    public void onDisconnected() {
        // Handle disconnection success
        Log.i(TAG, "on Disconnected");
    }

    @Override
    public void onDisconnectionError() {
        // Handle disconnection error
        Log.i(TAG, "on Disconnection Error, trying to reconnect");
    }
}
