package com.cornellappdev.android.eatery.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CafeteriaDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "cafeteria.db";
    private static final int DATABASE_VERSION = 2;

    public CafeteriaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery =
                "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, %s TEXT NOT "
                        + "NULL);";
        final String SQL_CREATE_TABLE = String.format(createTableQuery,
                CafeteriaContract.CafeteriaEntry.TABLE_NAME,
                CafeteriaContract.CafeteriaEntry._ID,
                CafeteriaContract.CafeteriaEntry.COLUMN_DATA);
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CafeteriaContract.CafeteriaEntry.TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String json) {
        ContentValues values = new ContentValues();
        values.put(CafeteriaContract.CafeteriaEntry.COLUMN_DATA, json);
        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert(CafeteriaContract.CafeteriaEntry.TABLE_NAME, null, values);
        db.close();
        return result == -1 ? false : true;
    }

    public String getLastRow() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String lastRowQuery = "SELECT * FROM %s ORDER BY %s DESC LIMIT 1";
        String query = String.format(lastRowQuery, CafeteriaContract.CafeteriaEntry.TABLE_NAME,
                CafeteriaContract.CafeteriaEntry._ID);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            dbString =
                    cursor.getString(
                            cursor.getColumnIndex(CafeteriaContract.CafeteriaEntry.COLUMN_DATA));
        }
        cursor.close();
        return dbString;
    }

    public void removeData() {
        SQLiteDatabase db = getWritableDatabase();
        String deleteQuery = "DELETE FROM %s WHERE %s = 1";
        db.execSQL(String.format(deleteQuery, CafeteriaContract.CafeteriaEntry.TABLE_NAME,
                CafeteriaContract.CafeteriaEntry._ID));
    }

    public long getProfilesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, CafeteriaContract.CafeteriaEntry.TABLE_NAME);
        db.close();
        return count;
    }

    public String databaseToString() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + CafeteriaContract.CafeteriaEntry.TABLE_NAME + " WHERE 1";

        // Cursor point to a location in your results
        // rawQuery can do SELECT etc
        // CURSOR points to first in the queried result
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (c.moveToNext()) {
            String columnEntry = c.getString(
                    c.getColumnIndex(CafeteriaContract.CafeteriaEntry.COLUMN_DATA));
            if (columnEntry != null) {
                dbString += columnEntry + " ";
            }
        }

        db.close();
        return dbString;
    }
}
