package challenge.magnet.android.whisper.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.sromku.simple.fb.entities.Profile;
import java.util.ArrayList;
import java.util.List;
import challenge.magnet.android.whisper.R;
import challenge.magnet.android.whisper.adapters.FbRecyclerViewAdapter;
import challenge.magnet.android.whisper.databases.TablesDefn.FriendsTable;
import challenge.magnet.android.whisper.databases.TablesDefn.InstaFriends;
import challenge.magnet.android.whisper.databases.TablesDefn.RecentConversations;
import challenge.magnet.android.whisper.databases.TablesDefn.UserTableDefn;
import challenge.magnet.android.whisper.databases.TablesDefn.WhisperDB;
import challenge.magnet.android.whisper.models.FbUser;
import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;


public class ShowFriends extends ActionBarActivity{
     RecyclerView rvUsers;
    List<FbUser> userlist;
    FbRecyclerViewAdapter fbRecyclerViewAdapter;
    Intent intent;
    List<String> profilepics;
    List<String> id;
    List<String> displayname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        if(intent != null) {
           if (intent.getStringExtra("fb") != null && intent.getStringExtra("fb").contains("true")) {
               initLayout();
               fetchFbFrndsFrmDb();
            }
            else if(intent.getStringExtra("insta") != null && intent.getStringExtra("insta").contentEquals("true")){
               initLayout();
               fetchInstaFrndsFrmDb();
           }
            else{
               initLayout();
               fetchFrndsFrmDb();
           }

        }
    }

    private void fetchFrndsFrmDb() {
        WhisperDB whisperDB = new WhisperDB(this);
        SQLiteDatabase sqLiteDatabase = whisperDB.getReadableDatabase();
        String[] projections =  new String[]{UserTableDefn.TableDfn._ID,UserTableDefn.TableDfn.COLUMN_NAME_USERNAME,UserTableDefn.TableDfn.COLUMN_NAME_PROFILE_PIC,UserTableDefn.TableDfn.COLUMN_NAME_DISPLAY_NAME};
        Cursor cursor = sqLiteDatabase.query(false,UserTableDefn.TableDfn.TABLE_NAME, projections, null, null, null, null, null, null);
        if(cursor != null) {
            loopThruCursor(cursor,UserTableDefn.TableDfn.COLUMN_NAME_PROFILE_PIC,UserTableDefn.TableDfn.COLUMN_NAME_USERNAME,UserTableDefn.TableDfn.COLUMN_NAME_DISPLAY_NAME);
        }
        else{
            goToFindFrndsActivity();
        }
    }

    private String getUsername() {
        return null;
    }

    private void fetchInstaFrndsFrmDb() {
        WhisperDB whisperDB = new WhisperDB(this);
        SQLiteDatabase sqLiteDatabase = whisperDB.getReadableDatabase();
        String[] projections =  new String[]{InstaFriends.TableDfn._ID,InstaFriends.TableDfn.COLUMN_NAME_INSTAGRAMID,InstaFriends.TableDfn.COLUMN_NAME_PROFILE_PIC,FriendsTable.TableDfn.COLUMN_NAME_DISPLAY_NAME};
        Cursor cursor = sqLiteDatabase.query(false, InstaFriends.TableDfn.TABLE_NAME, projections, null, null, null, null, null, null);
        if(cursor != null) {
            loopThruCursor(cursor,InstaFriends.TableDfn.COLUMN_NAME_PROFILE_PIC,InstaFriends.TableDfn.COLUMN_NAME_INSTAGRAMID,InstaFriends.TableDfn.COLUMN_NAME_DISPLAY_NAME);
        }
        else{
            goToFindFrndsActivity();
        }
    }

    private void initLayout() {
        setContentView(R.layout.activity_whisperers);
        rvUsers = (RecyclerView) findViewById(R.id.rvUsers);
        userlist = new ArrayList<>();
        fbRecyclerViewAdapter = new FbRecyclerViewAdapter(this, userlist);
        rvUsers.setAdapter(new SlideInBottomAnimationAdapter(fbRecyclerViewAdapter));
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvUsers.setLayoutManager(layoutManager);
    }

    private void fetchFbFrndsFrmDb() {
        WhisperDB whisperDB = new WhisperDB(this);
        SQLiteDatabase sqLiteDatabase = whisperDB.getReadableDatabase();
        String[] projections =  new String[]{FriendsTable.TableDfn._ID,FriendsTable.TableDfn.COLUMN_NAME_FBID,FriendsTable.TableDfn.COLUMN_NAME_PROFILE_PIC,FriendsTable.TableDfn.COLUMN_NAME_DISPLAY_NAME};
        Cursor cursor = sqLiteDatabase.query(false, FriendsTable.TableDfn.TABLE_NAME, projections, null, null, null, null, null, null);
        if(cursor != null) {
            loopThruCursor(cursor,FriendsTable.TableDfn.COLUMN_NAME_PROFILE_PIC,FriendsTable.TableDfn.COLUMN_NAME_FBID,FriendsTable.TableDfn.COLUMN_NAME_DISPLAY_NAME);
        }
        else{
            goToFindFrndsActivity();
        }
    }

    private void goToFindFrndsActivity() {
        Intent intent = new Intent(this,FindFriendsSetup.class);
        startActivity(intent);
    }

    private void loopThruCursor(Cursor cursor,String colProfilePic,String colId,String colDisplay) {
        cursor.moveToFirst();
        profilepics = new ArrayList<>();
        id = new ArrayList<>();
        displayname = new ArrayList<>();
        for(int i = 0; i < cursor.getCount(); i++){
            profilepics.add(cursor.getString(cursor.getColumnIndex(colProfilePic)));
            id.add(cursor.getString(cursor.getColumnIndex(colId)));
            displayname.add(cursor.getString(cursor.getColumnIndex(colDisplay)));
            cursor.moveToNext();
        }
        if (intent.getStringExtra("fb") != null && intent.getStringExtra("fb").contains("true")) {
            checkUserFb(id);
        }
        else if(intent.getStringExtra("insta") != null && intent.getStringExtra("insta").contains("true")){
            checkUserInstagram(id);
        }
        else{
            checkUserAll(id);
        }
    }

    private void checkUserAll(List<String> id) {
        List<Cursor> cursor = new ArrayList<>();
        String selection = UserTableDefn.TableDfn.COLUMN_NAME_USERNAME;
        WhisperDB whisperDB = new WhisperDB(this);
        SQLiteDatabase sqLiteDatabase = whisperDB.getReadableDatabase();
        String[] columns = new String[]{UserTableDefn.TableDfn._ID,UserTableDefn.TableDfn.COLUMN_NAME_USERNAME};
        for(int i = 0; i < id.size(); i++){
            String[] args = new String[]{id.get(i)};
            cursor.add(i, sqLiteDatabase.query(true, UserTableDefn.TableDfn.TABLE_NAME, columns, selection + "!=?", args, null, null, null, null));
        }
        changeCursorToList(cursor);
    }

    private void checkUserInstagram(List<String> instaId) {
        List<Cursor> cursor = new ArrayList<>();
        String selection = UserTableDefn.TableDfn.COLUMN_NAME_INSTAGRAMID;
        WhisperDB whisperDB = new WhisperDB(this);
        SQLiteDatabase sqLiteDatabase = whisperDB.getReadableDatabase();
        String[] columns = new String[]{UserTableDefn.TableDfn._ID,UserTableDefn.TableDfn.COLUMN_NAME_USERNAME};
        for(int i = 0; i < instaId.size(); i++){
            String[] args = new String[]{instaId.get(i)};
            cursor.add(i, sqLiteDatabase.query(true, UserTableDefn.TableDfn.TABLE_NAME, columns, selection + "=?", args, null, null, null, null));
        }
        changeCursorToList(cursor);
    }

    private void checkUserFb(List<String> fbid) {
        List<Cursor> cursor = new ArrayList<>();
        String selection = UserTableDefn.TableDfn.COLUMN_NAME_FBID;
        WhisperDB whisperDB = new WhisperDB(this);
        SQLiteDatabase sqLiteDatabase = whisperDB.getReadableDatabase();
        String[] columns = new String[]{UserTableDefn.TableDfn._ID,UserTableDefn.TableDfn.COLUMN_NAME_USERNAME};
        for(int i = 0; i < fbid.size(); i++){
            String[] args = new String[]{fbid.get(i)};
            cursor.add(i, sqLiteDatabase.query(true, UserTableDefn.TableDfn.TABLE_NAME, columns, selection + "=?", args, null, null, null, null));
        }
        changeCursorToList(cursor);
    }

    private void changeCursorToList(List<Cursor> cursor) {
        List<FbUser> fbUserList = new ArrayList<>();
        for (int i = 0; i < cursor.size(); i++){
            cursor.get(i).moveToFirst();
            FbUser fbUser = new FbUser(); // also used as Instagram user
            fbUser.setfbProfilePic(profilepics.get(i));
            fbUser.setFbDisplayName(displayname.get(i));
            fbUser.setId(cursor.get(i).getString(cursor.get(i).getColumnIndex(UserTableDefn.TableDfn.COLUMN_NAME_USERNAME))); // actually this contain the whisper username
            fbUserList.add(i,fbUser);
        }
        refreshListView(fbUserList);
    }


    protected void refreshListView(final List<FbUser> fbUserList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fbRecyclerViewAdapter.clear();
                if (fbUserList != null && fbUserList.size() > 0) {
                    fbRecyclerViewAdapter.addAll(fbUserList);
                    rvUsers.getAdapter().notifyDataSetChanged();
                }
            }
        });
    }

}
