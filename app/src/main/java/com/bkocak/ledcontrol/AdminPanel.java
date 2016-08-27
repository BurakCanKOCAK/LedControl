package com.bkocak.ledcontrol;

import android.app.Activity;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Burak on 24/08/16.
 */

public class AdminPanel extends Activity implements View.OnClickListener{
    private EditText etMacAddress;
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

        etMacAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do Nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do Nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int letterCounter=etMacAddress.getText().toString().replace(":","").length();
                if(letterCounter%2==0&&letterCounter!=0)
                {
                    etMacAddress.setText(etMacAddress.getText()+":");
                }
                if(letterCounter==17)
                {

                }
            }
        });

        sharedPrefs  = getSharedPreferences("config",MODE_PRIVATE);
        macAddress = sharedPrefs.getString("MAC_ADDRESS", "XX:XX:XX:XX:XX:XX");
        etMacAddress.setText(macAddress);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.bUpdateMacAddress:
                //TODO Get MAC Address and commit to shared preferences.
                break;
        }
    }
}
