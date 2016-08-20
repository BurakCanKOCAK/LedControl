package com.bkocak.ledcontrol;

/**
 * Created by BurakCan on 18/06/2016.
 */
//********************************************************************************************************
import java.lang.ref.WeakReference;
import java.util.Set;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.Toast;
//********************************************************************************************************
@SuppressWarnings("deprecation")
public class Opening extends Activity implements OnClickListener {
    Intent openMain;
    // 30:14:06:09:09:34 (yeni)
    // private static String address = "30:14:06:26:03:67"; //(tiflis)
    // private static String address = "30:14:06:09:09:34";//(bulancak)
    //private static String address = "20:14:04:29:35:28"; // (Nawroz City)
    //private static String address = "98:D3:31:B3:11:8F";
    private static String address = "00:14:04:01:33:64";
    //--------------------------------------------------------------------------------------------//
    //In order to block connection order
    public static String[] blocks = {"A1", "B1", "C1", "D2", "C2", "B2", "A2"};
    //Thresholds
    public static int[] numberOfFlats = {48, 48, 48, 24, 24, 24, 24};
    //--------------------------------------------------------------------------------------------//

    private static cBluetooth bl = null;
    private static boolean BT_is_connect;
    private static final int REQUEST_ENABLE_BT = 1;
    private String cmdSend = "";
    private BluetoothAdapter myBluetoothAdapter;
    private static AbsoluteLayout mainL;
    private Set<BluetoothDevice> pairedDevices;
    static Button aBLOCK, bBLOCK, cBLOCK, dBLOCK, eBLOCK, fBLOCK, gBLOCK,
            hBLOCK, iBLOCK, jBLOCK, kBLOCK, lBLOCK, mButtonEffect2, ButtonAOn,
            ButtonBOn, ButtonCOn,  bOnSale1_1D,bALLA,bALLB,bALLC,bALLD,bALLE,bShops,bTerasli,
            Button1_1D;
    static Button b1_1;
    static Button b2_1;
    static Button b2_1D;
    static Button b4_1;
    static Button b5_1;
    static Button b6_1;
    static Button bOnSale1_1;
    static Button bOnSale2_1;
    static Button bOnSale2_1D;
    static Button bBTOn;
    static Button bBTOff;
    static Button bSold;

    static Button ButtonLEDOFF;
    static Button ButtonLEDON;
    static Button ButtonShops;
    static Button ButtonEffect;
    private PowerManager.WakeLock wl;
    //********************************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("::OPENING.java::", "::: onCreate() :::");
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opening_xml);
        bl = new cBluetooth(this, mHandler);
        bl.checkBTState();
        bl.sendData("0000");
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Thread t = new Thread() {
            public void run() {
                bl.sendData("9999");
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        t.start();

        mainL = (AbsoluteLayout) findViewById(R.id.MainLayout);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Power Lock On");
        wl.acquire();
        aBLOCK = (Button) findViewById(R.id.bABLOCK);
        bBLOCK = (Button) findViewById(R.id.bBBLOCK);
        cBLOCK = (Button) findViewById(R.id.bCBLOCK);

        // ButtonAOn=(Button)findViewById(R.id.ButtonAOn);
        // ButtonBOn=(Button)findViewById(R.id.ButtonBOn);
        // ButtonCOn=(Button)findViewById(R.id.ButtonCOn);
        b2_1 = (Button) findViewById(R.id.Button2_1);
        bOnSale2_1= (Button) findViewById(R.id.bOnSale2_1);
        b2_1D = (Button) findViewById(R.id.Button2_1D);
        bOnSale2_1D= (Button) findViewById(R.id.bOnSale2_1D);
        ButtonLEDON = (Button) findViewById(R.id.ButtonLEDON);
        ButtonLEDOFF = (Button) findViewById(R.id.ButtonLEDOFF);
        ButtonEffect = (Button) findViewById(R.id.ButtonEffect);
        bSold = (Button) findViewById(R.id.bSold);
        bTerasli = (Button) findViewById(R.id.bterasli);
        b1_1 = (Button) findViewById(R.id.Button1_1);

        bOnSale1_1 = (Button) findViewById(R.id.bOnSale1_1);
        bALLC = (Button) findViewById(R.id.bALLC);
        bALLD = (Button) findViewById(R.id.bALLD);
        bALLE = (Button) findViewById(R.id.bALLE);
        bShops = (Button) findViewById(R.id.bShops);

        bSold.setOnClickListener(this);
        aBLOCK.setOnClickListener(this);
        bBLOCK.setOnClickListener(this);
        cBLOCK.setOnClickListener(this);
        b2_1.setOnClickListener(this);
        bOnSale2_1.setOnClickListener(this);
        b2_1D.setOnClickListener(this);
        bOnSale2_1D.setOnClickListener(this);
        bTerasli.setOnClickListener(this);
        b1_1.setOnClickListener(this);
        bOnSale1_1.setOnClickListener(this);
        ButtonLEDOFF.setOnClickListener(this);
        ButtonLEDON.setOnClickListener(this);
        ButtonEffect.setOnClickListener(this);
        bALLC.setOnClickListener(this);
        bALLD.setOnClickListener(this);
        bALLE.setOnClickListener(this);
        bShops.setOnClickListener(this);
        // ButtonAOn.setOnClickListener(this);
        // ButtonBOn.setOnClickListener(this);
        // ButtonCOn.setOnClickListener(this);
        // buttonDisable();
        mHandler.postDelayed(sRunnable, 600000);
        //////////////////////////////////////
        SpecialSettings();
        //////////////////////////////////////
    }
    //********************************************************************************************************
    private void SpecialSettings() {
        // TODO Auto-generated method stub
        bShops.setVisibility(View.INVISIBLE);
        b2_1D.setVisibility(View.INVISIBLE);
        bOnSale2_1D.setVisibility(View.INVISIBLE);
        bTerasli.setVisibility(View.INVISIBLE);
    }
    //********************************************************************************************************
    public void BTOff(View view) {
        Log.i("::OPENING.java::", "::: BTOff() :::");
        Log.i("::OPENING.java::", "::: - BT OFF Button Pressed - :::");
        bl.BT_onPause();
        // myBluetoothAdapter.disable();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Log.e("::EXCEPTION THROWED::", "::(!) BTOff() (!)::");
            e.printStackTrace();
        }
    }
    //********************************************************************************************************
    public void BTOn(final View view) {
        Log.i("::OPENING.java::", ":::BTOn():::");
        BT_is_connect = bl.BT_Connect(address, false);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Log.e("::EXCEPTION THROWED::", "::(!) BTOn() (!)::");
            e.printStackTrace();
        }
    }
    //********************************************************************************************************
    @Override
    public void onClick(View v) {
        Log.i("::OPENING.java::", "::: onClick() :::");
        SharedPreferences sharedPref = getSharedPreferences("data",
                MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String key = "block";
        String name = null;

        switch (v.getId()) {

            case R.id.bterasli:
                mainL.setBackgroundResource(R.drawable.block_main);
                Log.v("::OPENING.java::","::: TERASLI DAIRELER => 5800 :::" );
                bl.sendData("5800");
                break;

            case R.id.bALLC:
                mainL.setBackgroundResource(R.drawable.block_c);
                Log.v("::OPENING.java::","::: ALL LIGHT UP : C => 4100 :::" );
                bl.sendData("4100");
                break;

            case R.id.bALLD:
                mainL.setBackgroundResource(R.drawable.block_d);
                Log.v("::OPENING.java::","::: ALL LIGHT UP : D => 4200 :::" );
                bl.sendData("4200");
                break;

            case R.id.bALLE:
                mainL.setBackgroundResource(R.drawable.block_e);
                Log.v("::OPENING.java::","::: ALL LIGHT UP : E => 4300 :::" );
                bl.sendData("4300");
                break;

            case R.id.bShops:
                mainL.setBackgroundResource(R.drawable.block_f);
                Log.v("::OPENING.java::","::: SHOPS => 5700 :::" );
                bl.sendData("5700");
                break;


            case R.id.bSold:
                mainL.setBackgroundResource(R.drawable.block_main);
                Log.v("::OPENING.java::","::: ALL OnSale => 9400 :::" );
                bl.sendData("9400");
                break;
            case R.id.bABLOCK:
                mainL.setBackgroundResource(R.drawable.block_c);
                name = "C";
                editor.putString(key, name);
                editor.commit();
                openMain = new Intent("com.bkocak.ledcontrol.MainActivity");
                startActivity(openMain);

                break;
            case R.id.bBBLOCK:
                mainL.setBackgroundResource(R.drawable.block_d);
                name = "D";
                editor.putString(key, name);
                editor.commit();
                openMain = new Intent("com.bkocak.ledcontrol.MainActivity");
                startActivity(openMain);

                break;
            case R.id.bCBLOCK:
                mainL.setBackgroundResource(R.drawable.block_e);
                name = "E";
                editor.putString(key, name);
                editor.commit();
                openMain = new Intent("com.bkocak.ledcontrol.MainActivity");
                startActivity(openMain);
                break;


            case R.id.Button1_1:
                mainL.setBackgroundResource(R.drawable.block_main);
                bl.sendData("5100");
                Log.v("::OPENING.java::", "::: 1+1  All => 5100 :::");

                break;

            case R.id.bOnSale1_1:
                mainL.setBackgroundResource(R.drawable.block_main);
                Log.v("::OPENING.java::", "::: 1+1  Sold => 5400 :::");
                bl.sendData("5400");
                break;


            case R.id.Button2_1:
                mainL.setBackgroundResource(R.drawable.block_main);
                Log.v("::OPENING.java::", "::: All 2+1 => 5200 :::");
                bl.sendData("5200");
                break;

            case R.id.bOnSale2_1:
                mainL.setBackgroundResource(R.drawable.block_main);
                Log.v("::OPENING.java::", "::: 2+1 Sold => 5500 :::");
                bl.sendData("5500");
                break;

            case R.id.bOnSale2_1D:
                mainL.setBackgroundResource(R.drawable.block_main);
                Log.v("::OPENING.java::", "::: 3+1 Sold => 5600 :::");
                bl.sendData("5600");
                break;

            case R.id.Button2_1D:
                Log.v("::OPENING.java::", "::: ALL 3+1 => 5300 :::");
                bl.sendData("5300");
                break;


            case R.id.ButtonLEDON:
                mainL.setBackgroundResource(R.drawable.block_allon);
                Log.v("::OPENING.java::", "::: ALL ON => 8888 :::");
                bl.sendData("8888");
                break;

            case R.id.ButtonLEDOFF:
                mainL.setBackgroundResource(R.drawable.block_main);
                Log.v("::OPENING.java::", "::: ALL OFF => 0000 :::");
                bl.sendData("0000");
                break;

            case R.id.ButtonEffect:
                mainL.setBackgroundResource(R.drawable.block_main);
                Log.v(":::OPENING.java::", "::: Effect => 9100 :::");
                bl.sendData("9100");
                break;
	/*	case R.id.ButtonAOn:
			bl.sendData("3500");
			Log.d(":::OPENING.java::", "Effect = 3500");
			break;
		case R.id.ButtonBOn:
			bl.sendData("3600");
			Log.d(":::OPENING.java::", "Effect = 3600");
			break;
		case R.id.ButtonCOn:
			bl.sendData("3700");
			Log.d(":::OPENING.java::", "Effect = 3700");
			break;
		*/
        }
    }
    //********************************************************************************************************
    private final MyHandler mHandler = new MyHandler(this);
    //********************************************************************************************************
    private final static Runnable sRunnable = new Runnable() {
        public void run() {
        }
    };
    //********************************************************************************************************
    // ---------------- HANDLER ---------------------------------
    private static class MyHandler extends Handler {
        private final WeakReference<Opening> mActivity;
        //********************************************************
        public MyHandler(Opening activity) {
            mActivity = new WeakReference<Opening>(activity);
        }
        //********************************************************
        @Override
        public void handleMessage(Message msg) {
            Opening activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case cBluetooth.BL_NOT_AVAILABLE:
                        Log.i("::OPENING.java::", "::: handleMessage() => BL_NOT_AVAILABLE :::");
                        // tvBTStatus.setText("BT Not Available");
                        Toast.makeText(activity.getBaseContext(),
                                "Bluetooth is not available", Toast.LENGTH_SHORT)
                                .show();
                        activity.finish();
                        break;
                    case cBluetooth.BL_INCORRECT_ADDRESS:
                        Log.i("::OPENING.java::", "::: handleMessage() => BL_INCORRECT_ADDRESS :::");
                        // tvBTStatus.setText("BT Incorrect Address");
                        Toast.makeText(activity.getBaseContext(),
                                "Incorrect Bluetooth address", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case cBluetooth.BL_REQUEST_ENABLE:
                        Log.i("::OPENING.java::", "::: handleMessage() => BL_REQUEST_ENABLE :::");
                        // tvData.setText("Connecting...");
                        // tvBTStatus.setText("BT Enabling");
                        BluetoothAdapter.getDefaultAdapter();
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        activity.startActivityForResult(enableBtIntent, 1);
                        break;
                    case cBluetooth.BL_SOCKET_FAILED:
                        Log.i("::OPENING.java::", "::: handleMessage() => BL_SOCKET_FAILED :::");
                        // tvBTStatus.setText("BT Socket Failed!");
                        Toast.makeText(activity.getBaseContext(), "Please Wait",
                                Toast.LENGTH_SHORT).show();
                        socket();
                        break;

                    case cBluetooth.BL_CONNECTED_OK:
                        Toast.makeText(activity.getBaseContext(),"System Connected",Toast.LENGTH_LONG).show();
                        Log.i("::OPENING.java::", "::: handleMessage() => BL_CONNECTED_OK :::");
                        // RelLay.setBackgroundResource(R.drawable.back_green); //
                        // tvData.setText("Connected"); // Ba�lant�
                        // Sa�land�ysa-----------------------------------
                        //buttonEnable(); //
                        break;
                }
            }
        }
        //********************************************************
        private void socket() {
            // TODO Auto-generated method stub
            Thread connection = new Thread() {
                public void run() {
                    BT_is_connect = bl.BT_Connect(address, false);
                }
            };
            connection.start();
        }
        //********************************************************
        private void buttonDisable() {

        }
        //********************************************************************************************************
        private void buttonEnable() {
            // TODO Auto-generated method stub
            b1_1.setEnabled(true);
            b2_1.setEnabled(true);
            bOnSale1_1.setEnabled(true);
            bOnSale2_1.setEnabled(true);

            ButtonEffect.setEnabled(true);
        }
    }   //********************************************************
    // ****************END OF HANDLER ****************************
    // ----------------ON--------------------------------------------
    public void on(View view) {
        if (!myBluetoothAdapter.isEnabled()) {

            Intent turnOnIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

            Toast.makeText(getApplicationContext(), "Bluetooth turned on",
                    Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already on",
                    Toast.LENGTH_LONG).show();
        }
    }
    //********************************************************************************************************
    // --------------------ACTIVITY RESULT--------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == REQUEST_ENABLE_BT) {
            if (myBluetoothAdapter.isEnabled()) {
                // tvData.setText("Connecting...");
                // bTop.setBackgroundResource(R.drawable.head_red);
            } else {
                // tvData.setText("Disconnected");
                // bTop.setBackgroundResource(R.drawable.head_green);
            }
        }

    }
    //********************************************************************************************************
    // ------------------BROADCAST RECEIVER ----------------------
    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            }
        }
    };
    // *********************************************************
    // ------------------------BT OFF--------------------------
    //********************************************************************************************************
    public void off(View view) {
        // Indicator.setBackgroundResource(R.drawable.red);
        myBluetoothAdapter.disable();
        // text.setText("Status: Disconnected");
        // Send.setEnabled(false);
        // Komut.setEnabled(false);
        Toast.makeText(getApplicationContext(), "Bluetooth turned off",
                Toast.LENGTH_LONG).show();
    }
    //********************************************************************************************************
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        bl.BT_onPause();
    }
    //********************************************************************************************************
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mainL.setBackgroundResource(R.drawable.block_main);
        final Dialog emailDialog = new Dialog(Opening.this,
                android.R.style.Theme_DeviceDefault);
        emailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        emailDialog.setCancelable(false);
        emailDialog.setContentView(R.layout.dialoglayout);
        emailDialog.show();
        Thread connection = new Thread() {

            public void run() {
                BT_is_connect = bl.BT_Connect(address, false);
                bl.sendData("9999");

            }
        };
        connection.start();
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    emailDialog.dismiss();
                    bl.sendData("9999");

                    Thread timer2 = new Thread() {
                        public void run() {
                            try {
                                sleep(4000);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                emailDialog.dismiss();

                            }

                        }
                    };
                    timer2.start();
                }

            }
        };
        timer.start();
        Log.e("::OPENING::ON_RESUME::", ":::ON RESUME ANOUNCED::");

    }
    //--------------------------------------------------------------------------------------------//
    public int calculateBlockThresholdValue(String blockName) {
        int index = 0;
        for (String block : Opening.blocks) {
            if (block.equals(blockName)) {
                int threshold = 0;
                for (int i = 0; i < index; i++) {
                    threshold += Opening.numberOfFlats[i];
                }
                return threshold;
            } else {
                index++;
            }
        }
        return 0;
    }
    //--------------------------------------------------------------------------------------------//
    //********************************************************************************************************
}
