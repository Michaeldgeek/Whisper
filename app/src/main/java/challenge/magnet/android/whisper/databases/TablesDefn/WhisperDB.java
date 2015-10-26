package challenge.magnet.android.whisper.databases.TablesDefn;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class WhisperDB extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_TABLE_RECENT_CHAT =
            "CREATE TABLE " + RecentChatTableDefnDB.RecentChatTableDfn.TABLE_NAME + " (" +
                    RecentChatTableDefnDB.RecentChatTableDfn._ID + " INTEGER PRIMARY KEY," +
                    RecentChatTableDefnDB.RecentChatTableDfn.COLUMN_NAME_DATE + TEXT_TYPE + NOT_NULL  + COMMA_SEP +
                    RecentChatTableDefnDB.RecentChatTableDfn.COLUMN_NAME_PROFILE_PIC + TEXT_TYPE + NOT_NULL  + COMMA_SEP +
                    RecentChatTableDefnDB.RecentChatTableDfn.COLUMN_NAME_USER  + TEXT_TYPE + NOT_NULL + " UNIQUE" +

            " )";
    private static final String SQL_CREATE_TABLE_RECENT_CONVERSATIONS =
            "CREATE TABLE " + RecentConversations.TableDefn.TABLE_NAME + " (" +
                    RecentConversations.TableDefn._ID + " INTEGER PRIMARY KEY," +
                    RecentConversations.TableDefn.COLUMN_NAME_DATE + TEXT_TYPE + NOT_NULL  + COMMA_SEP +
                    RecentConversations.TableDefn.COLUMN_NAME_MESSAGE + TEXT_TYPE + NOT_NULL  + COMMA_SEP +
                    RecentConversations.TableDefn.COLUMN_NAME_MESSAGE_ID + TEXT_TYPE + " UNIQUE"  + COMMA_SEP +
                    RecentConversations.TableDefn.COLUMN_NAME_DELIVERED + TEXT_TYPE  + COMMA_SEP +
                    RecentConversations.TableDefn.COLUMN_NAME_USER + TEXT_TYPE + NOT_NULL  + COMMA_SEP +
                    RecentConversations.TableDefn.COLUMN_NAME_MESSAGE_TYPE + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    RecentConversations.TableDefn.COLUMN_NAME_MESSAGE_IS_LEFT + TEXT_TYPE  + COMMA_SEP +
                    RecentConversations.TableDefn.COLUMN_NAME_MESSAGE_SENT + TEXT_TYPE   +
                    " )";
    private static final String SQL_CREATE_TABLE_USERS =
            "CREATE TABLE " + UserTableDefn.TableDfn.TABLE_NAME + " (" +
                    UserTableDefn.TableDfn._ID + " INTEGER PRIMARY KEY," +
                    UserTableDefn.TableDfn.COLUMN_NAME_USERNAME + TEXT_TYPE + NOT_NULL + " UNIQUE" + COMMA_SEP +
                    UserTableDefn.TableDfn.COLUMN_NAME_EMAIL + TEXT_TYPE + NOT_NULL  + COMMA_SEP +
                    UserTableDefn.TableDfn.COLUMN_NAME_DISPLAY_NAME + TEXT_TYPE + NOT_NULL  + COMMA_SEP +
                    UserTableDefn.TableDfn.COLUMN_NAME_FBID + TEXT_TYPE  + COMMA_SEP +
                    UserTableDefn.TableDfn.COLUMN_NAME_INSTAGRAMID + TEXT_TYPE  + COMMA_SEP +
                    UserTableDefn.TableDfn.COLUMN_NAME_PROFILE_PIC + TEXT_TYPE  +
                    " )";
    private static final String SQL_CREATE_TABLE_FRIENDS =
            "CREATE TABLE " + FriendsTable.TableDfn.TABLE_NAME + " (" +
                    FriendsTable.TableDfn._ID + " INTEGER PRIMARY KEY," +
                    FriendsTable.TableDfn.COLUMN_NAME_DISPLAY_NAME + TEXT_TYPE + NOT_NULL  + COMMA_SEP +
                    FriendsTable.TableDfn.COLUMN_NAME_FBID + TEXT_TYPE +  " UNIQUE" + COMMA_SEP +
                    FriendsTable.TableDfn.COLUMN_NAME_PROFILE_PIC + TEXT_TYPE  +
                    " )";
    private static final String SQL_CREATE_TABLE_INSTAGRAM_FRIENDS =
            "CREATE TABLE " + InstaFriends.TableDfn.TABLE_NAME + " (" +
                    InstaFriends.TableDfn._ID + " INTEGER PRIMARY KEY," +
                    InstaFriends.TableDfn.COLUMN_NAME_DISPLAY_NAME + TEXT_TYPE + NOT_NULL  + COMMA_SEP +
                    InstaFriends.TableDfn.COLUMN_NAME_INSTAGRAMID + TEXT_TYPE +  " UNIQUE" + COMMA_SEP +
                    InstaFriends.TableDfn.COLUMN_NAME_PROFILE_PIC + TEXT_TYPE  +
                    " )";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Whisper.db";

    public WhisperDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_RECENT_CHAT);
        db.execSQL(SQL_CREATE_TABLE_RECENT_CONVERSATIONS);
        db.execSQL(SQL_CREATE_TABLE_USERS);
        db.execSQL(SQL_CREATE_TABLE_FRIENDS);
        db.execSQL(SQL_CREATE_TABLE_INSTAGRAM_FRIENDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
