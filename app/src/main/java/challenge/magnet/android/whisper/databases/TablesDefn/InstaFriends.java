package challenge.magnet.android.whisper.databases.TablesDefn;

import android.provider.BaseColumns;

/**
 * Created by User Pc on 10/23/2015.
 */
public final class InstaFriends {

    public InstaFriends(){}

    /* Inner class that defines the table contents */
    public static abstract class TableDfn implements BaseColumns {
        public static final String TABLE_NAME = "InstaFriends";
        public static final String COLUMN_NAME_DISPLAY_NAME = "displayname";
        public static final String COLUMN_NAME_INSTAGRAMID= "instaid";
        public static final String COLUMN_NAME_PROFILE_PIC = "profilepic";

    }
}
