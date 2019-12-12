package com.cornellappdev.android.eatery.util;

import android.content.Context;

import com.cornellappdev.android.eatery.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/* Class that manages and saves user data to the local filesystem. Encryption is taken from:
 * https://stackoverflow.com/questions/1132567/encrypt-password-in-configuration-files
 * Upon pushing to the play store, change the encryption values in strings
 */
public class AccountManagerUtil {
    // Returns null if there is no existing account stored on the device
    public static String[] readSavedCredentials(Context context) {
        FileInputStream inputStream;
        String fileContents = "";
        try {
            inputStream = context.openFileInput("saved_data");
            byte nextByte = -1;
            while (inputStream.available() > 0) {
                nextByte = (byte) inputStream.read();
                fileContents += (char) nextByte;
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SecretKeySpec key = createSecretKey(context);
        String decrypted = decrypt(fileContents, key);
        // If the decrypted text has a newline character in it, then read netid and password
        if (decrypted != null && decrypted.indexOf('\n') > 0) {
            String temp_netid = decrypted.substring(0, decrypted.indexOf('\n'));
            String temp_pass = decrypted.substring(decrypted.indexOf('\n') + 1);
            String[] to_return = new String[2];
            to_return[0] = temp_netid;
            to_return[1] = temp_pass;
            return to_return;
        }
        return null;
    }

    public static void outputCredentialsToFile(String netid, String pass, Context context) {
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput("saved_data", Context.MODE_PRIVATE);
            String outputString = netid + '\n' + pass;
            SecretKeySpec key = createSecretKey(context);
            outputStream.write(encrypt(outputString, key).getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void eraseSavedCredentials(Context context) {
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput("saved_data", Context.MODE_PRIVATE);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Generate a key. Will be the same upon each instance of running the app
    private static SecretKeySpec createSecretKey(Context context) {
        try {
            char[] key = context.getResources().getString(R.string.encryption_key).toCharArray();
            byte[] salt = context.getResources().getString(R.string.encryption_salt).getBytes();
            int encryptionIteration = 4000;
            int keyLength = 128;
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2withHmacSHA1And8BIT");
            PBEKeySpec keySpec = new PBEKeySpec(key, salt, encryptionIteration, keyLength);
            SecretKey keyTmp = keyFactory.generateSecret(keySpec);
            return new SecretKeySpec(keyTmp.getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String encrypt(String property, SecretKeySpec key) {
        try {
            Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            pbeCipher.init(Cipher.ENCRYPT_MODE, key);
            AlgorithmParameters parameters = pbeCipher.getParameters();
            IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
            byte[] cryptoText = pbeCipher.doFinal(property.getBytes(StandardCharsets.UTF_8));
            byte[] iv = ivParameterSpec.getIV();
            return base64Encode(iv) + ":" + base64Encode(cryptoText);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String base64Encode(byte[] bytes) {
        return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
    }

    private static String decrypt(String string, SecretKeySpec key) {
        try {
            if (string.indexOf(":") > 0) {
                String iv = string.split(":")[0];
                String property = string.split(":")[1];
                Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                pbeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(base64Decode(iv)));
                return new String(pbeCipher.doFinal(base64Decode(property)));
            } else {
                return null;
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] base64Decode(String property) {
        return android.util.Base64.decode(property, android.util.Base64.DEFAULT);
    }
}