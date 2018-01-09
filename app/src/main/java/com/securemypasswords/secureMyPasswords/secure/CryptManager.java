package com.securemypasswords.secureMyPasswords.secure;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CryptManager {

    private final String cryptAlgorithm;

    public CryptManager(String cryptAlgorithm){
        this.cryptAlgorithm = cryptAlgorithm;
    }

    public String encrypt(String data, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher cipher = Cipher.getInstance(cryptAlgorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(encVal, Base64.DEFAULT);
    }

    public String decrypt(String data, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher cipher = Cipher.getInstance(cryptAlgorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodeValue = Base64.decode(data,Base64.DEFAULT);
        byte[] decValue = cipher.doFinal(decodeValue);
        return new String(decValue);
    }

    private SecretKeySpec generateKey(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes,0,bytes.length);
        byte[] key = digest.digest();
        return new SecretKeySpec(key, cryptAlgorithm);
    }
}
