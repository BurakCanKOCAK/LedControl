package com.bkocak.ledcontrol;
//**************************************************************************************************

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.server.converter.StringToIntConverter;

//**************************************************************************************************
public class MainActivity extends Activity implements OnClickListener {
    //IDEAS :
    //TODO      1)Use sharedPrefs for already on or off flats use : "A1_10" ("XX_YY , XX:BlockName YY:Flat Number
    //TODO  and save as boolean  "1" for already on , "0" for off flats.
    //TODO      2)Assign already onFlats' numbers to listView on every onCreate method recall and update list every
    //TODO  on/off button pressed
    //----------------------------------------------------------------------------------------------
    private static final String FILENAME = "myFile.txt";
    static Intent openMain;
    // private String address = "00:14:03:18:20:95";
    // 30:14:06:09:09:34 (yeni)W
    // private static String address = "30:14:06:26:03:67"; //(tiflis)
    // private static String address = "30:14:06:09:09:34";// (Bulancak/Giresun)
    // private static String address = "20:14:04:29:35:28"; // (Nawroz City)
    //private static String address = "98:D3:31:B3:11:8F";
    //private static String address = "00:14:04:01:33:64"; //Benim modul
    private static String address = "98:D3:32:10:52:F6"; //Karabuk (Patyo) - 2016
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public SharedPreferences sharedPref;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static cBluetooth bl = null;
    ArrayAdapter<String> adapter;
    private static boolean BT_is_connect;
    private String cmdSend = "";
    private static boolean flag = false;
    static boolean reconnect_flag = false;
    private static ImageView Indicator;
    private static final int REQUEST_ENABLE_BT = 1;
    private static Button b1, b2, b3, b4, b5, b6, b7, b8, b9, b0, bYak,
            bSondur, bTop, bb1_1, bb2_1, bb3_1, bb4_1, bb5_1, bb6_1, bEffect,
            bBTOff, bBTOn, bErase, bMainMenu, bSell, bUnSell, bOnSale, bAllOn, bAllOff;
    private static TextView tvData, tvDatatoSend, tvBlock, tvBTStatus;
    private static RelativeLayout RelLay;
    private static EditText eT_sell;
    private static SlidingDrawer slidingDrawer1;
    private static int daire = 0, daire2 = 0;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private static String block_name = null;
    private Handler mUiHandler;
    private Handler mBackgroundHandler;
    private static int[] saved_list = new int[10000];
    private static Boolean[] isFlatOnList;
    private static int adminCount = 0;

    public ArrayList<String> FlatOnList;
    private ArrayAdapter<String> BTArrayAdapter;

    static boolean isFlatOff = true;
    static boolean isFlatOn = true;
    private static Button bALLC, bALLB, bALLA;
    private PowerManager.WakeLock wl;
    private static StringBuilder sb = new StringBuilder();
    private static ListView lvOnFlatNumbers;
    private static String TAG_MSG_HANDLER = "BT_MESSAGE_HANDLER";
    private static String TAG_CONTROL = "CONTROL_LEVEL";
    Config config = new Config();

    // --------ON CREATE -------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keyboard_xml);
        //
        lvOnFlatNumbers = (ListView) findViewById(R.id.lvOnFlatNumbers);
        FlatOnList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtItem, FlatOnList);
        lvOnFlatNumbers.setAdapter(adapter);
        lvOnFlatNumbers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object o = lvOnFlatNumbers.getItemAtPosition(position);
                String str = (String) o;//As you are using Default String Adapter
                tvDatatoSend.setText(str);
                tvDatatoSend.setTextColor(Color.BLACK);
                daire = Integer.valueOf(str);
            }
        });
        //
        if (!config.isEmulatorMode()) {
            bl = new cBluetooth(this, mHandler);
            bl.sendData("9999");
            // take an instance of BluetoothAdapter - Bluetooth radio
            myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            // setPrefButtons();
            if (myBluetoothAdapter == null) {
                // Bluetooth adapter yoksa disable et

                Toast.makeText(getApplicationContext(),
                        "Your device does not support Bluetooth", Toast.LENGTH_LONG)
                        .show();
            }
        }

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Power Lock On");
        wl.acquire();

/*
        Thread connection = new Thread() {
            public void run() {
                bl.checkBTState();
            }
        };
        connection.start();
*/

        this.sharedPref = getSharedPreferences("data",
                MODE_PRIVATE);
        block_name = sharedPref.getString("block", "null");
        getFlatStatus();

        // Indicator = (ImageView) findViewById(R.id.ivIndicator);
        // Indicator.setBackgroundResource(R.drawable.red);
        //--------------------------------------------------------------------------------------------//
        //Block general operations . To enable Sliding drawer , comment out setVisibility code line
        slidingDrawer1 = (SlidingDrawer) findViewById(R.id.slidingDrawer1);
        slidingDrawer1.setVisibility(View.VISIBLE);   //To disable slidingDrawer1 , make here INVISIBLE
        //--------------------------------------------------------------------------------------------//
        //Buttons
        bAllOff = (Button) findViewById(R.id.bAllOff);
        bAllOn = (Button) findViewById(R.id.bAllOn);
        bMainMenu = (Button) findViewById(R.id.bMainMenu);
        bErase = (Button) findViewById(R.id.bErase);

        tvData = (TextView) findViewById(R.id.tvData);

        b1 = (Button) findViewById(R.id.bOne);
        b2 = (Button) findViewById(R.id.bTwo);
        b3 = (Button) findViewById(R.id.bThree);
        b4 = (Button) findViewById(R.id.bFour);
        b5 = (Button) findViewById(R.id.bFive);
        b6 = (Button) findViewById(R.id.bSix);
        b7 = (Button) findViewById(R.id.bSeven);
        b8 = (Button) findViewById(R.id.bEight);
        b9 = (Button) findViewById(R.id.bNine);
        b0 = (Button) findViewById(R.id.bZero);
        bSondur = (Button) findViewById(R.id.bOff);
        bYak = (Button) findViewById(R.id.bOn);
        bTop = (Button) findViewById(R.id.bTop);
        bSell = (Button) findViewById(R.id.bSell);
        bUnSell = (Button) findViewById(R.id.bUnSell);
        bOnSale = (Button) findViewById(R.id.bOnSale);

        eT_sell = (EditText) findViewById(R.id.eT_sell);
        // eT_sell.setText("0");
        bb2_1 = (Button) findViewById(R.id.b2_1);
        bb3_1 = (Button) findViewById(R.id.b3_1);

        bBTOn = (Button) findViewById(R.id.bBTOn);
        bBTOff = (Button) findViewById(R.id.bBTOff);
        bBTOn.setVisibility(View.INVISIBLE);
        bBTOff.setVisibility(View.INVISIBLE);
        // *************************
        // ONCLICK THIS ------------
        bAllOff.setOnClickListener(this);
        bAllOn.setOnClickListener(this);
        bMainMenu.setOnClickListener(this);
        bErase.setOnClickListener(this);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        b5.setOnClickListener(this);
        b6.setOnClickListener(this);
        b7.setOnClickListener(this);
        b8.setOnClickListener(this);
        b8.setOnClickListener(this);
        b9.setOnClickListener(this);
        b0.setOnClickListener(this);
        bSondur.setOnClickListener(this);
        bYak.setOnClickListener(this);
        bTop.setOnClickListener(this);
        bSell.setOnClickListener(this);
        bUnSell.setOnClickListener(this);
        bOnSale.setOnClickListener(this);

        bb2_1.setOnClickListener(this);
        bb3_1.setOnClickListener(this);
        tvData.setOnClickListener(this);
        setPrefButtons();
        // ------------------BT ON--------------
        bBTOn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                tvData.setText("Connecting...");
                BT_is_connect = bl.BT_Connect(address, false);
            }
        });
        // ------------------BT OFF-----------
        bBTOff.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                tvBTStatus.setText("BT Connection OFF");
                tvData.setText("Disconnected");
                bl.BT_onPause();
                // myBluetoothAdapter.disable();
                // RelLay.setBackgroundResource(R.drawable.back_red);

            }
        });

        // **************************

        tvData = (TextView) findViewById(R.id.tvData);
        tvDatatoSend = (TextView) findViewById(R.id.tvDatatoSend);
        tvBlock = (TextView) findViewById(R.id.tvBlock);

        tvBlock.setText(block_name + " Block Selected");

        tvBTStatus = (TextView) findViewById(R.id.tvBTStatus);
        RelLay = (RelativeLayout) findViewById(R.id.RelLay_keyboard);
        mHandler.postDelayed(sRunnable, 600000);
    }

    //**********************************************************************************************
    // ***********END OF ON CREATE***************************
    private final MyHandler mHandler = new MyHandler(this);
    //**********************************************************************************************
    private final static Runnable sRunnable = new Runnable() {
        public void run() {
        }
    };

    //**********************************************************************************************
    // ---------------- HANDLER ---------------------------------
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case cBluetooth.BL_NOT_AVAILABLE:
                        // tvBTStatus.setText("BT Not Available");
                        Log.e("::Main_Activity::HM::", ":::BLUETOOTH_NOT_AVAILABLE::");
                        Toast.makeText(activity.getBaseContext(),
                                "Bluetooth is not available", Toast.LENGTH_SHORT)
                                .show();
                        activity.finish();
                        break;
                    case cBluetooth.BL_INCORRECT_ADDRESS:
                        // tvBTStatus.setText("BT Incorrect Address");
                        Log.e("::Main_Activity::HM::",
                                ":::INCORRECT_MAC_ADDRESS::");
                        Toast.makeText(activity.getBaseContext(),
                                "Incorrect Bluetooth address", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case cBluetooth.BL_REQUEST_ENABLE:
                        Log.e("::Main_Activity::HM::",
                                ":::BLUETOOTH_ENABLE_REQUEST_SENT::");
                        tvBTStatus.setText("Connecting to " + block_name + "...");
                        BluetoothAdapter.getDefaultAdapter();
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        activity.startActivityForResult(enableBtIntent, 1);
                        break;
                    case cBluetooth.BL_SOCKET_FAILED:
                        // tvBTStatus.setText("BT Socket Failed!");
                        Toast.makeText(activity.getBaseContext(), "Connection Error!",
                                Toast.LENGTH_SHORT).show();
                        RelLay.setBackgroundResource(R.drawable.back_red);
                        bTop.setBackgroundResource(R.drawable.head_red);
                        tvData.setText("Disconnected");
                        socket_failed();
                        Log.e("::Main_Activity::HM::",
                                ":::BLUETOOTH_SOCKET_FAILED::");

                        reconnect_flag = true;

                        // Indicator.setBackgroundResource(R.drawable.red);
                        // activity.finish();
                        break;

                    case cBluetooth.BL_CONNECTED_OK:
                        tvData.setText("Connected");
                        RelLay.setBackgroundResource(R.drawable.back_green);
                        Toast.makeText(activity.getBaseContext(), "System Connected", Toast.LENGTH_SHORT).show();
                        tvBTStatus.setText("Connected to " + block_name);
                        //if (block_name.equals("C")) {
                        //    // block_name="C";
                        //    tvBTStatus.setText("Connected Block : " + "C");
                        //} else if (block_name.equals("D")) {
                        //   // block_name="D";
                        //    tvBTStatus.setText("Connected Block : " + "D");
                        //} else if (block_name.equals("E")) {
                        //    // block_name="E";
                        //    tvBTStatus.setText("Connected Block : " + "E");
                        //}
                        // tvBTStatus.setText("Connected Block : "+block_name);
                        RelLay.setBackgroundResource(R.drawable.back_green);
                        tvData.setText("Connected"); // Ba�lant�
                        Log.e("::Main_Activity::",
                                "::BLUETOOTH_CONNECTED_OK::");

                        break;

                    case cBluetooth.RECIEVE_MESSAGE: // if message is recieved (����
                        // ��������� ��������)
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);
                        sb.append(strIncom); // append string (������������ ������)

                        int FDataLineIndex = sb.indexOf("FData:"); // string with
                        // Flash Data
                        int FWOKLineIndex = sb.indexOf("FWOK"); // string with the
                        // message of the
                        // succesfull record
                        // in Flash
                        int endOfLineIndex = sb.indexOf("\r\n");

                        if (FDataLineIndex >= 0 && endOfLineIndex > 0
                                && endOfLineIndex > FDataLineIndex) {
                            String sbprint = sb.substring("FData:".length(),
                                    endOfLineIndex);
                            // sbprint = sbprint.replace("\r","").replace("\n","");

                            if (sbprint.substring(0, 1).equals("1")) {
                                // cb_AutoOFF.setChecked(true);
                            } else {
                                // cb_AutoOFF.setChecked(false);
                            }
                            Float edit_data_AutoOFF = Float.parseFloat(sbprint
                                    .substring(1, 4)) / 10;
                            // edit_AutoOFF.setText(String.valueOf(edit_data_AutoOFF));

                            sb.delete(0, sb.length());
                        } else if (FWOKLineIndex >= 0 && endOfLineIndex > 0
                                && endOfLineIndex > FWOKLineIndex) {
                            // Toast.makeText(activity.getBaseContext(),
                            // flash_success, Toast.LENGTH_SHORT).show();
                            sb.delete(0, sb.length());
                        } else if (endOfLineIndex > 0) {
                            // Toast.makeText(activity.getBaseContext(),
                            // error_get_data, Toast.LENGTH_SHORT).show();
                            sb.delete(0, sb.length());
                        }
                        break;
                    // ********************************** END RECEIVE FUNCTION
                    // *******************************************
                }

            }

        }

        private void socket_failed() {
            Thread connection = new Thread() {
                public void run() {
                    BT_is_connect = bl.BT_Connect(address, false);
                    try {
                        Log.e("::Main_Activity::SF::",
                                ":::SLEEP TIMER STARTED:2000::");
                        sleep(2000);
                        Log.e("::Main_Activity::SF::",
                                ":::SLEEP TIMER ENDED::");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            connection.start();
        }
    }

    // ****************END OF HANDLER ****************************
    // ----------------ON--------------------------------------------
    /*
     * public void on(View view) { if (!myBluetoothAdapter.isEnabled()) {
	 *
	 * Intent turnOnIntent = new Intent(
	 * BluetoothAdapter.ACTION_REQUEST_ENABLE);
	 * startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
	 *
	 * Toast.makeText(getApplicationContext(), "Bluetooth turned on",
	 * Toast.LENGTH_LONG).show();
	 *
	 * } else { Toast.makeText(getApplicationContext(),
	 * "Bluetooth is already on", Toast.LENGTH_LONG).show(); } }
	 */
    //**********************************************************************************************
    // --------------------ACTIVITY RESULT--------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (myBluetoothAdapter.isEnabled()) {
                tvData.setText("Connected");
                System.out.println(":::REMOTE DEVICE CONNECTED:::");
                bTop.setBackgroundResource(R.drawable.head_green);
            } else {
                tvData.setText("Disconnected");
                System.out.println(":::REMOTE DEVICE DISCONNECTED:::");
                bTop.setBackgroundResource(R.drawable.head_red);
            }
        }

    }

    //**********************************************************************************************
    // ------------------BROADCAST RECEIVER ----------------------
    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the
                // arrayAdapter
                BTArrayAdapter.add(device.getName() + "\n"
                        + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    //**********************************************************************************************
    // ------------------------BT OFF--------------------------
    public void off(View view) {
        // Indicator.setBackgroundResource(R.drawable.red);
        // myBluetoothAdapter.disable();
        bl.BT_onPause();
        // text.setText("Status: Disconnected");
        // Send.setEnabled(false);
        // Komut.setEnabled(false);
        Toast.makeText(getApplicationContext(), "Bluetooth turned off",
                Toast.LENGTH_LONG).show();
    }

    //**********************************************************************************************
    // --------------------ON DESTROY --------------------
    //**********************************************************************************************
    // ---------------------ON RESUME---------------------
    @Override
    protected void onResume() {
        Log.e("::Main_Activity::OR::", ":::OnResume:Start	::");
        super.onResume();
        Log.e("::Main_Activity::OR::",
                ":::Creating Connecting Dialog::");
        final Dialog connectDialog = new Dialog(MainActivity.this,
                android.R.style.Theme_DeviceDefault);
        connectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        connectDialog.setCancelable(false);
        connectDialog.setContentView(R.layout.dialoglayout);
        connectDialog.show();

        if (!config.isEmulatorMode()) {
            Thread connection = new Thread() {
                public void run() {
                    Log.e("::Main_Activity::CT::",
                            ":::Thread:Started::");
                    BT_is_connect = bl.BT_Connect(address, false);
                    bl.sendData("9999");

                }
            };
            connection.start();
        }
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(2200);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    connectDialog.dismiss();
                    //bl.sendData("9999");    //UNCOMMENT
                }

            }
        };
        timer.start();

        Log.e("::Main_Activity::OR::", ":::OnResume:End::");

		/*
         *
		 *
		 * if (myBluetoothAdapter.isEnabled()) {
		 *
		 * BT_is_connect = bl.BT_Connect(address, false); if (BT_is_connect ==
		 * true) { bTop.setBackgroundResource(R.drawable.head_green);
		 * tvData.setText("Connected");
		 *
		 * // Indicator.setBackgroundResource(R.drawable.green); } //
		 * text.setVisibility(View.VISIBLE); // text.setText("Status: Enabled");
		 * // Send.setEnabled(true); // Komut.setEnabled(true);
		 *
		 * } else { bTop.setBackgroundResource(R.drawable.head_red);
		 * tvData.setText("Disconnected");
		 *
		 * // Indicator.setBackgroundResource(R.drawable.red); //
		 * text.setText("Status: Disabled"); // Send.setEnabled(false); //
		 * Komut.setEnabled(false); }
		 */
    }

    //**********************************************************************************************
    // -----------------ON PAUSE ---------------------
    @Override
    protected void onPause() {
        super.onPause();
        // BT_is_connect = bl.BT_Connect(address, false);
        bl.BT_onPause();
    }

    @Override
    public void onClick(View v) {
        /*
         * if (reconnect_flag == true) { myBluetoothAdapter.enable();
		 * bl.BT_Connect(address, false); reconnect_flag = false;
		 *
		 * }
		 */

        switch (v.getId()) {

		/*
         * case R.id.bTop: bTop.setBackgroundResource(R.drawable.head_red);
		 * tvData.setText("Connecting..."); bl.checkBTState(); // -/
		 * bl.BT_Connect(address, false); break;
		 */
            case R.id.tvData:
                adminCount++;
                if (adminCount == 200) {
                    adminCount = 0;
                    Intent openMain2 = new Intent("com.bkocak.ledcontrol.AdminPanel");
                    startActivity(openMain2);
                }
                break;

            case R.id.bMainMenu:
                Log.e("::GENERAL::CTRL::::", ":::BT ON BUTTON PRESSED:::");
                openMain = new Intent("com.bkocak.ledcontrol.Opening");
                FlatOnList.clear();
                this.finish();
                startActivity(openMain);

                break;
            case R.id.bAllOn:
                Log.e("::MAIN ACTIVITY::", ":::ALL ON:::");
                bl.sendData(Opening.codeAllOn);
                tvDatatoSend.setText("-");
                tvDatatoSend.setTextColor(Color.GREEN);
                daire = 0;
                setAllFlatStatusOff();
                break;
            case R.id.bAllOff:
                Log.e("::MAIN ACTIVITY::", ":::ALL OFF:::");
                bl.sendData(Opening.codeAllOff);
                tvDatatoSend.setText("-");
                tvDatatoSend.setTextColor(Color.RED);
                daire = 0;
                setAllFlatStatusOff();
                break;
//--------------------------------------------------------------------------------------------//
//          TODO OnSale & Sale methods will be implemented if necessary.
            case R.id.b2_1:  //DISABLED
                if (block_name.equals("C")) {
                    bl.sendData("4100");
                } else if (block_name.equals("D")) {
                    bl.sendData("4200");
                } else if (block_name.equals("E")) {
                    bl.sendData("4300");
                }

                tvDatatoSend.setTextColor(Color.MAGENTA);
                // tvDatatoSend.setText("2+1");
                break;
            case R.id.b3_1: //DISABLED
                if (block_name.equals("C")) {
                    bl.sendData("4400");
                } else if (block_name.equals("D")) {
                    bl.sendData("4500");
                } else if (block_name.equals("E")) {
                    bl.sendData("4600");
                }
                tvDatatoSend.setTextColor(Color.MAGENTA);
                // tvDatatoSend.setText("3+1");
                break;

            case R.id.bSell:

                int flatNumberSell=0;
                try{
                    flatNumberSell=Integer.parseInt(eT_sell.getText().toString());
                }catch (Exception e)  //NUMERIK OLMAYAN KARAKTER IRILMESI DURUMU
                {
                    Toast.makeText(this.getBaseContext(),
                            "Please enter a valid flat number to sell.",Toast.LENGTH_SHORT).show();
                    break;
                }
                if (eT_sell.getText().toString().matches("")) { //BOS BIRAKILMASI DURUMU
                    Log.e("::::::ERROR:::::", eT_sell.getText().toString());
                    Toast.makeText(this.getBaseContext(),
                            "Please enter a valid flat number to sell.",
                            Toast.LENGTH_SHORT).show();

                    break;
                }else if(flatNumberSell>Opening.numberOfFlats[0] || flatNumberSell==0)  // 56DAN BUYUK DAIRE NUMARASI GIRILMESI DURUMU
                {
                    Log.e("::::::ERROR:::::", eT_sell.getText().toString());
                    Toast.makeText(this.getBaseContext(),
                            "Please enter a valid flat number to sell.(Max Flat Number is "+Opening.numberOfFlats[0]+")",
                            Toast.LENGTH_SHORT).show();
                    break;
                }

                switch(block_name)
                {
                    case "Main":
                        flatNumberSell += 3000;
                        bl.sendData(Integer.toString(flatNumberSell));
                        Log.e("[ FLAT SOLD ]::[ "+ block_name+" ]::[ FLAT NUMBER : ",
                                Integer.toString(flatNumberSell - 3000)+" ]");
                        break;
                }

                break;

            case R.id.bUnSell:
                int flatNumberUnSell=0;
                try{
                    flatNumberUnSell=Integer.parseInt(eT_sell.getText().toString());
                }catch (Exception e)  //NUMERIK OLMAYAN KARAKTER IRILMESI DURUMU
                {
                    Toast.makeText(this.getBaseContext(),
                            "Please enter a valid flat number to unsell.",Toast.LENGTH_SHORT).show();
                    break;
                }
                if (eT_sell.getText().toString().matches("")) { //BOS BIRAKILMASI DURUMU
                    Log.e("::::::ERROR:::::", eT_sell.getText().toString());
                    Toast.makeText(this.getBaseContext(),
                            "Please enter a valid flat number to unsell.",
                            Toast.LENGTH_SHORT).show();

                    break;
                }else if(flatNumberUnSell>Opening.numberOfFlats[0] || flatNumberUnSell==0)  // 56DAN BUYUK DAIRE NUMARASI GIRILMESI DURUMU
                {
                    Log.e("::::::ERROR:::::", eT_sell.getText().toString());
                    Toast.makeText(this.getBaseContext(),
                            "Please enter a valid flat number to unsell.(Max Flat Number is "+Opening.numberOfFlats[0]+")",
                            Toast.LENGTH_SHORT).show();
                    break;
                }

                switch(block_name)
                {
                    case "Main":
                        flatNumberUnSell += 2000;
                        bl.sendData(Integer.toString(flatNumberUnSell));
                        Log.e("[ FLAT UN SOLD ]::[ "+ block_name+" ]::[ FLAT NUMBER : ",
                                Integer.toString(flatNumberUnSell - 2000)+" ]");
                        break;
                }


                break;

            case R.id.bOnSale:
                Log.v("::OPENING.java::", "[ ON SALE ALL ][ 9400 ]:::");
                bl.sendData("9400");
                break;
//--------------------------------------------------------------------------------------------//
            case R.id.bOn:
                //Edit currently on leds list
                if (saved_list[daire + Opening.calculateBlockThresholdValue(block_name)] == 1) {
                    //Daire yanik konumdaysa tekrar data gondermeyi engelle
                    isFlatOff = false;
                } else if (tvDatatoSend.getText().equals("-") || daire == 0) {
                    isFlatOff = false;
                } else {
                    //Daire yanik degil ; yananlar listesine daireyi ekle.
                    saved_list[daire + Opening.calculateBlockThresholdValue(block_name)] = 1;
                    isFlatOff = true;
                }

                //fucker  ->  isFlatOff ?
                if (isFlatOff) {
                    if (daire == 0) {
                        tvDatatoSend.setText("-");
                    }
                    Log.v("::CTRL_ON_FUNC::", "--STARTING TO SEND--");
                    cmdSend = Integer.toString(daire + Opening.calculateBlockThresholdValue(block_name) + 1000);
                    Log.w("Block : " + block_name + " :", " Flat : " + daire + " ( On | Data : " + cmdSend + " )");
                    tvDatatoSend.setTextColor(Color.GREEN);
                    bl.sendData(cmdSend);
                    setFlatStatus(daire, true);
                    daire = 0;
                    daire2 = 0;
                    break;

                } else {
                    Log.e("bON - Invalid Message:", Integer.toString(daire));
                }

                daire = 0;
                daire2 = 0;
                break;
            //--------------------------------------------------------------------------------------------//
            case R.id.bOff:

                if (saved_list[daire + Opening.calculateBlockThresholdValue(block_name)] == 0) {
                    //Daire sonukse tekrar sondurme komutunu gondermeyi engellemek icin isFlatOn=false
                    isFlatOn = false;
                } else if (tvDatatoSend.getText().equals("-") || daire == 0) {
                    isFlatOn = false;
                } else {
                    //Daire yanik konumda , sondurme komutunu gonderebilmek icin isFlatOn=true set et ve
                    // yanan daireler listesinde dairenin degerini "0" (sonuk) yap
                    saved_list[daire + Opening.calculateBlockThresholdValue(block_name)] = 0;
                    isFlatOn = true;
                }

                //fucker2 -> isFlatOn ?
                if (isFlatOn) {
                    if (daire == 0) {
                        tvDatatoSend.setText("-");
                    }

                    int length = (int) (Math.log10(daire + Opening.calculateBlockThresholdValue(block_name)) + 1);
                    String data2Send = "";
                    for (int i = 0; i < (4 - length); i++) {
                        data2Send += "0";
                    }
                    data2Send += daire + Opening.calculateBlockThresholdValue(block_name);

                    Log.w("Block : " + block_name + " :", " Flat : " + daire + "( Off | Data : " + data2Send +
                            " )");
                    tvDatatoSend.setTextColor(Color.GRAY);
                    bl.sendData(data2Send);
                    setFlatStatus(daire, false);
                    daire = 0;
                }
                /*else if (daire == 0) {
                    tvDatatoSend.setTextColor(Color.GREEN);
                    Log.v("MODE :", "OFF");
                    bl.sendData(Integer.toString(daire));
                    bl.sendData(Integer.toString(daire));
                    bl.sendData(Integer.toString(daire));
                    bl.sendData(Integer.toString(daire));
                    tvDatatoSend.setText("OFF");
                    for (int i = 0; i < 865; i++) {
                        saved_list[i] = 0;
                    }
                    setFlatStatus(daire,false);
                    daire = 0;
                } */
                else {

                    Log.e(" bOff - Invalid Message", Integer.toString(daire));
                    tvDatatoSend.setTextColor(Color.RED);
                }
                daire = 0;
                daire2 = 0;
                break;

            case R.id.bOne:
                if (checkFlatNumber(block_name, 1)) {
                    tvDatatoSend.setTextColor(Color.BLACK);
                    tvDatatoSend.setText(Integer.toString(daire));
                }
                break;
            case R.id.bTwo:
                if (checkFlatNumber(block_name, 2)) {
                    tvDatatoSend.setTextColor(Color.BLACK);
                    tvDatatoSend.setText(Integer.toString(daire));
                }
                break;
            case R.id.bThree:
                if (checkFlatNumber(block_name, 3)) {
                    tvDatatoSend.setTextColor(Color.BLACK);
                    tvDatatoSend.setText(Integer.toString(daire));
                }

                break;
            case R.id.bFour:
                if (checkFlatNumber(block_name, 4)) {
                    tvDatatoSend.setTextColor(Color.BLACK);
                    tvDatatoSend.setText(Integer.toString(daire));
                }

                break;

            case R.id.bFive:
                if (checkFlatNumber(block_name, 5)) {
                    tvDatatoSend.setTextColor(Color.BLACK);
                    tvDatatoSend.setText(Integer.toString(daire));
                }

                break;
            case R.id.bSix:
                if (checkFlatNumber(block_name, 6)) {
                    tvDatatoSend.setTextColor(Color.BLACK);
                    tvDatatoSend.setText(Integer.toString(daire));
                }

                break;
            case R.id.bSeven:
                if (checkFlatNumber(block_name, 7)) {
                    tvDatatoSend.setTextColor(Color.BLACK);
                    tvDatatoSend.setText(Integer.toString(daire));
                }

                break;
            case R.id.bEight:
                if (checkFlatNumber(block_name, 8)) {
                    tvDatatoSend.setTextColor(Color.BLACK);
                    tvDatatoSend.setText(Integer.toString(daire));
                }

                break;
            case R.id.bNine:
                if (checkFlatNumber(block_name, 9)) {
                    tvDatatoSend.setTextColor(Color.BLACK);
                    tvDatatoSend.setText(Integer.toString(daire));
                }

                break;
            case R.id.bZero:
                if (tvDatatoSend.getText().toString().contains("-")) {
                    break;
                }
                if (checkFlatNumber(block_name, 0)) {
                    tvDatatoSend.setTextColor(Color.BLACK);
                    tvDatatoSend.setText(Integer.toString(daire));
                }
                break;
            //--------------------------------------------------------------------------------------
            case R.id.bErase:
                daire = daire / 10;
                if (daire == 0) {
                    tvDatatoSend.setText("-");
                } else {
                    tvDatatoSend.setText(Integer.toString(daire));
                }
                tvDatatoSend.setTextColor(Color.BLACK);

                break;
        }

    }

    //----------------------------------------------------------------------------------------------
    private static void setPrefButtons() {

        bb2_1.setVisibility(View.INVISIBLE);
        bb3_1.setVisibility(View.INVISIBLE);

        if (block_name.equals("C")) {

            bb2_1.setVisibility(View.VISIBLE);
            bb3_1.setVisibility(View.VISIBLE);

        }
        if (block_name.equals("D")) {

            bb2_1.setVisibility(View.VISIBLE);
            bb3_1.setVisibility(View.VISIBLE);

        }
        if (block_name.equals("E")) {

            bb2_1.setVisibility(View.VISIBLE);
            bb3_1.setVisibility(View.VISIBLE);

        }

    }

    //----------------------------------------------------------------------------------------------
    private boolean checkFlatNumber(String block, int numberPressed) {
        if (daire != 0 || numberPressed != 0) {
            int number = daire * 10 + numberPressed;
            for (int i = 0; i < Opening.blocks.length; i++) {
                if (block_name.equals(Opening.blocks[i])) {
                    if (number <= Opening.numberOfFlats[i]) {
                        daire = daire * 10 + numberPressed;
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            return false;
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------
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

    //----------------------------------------------------------------------------------------------
    public void getFlatStatus() {
        int index = 0;
        for (int j = 0; j < Opening.blocks.length; j++) {
            if (Opening.blocks[j].equals(block_name)) {
                index = j;
                break;
            }
        }

        isFlatOnList = new Boolean[Opening.numberOfFlats[index]];
        for (int i = 0; i < Opening.numberOfFlats[index]; i++) {
            try {
                isFlatOnList[i] = sharedPref.getBoolean(block_name + "_" + i, false);
                if (isFlatOnList[i] == true) {
                    FlatOnList.add(String.valueOf(i + 1));
                    adapter.notifyDataSetChanged();

                    saved_list[i + 1 + Opening.calculateBlockThresholdValue(block_name)] = 1;
                } else {
                    saved_list[i + 1 + Opening.calculateBlockThresholdValue(block_name)] = 0;
                }
            } catch (Exception e) {
                Log.e("Cant find flat status :", "Block :" + block_name + " - Flat :" + index);
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    public void setFlatStatus(int flatNumber, Boolean status) {
        //TODO Update listView !
        int index = 0;
        for (index = 0; index < Opening.blocks.length; index++) {
            if (Opening.blocks[index].equals(block_name)) {
                break;
            }
            index++;
        }

        int indexFlatNumber = --flatNumber;
        isFlatOnList[indexFlatNumber] = true;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(block_name + "_" + indexFlatNumber, status);
        editor.commit();
        FlatOnList.clear();
        adapter.notifyDataSetChanged();
        getFlatStatus();
        for (int i = 0; i < FlatOnList.size(); i++) {
            // if(FlatOnList.)
        }

    }

    //----------------------------------------------------------------------------------------------
    public void setAllFlatStatusOff() {
        for (int i = 0; i < Opening.blocks.length; i++) {
            for (int j = 0; j < Opening.numberOfFlats[i]; j++) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(Opening.blocks[i] + "_" + j, false);
                editor.commit();
            }
        }
        Log.e(TAG_CONTROL, "...All flat status cleared !...");
        FlatOnList.clear();
        adapter.notifyDataSetChanged();
        getFlatStatus();
    }
}
