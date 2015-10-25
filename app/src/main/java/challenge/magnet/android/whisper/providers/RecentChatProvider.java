package challenge.magnet.android.whisper.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import challenge.magnet.android.whisper.Constants;
import challenge.magnet.android.whisper.databases.TablesDefn.RecentChatTableDefnDB;
import challenge.magnet.android.whisper.databases.TablesDefn.RecentConversations;
import challenge.magnet.android.whisper.databases.TablesDefn.WhisperDB;


public class RecentChatProvider extends ContentProvider {
    private WhisperDB whisperDB;
    private Uri uri;
    private String[] projection;
    static final int RECENT_CHAT_CODE_MATCH = 1;
    static final int RECENT_CHAT_CODE_MATCH_ROW = 2;
    static final int RECENT_CONVERSATION_MATCH = 3;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{

        sUriMatcher.addURI(Constants.RECENT_CHAT_PROVIDER_AUTORITY,  RecentChatTableDefnDB.RecentChatTableDfn.TABLE_NAME,RECENT_CHAT_CODE_MATCH);//  matches the table recent chat
        sUriMatcher.addURI(Constants.RECENT_CHAT_PROVIDER_AUTORITY, RecentChatTableDefnDB.RecentChatTableDfn.TABLE_NAME +"/#",RECENT_CHAT_CODE_MATCH_ROW);//matches a row in recent chat
        sUriMatcher.addURI(Constants.RECENT_CHAT_PROVIDER_AUTORITY,  RecentConversations.TableDefn.TABLE_NAME,RECENT_CONVERSATION_MATCH);

    }

    @Override
    public boolean onCreate() {
        whisperDB = new WhisperDB(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
        this.uri = uri;
        this.projection = projection;
        if (sUriMatcher.match(uri) == RECENT_CHAT_CODE_MATCH){
            SQLiteDatabase sqLiteDatabase = whisperDB.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.query(true, RecentChatTableDefnDB.RecentChatTableDfn.TABLE_NAME, projection, null, null, null, null, null, null);
            return cursor;
        }
        else {
            return null;
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        //this.uri = uri;
        //int i = sUriMatcher.match(uri);
        //sUriMatcher.match(this.uri);
      //  if(sUriMatcher.match(uri) == RECENT_CONVERSATION_MATCH){
            SQLiteDatabase sqLiteDatabase = whisperDB.getWritableDatabase();
            Long newRowId  = sqLiteDatabase.insert(RecentConversations.TableDefn.TABLE_NAME, null, contentValues);
            return uri.withAppendedPath(Uri.parse(Constants.RECENT_CHAT_PROVIDER_AUTORITY + "/" +RecentConversations.TableDefn.TABLE_NAME),newRowId.toString());
        //}

//        else {
  //          return null;
    //    }

    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
