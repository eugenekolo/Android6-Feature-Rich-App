package com.flyingcats.vacation.db;

import android.provider.BaseColumns;

/**
 * Created by Jonathan on 4/3/16.
 */

public class ItemContract {
    public static final String DB_NAME = "com.flyingcats.vacation.items";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "items";

    public class Columns {
        public static final String ITEM = "item";
        public static final String _ID = BaseColumns._ID;
    }
}
