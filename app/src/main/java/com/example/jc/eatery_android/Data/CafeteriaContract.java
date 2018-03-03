package com.example.jc.eatery_android.Data;

import android.provider.BaseColumns;

/**
 * Created by JC on 3/2/18.
 */

public class CafeteriaContract {

    public static final class CafeteriaEntry implements BaseColumns {
        public static final String TABLE_NAME = "cafeteria";
        public static final String COLUMN_DATA = "json";
        public static final String COLUMN_ID = "columnId";

    }
}