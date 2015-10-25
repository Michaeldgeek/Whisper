package challenge.magnet.android.whisper.databases.TablesDefn;

import android.provider.BaseColumns;

/**
 * Created by User Pc on 10/19/2015.
 */
public final class FriendsTable {

    public FriendsTable(){}

    /* Inner class that defines the table contents */
    public static abstract class TableDfn implements BaseColumns {
        public static final String TABLE_NAME = "FbFriends";
        public static final String COLUMN_NAME_DISPLAY_NAME = "displayname";
        public static final String COLUMN_NAME_FBID = "fbid";
        public static final String COLUMN_NAME_PROFILE_PIC = "profilepic";

    }
}
