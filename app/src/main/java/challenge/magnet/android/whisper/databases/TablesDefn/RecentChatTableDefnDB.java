package challenge.magnet.android.whisper.databases.TablesDefn;

import android.provider.BaseColumns;

public final class RecentChatTableDefnDB {

    public RecentChatTableDefnDB() {}

    /* Inner class that defines the table contents */
    public static abstract class RecentChatTableDfn implements BaseColumns {
        public static final String TABLE_NAME = "recentChat";
        public static final String COLUMN_NAME_USER = "username";
        public static final String COLUMN_NAME_DELIVERED = "delivered";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_NETWORK = "network";
        public static final String COLUMN_NAME_PROFILE_PIC = "profilePic";
        public static final String COLUMN_NAME_UNREAD_COUNT = "unreadCount";

    }



}
