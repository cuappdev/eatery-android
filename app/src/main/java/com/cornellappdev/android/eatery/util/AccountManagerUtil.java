package com.cornellappdev.android.eatery.util;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AccountManagerUtil {
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
        if (fileContents.indexOf('\n') > 0) {
            String temp_netid = fileContents.substring(0, fileContents.indexOf('\n'));
            String temp_pass = fileContents.substring(fileContents.indexOf('\n') + 1);
            String[] to_return = new String[2];
            to_return[0]=temp_netid;
            to_return[1]=temp_pass;
            return to_return;
        }
        return null;
    }

    public static void outputCredentialsToFile(String netid, String pass, Context context) {
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput("saved_data", Context.MODE_PRIVATE);
            outputStream.write(netid.getBytes());
            outputStream.write('\n');
            outputStream.write(pass.getBytes());
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
}
