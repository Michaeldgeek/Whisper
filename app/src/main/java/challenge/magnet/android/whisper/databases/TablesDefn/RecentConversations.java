package challenge.magnet.android.whisper.databases.TablesDefn;

import android.provider.BaseColumns;

/**
 * Created by User Pc on 10/16/2015.
 */
public final class RecentConversations {

    public RecentConversations(){}

    /* Inner class that defines the table contents */
    public static abstract class TableDefn implements BaseColumns {
        public static final String TABLE_NAME = "recentConversations";
        public static final String COLUMN_NAME_USER = "username";
        public static final String COLUMN_NAME_DELIVERED = "delivered";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_MESSAGE_TYPE = "messageType";
        public static final String COLUMN_NAME_MESSAGE_SENT = "sent";
        public static final String COLUMN_NAME_MESSAGE_IS_LEFT = "isLeft";
        public static final String COLUMN_NAME_MESSAGE_ID = "messageId";
    }

}
