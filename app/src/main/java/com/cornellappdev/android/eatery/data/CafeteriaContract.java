package com.cornellappdev.android.eatery.data;

import android.provider.BaseColumns;

public class CafeteriaContract {

  public static final class CafeteriaEntry implements BaseColumns {
    public static final String TABLE_NAME = "cafeteria";
    public static final String COLUMN_DATA = "json";
  }
}
