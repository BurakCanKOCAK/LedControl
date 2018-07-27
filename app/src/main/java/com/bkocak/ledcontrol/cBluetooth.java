package com.bkocak.ledcontrol;

/**
 * Created by BurakCan on 18/06/2016.
 * <p>
 * Class for Bluetooth
 *
 * @version 1.2.1
 * 14.08.2013
 * Koltykov a.V. http://cxem.net, http://english.cxem.net
 * <p>
 * Class for Bluetooth
 * @version 1.2.1
 * 14.08.2013
 * Koltykov a.V. http://cxem.net, http://english.cxem.net
 * <p>
 * Class for Bluetooth
 * @version 1.2.1
 * 14.08.2013
 * Koltykov a.V. http://cxem.net, http://english.cxem.net
 * <p>
 * Class for Bluetooth
 * @version 1.2.1
 * 14.08.2013
 * Koltykov a.V. http://cxem.net, http://english.cxem.net
 * <p>
 * Class for Bluetooth
 * @version 1.2.1
 * 14.08.2013
 * Koltykov a.V. http://cxem.net, http://english.cxem.net
 * <p>
 * Class for Bluetooth
 * @version 1.2.1
 * 14.08.2013
 * Koltykov a.V. http://cxem.net, http://english.cxem.net
 */
/**
 *  Class for Bluetooth
 *  @version 1.2.1
 *  14.08.2013
 *  Koltykov a.V. http://cxem.net, http://english.cxem.net
 *
 */

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class cBluetooth {

    public final static String TAG = "::BLUETOOTH_ADAPTER::";

    private static BluetoothAdapter btAdapter = null;

    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private ConnectedThread mConnectedThread;

    // SPP UUID service
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final Handler mHandler;

    // statuses for Handler
    public final static int BL_NOT_AVAILABLE = 1; // Bluetooth is not available
    public final static int BL_INCORRECT_ADDRESS = 2; // incorrect MAC-address
    public final static int BL_REQUEST_ENABLE = 3; // request enable Bluetooth
    public final static int BL_SOCKET_FAILED = 4; // socket error
    public final static int RECIEVE_MESSAGE = 5; // receive message
    public final static int BL_CONNECTED_OK = 6;

    //********************************************************************************************************
    cBluetooth(Context context, Handler handler) {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mHandler = handler;
        if (btAdapter == null) {
            mHandler.sendEmptyMessage(BL_NOT_AVAILABLE);
            return;
        }
    }

    //********************************************************************************************************
    public boolean checkBTState() {
        if (btAdapter == null) {
            mHandler.sendEmptyMessage(BL_NOT_AVAILABLE);
            return false;
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "Bluetooth ON");
                return true;
            } else {
                mHandler.sendEmptyMessage(BL_REQUEST_ENABLE);
                return false;
            }
        }
    }

    //********************************************************************************************************
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device)
            throws IOException {
        if (Build.VERSION.SDK_INT >= 10) {
            try {
                final Method m = device.getClass().getMethod(
                        "createInsecureRfcommSocketToServiceRecord",
                        new Class[]{UUID.class});
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            }
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    //********************************************************************************************************
    public boolean BT_Connect(String address, boolean listen_InStream) {
        Log.d(TAG, "Connecting to : " + address + "...");

        boolean connected = false;

        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            mHandler.sendEmptyMessage(BL_INCORRECT_ADDRESS);
            return false;
        } else {

            BluetoothDevice device = btAdapter.getRemoteDevice(address);
            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e1) {
                Log.e(TAG,
                        "In BT_Connect() socket create failed: "
                                + e1.getMessage());
                mHandler.sendEmptyMessage(BL_SOCKET_FAILED);
                return false;
            }

            if (btAdapter.isDiscovering()) {
                btAdapter.cancelDiscovery();
            }

            Log.w(TAG, "...Connecting...");
            try {
                btSocket.connect();
                Log.w(TAG, "...Connection ok...");
            } catch (IOException e) {
                try {
                    btSocket.close();
                    Log.w(TAG, "...Socket closed...");
                } catch (IOException e2) {
                    Log.e(TAG,
                            "In BT_Connect() unable to close socket during connection failure"
                                    + e2.getMessage());
                    mHandler.sendEmptyMessage(BL_SOCKET_FAILED);
                    return false;
                }
            }

            // Create a data stream so we can talk to server.
            Log.w(TAG, "...Create Socket...");

            try {
                outStream = btSocket.getOutputStream();
                connected = true;
                Log.w(TAG, "::::::CONNECTED:::::");
                mHandler.sendEmptyMessage(BL_CONNECTED_OK);
            } catch (IOException e) {
                Log.e(TAG,
                        "In BT_Connect() output stream creation failed:"
                                + e.getMessage());
                mHandler.sendEmptyMessage(BL_SOCKET_FAILED);
                return false;
            }
            if (listen_InStream) {
                mConnectedThread = new ConnectedThread();
                mConnectedThread.start();
            }
        }
        return connected;
    }

    //********************************************************************************************************
    public void BT_onPause() {
        Log.e(TAG, "...On Pause...");
        if (outStream != null) {
            try {
                outStream.flush();
                Log.e(TAG, "BT ON PAUSE , FLUSH MECHANISM ");
            } catch (IOException e) {
                Log.e(TAG, "In onPause() and failed to flush output stream: "
                        + e.getMessage());
                mHandler.sendEmptyMessage(BL_SOCKET_FAILED);
                return;
            }
        }

        if (btSocket != null) {
            try {
                btSocket.close();
                Log.e(TAG, "BT ON PAUSE , SOCKET CLOSE MECHANISM ");
            } catch (IOException e2) {
                Log.e(TAG,
                        "In onPause() and failed to close socket."
                                + e2.getMessage());
                mHandler.sendEmptyMessage(BL_SOCKET_FAILED);
                return;
            }
        }
    }

    //********************************************************************************************************
    public void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.w(TAG, "Send data: " + message);

        if (outStream != null) {
            try {
                outStream.write(msgBuffer);
                Log.w(TAG, ":: DATA SENT ::");
            } catch (IOException e) {
                Log.e(TAG, ":: Exception occurred during write: "
                        + e.getMessage());
                mHandler.sendEmptyMessage(BL_SOCKET_FAILED);
                return;
            }
        } else
            Log.e(TAG, "Error Send data: outStream is Null");
    }

    //----------------------------------------------------------------------------------------------
    private class ConnectedThread extends Thread {

        private final InputStream mmInStream;

        public ConnectedThread() {
            InputStream tmpIn = null;
            // Get the input and output streams, using temp objects because
            // meAmber streams are final
            try {
                tmpIn = btSocket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG,
                        "In ConnectedThread() error getInputStream(): "
                                + e.getMessage());
            }

            mmInStream = tmpIn;
        }

        //----------------------------------------------------------------------------------------------
        public void run() {
            byte[] buffer = new byte[256]; // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        //----------------------------------------------------------------------------------------------
        public boolean closeConnection() {
            if (btAdapter.isEnabled()) {
                try {
                    btAdapter.disable();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return false;
            }
        }


    }
}
