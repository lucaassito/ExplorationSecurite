package com.example.explorationsecurite;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {

    private Com com;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        com = new Com(this);
    }

    public void onClickSendButton(View v){
        String msg = ((EditText) findViewById(R.id.editTextMessage)).getText().toString();
        com.sendMessage(msg);
    }

    public void onClickConnectButton(View view){
        com.establishConnection();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        com.disconnect();
    }
}