package com.bkocak.ledcontrol;

/**
 * Created by BurakCan on 18/06/2016.
 */
//********************************************************************************************************

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.deser.std.DateDeserializers;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;

import static android.view.View.VISIBLE;

//********************************************************************************************************
@SuppressWarnings("deprecation")
public class Opening extends Activity implements OnClickListener {
    private static final int REQUEST_ENABLE_BT = 1;
    //********************************************************************************************************
    private final static Runnable sRunnable = new Runnable() {
        public void run() {
        }
    };
    //--------------------------------------------------------------------------------------------//
    //Block list
    public static String[] blocks = {"Villa"};
    //Thresholds
    public static int[] numberOfFlats = {13};
    //--------------------------------------------------------------------------------------------//
    //Code list
    public static String codeEffect = "9100";
    public static String codeAllOn = "8888";
    public static String codeAllOff = "0000";
    public static String codeOnSale = "9400";
    public static String codeCommercial = "5300";
    static Button bDisconnect;
    static Button bConnect;
    static Button ButtonLEDOFF;
    static Button ButtonLEDON;
    static Button ButtonEffect;
    // 30:14:06:09:09:34 (yeni)
    // private static String address = "30:14:06:26:03:67"; //(tiflis)
    // private static String address = "30:14:06:09:09:34";//(bulancak)
    //private static String address = "20:14:04:29:35:28"; // (Nawroz City)
    //private static String address = "98:D3:31:B3:11:8F";
    //private static String address = "00:14:04:01:33:64"; //Benim modul address
    //private static String address = "20:16:03:10:85:85"; //1071 Manzara - 2016
    //private static String address = "98:D3:32:10:52:F6"; //Karabuk (Patyo) - 2016
    //private static String address = "20:15:04:29:57:32"; //Huseyin Test(Patyo) - 2016
    private static String address = "30:14:06:26:03:67"; //Villa Project(Patyo) - 20.02.2017
    //--------------------------------------------------------------------------------------------//
    private static Button bMainBlock, bOnSaleType1, bOnSaleType2, bEffect, bOnSale;
    //Test Mode Buttons
    private static Button bCommercial, bBlockEOnSale, bBlockDOnSale;
    private static Button block1, block2, block3, block4, block5, block6, block7, block8, block9, block10, block11, block12, block13;
    private static Button saleMode;
    private static ListView SoldList;
    private static ImageView salesListBackgroundImage;
    private static cBluetooth bl = null;
    private static boolean BT_is_connect;
    private static AbsoluteLayout mainL;
    private static boolean saleModeState = false;


    public ArrayList<String> SoldListArray;
    ArrayAdapter<String> adapter;
    private static Boolean[] isVillaSoldList;
    private static Boolean[] isVillaOn ={false,false,false,false,false,false,false,false,false,false,false,false,false};
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
    //********************************************************************************************************
    private final MyHandler mHandler = new MyHandler(this);
    //--------------------------------------------------------------------------------------------//
    public SharedPreferences sharedPref;
    Intent openMain;
    Config config = new Config();
    private String cmdSend = "";
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private PowerManager.WakeLock wl;

    //--------------------------------------------------------------------------------------------//
    public static int calculateBlockThresholdValue(String blockName) {
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

    //********************************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("::OPENING.java::", "::: onCreate() :::");
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opening_xml);

        salesListBackgroundImage = (ImageView)findViewById(R.id.salesListBackground);
        SoldList = (ListView) findViewById(R.id.lvSoldList);
        isVillaSoldList = new Boolean[13];
        SoldListArray = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtItem, SoldListArray);
        SoldList.setAdapter(adapter);

        /*SoldList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object o = lvSoldList.getItemAtPosition(position);
                String str = (String) o;//As you are using Default String Adapter
                //tvDatatoSend.setText(str);
                //tvDatatoSend.setTextColor(Color.BLACK);
                //daire = Integer.valueOf(str);
            }
        });*/

        //-----------------------------------------------------------------------//
        this.sharedPref = getSharedPreferences("data",
                MODE_PRIVATE);
        getVillaStatus();
        //----//
        if (!config.isEmulatorMode()) {
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
        }
        mainL = (AbsoluteLayout) findViewById(R.id.MainLayout);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Power Lock On");
        wl.acquire();

        bDisconnect = (Button) findViewById(R.id.bDisconnect);
        bConnect = (Button) findViewById(R.id.bConnect);
        ButtonLEDON = (Button) findViewById(R.id.ButtonLEDON);
        ButtonLEDOFF = (Button) findViewById(R.id.ButtonLEDOFF);
        bEffect = (Button) findViewById(R.id.bEffect);

        bDisconnect.setOnClickListener(this);
        bConnect.setOnClickListener(this);
        ButtonLEDOFF.setOnClickListener(this);
        ButtonLEDON.setOnClickListener(this);
        bEffect.setOnClickListener(this);
        //--------------------------------------------------------------------------------------------//
        block1 = (Button) findViewById(R.id.bBlock1);
        block2 = (Button) findViewById(R.id.bBlock2);
        block3 = (Button) findViewById(R.id.bBlock3);
        block4 = (Button) findViewById(R.id.bBlock4);
        block5 = (Button) findViewById(R.id.bBlock5);
        block6 = (Button) findViewById(R.id.bBlock6);
        block7 = (Button) findViewById(R.id.bBlock7);
        block8 = (Button) findViewById(R.id.bBlock8);
        block9 = (Button) findViewById(R.id.bBlock9);
        block10 = (Button) findViewById(R.id.bBlock10);
        block11 = (Button) findViewById(R.id.bBlock11);
        block12 = (Button) findViewById(R.id.bBlock12);
        block13 = (Button) findViewById(R.id.bBlock13);

        saleMode = (Button) findViewById(R.id.bSalesMode);
        //--------------------------------------------------------------------------------------------//
        bBlockDOnSale = (Button) findViewById(R.id.bBlockDOnSale);
        bBlockEOnSale = (Button) findViewById(R.id.bBlockEOnSale);
        bOnSaleType1 = (Button) findViewById(R.id.bOnSaleType1);
        bOnSaleType2 = (Button) findViewById(R.id.bOnSaleType2);
        bOnSale = (Button) findViewById(R.id.bOnSale);
        //bMainBlock = (Button) findViewById(R.id.bMainBlock);
        bCommercial = (Button) findViewById(R.id.bCommercial);

        block1.setOnClickListener(this);
        block2.setOnClickListener(this);
        block3.setOnClickListener(this);
        block4.setOnClickListener(this);
        block5.setOnClickListener(this);
        block6.setOnClickListener(this);
        block7.setOnClickListener(this);
        block8.setOnClickListener(this);
        block9.setOnClickListener(this);
        block10.setOnClickListener(this);
        block11.setOnClickListener(this);
        block12.setOnClickListener(this);
        block13.setOnClickListener(this);

        saleMode.setOnClickListener(this);

        bBlockDOnSale.setOnClickListener(this);
        bBlockEOnSale.setOnClickListener(this);
        bOnSaleType1.setOnClickListener(this);
        bOnSaleType2.setOnClickListener(this);
        bOnSale.setOnClickListener(this);
        //bMainBlock.setOnClickListener(this);
        bCommercial.setOnClickListener(this);
        //--------------------------------------------------------------------------------------------//
        // buttonDisable();
        mHandler.postDelayed(sRunnable, 600000);
        SpecialSettings();
    }

    //********************************************************************************************************
    private void SpecialSettings() {
        // TODO Auto-generated method stub
        /*
        b2_1D.setVisibility(View.INVISIBLE);
        b2_1.setVisibility(View.INVISIBLE);
        b1_1.setVisibility(View.INVISIBLE);
        bOnSale1_1.setVisibility(View.INVISIBLE);
        bOnSale2_1.setVisibility(View.INVISIBLE);
        bOnSale2_1D.setVisibility(View.INVISIBLE);
        bTerasli.setVisibility(View.INVISIBLE);
        bALLE.setVisibility(View.INVISIBLE);
        bALLD.setVisibility(View.INVISIBLE);
        bALLC.setVisibility(View.INVISIBLE);
        bSold.setVisibility(View.INVISIBLE);
        */
    }

    //********************************************************************************************************
    public void BTOff(View view) {
        //TODO Check bt is connected , if yes then cut connection .If no then do nothing
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
        //TODO Check bt is connected , if yes then do nothing . If not Then connect.
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
            case R.id.bSalesMode:
                saleModeState = !saleModeState;
                if (saleModeState) {
                    SoldList.setVisibility(View.VISIBLE);
                    salesListBackgroundImage.setVisibility(View.VISIBLE);
                    saleMode.setBackgroundResource(R.drawable.nawroz_on);
                    Toast.makeText(getApplicationContext(), "Sale mode active",
                            Toast.LENGTH_SHORT).show();
                } else {
                    salesListBackgroundImage.setVisibility(View.INVISIBLE);
                    SoldList.setVisibility(View.INVISIBLE);
                    saleMode.setBackgroundResource(R.drawable.nawroz_off);
                    Toast.makeText(getApplicationContext(), "Sale mode turned off",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            //--------------------------------------------------------------------------------------------//
            // BLOCKS //////////////////////////////////////////////////////////////////////////////////////
/*            case R.id.bA1BLOCK:
                //mainL.setBackgroundResource(R.drawable.block_c);
                name = "A1";
                editor.putString(key, name);
                editor.commit();
                openMain = new Intent("com.bkocak.ledcontrol.MainActivity");
                startActivity(openMain);

                break;
            case R.id.bA2BLOCK:
                //mainL.setBackgroundResource(R.drawable.block_d);
                name = "A2";
                editor.putString(key, name);
                editor.commit();
                openMain = new Intent("com.bkocak.ledcontrol.MainActivity");
                startActivity(openMain);

                break;

            case R.id.bMainBlock:
                //mainL.setBackgroundResource(R.drawable.block_e);
                name = "Main";
                editor.putString(key, name);
                editor.commit();
                openMain = new Intent("com.bkocak.ledcontrol.MainActivity");
                startActivity(openMain);
                break;
*/
            case R.id.bBlock1:
                if (saleModeState) {
                    createDialog(1);
                } else {
                    if(isVillaOn[0])
                    {
                        //turn off
                        isVillaOn[0]=false;
                        bl.sendData("0001");
                        Log.v("::OPENING.java::", "[ VILLLA 1 ][ OFF ]");
                        Toast.makeText(getApplicationContext(), "Villa 1 OFF",
                                Toast.LENGTH_SHORT).show();
                    }else
                    {
                        //turn on
                        isVillaOn[0]=true;
                        bl.sendData("1001");
                        Log.v("::OPENING.java::", "[ VILLLA 1 ][ ON ]");
                        Toast.makeText(getApplicationContext(), "Villa 1 ON",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            //********************//
            case R.id.bBlock2:
                if (saleModeState) {
                    createDialog(2);
                } else {
                    if(isVillaOn[1])
                    {
                        //turn off
                        isVillaOn[1]=false;
                        bl.sendData("0002");
                        Toast.makeText(getApplicationContext(), "Villa 2 OFF",
                                Toast.LENGTH_SHORT).show();
                    }else
                    {
                        //turn on
                        isVillaOn[1]=true;
                        bl.sendData("1002");
                        Toast.makeText(getApplicationContext(), "Villa 2 ON",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            //********************//
            case R.id.bBlock3:
                if (saleModeState) {
                    createDialog(3);
                } else {
                    if(isVillaOn[2])
                    {
                        //turn off
                        isVillaOn[2]=false;
                        bl.sendData("0003");
                        Toast.makeText(getApplicationContext(), "Villa 3 OFF",
                                Toast.LENGTH_SHORT).show();
                    }else
                    {
                        //turn on
                        isVillaOn[2]=true;
                        bl.sendData("1003");
                        Toast.makeText(getApplicationContext(), "Villa 3 ON",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            //********************//
            case R.id.bBlock4:
                if (saleModeState) {
                    createDialog(4);
                } else {
                    if(isVillaOn[3])
                    {
                        //turn off
                        isVillaOn[3]=false;
                        bl.sendData("0004");
                        Toast.makeText(getApplicationContext(), "Villa 4 OFF",
                                Toast.LENGTH_SHORT).show();
                    }else
                    {
                        //turn on
                        isVillaOn[3]=true;
                        bl.sendData("1004");
                        Toast.makeText(getApplicationContext(), "Villa 4 ON",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            //********************//
            case R.id.bBlock5:
                if (saleModeState) {
                    createDialog(5);
                } else {
                    if(isVillaOn[4])
                    {
                        //turn off
                        isVillaOn[4]=false;
                        bl.sendData("0005");
                        Toast.makeText(getApplicationContext(), "Villa 5 OFF",
                                Toast.LENGTH_SHORT).show();
                    }else
                    {
                        //turn on
                        isVillaOn[4]=true;
                        bl.sendData("1005");
                        Toast.makeText(getApplicationContext(), "Villa 5 ON",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            //********************//
            case R.id.bBlock6:
                if (saleModeState) {
                    createDialog(6);
                } else {
                    if(isVillaOn[5])
                    {
                        //turn off
                        isVillaOn[5]=false;
                        bl.sendData("0006");
                        Toast.makeText(getApplicationContext(), "Villa 6 OFF",
                                Toast.LENGTH_SHORT).show();
                    }else
                    {
                        //turn on
                        isVillaOn[5]=true;
                        bl.sendData("1006");
                        Toast.makeText(getApplicationContext(), "Villa 6 ON",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            //********************//
            case R.id.bBlock7:
                if (saleModeState) {
                    createDialog(7);
                } else {
                    if(isVillaOn[6])
                    {
                        //turn off
                        isVillaOn[6]=false;
                        bl.sendData("0007");
                        Toast.makeText(getApplicationContext(), "Villa 7 OFF",
                                Toast.LENGTH_SHORT).show();
                    }else
                    {
                        //turn on
                        isVillaOn[6]=true;
                        bl.sendData("1007");
                        Toast.makeText(getApplicationContext(), "Villa 7 ON",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            //********************//
            case R.id.bBlock8:
                if (saleModeState) {
                    createDialog(8);
                } else {
                    if(isVillaOn[7])
                    {
                        //turn off
                        isVillaOn[7]=false;
                        bl.sendData("0008");
                        Toast.makeText(getApplicationContext(), "Villa 8 OFF",
                                Toast.LENGTH_SHORT).show();
                    }else
                    {
                        //turn on
                        isVillaOn[7]=true;
                        bl.sendData("1008");
                        Toast.makeText(getApplicationContext(), "Villa 8 ON",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            //********************//
            case R.id.bBlock9:
                if (saleModeState) {
                    createDialog(9);
                } else {
                    if(isVillaOn[8])
                    {
                        //turn off
                        isVillaOn[8]=false;
                        bl.sendData("0009");
                        Toast.makeText(getApplicationContext(), "Villa 9 OFF",
                                Toast.LENGTH_SHORT).show();
                    }else
                    {
                        //turn on
                        isVillaOn[8]=true;
                        bl.sendData("1009");
                        Toast.makeText(getApplicationContext(), "Villa 9 ON",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            //********************//
            case R.id.bBlock10:
                if (saleModeState) {
                    createDialog(10);
                } else {
                    if(isVillaOn[9])
                    {
                        //turn off
                        isVillaOn[9]=false;
                        bl.sendData("0010");
                        Toast.makeText(getApplicationContext(), "Villa 10 OFF",
                                Toast.LENGTH_SHORT).show();
                    }else
                    {
                        //turn on
                        isVillaOn[9]=true;
                        bl.sendData("1010");
                        Toast.makeText(getApplicationContext(), "Villa 10 ON",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            //********************//
            case R.id.bBlock11:
                if (saleModeState) {
                    createDialog(11);
                } else {
                    if(isVillaOn[10])
                    {
                        //turn off
                        isVillaOn[10]=false;
                        bl.sendData("0011");
                        Toast.makeText(getApplicationContext(), "Villa 11 OFF",
                                Toast.LENGTH_SHORT).show();
                    }else
                    {
                        //turn on
                        isVillaOn[10]=true;
                        bl.sendData("1011");
                        Toast.makeText(getApplicationContext(), "Villa 11 ON",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            //********************//
            case R.id.bBlock12:
                if (saleModeState) {
                    createDialog(12);
                } else {
                    if(isVillaOn[11])
                    {
                        //turn off
                        isVillaOn[11]=false;
                        bl.sendData("0012");
                        Toast.makeText(getApplicationContext(), "Villa 12 OFF",
                                Toast.LENGTH_SHORT).show();
                    }else
                    {
                        //turn on
                        isVillaOn[11]=true;
                        bl.sendData("1012");
                        Toast.makeText(getApplicationContext(), "Villa 12 ON",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            //********************//
            case R.id.bBlock13:
                if (saleModeState) {
                    createDialog(13);
                } else {
                    if(isVillaOn[12])
                    {
                        //turn off
                        isVillaOn[12]=false;
                        bl.sendData("0013");
                        Toast.makeText(getApplicationContext(), "Villa 13 OFF",
                                Toast.LENGTH_SHORT).show();
                    }else
                    {
                        //turn on
                        isVillaOn[12]=true;
                        bl.sendData("1013");
                        Toast.makeText(getApplicationContext(), "Villa 13 ON",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            //--------------------------------------------------------------------------------------------//
            // ALL ON
            case R.id.ButtonLEDON:
                Log.v("::OPENING.java::", "[ ALL ON ][ 8888 ]");
                bl.sendData(codeAllOn);
                setAllFlatStatusOff();
                Toast.makeText(getApplicationContext(), "All ON",
                        Toast.LENGTH_SHORT).show();
                break;
            //ALL OFF
            case R.id.ButtonLEDOFF:
                Log.v("::OPENING.java::", "[ ALL OFF ][ 0000 ]");
                Toast.makeText(getApplicationContext(), "All OFF",
                        Toast.LENGTH_SHORT).show();
                bl.sendData(codeAllOff);
                setAllFlatStatusOff();
                break;
            //EFFECT MODE
            case R.id.bEffect:
                Log.v(":::OPENING.java::", "[ Effect ][ 9100 ]");
                bl.sendData(codeEffect);
                break;
            //ON SALE
            case R.id.bOnSale:
                Log.v("::OPENING.java::", "[ ON SALE ALL ][ 9400 ]:::");
                bl.sendData(codeOnSale);
                break;
            //COMMERCIAL
            case R.id.bCommercial:
                Log.v("::OPENING.java::", "[ COMMERCIAL ][ 5300 ]:::");
                bl.sendData(codeCommercial);
                break;
        }
    }

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
        if (!config.isEmulatorMode()) {
            bl.BT_onPause();
        }

    }

    //********************************************************************************************************
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mainL.setBackgroundResource(R.drawable.villa_background_4);
        final Dialog emailDialog = new Dialog(Opening.this,
                android.R.style.Theme_DeviceDefault);
        emailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        emailDialog.setCancelable(false);
        emailDialog.setContentView(R.layout.dialoglayout);
        emailDialog.show();
        if (!config.isEmulatorMode()) {
            Thread connection = new Thread() {

                public void run() {
                    BT_is_connect = bl.BT_Connect(address, false);
                    bl.sendData("9999");

                }
            };
            connection.start();
        }

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    emailDialog.dismiss();
                    //bl.sendData("9999");  //UNCOMMENT

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
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void setAllFlatStatusOff() {
        for (int i = 0; i < 13; i++) {
            isVillaOn[i]=false;
        }
        Log.e("Opening.java", "...All flat status cleared !...");
    }

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
                        Toast.makeText(activity.getBaseContext(), "System Connected", Toast.LENGTH_SHORT).show();
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
    }   //********************************************************

    public void getVillaStatus() {
        for (int i = 0; i < 13; i++) {
            try {
                isVillaSoldList[i] = sharedPref.getBoolean(blocks[0] + "_" + i, false);
                if (isVillaSoldList[i] == true) {
                    SoldListArray.add(String.valueOf(i + 1));
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                Log.e("Cant find flat status", "::Opening.java::");
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    public void setVillaStatus(int villaNumber, Boolean status) {
        //TODO Update listView !
        int indexVillaNumber = --villaNumber;
        isVillaSoldList[indexVillaNumber] = true;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(blocks[0] + "_" + indexVillaNumber, status);
        editor.commit();
        SoldListArray.clear();
        adapter.notifyDataSetChanged();
        getVillaStatus();

    }

    //----------------------------------------------------------------------------------------------

    public void createDialog(final int villaNumber){
        AlertDialog.Builder builderSell = new AlertDialog.Builder(this);

                    builderSell.setTitle("Confirm");
                    if(isVillaSoldList[villaNumber-1])
                    {
                        builderSell.setMessage("Un-Sell Villa "+villaNumber+" ?");

                    }else

                    {
                        builderSell.setMessage("Sell Villa "+villaNumber+" ?");
                    }
                        builderSell.setPositiveButton("YES",new DialogInterface.OnClickListener()
                        {

                                public void onClick (DialogInterface dialog,int which){
                                // Do nothing but close the dialog

                                if (isVillaSoldList[villaNumber-1]) {
                                    setVillaStatus(villaNumber, false);
                                    int data2Send = 2000+villaNumber;
                                    bl.sendData(String.valueOf(data2Send));
                                    Toast.makeText(getApplicationContext(), "Villa "+villaNumber+" Un-Sold",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    setVillaStatus(villaNumber, true);
                                    int data2Send = 3000+villaNumber;
                                    bl.sendData(String.valueOf(data2Send));
                                    Toast.makeText(getApplicationContext(), "Villa "+villaNumber+" Sold",
                                            Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                                }
                    });

                        builderSell.setNegativeButton("NO",new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick (DialogInterface dialog,int which){

            // Do nothing
            dialog.dismiss();
        }
        });
        AlertDialog alert = builderSell.create();
        alert.show();
    }

}
