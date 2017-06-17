package com.bkocak.ledcontrol;

/**
 * Created by BurakCan on 18/06/2016.
 */

//********************************************************************************************************
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;

//********************************************************************************************************
public class logoActivity extends Activity {
    private PowerManager.WakeLock wl;
    SharedPreferences prefs;

    // ********************************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialoglayout);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Power Lock On");
        wl.acquire();
        fetchMACAddress();
        //Enables Bluetooth If Not Enabled
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Config config = new Config();
        if(!config.isEmulatorMode()) {
            if (!mBluetoothAdapter.isEnabled()) {
                setBluetooth(true);
            }
        }
        //
        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(2000);

                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
                finally{
                    Intent openMain=new Intent ("com.bkocak.ledcontrol.Opening");
                    startActivity(openMain);
                }

            }
        };
        timer.start();
    }

    // ********************************************************************************************************
    public static boolean setBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (enable && !isEnabled) {
            return bluetoothAdapter.enable();
        } else if (!enable && isEnabled) {
            return bluetoothAdapter.disable();
        }
        // No need to change bluetooth state
        return true;
    }

    // ********************************************************************************************************
    private void fetchMACAddress(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String mac = preferences.getString("MAC_ADDRESS", "");
        Config.setCtx(this.getBaseContext());
        if(!mac.equalsIgnoreCase(""))
        {
            Config.setMacAddress(mac);
        }
    }

    // ********************************************************************************************************
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();

    }
}