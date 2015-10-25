package challenge.magnet.android.whisper;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ChosenVideo;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.kbeanie.imagechooser.api.VideoChooserListener;
import com.kbeanie.imagechooser.api.VideoChooserManager;
import com.kbeanie.imagechooser.exceptions.ChooserException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMXUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import challenge.magnet.android.whisper.activities.LoginActivity;
import challenge.magnet.android.whisper.adapters.MessageRecyclerViewAdapter;
import challenge.magnet.android.whisper.databases.TablesDefn.RecentChatTableDefnDB;
import challenge.magnet.android.whisper.databases.TablesDefn.RecentConversations;
import challenge.magnet.android.whisper.databases.TablesDefn.WhisperDB;
import challenge.magnet.android.whisper.models.MessageImage;
import challenge.magnet.android.whisper.models.MessageMap;
import challenge.magnet.android.whisper.models.MessageText;
import challenge.magnet.android.whisper.models.MessageVideo;
import challenge.magnet.android.whisper.services.GPSTracker;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;
import ly.kite.instagramphotopicker.InstagramPhoto;
import ly.kite.instagramphotopicker.InstagramPhotoPicker;

public class ChatActivity extends ActionBarActivity implements OnMenuItemClickListener, OnMenuItemLongClickListener,ImageChooserListener,VideoChooserListener {
    public static final String KEY_MESSAGE_TEXT = "text";
    public static final String KEY_MESSAGE_IMAGE = "photo";
    public static final String KEY_MESSAGE_MAP = "location";
    public static final String KEY_MESSAGE_VIDEO = "video";
    public static final String KEY_MESSAGE_IS_TYPING = "typing";
    Intent intent;
    private RecyclerView rvMessages;
    private EditText chatEditText1;
    private ImageView enterChatView1;
    List<Object> messageList;
    private MessageRecyclerViewAdapter adapter;
    GPSTracker mGPS;
    private int chooserType;
    private ImageChooserManager imageChooserManager;
    private VideoChooserManager videoChooserManager;
    private String imgFilePath;
    MessageText messageTextObj;
    final private int INTENT_REQUEST_GET_IMAGES = 14;
    final private int INTENT_SELECT_VIDEO = 13;
    private FragmentManager fragmentManager;
    private DialogFragment mMenuDialogFragment;
    final MMXUser[] recipients= new MMXUser[1];


    // register event listner
    private MMX.EventListener mEventListener = new MMX.EventListener() {
        public boolean onMessageReceived(MMXMessage mmxMessage) {
            Date date = mmxMessage.getTimestamp();
            String dateString = String.valueOf(date.getHours()) + ":" + String.valueOf(date.getMinutes());
            String type = mmxMessage.getContent().get("type");

            switch (type) {
                case KEY_MESSAGE_TEXT:
                    updateList(type, mmxMessage.getContent().get("message"), true,dateString,mmxMessage.getSender().getUsername(),false,false);
                    saveToDb(mmxMessage.getContent().get("message"),mmxMessage.getContent().get("type"),mmxMessage.getId(),"true");
                    mmxMessage.acknowledge(new MMXMessage.OnFinishedListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }

                        @Override
                        public void onFailure(MMXMessage.FailureCode failureCode, Throwable throwable) {

                        }
                    });
                    break;
                case KEY_MESSAGE_IMAGE:
                    updateList(type, mmxMessage.getContent().get("message"), true, dateString, mmxMessage.getSender().getUsername(), false, false);
                    saveToDb(mmxMessage.getContent().get("message"),mmxMessage.getContent().get("type"),mmxMessage.getId(),"true");
                    mmxMessage.acknowledge(new MMXMessage.OnFinishedListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }

                        @Override
                        public void onFailure(MMXMessage.FailureCode failureCode, Throwable throwable) {

                        }
                    });
                    break;
                case KEY_MESSAGE_MAP:
                    updateList(type, mmxMessage.getContent().get("latitude") + "," + mmxMessage.getContent().get("longitude"), true,dateString,mmxMessage.getSender().getUsername(),false,false);
                    break;
                case KEY_MESSAGE_VIDEO:
                    updateList(type, mmxMessage.getContent().get("url"), true,dateString,mmxMessage.getSender().getUsername(),false,false);
                    break;
                case KEY_MESSAGE_IS_TYPING:
                    break;
            }
            return true;
        }

        @Override
        public boolean onMessageAcknowledgementReceived(MMXUser mmXid, String s) {
            messageText.setDelivered(true);
            rvMessages.getAdapter().notifyDataSetChanged();
            rvMessages.scrollToPosition(adapter.getItemCount() - 1);
            return true;
        }

    };
    private final TextWatcher watcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (chatEditText1.getText().toString().equals("")) {

            } else {
                enterChatView1.setImageResource(R.drawable.ic_chat_send);

            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.length()==0){
                enterChatView1.setImageResource(R.drawable.ic_chat_send);
            }else{
                enterChatView1.setImageResource(R.drawable.ic_chat_send_active);
            }
        }
    };
    private EditText.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press

                EditText editText = (EditText) v;

                if(v==chatEditText1)
                {
                    sendMessage();
                }



                return true;
            }
            return false;

        }
    };
    private String messageID;
    MessageText messageText;
    MessageImage messageImage;
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);
       if (isUserLoggedOut()){}
        else {
            // user is logged in
            /* move to next state, request details */
            intent = getIntent();
            if (intent != null ) {
                MMX.registerListener(mEventListener);
                setupActionBar(getChatUsername(intent), getChatPicture(intent));
                Cursor cursor = fetchPreviousConversation();
                mGPS = new GPSTracker(this);
                MMX.start();
                rvMessages = (RecyclerView) findViewById(R.id.rvMessages);
                chatEditText1 = (EditText) findViewById(R.id.chat_edit_text1);
                enterChatView1 = (ImageView) findViewById(R.id.enter_chat1);
                messageList = new ArrayList<>();
                adapter = new MessageRecyclerViewAdapter(this, messageList);
                rvMessages.setAdapter(new SlideInBottomAnimationAdapter(adapter));
                final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                layoutManager.setStackFromEnd(true);
                layoutManager.setReverseLayout(false);
                chatEditText1.setOnKeyListener(keyListener);
                enterChatView1.setOnClickListener(clickListener);
                chatEditText1.addTextChangedListener(watcher1);
                rvMessages.setLayoutManager(layoutManager);
                if(cursor != null){
                    displayPreviousMessages(cursor);
                }
                fragmentManager = getSupportFragmentManager();
                initMenuFragment();


            }
        }

    }

    private void sendMessage() {
        String messageText = chatEditText1.getText().toString();
        if (messageText.isEmpty()) {
            return;
        }
        String date = String.valueOf(new Date().getHours()) + ":" + String.valueOf(new Date().getMinutes());
        if(isUserLoggedOut()){}
        else {
            updateList(KEY_MESSAGE_TEXT, messageText, false, date, MMX.getCurrentUser().getUsername(), false, false);
            chatEditText1.setText(null);
            HashMap<String, String> content = new HashMap<>();
            content.put("type", KEY_MESSAGE_TEXT);
            content.put("message", messageText);
            send(content); // send to mmx server
            saveToDb(content.get("message"),content.get("type"), messageID,"false");// must be after send so that messageID is not empty.
        }

    }

    private void send(HashMap<String, String> content) {
        
        MMXMessage.OnFinishedListener<String> listener = new MMXMessage.OnFinishedListener<String>(){

            @Override
            public void onSuccess(String s) { 
                updateDb(messageID, RecentConversations.TableDefn.COLUMN_NAME_MESSAGE_SENT);
                //makeAsSent();
                if(status != null){
                    status.setText("sent");
                }
            }

            @Override
            public void onFailure(MMXMessage.FailureCode failureCode, Throwable throwable) {
                if(status != null){
                    status.setText("Not sent");
                }
            }
        };
        if(isUserLoggedOut()) {;}
        else {
            HashSet<MMXUser> recipients = new HashSet<>();
            recipients.add(new MMXUser.Builder().username(getChatUsername(intent)).build());
            messageID = new MMXMessage.Builder()
                    .content(content)
                    .recipients(recipients)
                    .build()
                    .send(listener);
        }
    }


    private boolean isUserLoggedOut(){
        if (MMX.getCurrentUser() == null) {
            if(mEventListener != null) {
                MMX.unregisterListener(mEventListener);
            }
            MMX.logout(null);
            Toast.makeText(getActivity(),"Your login credentials has expired.",Toast.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(),LoginActivity.class));
            return true;
        }
        else{
            return false;
        }
    }

    private void updateDb(String messageID,String colToUpdtate) {
        WhisperDB whisperDB = new WhisperDB(getActivity());
        ContentValues values = new ContentValues();
        values.put(colToUpdtate, "true");
        String selection = RecentConversations.TableDefn.COLUMN_NAME_MESSAGE_ID;
        String[] args = {messageID};
        SQLiteDatabase sqLiteDatabase = whisperDB.getWritableDatabase();
        sqLiteDatabase.update(RecentConversations.TableDefn.TABLE_NAME, values, selection + "=?", args);
    }

    private void saveToDb(String message, String type,String messageID,String isLeft) {
        String dateString = String.valueOf(new Date().getHours()) + ":" + String.valueOf(new Date().getMinutes());
        WhisperDB whisperDB = new WhisperDB(getActivity());
        ContentValues values = new ContentValues();
        values.put(RecentConversations.TableDefn.COLUMN_NAME_MESSAGE_TYPE,type);
        values.put(RecentConversations.TableDefn.COLUMN_NAME_USER,getChatUsername(intent));
        values.put(RecentConversations.TableDefn.COLUMN_NAME_MESSAGE, message);
        values.put(RecentConversations.TableDefn.COLUMN_NAME_DELIVERED, "false");
        values.put(RecentConversations.TableDefn.COLUMN_NAME_DATE, dateString);
        values.put(RecentConversations.TableDefn.COLUMN_NAME_MESSAGE_IS_LEFT, isLeft);
        values.put(RecentConversations.TableDefn.COLUMN_NAME_MESSAGE_SENT, "false");
        values.put(RecentConversations.TableDefn.COLUMN_NAME_MESSAGE_ID,messageID);
        SQLiteDatabase sqLiteDatabase = whisperDB.getWritableDatabase();
        sqLiteDatabase.insert(RecentConversations.TableDefn.TABLE_NAME, null, values);
    }



    private ImageView.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v==enterChatView1)
            {
                sendMessage();
            }

        }
    };



    private void displayPreviousMessages(Cursor cursor) {
        if(cursor.getCount() > 0){
            //cursor.moveToFirst();
            //for(int i = 0; i < cursor.getCount(); i++){
            while (cursor.moveToNext()) {
                String type = cursor.getString(cursor.getColumnIndex(RecentConversations.TableDefn.COLUMN_NAME_MESSAGE_TYPE));
                String content = cursor.getString(cursor.getColumnIndex(RecentConversations.TableDefn.COLUMN_NAME_MESSAGE));
                boolean orientation = Boolean.getBoolean(cursor.getString(cursor.getColumnIndex(RecentConversations.TableDefn.COLUMN_NAME_MESSAGE_IS_LEFT)));
                boolean isSent = Boolean.getBoolean(cursor.getString(cursor.getColumnIndex(RecentConversations.TableDefn.COLUMN_NAME_MESSAGE_SENT)));
                boolean isDelivered = Boolean.getBoolean(cursor.getString(cursor.getColumnIndex(RecentConversations.TableDefn.COLUMN_NAME_DELIVERED)));
                String user = cursor.getString(cursor.getColumnIndex(RecentConversations.TableDefn.COLUMN_NAME_USER));
                String date = cursor.getString(cursor.getColumnIndex(RecentConversations.TableDefn.COLUMN_NAME_DATE));
                switch (type) {
                    case KEY_MESSAGE_TEXT:
                        updateList(KEY_MESSAGE_TEXT,content,orientation,date,user,isSent,isDelivered);
                        break;
                    case KEY_MESSAGE_IMAGE:
                        updateList(KEY_MESSAGE_IMAGE,content,orientation,date,user,isSent,isDelivered);
                        break;
                    case KEY_MESSAGE_MAP:
                        updateList(KEY_MESSAGE_MAP,content,orientation,date,user,isSent,isDelivered);
                        break;
                    case KEY_MESSAGE_VIDEO:
                        updateList(KEY_MESSAGE_MAP,content,orientation,date,user,isSent,isDelivered);
                        break;
                }
               // cursor.moveToNext();
            }
        }
        else {
            return;
        }
    }

    private void setupActionBar(String username, String picture) {
        if(this.getSupportActionBar() != null) {
            final ActionBar actionBar = this.getSupportActionBar();
            /*Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    actionBar.setIcon(new BitmapDrawable(bitmap));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    actionBar.setIcon(R.drawable.avatar);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    actionBar.setIcon(R.drawable.avatar);
                }
            };
            Picasso.with(getActivity()).load(picture).into(target);*/
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
            ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_actionbar, null);
            status = (TextView) actionBarLayout.findViewById(R.id.actionBarStatus);
            TextView usernameTextView = (TextView) actionBarLayout.findViewById(R.id.actionBarUsername);
            usernameTextView.setText(username);
            actionBar.setCustomView(actionBarLayout, layout);
        }
    }

    private ChatActivity getActivity() {
        return this;
    }

    private String getChatPicture(Intent intent) {
        String userChattingWithPicture = null;
        if(intent != null) {
            userChattingWithPicture = intent.getStringExtra("picture");
        }
        return userChattingWithPicture;
    }

    private String getChatUsername(Intent intent) {
        String userChattingWithName = null;
        if(intent != null) {
            userChattingWithName = intent.getStringExtra("username");
        }
        return userChattingWithName;
    }


    private void updateList(String type, String content, boolean orientation,String date, String user, boolean sent, boolean isDelivered) {
        switch (type) {
            case KEY_MESSAGE_TEXT:
               messageText = new MessageText();
                messageText.setDate(date);messageText.setDelivered(isDelivered);messageText.setLeft(orientation);
                messageText.setSent(sent);messageText.setText(content);messageText.setUser(user);
                adapter.add(messageText);
                break;
            case KEY_MESSAGE_IMAGE:
                messageImage = new MessageImage();
                messageImage.setText(content);messageImage.setUser(user);messageImage.setDelivered(isDelivered);
                messageImage.setLeft(orientation);messageImage.setSent(sent);messageImage.setDate(date);
                adapter.add(messageImage);
                break;
            case KEY_MESSAGE_MAP:
                MessageMap messageMap = new MessageMap();
                messageMap.setLeft(orientation);messageMap.setDate(date);messageMap.setSent(sent);messageMap.setDelivered(isDelivered);
                messageMap.setLatlng(content);messageMap.setUser(user);
                adapter.add(messageMap);
                break;
            case KEY_MESSAGE_VIDEO:
                MessageVideo messageVideo = new MessageVideo();
                messageVideo.setLeft(orientation);messageVideo.setDate(date);messageVideo.setSent(sent);messageVideo.setDelivered(isDelivered);
                messageVideo.setVideoUrl(content);messageVideo.setUser(user);
                adapter.add(messageVideo);
                break;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rvMessages.getAdapter().notifyDataSetChanged();
                rvMessages.scrollToPosition(rvMessages.getAdapter().getItemCount() - 1);
            }
        });
    }

    private Cursor fetchPreviousConversation() {
        WhisperDB whisperDB = new WhisperDB(this);
        SQLiteDatabase sqLiteDatabase = whisperDB.getReadableDatabase();
        String selection = RecentConversations.TableDefn.COLUMN_NAME_USER;
        String[] args = {getChatUsername(intent)};
        Cursor cursor = sqLiteDatabase.query(false, RecentConversations.TableDefn.TABLE_NAME, new String[]{RecentConversations.TableDefn.COLUMN_NAME_DATE,
                        RecentConversations.TableDefn.COLUMN_NAME_MESSAGE,RecentConversations.TableDefn.COLUMN_NAME_MESSAGE_ID, RecentConversations.TableDefn.COLUMN_NAME_DELIVERED, RecentConversations.TableDefn.COLUMN_NAME_MESSAGE_TYPE, RecentConversations.TableDefn.COLUMN_NAME_MESSAGE_IS_LEFT,RecentConversations.TableDefn.COLUMN_NAME_MESSAGE_SENT,RecentConversations.TableDefn.COLUMN_NAME_USER},
                selection + "=?", args, null, null, null, null); //limit the number of query returned
        return cursor;
    }
    @Override
    public void onDestroy() {
        if(mEventListener != null) {
            MMX.unregisterListener(mEventListener);
        }
        if(mGPS != null) {
            mGPS.stopUsingGPS();
        }
        super.onDestroy();
        //NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    }

    @Override
    protected void onPause() {
        super.onPause();
       // hideEmojiPopup();
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        }
        WhisperDB whisperDB = new WhisperDB(this);
        SQLiteDatabase sqLiteDatabase = whisperDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RecentChatTableDefnDB.RecentChatTableDfn.COLUMN_NAME_PROFILE_PIC,getChatPicture(intent));
        values.put(RecentChatTableDefnDB.RecentChatTableDfn.COLUMN_NAME_DATE, String.valueOf(new Date().getHours()) + ":" + String.valueOf(new Date().getMinutes()));
        values.put(RecentChatTableDefnDB.RecentChatTableDfn.COLUMN_NAME_USER,getChatUsername(intent));
        sqLiteDatabase.insert(RecentChatTableDefnDB.RecentChatTableDfn.TABLE_NAME,null,values);
    }
    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
    }
    private List<MenuObject> getMenuObjects() {

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icn_close);

        MenuObject photo = new MenuObject(getString(R.string.photo));
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_camera);
        photo.setBitmap(b);

/*        MenuObject video = new MenuObject(getString(R.string.video));
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_video));
        video.setDrawable(bd);*/

        //MenuObject audio = new MenuObject(getString(R.string.audio));
        //audio.setResource(R.drawable.ic_audio_file);

        MenuObject location = new MenuObject(getString(R.string.location));
        location.setResource(R.drawable.ic_location);

        //MenuObject contact = new MenuObject(getString(R.string.contact));
        //contact.setResource(R.drawable.ic_user);

        //MenuObject other_files = new MenuObject(getString(R.string.other_files));
        //other_files.setResource(R.drawable.ic_add_file);

        menuObjects.add(close);
        menuObjects.add(photo);
       // menuObjects.add(video);
       // menuObjects.add(audio);
        menuObjects.add(location);
        //menuObjects.add(contact);
        //menuObjects.add(other_files);
        return menuObjects;

    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.context_menu) {
            if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
            }
            return true;
        }
        else if(id == android.R.id.home){
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(upIntent)
                        .startActivities();
            } else {
                NavUtils.navigateUpTo(this, upIntent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        } else{
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(upIntent)
                        .startActivities();
            } else {
                NavUtils.navigateUpTo(this, upIntent);
            }

        }
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        if(position == 1) { // Photo
            new MaterialDialog.Builder(this)
                    .title(R.string.title_photo_src)
                    .items(R.array.items_photo)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            if(which == 0 ) {
                                chooseImage();
                                //selectImage();
                            }
                            else if(which == 1) {
                                takePicture();
                            }
                            else if (which == 2){
                                //instagram
                                InstagramPhotoPicker.startPhotoPickerForResult(getActivity(), Constants.CLIENT_ID, Constants.REDIRECT_URI, Constants.REQUEST_CODE_INSTAGRAM_PICKER);
                            }
                            return true;
                        }
                    })
                    .positiveText(R.string.choose)
                    .positiveColorRes(R.color.md_blue_grey_700)
                    .widgetColorRes(R.color.md_blue_grey_500)
                    .titleColorRes(R.color.md_blue_grey_300)
                    .show();
        }
     /*   else if (position == 2) { // Video
            new MaterialDialog.Builder(this)
                    .title(R.string.title_video_src)
                    .items(R.array.items_video)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            if(which == 0 ) {
                                pickVideo();
                            }
                            else if(which == 1) {
                                captureVideo();
                            }
                            return true;
                        }
                    })
                    .positiveText(R.string.choose)
                    .positiveColorRes(R.color.md_blue_grey_700)
                    .widgetColorRes(R.color.md_blue_grey_500)
                    .titleColorRes(R.color.md_blue_grey_300)
                    .show();
        }*/
        else if (position == 2) { // location
            sendLocation();
        }
        else {
            //Toast.makeText(this, "Clicked on position: " + position, Toast.LENGTH_SHORT).show();
        }
    }


    private void chooseImage() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE,"whisper",true);
        imageChooserManager.setImageChooserListener(ChatActivity.this);
        imageChooserManager.clearOldFiles();
        try {
            imageChooserManager.choose();
        } catch (ChooserException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onImageChosen(final ChosenImage chosenImage) {
        ChatActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendPicture(KEY_MESSAGE_IMAGE, chosenImage.getFileThumbnail());
                String date = String.valueOf(new Date().getHours()) + ":" + String.valueOf(new Date().getMinutes());
                updateList(KEY_MESSAGE_IMAGE, imgFilePath, false, date, getChatUsername(intent), false, false);
            }
        });
    }

    private void sendPicture(final String keyMessageImage, final String imgFilePath) {
        File file = new File(imgFilePath);
        final AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        try {
            params.put("pix", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        client.setEnableRedirects(true);
        client.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1");
        client.post(getActivity(), Constants.WHISPER_URL_SAVE_PIX, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String s = new String(responseBody, "UTF-8");
                    HashMap<String, String> content = new HashMap<>();
                    content.put("type", KEY_MESSAGE_IMAGE);
                    content.put("message", s);
                    send(content);
                    saveToDb(content.get("message"),content.get("type"), messageID,"false");// must be after send so that messageID is not empty.
                    Log.i("success", "true");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(status != null){
                    status.setText("Failed to send");
                }
            }

            @Override
            public void onStart() {

            }
        });
    }

    @Override
    public void onVideoChosen(final ChosenVideo chosenVideo) {
        ChatActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendVideo(KEY_MESSAGE_VIDEO, chosenVideo.getVideoFilePath());
            }
        });
    }

    private void sendVideo(String keyMessageVideo, String videoFilePath) {
    }

    @Override
    public void onError(String s) {
        ChatActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ChatActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK
                && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (imageChooserManager == null) {
                //reinitializeImageChooser();
            }
            imageChooserManager.submit(requestCode, data);
        }

        else if (resultCode == RESULT_OK
                && (requestCode == ChooserType.REQUEST_CAPTURE_VIDEO || requestCode == ChooserType.REQUEST_PICK_VIDEO)) {
            if (videoChooserManager == null) {
                //reinitializeVideoChooser();
            }
            videoChooserManager.submit(requestCode, data);
        }
        else if ((requestCode == Constants.REQUEST_CODE_INSTAGRAM_PICKER) && (resultCode == Activity.RESULT_OK)) {
            InstagramPhoto[] instagramPhotos = InstagramPhotoPicker.getResultPhotos(data);
            Log.i("dbotha", "User selected " + instagramPhotos.length + " Instagram photos");
            for (int i = 0; i < instagramPhotos.length; ++i) {
                Log.i("dbotha", "Photo: " + instagramPhotos[i].getFullURL());
                //mutiple images
            }
            String dateString = String.valueOf(new Date().getHours()) + ":" + String.valueOf(new Date().getMinutes());
            updateList(KEY_MESSAGE_IMAGE,instagramPhotos[0].getFullURL().toString(),false,dateString,getChatPicture(intent),false,false);
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "You have selected no photo", Toast.LENGTH_LONG).show();
        }

        else {

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void takePicture() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_CAPTURE_PICTURE,"whisper",true);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.clearOldFiles();
        try {
            imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void captureVideo() {
        chooserType = ChooserType.REQUEST_CAPTURE_VIDEO;
        videoChooserManager = new VideoChooserManager(this,
                ChooserType.REQUEST_CAPTURE_VIDEO,"whisper",true);
        videoChooserManager.setVideoChooserListener(this);
        try {
            videoChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void pickVideo() {
        chooserType = ChooserType.REQUEST_PICK_VIDEO;
        videoChooserManager = new VideoChooserManager(this,
                ChooserType.REQUEST_PICK_VIDEO,"whisper",true);
        videoChooserManager.setVideoChooserListener(this);
        try {
            videoChooserManager.choose();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onMenuItemLongClick(View view, int i) {

    }

    private void sendLocation() {
        if (mGPS.canGetLocation() && mGPS.getLatitude() != 0.00 && mGPS.getLongitude() != 0.00) {
            String date = String.valueOf(new Date().getHours()) + ":" + String.valueOf(new Date().getMinutes());
            double myLat = mGPS.getLatitude();
            double myLong = mGPS.getLongitude();
            String latlng = (Double.toString(myLat) + "," + Double.toString(myLong));
            if(isUserLoggedOut()){}
            else {
                updateList(KEY_MESSAGE_MAP, latlng, false, date, MMX.getCurrentUser().getUsername(), false, false);
                HashMap<String, String> content = new HashMap<>();
                content.put("type", KEY_MESSAGE_MAP);
                content.put("latitude", Double.toString(myLat));
                content.put("longitude", Double.toString(myLong));
                send(content);
            }
        }else{
            mGPS.showSettingsAlert(this);
        }
    }

}