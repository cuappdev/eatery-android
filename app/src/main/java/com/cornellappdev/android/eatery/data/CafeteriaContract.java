package com.cornellappdev.android.eatery.data;

import android.provider.BaseColumns;

/** Created by JC on 3/2/18. */

// TODO(lesley): explain purpose of this class
public class CafeteriaContract {

  public static final class CafeteriaEntry implements BaseColumns {
    public static final String TABLE_NAME = "cafeteria";
    public static final String COLUMN_DATA = "json";
  }
}
