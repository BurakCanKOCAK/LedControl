package com.bkocak.ledcontrol.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

/**
 * Created by BurakCan on 11/11/2017.
 */

public class BluetoothManagerAPI {
    static BluetoothAdapter bta;
    private String address="";
    ConnectThread thr;
    private static Handler handler;


    //---------------------------------------------------------------------------------------------
    public void connectToBluetoothScanner(String address) {
        this.address=address;

        bta = BluetoothAdapter.getDefaultAdapter();

        BluetoothDevice btDevice = null;
        Set<BluetoothDevice> devices = bta.getBondedDevices();

        for (BluetoothDevice device : devices) {
            String tt = device.getName();
            Log.e("Device Name", tt);
            if (device.getName().equals("AT288")) {
                btDevice = device;
            }
        }

        if (btDevice != null) {
            thr = new ConnectThread(btDevice);
            thr.start();
        }
    }

    //--------------------------------------------------------------------------------------------
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private BluetoothDevice mmDevice;
        private final UUID SERIAL_PROFILE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        public ConnectThread(BluetoothDevice device) {
            if(!BluetoothAdapter.checkBluetoothAddress(address)){
                // Use a temporary object that is later assigned to mmSocket,
                // because mmSocket is final
                BluetoothSocket tmp = null;
                mmDevice = device;

                // Get a BluetoothSocket to connect with the given BluetoothDevice
                try {
                    // MY_UUID is the app's UUID string, also used by the server code
                    Log.e("CONNECTTHREAD ", "Trying to create communication with device :" + mmDevice.getName());
                    if (Build.VERSION.SDK_INT >= 10) {
                        try {
                            final Method m = device.getClass().getMethod(
                                    "createInsecureRfcommSocketToServiceRecord",
                                    new Class[]{UUID.class});
                            tmp = (BluetoothSocket) m.invoke(device, SERIAL_PROFILE_UUID);
                        } catch (Exception e) {
                            Log.e("CONNECTTHREAD ", "Could not find Insecure RFComm Connection", e);
                        }
                    }
                    tmp = device.createRfcommSocketToServiceRecord(SERIAL_PROFILE_UUID);
                    //tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(SERIAL_PROFILE_UUID);
                    mmSocket = tmp;
                } catch (IOException e) {
                    Log.e("CONNECTTHREAD ", "Socket creation attempt failed with Device :" + mmDevice.getName() + " // " + e.toString());
                }

                mmSocket = tmp;
            }

        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            bta.cancelDiscovery();

            if (!mmSocket.isConnected()) {
                try {
                    // Connect the device through the socket. This will block
                    // until it succeeds or throws an exception
                    mmSocket.connect();
                } catch (IOException connectException) {
                    Log.e("CONNECTTHREAD ", "Could not Connect to Device:" + mmDevice.getName() + " // " + connectException.toString());
                    // Unable to connect; close the socket and get
                    // out
                    try {
                        mmSocket.close();
                    } catch (IOException closeException) {
                        Log.e("CONNECTTHREAD ", "Could not close connection:" + closeException.toString());
                    }
                    return;
                }
            } else {
                Log.e("CONNECTTHREAD", "Already Connected to Device : " + mmDevice.getName());
            }
            if (mmSocket.isConnected()) {
                Message msg = new Message();
                msg.arg1 = 0;  //Established connection : 0
                handler.sendMessage(msg);
            } else {
                Message msg = new Message();
                msg.arg1 = 2;  //Failed connection : 2
                handler.sendMessage(msg);
            }

            // Do work to manage the connection (in a separate thread)
            //manageConnectedSocket(mmSocket);
            InputStream input;
            while (mmSocket.isConnected()) {
                try {
                    Log.e("CONNECTTHREAD ", "Reading Socket...");
                    input = mmSocket.getInputStream();
                    DataInputStream dinput = new DataInputStream(input);
                    String contents = "";
                    while (true) {
                        String receivedDigit = String.valueOf((char) mmSocket.getInputStream().read());
                        if (receivedDigit.equals("\r")) {
                            break;
                        } else {
                            contents += receivedDigit;
                        }
                    }
                    Log.e("CONTENT : ", contents);
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = contents;
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }
}
