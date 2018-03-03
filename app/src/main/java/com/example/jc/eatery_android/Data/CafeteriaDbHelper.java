package com.example.jc.eatery_android.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JC on 3/2/18.
 */

public class CafeteriaDbHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "cafeteria.db";
    private static final int DATABASE_VERSION = 1;
    public CafeteriaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_TABLE =

                "CREATE TABLE " + CafeteriaContract.CafeteriaEntry.TABLE_NAME + " (" +


                        CafeteriaContract.CafeteriaEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +


                        CafeteriaContract.CafeteriaEntry.COLUMN_DATA + " TEXT NOT NULL "  +

                        ");";
        db.execSQL(SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ CafeteriaContract.CafeteriaEntry.TABLE_NAME);
        onCreate(db);
    }
}
