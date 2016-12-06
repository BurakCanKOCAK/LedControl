package com.bkocak.ledcontrol;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

import java.util.Random;

/**
 * Created by BurakCan on 11/07/2016.
 */
public class Registration extends Activity {
    private static final String FIREBASE_URL = "https://ledcontrol-9a2b6.firebaseio.com/";
    Button register;
    TextView textView;
    EditText editText;
    private String mUsername;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);
        setupUsername();
        initialize();

        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase("https://ledcontrol-9a2b6.firebaseio.com/");

        Firebase alanRef = ref.child("Users").child("User1");
        User alan = new User("Burak Can Kocak", "Password");
        alanRef.setValue(alan);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User mUser = new User(editText.getText().toString(), "password");
                saveToFirebase(mUser);
            }
        });
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void initialize() {
        register = (Button) findViewById(R.id.bRegister);
        textView = (TextView) findViewById(R.id.tvRegister);
        editText = (EditText) findViewById(R.id.etRegister);

    }

    private void setupUsername() {
        SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
        mUsername = prefs.getString("username", null);
        if (mUsername == null) {
            Random r = new Random();
            // Assign a random user name if we don't have one saved.
            mUsername = "JavaUser" + r.nextInt(100000);
            prefs.edit().putString("username", mUsername).commit();
        }
    }

    private void saveToFirebase(User user) {
        Firebase ref = new Firebase("https://ledcontrol-9a2b6.firebaseio.com/");
        Firebase alanRef = ref.child("Users").child("Username");
        this.user = user;
        alanRef.setValue(user);
    }


}
