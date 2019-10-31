package com.cornellappdev.android.eatery.util;

import android.content.Context;
import com.cornellappdev.android.eatery.model.enums.CacheType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class InternalStorage{

    // Prevent instantiation with private access
    private InternalStorage() {}

    public static void writeObject(Context context, CacheType key, Object object) throws IOException {
        FileOutputStream fos = context.openFileOutput(key.getString(), Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    public static Object readObject(Context context, CacheType key) throws IOException,
            ClassNotFoundException {
            FileInputStream fis = context.openFileInput(key.getString());
            ObjectInputStream ois = new ObjectInputStream(fis);
            return ois.readObject();
    }
}