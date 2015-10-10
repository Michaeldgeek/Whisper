package challenge.magnet.android.whisper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.magnet.mmx.client.MMXDeviceManager;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMXUser;
import com.magnet.mmx.client.common.MMXConnection;
import com.magnet.mmx.client.common.MMXPayload;
import com.magnet.mmx.client.common.MMXSettings;
import com.magnet.mmx.protocol.MmxHeaders;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.entities.Photo;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnPhotosListener;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import challenge.magnet.android.whisper.adapters.MessageRecyclerViewAdapter;
import challenge.magnet.android.whisper.models.MessageImage;
import challenge.magnet.android.whisper.models.MessageMap;
import challenge.magnet.android.whisper.models.MessageText;
import challenge.magnet.android.whisper.models.MessageVideo;
import challenge.magnet.android.whisper.services.GPSTracker;
import challenge.magnet.android.whisper.widgets.Emoji;
import challenge.magnet.android.whisper.widgets.EmojiView;
import challenge.magnet.android.whisper.widgets.SizeNotifierRelativeLayout;
import ly.kite.instagramphotopicker.InstagramPhoto;
import ly.kite.instagramphotopicker.InstagramPhotoPicker;

public class ChatActivity extends ActionBarActivity implements SizeNotifierRelativeLayout.SizeNotifierRelativeLayoutDelegate, NotificationCenter.NotificationCenterDelegate,OnMenuItemClickListener,
        OnMenuItemLongClickListener,ImageChooserListener,VideoChooserListener {
    final private int INTENT_REQUEST_GET_IMAGES = 14;
    final private int INTENT_SELECT_VIDEO = 13;
    private FragmentManager fragmentManager;
    private DialogFragment mMenuDialogFragment;
    private EditText chatEditText1;
    private ImageView enterChatView1, emojiButton;
    private EmojiView emojiView;
    private SizeNotifierRelativeLayout sizeNotifierRelativeLayout;
    private boolean showingEmoji;
    private int keyboardHeight;
    private boolean keyboardVisible;
    private WindowManager.LayoutParams windowLayoutParams;
    private int chooserType;
    private ImageChooserManager imageChooserManager;
    private VideoChooserManager videoChooserManager;
    private String imgFilePath;
    GPSTracker mGPS;

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

    private ImageView.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v==enterChatView1)
            {
                sendMessage();
            }

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
    private RecyclerView rvMessages;
    private ArrayList<Object> messageList;
    private MessageRecyclerViewAdapter adapter;
    public static final String KEY_MESSAGE_TEXT = "text";
    public static final String KEY_MESSAGE_IMAGE = "photo";
    public static final String KEY_MESSAGE_MAP = "location";
    public static final String KEY_MESSAGE_VIDEO = "video";
    private SimpleFacebook mSimpleFacebook;

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);
        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(getString(R.string.app_id))
                .setNamespace(Constants.APP_NS)
                .setPermissions(Constants.permissions)
                .build();
        SimpleFacebook.setConfiguration(configuration);
        fragmentManager = getSupportFragmentManager();
        initMenuFragment();
        AndroidUtilities.statusBarHeight = getStatusBarHeight();
        messageList = new ArrayList<>();
        rvMessages = (RecyclerView) findViewById(R.id.rvMessages);
        chatEditText1 = (EditText) findViewById(R.id.chat_edit_text1);
        enterChatView1 = (ImageView) findViewById(R.id.enter_chat1);
        adapter = new MessageRecyclerViewAdapter(this, messageList);
        mGPS = new GPSTracker(this);
        //rvMessages.setAdapter(new SlideInBottomAnimationAdapter(adapter));
        rvMessages.setAdapter(adapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(false);
        rvMessages.setLayoutManager(layoutManager);

        // Hide the emoji on click of edit text
        chatEditText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showingEmoji)
                    hideEmojiPopup();
            }
        });


        emojiButton = (ImageView)findViewById(R.id.emojiButton);

        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmojiPopup(!showingEmoji);
            }
        });

        chatEditText1.setOnKeyListener(keyListener);

        enterChatView1.setOnClickListener(clickListener);

        chatEditText1.addTextChangedListener(watcher1);

        sizeNotifierRelativeLayout = (SizeNotifierRelativeLayout) findViewById(R.id.chat_layout);
        sizeNotifierRelativeLayout.delegate = this;

        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);

    }

    private Activity getActivity()
    {
        return this;
    }


    /**
     * Show or hide the emoji popup
     *
     * @param show
     */
    private void showEmojiPopup(boolean show) {
        showingEmoji = show;

        if (show) {
            if (emojiView == null) {
                if (getActivity() == null) {
                    return;
                }
                emojiView = new EmojiView(getActivity());

                emojiView.setListener(new EmojiView.Listener() {
                    public void onBackspace() {
                        chatEditText1.dispatchKeyEvent(new KeyEvent(0, 67));
                    }

                    public void onEmojiSelected(String symbol) {
                        int i = chatEditText1.getSelectionEnd();
                        if (i < 0) {
                            i = 0;
                        }
                        try {
                            CharSequence localCharSequence = Emoji.replaceEmoji(symbol, chatEditText1.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20));
                            chatEditText1.setText(chatEditText1.getText().insert(i, localCharSequence));
                            int j = i + localCharSequence.length();
                            chatEditText1.setSelection(j, j);
                        } catch (Exception e) {
                            Log.e(Constants.TAG, "Error showing emoji");
                        }
                    }
                });


                windowLayoutParams = new WindowManager.LayoutParams();
                windowLayoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
                if (Build.VERSION.SDK_INT >= 21) {
                    windowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
                } else {
                    windowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
                    windowLayoutParams.token = getActivity().getWindow().getDecorView().getWindowToken();
                }
                windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            }

            final int currentHeight;

            if (keyboardHeight <= 0)
                keyboardHeight = App.getInstance().getSharedPreferences("emoji", 0).getInt("kbd_height", AndroidUtilities.dp(200));

            currentHeight = keyboardHeight;

            WindowManager wm = (WindowManager) App.getInstance().getSystemService(Activity.WINDOW_SERVICE);

            windowLayoutParams.height = currentHeight;
            windowLayoutParams.width = AndroidUtilities.displaySize.x;

            try {
                if (emojiView.getParent() != null) {
                    wm.removeViewImmediate(emojiView);
                }
            } catch (Exception e) {
                Log.e(Constants.TAG, e.getMessage());
            }

            try {
                wm.addView(emojiView, windowLayoutParams);
            } catch (Exception e) {
                Log.e(Constants.TAG, e.getMessage());
                return;
            }

            if (!keyboardVisible) {
                if (sizeNotifierRelativeLayout != null) {
                    sizeNotifierRelativeLayout.setPadding(0, 0, 0, currentHeight);
                }

                return;
            }

        }
        else {
            removeEmojiWindow();
            if (sizeNotifierRelativeLayout != null) {
                sizeNotifierRelativeLayout.post(new Runnable() {
                    public void run() {
                        if (sizeNotifierRelativeLayout != null) {
                            sizeNotifierRelativeLayout.setPadding(0, 0, 0, 0);
                        }
                    }
                });
            }
        }


    }


    /**
     * Remove emoji window
     */
    private void removeEmojiWindow() {
        if (emojiView == null) {
            return;
        }
        try {
            if (emojiView.getParent() != null) {
                WindowManager wm = (WindowManager) App.getInstance().getSystemService(Context.WINDOW_SERVICE);
                wm.removeViewImmediate(emojiView);
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, e.getMessage());
        }
    }



    /**
     * Hides the emoji popup
     */
    public void hideEmojiPopup() {
        if (showingEmoji) {
            showEmojiPopup(false);
        }
    }

    /**
     * Check if the emoji popup is showing
     *
     * @return
     */
    public boolean isEmojiPopupShowing() {
        return showingEmoji;
    }



    /**
     * Updates emoji views when they are complete loading
     *
     * @param id
     * @param args
     */
    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.emojiDidLoaded) {
            if (emojiView != null) {
                emojiView.invalidateViews();
            }

            if (rvMessages != null) {
                rvMessages.invalidate();
            }
        }
    }

    @Override
    public void onSizeChanged(int height) {

        Rect localRect = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);

        WindowManager wm = (WindowManager) App.getInstance().getSystemService(Activity.WINDOW_SERVICE);
        if (wm == null || wm.getDefaultDisplay() == null) {
            return;
        }


        if (height > AndroidUtilities.dp(50) && keyboardVisible) {
            keyboardHeight = height;
            App.getInstance().getSharedPreferences("emoji", 0).edit().putInt("kbd_height", keyboardHeight).commit();
        }


        if (showingEmoji) {
            int newHeight = 0;

            newHeight = keyboardHeight;

            if (windowLayoutParams.width != AndroidUtilities.displaySize.x || windowLayoutParams.height != newHeight) {
                windowLayoutParams.width = AndroidUtilities.displaySize.x;
                windowLayoutParams.height = newHeight;

                wm.updateViewLayout(emojiView, windowLayoutParams);
                if (!keyboardVisible) {
                    sizeNotifierRelativeLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            if (sizeNotifierRelativeLayout != null) {
                                sizeNotifierRelativeLayout.setPadding(0, 0, 0, windowLayoutParams.height);
                                sizeNotifierRelativeLayout.requestLayout();
                            }
                        }
                    });
                }
            }
        }


        boolean oldValue = keyboardVisible;
        keyboardVisible = height > 0;
        if (keyboardVisible && sizeNotifierRelativeLayout.getPaddingBottom() > 0) {
            showEmojiPopup(false);
        } else if (!keyboardVisible && keyboardVisible != oldValue && showingEmoji) {
            showEmojiPopup(false);
        }

    }

    @Override
    public void onDestroy() {
        // MMX.unregisterListener(mEventListener);
        //S3UploadService.destroy();
        mGPS.stopUsingGPS();
        super.onDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    }

    /**
     * Get the system status bar height
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onPause() {
        super.onPause();

        hideEmojiPopup();
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        }
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

        MenuObject video = new MenuObject(getString(R.string.video));
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_video));
        video.setDrawable(bd);

        MenuObject audio = new MenuObject(getString(R.string.audio));
        audio.setResource(R.drawable.ic_audio_file);

        MenuObject location = new MenuObject(getString(R.string.location));
        location.setResource(R.drawable.ic_location);

        MenuObject contact = new MenuObject(getString(R.string.contact));
        contact.setResource(R.drawable.ic_user);

        MenuObject other_files = new MenuObject(getString(R.string.other_files));
        other_files.setResource(R.drawable.ic_add_file);

        menuObjects.add(close);
        menuObjects.add(photo);
        menuObjects.add(video);
        menuObjects.add(audio);
        menuObjects.add(location);
        menuObjects.add(contact);
        menuObjects.add(other_files);
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        } else{
            finish();
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
                            else if(which == 2) {
                                //Facebook
                                OnPhotosListener onPhotosListener = new OnPhotosListener() {
                                    @Override
                                    public void onComplete(List<Photo> photos) {
                                        Log.i("fbphoto", "Number of photos = " + photos.size());
                                    }
                                };
                                OnLoginListener onLoginListener = new OnLoginListener() {

                                    @Override
                                    public void onLogin(String accessToken, List<Permission> acceptedPermissions, List<Permission> declinedPermissions) {
                                        // change the state of the button or do whatever you want
                                        Log.i("logstatus", "Logged in");
                                    }

                                    @Override
                                    public void onCancel() {
                                        // user canceled the dialog
                                    }

                                    @Override
                                    public void onFail(String reason) {
                                        // failed to login
                                    }

                                    @Override
                                    public void onException(Throwable throwable) {
                                        // exception from facebook
                                    }

                                };
                                //mSimpleFacebook.login(onLoginListener);
                                mSimpleFacebook.getPhotos("Profile",onPhotosListener);
                            }
                            else if (which == 3){
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
        else if (position == 2) { // Video
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
        }
        else if (position == 4) { // locatiob
            sendLocation();
        }
        else {
            Toast.makeText(this, "Clicked on position: " + position, Toast.LENGTH_SHORT).show();
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
                sendMedia(KEY_MESSAGE_IMAGE, chosenImage.getFileThumbnail());
                //sendMessage.sendImageMessage(imageMessage.getImgPath(), imageMessage.getUserType(), imageMessage.getUserMessageContent());
            }
        });
    }

    @Override
    public void onVideoChosen(final ChosenVideo chosenVideo) {
        ChatActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendMedia(KEY_MESSAGE_VIDEO, chosenVideo.getVideoFilePath());
            }
        });
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
                updateList(KEY_MESSAGE_IMAGE,instagramPhotos[0].getFullURL().toString(),false,true);
            }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "You have selected no photo", Toast.LENGTH_LONG).show();
        }

        else {
            mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
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
        //outState.pu;
    }

    public void sendMessage() {
        String messageText = chatEditText1.getText().toString();
        if (messageText.isEmpty()) {
            return;
        }
        updateList(KEY_MESSAGE_TEXT, messageText, false,false);
        HashMap<String, String> content = new HashMap<>();
        content.put("type", KEY_MESSAGE_TEXT);
        content.put("message", messageText);
        send(content);
        chatEditText1.setText(null);
    }

    private void send(HashMap<String, String> content) {
        MMX.start();
        HashSet<MMXUser> recipients = new HashSet<>();
        recipients.add( MMX.getCurrentUser());
        String messageID = new MMXMessage.Builder()
                .content(content)
                .recipients(recipients)
                .build()
                .send(new MMXMessage.OnFinishedListener<String>() {
                    public void onSuccess(String s) {
                        Toast.makeText(ChatActivity.this, "Message sent.", Toast.LENGTH_LONG).show();
                    }

                    public void onFailure(MMXMessage.FailureCode failureCode, Throwable e) {
                        Toast.makeText(ChatActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        MMX.registerListener(new MMX.EventListener() {
            @Override
            public boolean onMessageReceived(MMXMessage mmxMessage) {
                updateList(KEY_MESSAGE_TEXT,mmxMessage.getContent().get("message"),true,false);
                mmxMessage.acknowledge(new MMXMessage.OnFinishedListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }

                    @Override
                    public void onFailure(MMXMessage.FailureCode failureCode, Throwable throwable) {

                    }
                });
                return true;
            }
        });
    }

    public void updateList(String type, String content, boolean orientation,boolean isInstagram) {
        switch (type) {
            case KEY_MESSAGE_TEXT:
                adapter.add(new MessageText(orientation,content));
                break;
            case KEY_MESSAGE_IMAGE:
                adapter.add(new MessageImage(orientation, content, isInstagram));
                break;
            case KEY_MESSAGE_MAP:
                adapter.add(new MessageMap(orientation, content));
                break;
            case KEY_MESSAGE_VIDEO:
                adapter.add(new MessageVideo(orientation, content));
                break;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rvMessages.getAdapter().notifyDataSetChanged();
                rvMessages.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    private void sendMedia(final String mediaType, String filePath) {
        File f = new File(filePath);
        //final String key = S3UploadService.generateKey(f);
        //S3UploadService.uploadFile(key, f, new TransferListener() {
        //  public void onStateChanged(int id, TransferState state) {
        //    switch (state) {
        //      case COMPLETED:
        //updateList(mediaType, S3UploadService.buildUrl(key), false);
        updateList(mediaType, filePath, false,false);
        //HashMap<String, String> content = new HashMap<>();
        //content.put("type", mediaType);
        //content.put("url", S3UploadService.buildUrl(key));
        //send(content);
        //        break;
        //  case CANCELED:
        //case FAILED:
        //  Toast.makeText(ChatActivity.this, "Unable to upload.", Toast.LENGTH_LONG).show();
        // break;
        // }
        //}

         /*   public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            public void onError(int id, Exception ex) {
                Log.e(TAG, "send(): exception during upload", ex);
            }
        });*/
    }

    @Override
    public void onMenuItemLongClick(View view, int i) {

    }

    private void sendLocation() {
        if (mGPS.canGetLocation() && mGPS.getLatitude() != 0.00 && mGPS.getLongitude() != 0.00) {
            double myLat = mGPS.getLatitude();
            double myLong = mGPS.getLongitude();
            String latlng = (Double.toString(myLat) + "," + Double.toString(myLong));

            updateList(KEY_MESSAGE_MAP, latlng, false,false);

            //HashMap<String, String> content = new HashMap<>();
            //content.put("type", KEY_MESSAGE_MAP);
            //content.put("latitude", Double.toString(myLat));
            //content.put("longitude", Double.toString(myLong));
            //send(content);
        }else{
            mGPS.showSettingsAlert(this);
        }
    }


}
