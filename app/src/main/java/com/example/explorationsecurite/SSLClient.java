package com.example.explorationsecurite;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class SSLClient extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "SSLClient";
    private static final String KEYSTORE_PASSWORD = "marco";

    private Context context;

    public SSLClient(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            // Charger le certificat du serveur depuis les ressources
            InputStream serverCertInputStream = context.getResources().openRawResource(R.raw.cert);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("server", certificateFactory.generateCertificate(serverCertInputStream));

            // Initialiser le gestionnaire de confiance avec le certificat du serveur
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            // Initialiser le gestionnaire de clés avec le certificat du client (s'il est requis)
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, KEYSTORE_PASSWORD.toCharArray());

            // Initialiser le contexte SSL
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

            // Créer la socket SSL
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket("192.168.60.36", 12346);

            // Établir la connexion
            sslSocket.startHandshake();

            // Envoyer des données
            OutputStream outputStream = sslSocket.getOutputStream();
            outputStream.write("Hello, Server!".getBytes());

            // Recevoir des données
            InputStream inputStream = new BufferedInputStream(sslSocket.getInputStream());
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                String receivedData = new String(buffer, 0, bytesRead);
                Log.d(TAG, "Received data: " + receivedData);
            }

            // Fermer la connexion
            sslSocket.close();
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }

        return null;
    }
}
