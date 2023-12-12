package com.example.explorationsecurite;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtils {

    private static String ALGORITHM = "AES";
    private static String MODE = "AES/CBC/PKCS5PADDING";
    private static String IV = "abcdefghabcdefgh";
    private static String Key = "azertyuiopazerty";


    public static byte[] encrypt(byte[] message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        SecretKeySpec secretKeySpec = new SecretKeySpec(Key.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(MODE);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(IV.getBytes()));
        return cipher.doFinal(message);
    }

    public static byte[] decrypt(byte[] message) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {

        SecretKeySpec secretKeySpec = new SecretKeySpec(Key.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(MODE);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(IV.getBytes()));
        return cipher.doFinal(message);
    }

}
