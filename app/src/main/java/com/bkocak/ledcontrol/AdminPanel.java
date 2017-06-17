package com.bkocak.ledcontrol;

import android.app.Activity;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import javax.xml.datatype.Duration;

/**
 * Created by Burak on 24/08/16.
 */

public class AdminPanel extends Activity implements View.OnClickListener{
    private EditText etMacAddress;
    private TextView tvMacAddress;
    private Button bUpdateMacAddress;
    private String macAddress;
    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_panel);
        initializeContent();

    }

    private void initializeContent() {
        etMacAddress = (EditText)findViewById(R.id.etMacAddress);
        bUpdateMacAddress = (Button)findViewById(R.id.bUpdateMacAddress);
        tvMacAddress = (TextView) findViewById(R.id.tvMacAddress);
        tvMacAddress.setText(Config.getMacAddress());

        bUpdateMacAddress.setOnClickListener(this);
      //  etMacAddress.setText(Config.getMacAddress());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.bUpdateMacAddress:
                String macAdd = etMacAddress.getText().toString();
                if(macAdd.length()==17)
                {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Config.getCtx());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("MAC_ADDRESS",macAdd.toUpperCase().toString());
                    editor.apply();

                    Config.setMacAddress(macAdd.toUpperCase().toString());
                    tvMacAddress.setText(macAdd.toUpperCase().toString());
                    Toast.makeText(getApplicationContext(), "Mac address successfully saved !", Toast.LENGTH_SHORT) .show();
                }else
                {
                    Toast.makeText(getApplicationContext(),"Mac address is not valid !", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
