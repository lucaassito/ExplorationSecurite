package com.example.explorationsecurite;

import android.content.Context;
import android.content.res.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class ConnectTCP {

    private static final String SERVER_IP = "192.168.16.161";
    private static final int SERVER_PORT = 12346;
    private SSLSocket sslSocket;
    private ExecutorService readExecutorService;
    private ExecutorService writeExecutorService;

    private final Context context;

    boolean reading;

    public ConnectTCP(Context context) {
        this.context = context;
    }

    public interface ConnectTCPListener {
        void onConnected();
        void onConnectionError(Exception e);
        void onWrite(String message);
        void onRead(String message);
        void onDisconnected();
        void onDisconnectionError();
    }

    ConnectTCPListener connectTCPListener;

    public void setConnectTCPListener(ConnectTCPListener listener) {
        this.connectTCPListener = listener;
    }

    public void connect() {
        readExecutorService = Executors.newSingleThreadExecutor();
        writeExecutorService = Executors.newSingleThreadExecutor();

        readExecutorService.execute(() -> {
            try {
                Resources resources = context.getResources();
                InputStream certificateFile = resources.openRawResource(R.raw.server);
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                Certificate certificate = certificateFactory.generateCertificate(certificateFile);

                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);
                keyStore.setCertificateEntry("server", certificate);

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                sslSocket = (SSLSocket) sslSocketFactory.createSocket(SERVER_IP, SERVER_PORT);

                sslSocket.startHandshake();

                connectTCPListener.onConnected();
                read();

            } catch (Exception e) {
                connectTCPListener.onConnectionError(e);
            }
        });
    }

    public void disconnect() {
        if (sslSocket != null && !sslSocket.isClosed()) {
            try {
                sslSocket.close();

                connectTCPListener.onDisconnected();
            } catch (IOException e) {
                connectTCPListener.onDisconnectionError();
            }
        }
    }

    public void write(String message) {
        writeExecutorService.execute(() -> {
            try {
                OutputStream outputStream = sslSocket.getOutputStream();

                byte[] msg = new byte[message.length() + 1];

                msg[0] = (byte) message.length();

                System.arraycopy(message.getBytes(), 0, msg,0, message.length());

                outputStream.write(msg);

                connectTCPListener.onWrite(message);
            } catch (IOException e) {
                connectTCPListener.onConnectionError(e);
            }
        });
    }

    public void read() {
        reading = true;
        readExecutorService.execute(() -> {
            try {
                InputStream inputStream = sslSocket.getInputStream();
                byte[] buffer = new byte[1024];

                while (reading) {
                    int bytesRead = inputStream.read(buffer);

                    if (bytesRead == -1) {
                        reading = false;
                        disconnect();
                    } else if (bytesRead > 0) {
                        String message = new String(buffer, 0, bytesRead);
                        connectTCPListener.onRead(message);
                    }
                }
            } catch (IOException e) {
                connectTCPListener.onConnectionError(e);
            }
        });
    }

}