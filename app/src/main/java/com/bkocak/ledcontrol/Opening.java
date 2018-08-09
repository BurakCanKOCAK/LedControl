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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bkocak.ledcontrol.wifi.RESTService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

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
    public static String[] blocks = {"Flats","Commercials"};
    //Thresholds
    public static int[] numberOfFlats = {109,4};
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
    private static Button bOnSaleType1, bOnSaleType2, bEffect, bOnSale;
    //Test Mode Buttons
    private static Button bEntrance1On,bEntrance2On,bEntrance1Off,bEntrance2Off,b1_1On,b1_1Off,b3_1On,b3_1Off,bCommercialOn,bCommercialOff,bCommercialControl,bFlats;
    private static Button bA1BlOCK, bA2BlOCK, bCommercials;
    private static Button Ablock, Acommercial, B1block, B2block, Bcommercial, C1block, C2block, Ccommercial, D1block, D2block, E1block, E2block, Gcommercial, bFlatsOn, bFlatsOff;
    private static Button AOn, B1On, B2On, C1On, C2On, D1On, D2On, E1On, E2On, AComOn, BComOn, CComOn, GComOn;
    private static Button AOff, B1Off, B2Off, C1Off, C2Off, D1Off, D2Off, E1Off, E2Off, AComOff, BComOff, CComOff, GComOff;
    private static Button saleMode;
    private static Switch autoEffect;
    private static ListView SoldList;
    private static ImageView salesListBackgroundImage;
    private static cBluetooth bl = null;
    private static boolean BT_is_connect;
    private static AbsoluteLayout mainL;
    private static boolean saleModeState = false;
    public static Handler handler;
    static Dialog dialog;

    public ArrayList<String> SoldListArray;
    ArrayAdapter<String> adapter;
    private static Boolean[] isVillaSoldList;
    private static Boolean[] isVillaOn = {false, false, false, false, false, false, false, false, false, false, false, false, false};
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
    public final static int TIMER_START = 0;
    public final static int TIMER_RESET = 1;
    public final static int TIMER_STOP = 2;
    public static Timer timerObj = null;
    public static int time2Effect;
    public static boolean isAutoEffect = false;

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
        this.sharedPref = getSharedPreferences("data",
                MODE_PRIVATE);
        time2Effect = sharedPref.getInt("effectTime", 1800000);
        //-------------------------//
        handler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                //String content = msg.obj.toString();
                switch (msg.arg1) {
                    case TIMER_START:
                        //START TIMER WHEN ACTIVITY INITIALIZED
                        if (timerObj == null) {
                            timerObj = new Timer();
                            TimerTask timerTaskObj = new TimerTask() {
                                public void run() {
                                    try {
                                        RESTService.effect();
                                        Message msg = new Message();
                                        msg.arg1 = TIMER_STOP;
                                        handler.sendMessage(msg);
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            timerObj.schedule(timerTaskObj, time2Effect, time2Effect);
                            //Toast.makeText(getApplicationContext(), "TIMER STARTED", Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(getApplicationContext(), "TIMER ALREADY STARTED!", Toast.LENGTH_LONG).show();
                        }


                        break;

                    case TIMER_RESET:
                        //RESET TIMER (IN CASE OF NEW COMMAND RECEPTION)
                        if (isAutoEffect) {
                            if (timerObj != null) {
                                timerObj.cancel();
                            }
                            timerObj = new Timer();
                            final TimerTask timerTaskObj = new TimerTask() {
                                public void run() {
                                    try {
                                        RESTService.effectAsync();
                                        timerObj.cancel();
                                        timerObj = new Timer();
                                        Message msg = new Message();
                                        msg.arg1 = TIMER_RESET;
                                        handler.sendMessage(msg);
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            timerObj.schedule(timerTaskObj, time2Effect, time2Effect);
                            //Toast.makeText(getApplicationContext(), "TIMER RESET", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case TIMER_STOP:
                        //STOP TIMER
                        //Toast.makeText(getApplicationContext(), "TIMER STOPPED", Toast.LENGTH_LONG).show();
                        break;
                }
            }

        };
        //-------------------------//
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opening_xml);

        salesListBackgroundImage = (ImageView) findViewById(R.id.salesListBackground);
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
                        sleep(200);
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
        autoEffect = (Switch) findViewById(R.id.autoEffect);

        bFlatsOn = (Button) findViewById(R.id.bFlatsOn);
        bFlatsOff = (Button) findViewById(R.id.bFlatsOff);

        bEntrance1On= (Button) findViewById(R.id.bEntrance1On);
        bEntrance2On= (Button) findViewById(R.id.bEntrance2On);
        bEntrance1Off= (Button) findViewById(R.id.bEntrance1Off);
        bEntrance2Off= (Button) findViewById(R.id.bEntrance2Off);
        b1_1On= (Button) findViewById(R.id.b1_1ON);
        b1_1Off= (Button) findViewById(R.id.b1_1OFF);
        b3_1On= (Button) findViewById(R.id.b3_1ON);
        b3_1Off= (Button) findViewById(R.id.b3_1OFF);
        bCommercialOn= (Button) findViewById(R.id.bCommercialOn);
        bCommercialOff= (Button) findViewById(R.id.bCommercialOff);
        bCommercialControl= (Button) findViewById(R.id.bCommercialControl);
        bFlats= (Button) findViewById(R.id.bFlats);

        bEntrance1On.setOnClickListener(this);
        bEntrance2On.setOnClickListener(this);
        bEntrance1Off.setOnClickListener(this);
        bEntrance2Off.setOnClickListener(this);
        b1_1On.setOnClickListener(this);
        b1_1Off.setOnClickListener(this);
        b3_1On.setOnClickListener(this);
        b3_1Off.setOnClickListener(this);
        bCommercialOn.setOnClickListener(this);
        bCommercialOff.setOnClickListener(this);
        bCommercialControl.setOnClickListener(this);
        bFlats.setOnClickListener(this);

        bFlatsOn.setOnClickListener(this);
        bFlatsOff.setOnClickListener(this);
        bDisconnect.setOnClickListener(this);
        bConnect.setOnClickListener(this);
        ButtonLEDOFF.setOnClickListener(this);
        ButtonLEDON.setOnClickListener(this);
        bEffect.setOnClickListener(this);

        autoEffect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isAutoEffect = b;
                if (!isAutoEffect) {
                    Toast.makeText(getApplicationContext(), "Auto Effect Disabled", Toast.LENGTH_LONG).show();
                    timerObj.cancel();
                } else {
                    //---//
                    Toast.makeText(getApplicationContext(), "Auto Effect Enabled (" + String.valueOf(time2Effect / 60000) + "mins)", Toast.LENGTH_LONG).show();
                    Message msg = new Message();
                    msg.arg1 = TIMER_RESET;
                    handler.sendMessage(msg);
                    //---//
                }
            }
        });
        autoEffect.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final Dialog d = new Dialog(Opening.this);
                d.setTitle("Timer (Minutes)");
                d.setContentView(R.layout.dialog);
                Button b1 = (Button) d.findViewById(R.id.button1);
                Button b2 = (Button) d.findViewById(R.id.button2);
                final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
                np.setMaxValue(300);
                np.setMinValue(1);
                np.setWrapSelectorWheel(false);
                b1.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sharedPref = getSharedPreferences("data",
                                MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        String key = "effectTime";
                        editor.putInt(key, np.getValue() * 60 * 1000);
                        time2Effect = np.getValue() * 60 * 1000;
                        editor.commit();
                        Toast.makeText(getApplicationContext(), "Effect timer set for " + String.valueOf(np.getValue()) + "mins.", Toast.LENGTH_LONG).show();
                        d.dismiss();
                    }
                });
                b2.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                d.show();
                return false;
            }
        });

        //--------------------------------------------------------------------------------------------//
        saleMode = (Button) findViewById(R.id.bSalesMode);
        //--------------------------------------------------------------------------------------------//
        bOnSaleType1 = (Button) findViewById(R.id.bOnSaleType1);
        bOnSaleType2 = (Button) findViewById(R.id.bOnSaleType2);
        bOnSale = (Button) findViewById(R.id.bOnSale);
        //bMainBlock = (Button) findViewById(R.id.bMainBlock);

        saleMode.setOnClickListener(this);

        bOnSaleType1.setOnClickListener(this);
        bOnSaleType2.setOnClickListener(this);
        bOnSale.setOnClickListener(this);
        //bMainBlock.setOnClickListener(this);
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
    public static void resetTimer() {
        Message msg = new Message();
        msg.arg1 = TIMER_RESET;
        handler.sendMessage(msg);
    }

    //********************************************************************************************************
    public void BTOff(View view) {
        //TODO Check bt is connected , if yes then cut connection .If no then do nothing
        Log.i("::OPENING.java::", "::: BTOff() :::");
        Log.i("::OPENING.java::", "::: - BT alloff Button Pressed - :::");

        bl.BT_onPause();
        // myBluetoothAdapter.disable();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Log.e("::EXCEPTION THROWED::", "::(!) BTOff() (!)::");
            e.printStackTrace();
        }
        resetTimer();
    }


    //********************************************************************************************************
    public void BTOn(final View view) {
        //TODO Check bt is connected , if yes then do nothing . If not Then connect.
        Log.i("::OPENING.java::", ":::BTOn():::");
        BT_is_connect = bl.BT_Connect(address, false);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Log.e("::EXCEPTION THROWED::", "::(!) BTOn() (!)::");
            e.printStackTrace();
        }
        resetTimer();
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
            /*
            case R.id.bA1BLOCK:
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
            */
            case R.id.bEntrance1On:
                //mainL.setBackgroundResource(R.drawable.alloff);
                try {
                    RESTService.flatBuildingStatus("Entrance1","on");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resetTimer();
                break;

            case R.id.bEntrance1Off:
                //mainL.setBackgroundResource(R.drawable.alloff);
                try {
                    RESTService.flatBuildingStatus("Entrance1","off");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resetTimer();
                break;

            case R.id.bEntrance2On:
                //mainL.setBackgroundResource(R.drawable.alloff);
                try {
                    RESTService.flatBuildingStatus("Entrance2","on");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resetTimer();
                break;

            case R.id.bEntrance2Off:
                //mainL.setBackgroundResource(R.drawable.alloff);
                try {
                    RESTService.flatBuildingStatus("Entrance2","off");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resetTimer();
                break;

            case R.id.b1_1ON:
                //mainL.setBackgroundResource(R.drawable.alloff);
                try {
                    RESTService.flatBuildingStatus("11","on");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resetTimer();
                break;

            case R.id.b1_1OFF:
                //mainL.setBackgroundResource(R.drawable.alloff);
                try {
                    RESTService.flatBuildingStatus("11","off");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resetTimer();
                break;

            case R.id.b3_1OFF:
                //mainL.setBackgroundResource(R.drawable.alloff);
                try {
                    RESTService.flatBuildingStatus("31","off");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resetTimer();
                break;

            case R.id.b3_1ON:
                //mainL.setBackgroundResource(R.drawable.alloff);
                try {
                    RESTService.flatBuildingStatus("31","on");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resetTimer();
                break;

            case R.id.bCommercialOn:
                //mainL.setBackgroundResource(R.drawable.alloff);
                try {
                    RESTService.flatBuildingStatus("C","on");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resetTimer();
                break;

            case R.id.bCommercialOff:
                //mainL.setBackgroundResource(R.drawable.alloff);
                try {
                    RESTService.flatBuildingStatus("C","off");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resetTimer();
                break;

            case R.id.bFlatsOn:
                //mainL.setBackgroundResource(R.drawable.all);
                try {
                    RESTService.flatBuildingStatus("F","on");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resetTimer();
                break;
            case R.id.bFlatsOff:
                //mainL.setBackgroundResource(R.drawable.alloff);
                try {
                    RESTService.flatBuildingStatus("F","off");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resetTimer();
                break;

            case R.id.bFlats:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mainL.setBackgroundResource(R.drawable.e1);
                    }
                });

                name = "Flats";
                editor.putString(key, name);
                editor.commit();
                openMain = new Intent("com.bkocak.ledcontrol.MainActivity");
                resetTimer();
                startActivity(openMain);
                break;
            case R.id.bCommercialControl:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mainL.setBackgroundResource(R.drawable.e2);
                    }
                });

                name = "Commercials";
                editor.putString(key, name);
                editor.commit();
                openMain = new Intent("com.bkocak.ledcontrol.MainActivity");
                resetTimer();
                startActivity(openMain);
                break;

            //-------------------------------------------------------------------
            /*
            case R.id.bBlock1:
                if (saleModeState) {
                    createDialog(1);
                } else {
                    if(isVillaOn[0])
                    {
                        //turn off
                        isVillaOn[0]=false;
                        bl.sendData("0001");
                        Log.v("::OPENING.java::", "[ VILLLA 1 ][ alloff ]");
                        Toast.makeText(getApplicationContext(), "Villa 1 alloff",
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

            //********************
            case R.id.bBlock2:
                if (saleModeState) {
                    createDialog(2);
                } else {
                    if(isVillaOn[1])
                    {
                        //turn off
                        isVillaOn[1]=false;
                        bl.sendData("0002");
                        Toast.makeText(getApplicationContext(), "Villa 2 alloff",
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
            */
            //--------------------------------------------------------------------------------------------//
            // all ON
            case R.id.ButtonLEDON:
                Log.v("::OPENING.java::", "[ all ON ][ 8888 ]");
                //mainL.setBackgroundResource(R.drawable.all);
                //bl.sendData(codeAllOn);
                //setAllFlatStatusOff();
                try {
                    RESTService.allOn();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(getApplicationContext(), "All On",Toast.LENGTH_SHORT).show();
                resetTimer();
                break;
            //all alloff
            case R.id.ButtonLEDOFF:
                Log.v("::OPENING.java::", "[ all alloff ][ 0000 ]");
                //mainL.setBackgroundResource(R.drawable.alloff);
                //Toast.makeText(getApplicationContext(), "All alloff",Toast.LENGTH_SHORT).show();
                //bl.sendData(codeAllOff);
                //setAllFlatStatusOff();
                try {
                    RESTService.allOff();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resetTimer();
                break;
            //EFFECT MODE
            case R.id.bEffect:
                //mainL.setBackgroundResource(R.drawable.all);
                Log.v(":::OPENING.java::", "[ Effect ][ 9100 ]");
                //bl.sendData(codeEffect);
                try {
                    RESTService.effect();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resetTimer();
                break;
            //ON SALE
            case R.id.bOnSale:
                //mainL.setBackgroundResource(R.drawable.all);
                Log.v("::OPENING.java::", "[ ON SALE all ][ 9400 ]:::");
                //bl.sendData(codeOnSale);
                try {
                    RESTService.showOnSale();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resetTimer();
                break;
        }
    }

    // ****************END OF HANDLER ****************************
    // ----------------ON--------------------------------------------
    public void on(View view) {
        /*
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
        */
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
    // ------------------------BT alloff--------------------------
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
        //mainL.setBackgroundResource(R.drawable.alloff);
        if (isAutoEffect) {
            autoEffect.setChecked(true);
        }
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
                    sleep(100);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    emailDialog.dismiss();
                    //bl.sendData("9999");  //UNCOMMENT

                    Thread timer2 = new Thread() {
                        public void run() {
                            try {
                                sleep(100);

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
            isVillaOn[i] = false;
        }
        Log.e("Opening.java", "...All flat status cleared !...");
    }

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

    public void createDialog(final int villaNumber) {
        AlertDialog.Builder builderSell = new AlertDialog.Builder(this);

        builderSell.setTitle("Confirm");
        if (isVillaSoldList[villaNumber - 1]) {
            builderSell.setMessage("Un-Sell Villa " + villaNumber + " ?");

        } else

        {
            builderSell.setMessage("Sell Villa " + villaNumber + " ?");
        }
        builderSell.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog

                if (isVillaSoldList[villaNumber - 1]) {
                    setVillaStatus(villaNumber, false);
                    int data2Send = 2000 + villaNumber;
                    bl.sendData(String.valueOf(data2Send));
                    Toast.makeText(getApplicationContext(), "Villa " + villaNumber + " Un-Sold",
                            Toast.LENGTH_SHORT).show();
                } else {
                    setVillaStatus(villaNumber, true);
                    int data2Send = 3000 + villaNumber;
                    bl.sendData(String.valueOf(data2Send));
                    Toast.makeText(getApplicationContext(), "Villa " + villaNumber + " Sold",
                            Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        builderSell.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });
        AlertDialog alert = builderSell.create();
        alert.show();
    }

}
