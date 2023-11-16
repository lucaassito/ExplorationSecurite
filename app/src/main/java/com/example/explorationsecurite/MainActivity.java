package com.example.explorationsecurite;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class MainActivity extends AppCompatActivity {

    public static final int SERVER_PORT = 12346;
    public static final String SERVER_IP = "192.168.60.36";


    private static final String TAGSSL = "SSLClientExample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String message = "TestEncodeDecode";

        try {
            byte[] encrypted = EncryptionUtils.encrypt(message.getBytes());

            System.out.println("Encrypted message : " + encrypted);

            try {
                byte[] decrypted = EncryptionUtils.decrypt(encrypted);

                System.out.println("Decrypted message : " + new String(decrypted, StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickSendButton(View v) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        byte[] messageToEncrypt = ((EditText) findViewById(R.id.editTextMessage)).getText().toString().getBytes();

        byte[] encryptedMessage = EncryptionUtils.encrypt(messageToEncrypt);

        new SSLClientTask(this).execute();

        //TO DO : send message
    }

    private static class SSLClientTask extends AsyncTask<Void, Void, Void> {

        private Context context;

        public SSLClientTask(Context context) {
            this.context = context;
        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... params) {
            SSLClient sslClient = new SSLClient(context);
            sslClient.execute();
            return null;
        }
    }
}