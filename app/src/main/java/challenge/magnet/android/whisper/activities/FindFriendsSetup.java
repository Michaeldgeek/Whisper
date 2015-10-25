
package challenge.magnet.android.whisper.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PermissionInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONObject;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnFriendsListener;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnNewPermissionsListener;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.sromku.simple.fb.utils.Attributes;
import com.sromku.simple.fb.utils.PictureAttributes;
import org.json.JSONException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import challenge.magnet.android.whisper.Constants;
import challenge.magnet.android.whisper.R;
import challenge.magnet.android.whisper.databases.TablesDefn.FriendsTable;
import challenge.magnet.android.whisper.databases.TablesDefn.InstaFriends;
import challenge.magnet.android.whisper.databases.TablesDefn.UserTableDefn;
import challenge.magnet.android.whisper.databases.TablesDefn.WhisperDB;
import challenge.magnet.android.whisper.instagramApi.InstagramApp;
import challenge.magnet.android.whisper.instagramApi.JSONParser;
import challenge.magnet.android.whisper.models.InstagramUser;
import cz.msebera.android.httpclient.Header;

public class FindFriendsSetup extends ActionBarActivity {
    SimpleFacebook mSimpleFacebook;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    boolean isTrue = false;
    MaterialDialog materialDialog;
    String fbid;
    String instaid;
    InstagramApp mApp;
    private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();
    public static final String TAG_DATA = "data";
    public static final String TAG_ID = "id";
    public static final String TAG_PROFILE_PICTURE = "profile_picture";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_BIO = "bio";
    public static final String TAG_WEBSITE = "website";
    public static final String TAG_FULL_NAME = "full_name";
    String url = "";
    private Button buttonFb;
    private Button buttonInstagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager_find_friends);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        buttonFb = (Button) findViewById(R.id.fb);
        buttonInstagram = (Button)findViewById(R.id.instagram);
        buttonFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fbLogin();

            }
        });
        buttonInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instagramLogin();

            }
        });


    }



    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                userInfoHashmap = mApp.getUserInfo();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        saveUserInstagramIdOnline(instaid);
                    }
                });
            }
            else if (msg.what == InstagramApp.WHAT_ERROR) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (materialDialog != null) {
                            if (materialDialog.isShowing()) {
                                materialDialog.dismiss();
                                materialDialog = null;
                                new MaterialDialog.Builder(FindFriendsSetup.this)
                                        .content("An unknown error has occured")
                                        .positiveColorRes(R.color.actionbar_color)
                                        .positiveText("Retry")
                                        .callback(new MaterialDialog.ButtonCallback() {
                                            @Override
                                            public void onPositive(MaterialDialog dialog) {
                                                configureInstagram();
                                            }
                                        })
                                        .show();
                            }
                        }
                    }
                });
            }
            return false;
        }
    });

    public void instagramLogin() {
         mApp = new InstagramApp(this, Constants.CLIENT_ID,
                Constants.CLIENT_SECRET, Constants.REDIRECT_URI);
        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {
            @Override
            public void onSuccess() {
                instaid = mApp.getId();
                configureInstagram();

            }

            @Override
            public void onFail(String error) {
                showInstagramRetry("Permission denied");
            }
        });
        mApp.authorize();
    }

    private void configureInstagram() {
        if(materialDialog != null){
            if(materialDialog.isShowing()) {
                materialDialog.setContent("Please wait...");
            }
        }
        else {
            materialDialog = new MaterialDialog.Builder(FindFriendsSetup.this)
                    .content("Please wait...")
                    .progress(true, 0)
                    .cancelable(false)
                    .autoDismiss(false)
                    .show();
        }
        mApp.fetchUserName(handler);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
        Intent intent = getIntent();
        if(intent != null) {
            if (intent.getStringExtra("fb") != null && intent.getStringExtra("fb").contains("1")) {
                fbLogin();
            }
            else if (intent.getStringExtra("insta") != null && intent.getStringExtra("insta").contains("2")) {
                instagramLogin();
            }
        }
    }

    public void fbLogin(){
        mSimpleFacebook.login(new OnLoginListener() {
            @Override
            public void onLogin(String accessToken, List<Permission> acceptedPermissions, List<Permission> declinedPermissions) {
                Log.i("login", "successfully");
                if (isGrantedPermission(acceptedPermissions, declinedPermissions)) {
                    getUserFbDetails();
                } else {
                    showPermissionRetry("Permission Denied");

                }
            }

            @Override
            public void onCancel() {
                showRetry("Operation cancelled.");
            }

            @Override
            public void onException(Throwable throwable) {
                showRetry("Something went wrong");
            }

            @Override
            public void onFail(String s) {
                showRetry("Something went wrong");
            }


        });
    }

    private void showPermissionRetry(final String message) {
        FindFriendsSetup.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar
                        .make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Grant", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                                requestPermissionAgain();
                            }
                        });
                // Changing message text color
                snackbar.setActionTextColor(Color.RED);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
            }
        });
    }

    private void requestPermissionAgain() {
        OnNewPermissionsListener onNewPermissionsListener = new OnNewPermissionsListener() {

            @Override
            public void onSuccess(String accessToken, List<Permission> acceptedPermissions, List<Permission> declinedPermissions) {
                // updated access token
                getUserFbDetails();
            }

            @Override
            public void onCancel() {
                showPermissionRetry("Permission denied");
            }

            @Override
            public void onFail(String reason) {
                // failed
                showPermissionRetry("Permission denied");
            }

            @Override
            public void onException(Throwable throwable) {
                // exception from facebook
                showPermissionRetry("Permission denied");
            }

        };
        Permission[] permissions = new Permission[] {
                Permission.USER_FRIENDS
        };
        mSimpleFacebook.requestNewPermissions(permissions, onNewPermissionsListener);
    }

    private void getUserFbDetails() {

        OnProfileListener onProfileListener = new OnProfileListener() {
            @Override
            public void onComplete(Profile profile) {
                fbid = profile.getId();
                saveUserFbidOnline(profile.getId(), profile.getPicture());
            }

            @Override
            public void onException(Throwable throwable) {
                showGetUserFbRetry("Network issue");
            }

            @Override
            public void onFail(String reason) {
                showGetUserFbRetry("Network issue");
            }

            @Override
            public void onThinking() {
                materialDialog = new MaterialDialog.Builder(FindFriendsSetup.this)
                        .content("Please wait...")
                        .progress(true, 0)
                        .cancelable(false)
                        .autoDismiss(false)
                        .show();
            }

        };
        PictureAttributes pictureAttributes = Attributes.createPictureAttributes();
        pictureAttributes.setHeight(500);
        pictureAttributes.setWidth(500);
        Profile.Properties properties = new Profile.Properties.Builder()
                .add(Profile.Properties.FIRST_NAME)
                .add(Profile.Properties.LAST_NAME)
                .add(Profile.Properties.ID)
                .add(Profile.Properties.PICTURE,pictureAttributes)
                .build();
        mSimpleFacebook.getProfile(properties, onProfileListener);
    }

    private void showGetUserFbRetry(final String message) {
        FindFriendsSetup.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                materialDialog.dismiss();
                snackbar = Snackbar
                        .make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                                getUserFbDetails();
                            }
                        });
                // Changing message text color
                snackbar.setActionTextColor(Color.RED);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
            }
        });


    }

    private boolean isGrantedPermission(List<Permission> acceptedPermissions, List<Permission> declinedPermissions) {
        for(int i = 0; i < acceptedPermissions.size(); i++){
            if(acceptedPermissions.get(i).equals(Permission.USER_FRIENDS)) {
                Log.i("granted","true");
                isTrue = true;
            }
        }
        return isTrue;
    }

    private void showRetry(final String message) {
        FindFriendsSetup.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar
                        .make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                                fbLogin();
                            }
                        });
                // Changing message text color
                snackbar.setActionTextColor(Color.RED);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
            }
        });

    }

    private void showInstagramRetry(final String message) {
        FindFriendsSetup.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar
                        .make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                                instagramLogin();
                            }
                        });
                // Changing message text color
                snackbar.setActionTextColor(Color.RED);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
            }
        });

    }

    private void saveUserFbidOnline(final String id, final String pic) {
        if(materialDialog != null){
            if(materialDialog.isShowing()) {
                materialDialog.setContent("Saving data online...");
            }
        }
        else {
            materialDialog = new MaterialDialog.Builder(FindFriendsSetup.this)
                    .content("Saving data online...")
                    .progress(true, 0)
                    .cancelable(false)
                    .autoDismiss(false)
                    .show();
        }
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_login_details), Context.MODE_PRIVATE);
        String username = sharedPref.getString(getString(R.string.username), "notfound");
        if(username.contains("notfound")){
            goToLoginActivity();
        }
        else {
            final AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("fbid", id);
            params.put("username", username);
            params.put("profilepic", pic);
            client.setEnableRedirects(true);
            client.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1");
            client.post(this, Constants.WHISPER_URL_UPDATE_FB_ID, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String s = new String(responseBody, "UTF-8");
                        Log.i("success", "true");
                        syncUserTableOnline("fb");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (materialDialog != null) {
                        if (materialDialog.isShowing()) {
                            materialDialog.dismiss();
                            materialDialog = null;
                            new MaterialDialog.Builder(FindFriendsSetup.this)
                                    .content("Failed to connect to online server")
                                    .positiveText("Retry")
                                    .positiveColorRes(R.color.actionbar_color)
                                    .positiveColor(Color.parseColor("#009688"))
                                    .callback(new MaterialDialog.ButtonCallback() {
                                        @Override
                                        public void onPositive(MaterialDialog dialog) {
                                            saveUserFbidOnline(id, pic);
                                        }
                                    })
                                    .show();
                        }
                    }
                }

                @Override
                public void onStart() {
                    super.onStart();
                }


                @Override
                public void onCancel() {
                    if (materialDialog != null) {
                        if (materialDialog.isShowing()) {
                            materialDialog.dismiss();
                            materialDialog = null;
                            new MaterialDialog.Builder(FindFriendsSetup.this)
                                    .content("Failed to connect to online server")
                                    .positiveColorRes(R.color.actionbar_color)
                                    .positiveText("Retry")
                                    .callback(new MaterialDialog.ButtonCallback() {
                                        @Override
                                        public void onPositive(MaterialDialog dialog) {
                                            saveUserFbidOnline(id, pic);
                                        }
                                    })
                                    .show();
                        }
                    }
                }
            });
        }
    }

    private void saveUserInstagramIdOnline(final String id){
        if(materialDialog != null){
            if(materialDialog.isShowing()) {
                materialDialog.setContent("Saving data online...");
            }
        }
        else {
            materialDialog = new MaterialDialog.Builder(FindFriendsSetup.this)
                    .content("Saving data online...")
                    .progress(true, 0)
                    .cancelable(false)
                    .autoDismiss(false)
                    .show();
        }
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_login_details), Context.MODE_PRIVATE);
        String username = sharedPref.getString(getString(R.string.username), "notfound");
        if(username.contains("notfound")){
            goToLoginActivity();
        }
        else {
            final AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("instagramid", id);
            params.put("username", username);
            client.setEnableRedirects(true);
            client.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1");
            client.post(FindFriendsSetup.this, Constants.WHISPER_URL_UPDATE_INSTAGRAM_ID, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    syncUserTableOnline("insta");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (materialDialog != null) {
                        if (materialDialog.isShowing()) {
                            materialDialog.dismiss();
                            materialDialog = null;
                            new MaterialDialog.Builder(FindFriendsSetup.this)
                                    .content("Failed to connect to online server")
                                    .positiveColorRes(R.color.actionbar_color)
                                    .positiveText("Retry")
                                    .callback(new MaterialDialog.ButtonCallback() {
                                        @Override
                                        public void onPositive(MaterialDialog dialog) {
                                            saveUserInstagramIdOnline(id);
                                        }
                                    })
                                    .show();
                        }
                    }
                }

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onCancel() {
                    if (materialDialog != null) {
                        if (materialDialog.isShowing()) {
                            materialDialog.dismiss();
                            materialDialog = null;
                            new MaterialDialog.Builder(FindFriendsSetup.this)
                                    .content("Failed to connect to online server")
                                    .positiveColorRes(R.color.actionbar_color)
                                    .positiveText("Retry")
                                    .callback(new MaterialDialog.ButtonCallback() {
                                        @Override
                                        public void onPositive(MaterialDialog dialog) {
                                            saveUserInstagramIdOnline(id);
                                        }
                                    })
                                    .show();
                        }
                    }
                }

            });
        }
    }

    private void syncUserTableOnline(final String fborInsta) {
        if(materialDialog != null){
            if(materialDialog.isShowing()) {
                materialDialog.setContent("Synchronising data...");
            }
        }
        else {
            materialDialog = new MaterialDialog.Builder(FindFriendsSetup.this)
                    .content("Synchronising data...")
                    .progress(true, 0)
                    .cancelable(false)
                    .autoDismiss(false)
                    .show();
        }

        final AsyncHttpClient client = new AsyncHttpClient();
        client.setEnableRedirects(true);
        client.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1");
        client.get(this, Constants.WHISPER_URL_SYNC_DATA, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String s = new String(responseBody, "UTF-8");
                    Log.i("success", s);
                    updateSQLite(s,fborInsta);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(materialDialog != null) {
                    if(materialDialog.isShowing()) {
                        materialDialog.dismiss();
                        materialDialog = null;
                        new MaterialDialog.Builder(FindFriendsSetup.this)
                                .content("Failed to synchronise data.")
                                .positiveColorRes(R.color.actionbar_color)
                                .positiveText("Retry")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        syncUserTableOnline(fborInsta);
                                    }
                                })
                                .show();
                    }
                }
            }

            @Override
            public void onStart() {
                super.onStart();
            }


            @Override
            public void onCancel() {
                if(materialDialog != null) {
                    if(materialDialog.isShowing()) {
                        materialDialog.dismiss();
                        materialDialog = null;
                        new MaterialDialog.Builder(FindFriendsSetup.this)
                                .content("Failed to synchronise data.")
                                .positiveColorRes(R.color.actionbar_color)
                                .positiveText("Retry")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        syncUserTableOnline(fborInsta);
                                    }
                                })
                                .show();
                    }
                }
            }
        });

    }

    private void updateSQLite(String response, String fborInsta) {
        ArrayList<HashMap<String, String>> usersynclist;
        usersynclist = new ArrayList<HashMap<String, String>>();
        // Create GSON object
        Gson gson = new GsonBuilder().create();
        new WhisperDB(FindFriendsSetup.this).getReadableDatabase().delete(UserTableDefn.TableDfn.TABLE_NAME, null, null);
        ContentValues contentValues = new ContentValues();
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            // If no of array elements is not zero
            if (arr.length() != 0) {
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);
                    contentValues.put(UserTableDefn.TableDfn.COLUMN_NAME_USERNAME, (String) obj.get("username"));
                    contentValues.put(UserTableDefn.TableDfn.COLUMN_NAME_DISPLAY_NAME, (String) obj.get("displayname"));
                    contentValues.put(UserTableDefn.TableDfn.COLUMN_NAME_EMAIL, (String) obj.get("email"));
                    contentValues.put(UserTableDefn.TableDfn.COLUMN_NAME_FBID, (String) obj.get("fbid"));
                    contentValues.put(UserTableDefn.TableDfn.COLUMN_NAME_PROFILE_PIC, (String) obj.get("profilepic"));
                    contentValues.put(UserTableDefn.TableDfn.COLUMN_NAME_INSTAGRAMID, (String) obj.get("instagramid"));
                    WhisperDB whisperDB = new WhisperDB(FindFriendsSetup.this);
                    SQLiteDatabase sqLiteDatabase = whisperDB.getWritableDatabase();
                    sqLiteDatabase.insert(UserTableDefn.TableDfn.TABLE_NAME,null,contentValues);
                }
                if(fborInsta.contains("fb")){
                    storeFbIdInPref(fbid);
                }
                else if (fborInsta.contains("insta")) {
                    storeInstaIdInPref(instaid);
                }

            }
        }
        catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void storeInstaIdInPref(String instaid) {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_login_details), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.instaid), instaid);
        editor.commit();
        fetchUserInstagramFollowers();
    }


    private void storeFbIdInPref(String fbid) {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_login_details), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.fbid),fbid);
        editor.commit();
        fetchUserFbFriends();
    }

    private void fetchUserInstagramFollowers() {
        if(materialDialog != null){
            if(materialDialog.isShowing()) {
                materialDialog.setContent("Fetching Instagram followers details...");
            }
        }
        else {
            materialDialog = new MaterialDialog.Builder(FindFriendsSetup.this)
                    .content("Fetching your Instagram followers details...")
                    .progress(true, 0)
                    .cancelable(false)
                    .autoDismiss(false)
                    .show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                url = "https://api.instagram.com/v1/users/"
                        + mApp.getUserInfo().get(InstagramApp.TAG_ID)
                        + "/follows?access_token=" + mApp.getTOken();
                try {
                    JSONParser jsonParser = new JSONParser(); // make request from a thread.
                    JSONObject jsonObject = jsonParser.getJSONFromUrlByGet(url);
                    try {
                        JSONArray data = jsonObject.getJSONArray(TAG_DATA);
                        HashMap<String, String> hashMap = null;
                        if(data.length() > 0) {
                            for (int data_i = 0; data_i < data.length(); data_i++) {
                                hashMap = new HashMap<String, String>();
                                JSONObject data_obj = data.getJSONObject(data_i);
                                hashMap.put(TAG_PROFILE_PICTURE,
                                        data_obj.getString(TAG_PROFILE_PICTURE));
                                hashMap.put(TAG_USERNAME,
                                        data_obj.getString(TAG_USERNAME));
                                hashMap.put(TAG_ID,
                                        data_obj.getString(TAG_ID));
                                isInstagramFrndOnWhisper(hashMap);
                            }
                            goToShowFriendsActivity("insta");
                        }
                        else {
                            displaySadNews("Your instagram followers are yet to join Whisper");
                        }

                    } catch (JSONException e) {
                        if(materialDialog != null) {
                            if(materialDialog.isShowing()) {
                                materialDialog.dismiss();
                                materialDialog = null;
                                Toast.makeText(FindFriendsSetup.this,"Encountered an error. Try again",Toast.LENGTH_LONG).show();
                            }
                        }
                    } 

                }
                catch (Exception exception) {
                    if(materialDialog != null) {
                        if(materialDialog.isShowing()) {
                            materialDialog.dismiss();
                            materialDialog = null;
                            Toast.makeText(FindFriendsSetup.this,"Encountered an error. Try again",Toast.LENGTH_LONG).show();
                        }
                    }}
            }
        }).start();
    }

    private void isInstagramFrndOnWhisper(HashMap<String, String> hashMap) {
        WhisperDB whisperDB = new WhisperDB(FindFriendsSetup.this);
        SQLiteDatabase sqLiteDatabase = whisperDB.getReadableDatabase();
        String selection = UserTableDefn.TableDfn.COLUMN_NAME_INSTAGRAMID;
        String[] args =  {hashMap.get(TAG_ID)};
        String[] columns = {UserTableDefn.TableDfn.COLUMN_NAME_USERNAME,UserTableDefn.TableDfn.COLUMN_NAME_PROFILE_PIC};
        Cursor cursor =  sqLiteDatabase.query(true, UserTableDefn.TableDfn.TABLE_NAME, columns, selection + "=?", args, null,null,null,null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            sqLiteDatabase = whisperDB.getWritableDatabase();
            ContentValues values = new ContentValues();
          //  for(int i = 0; i < hashMap.size(); i++) {
                values.put(InstaFriends.TableDfn.COLUMN_NAME_INSTAGRAMID,hashMap.get(TAG_ID));
                values.put(InstaFriends.TableDfn.COLUMN_NAME_DISPLAY_NAME,hashMap.get(TAG_USERNAME));
                values.put(InstaFriends.TableDfn.COLUMN_NAME_PROFILE_PIC,hashMap.get(TAG_PROFILE_PICTURE));
                sqLiteDatabase.insert(InstaFriends.TableDfn.TABLE_NAME, null, values);
           // }

        }
    }

    private void fetchUserFbFriends() {
        if(materialDialog != null){
            if(materialDialog.isShowing()) {
                materialDialog.setContent("Fetching Facebook friends details...");
            }
        }
        else {
            materialDialog = new MaterialDialog.Builder(FindFriendsSetup.this)
                    .content("Fetching Facebook friends details...")
                    .progress(true, 0)
                    .cancelable(false)
                    .autoDismiss(false)
                    .show();
        }
        PictureAttributes pictureAttributes = Attributes.createPictureAttributes();
        pictureAttributes.setHeight(500);
        pictureAttributes.setWidth(500);
        Profile.Properties properties = new Profile.Properties.Builder()
                .add(Profile.Properties.FIRST_NAME)
                .add(Profile.Properties.LAST_NAME)
                .add(Profile.Properties.ID)
                .add(Profile.Properties.PICTURE,pictureAttributes)
                .build();
        mSimpleFacebook.getFriends(properties, new OnFriendsListener() {
            @Override
            public void onComplete(List<Profile> friends) {
                if (friends.size() > 0) {
                    createFrndsDb(friends);
                } else {
                    displaySadNews("Your Facebook friends are yet to join Whisper");
                }
            }

            @Override
            public void onFail(String reason) {
                if(materialDialog != null) {
                    if(materialDialog.isShowing()) {
                        materialDialog.dismiss();
                        materialDialog = null;
                        new MaterialDialog.Builder(FindFriendsSetup.this)
                                .content("Failed to fetch Facebook friends deatls.")
                                .positiveColorRes(R.color.actionbar_color)
                                .positiveText("Retry")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        fetchUserFbFriends();
                                    }
                                })
                                .show();
                    }
                }
            }

            @Override
            public void onThinking() {
                super.onThinking();
            }

            @Override
            public void onException(Throwable throwable) {
                if(materialDialog != null) {
                    if(materialDialog.isShowing()) {
                        materialDialog.dismiss();
                        materialDialog = null;
                        new MaterialDialog.Builder(FindFriendsSetup.this)
                                .content("Failed to fetch Facebook friends deatls.")
                                .positiveColorRes(R.color.actionbar_color)
                                .positiveText("Retry")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        fetchUserFbFriends();
                                    }
                                })
                                .show();
                    }
                }
            }
        });
    }

    private void displaySadNews(final String sadMesssage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(materialDialog != null) {
                    if(materialDialog.isShowing()) {
                        materialDialog.dismiss();
                        materialDialog = null;
                        new MaterialDialog.Builder(FindFriendsSetup.this)
                                .content(sadMesssage)
                                .positiveColorRes(R.color.actionbar_color)
                                .positiveText("Choose again")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                }
            }
        });

    }

    private void createFrndsDb(List<Profile> friends) {
        WhisperDB whisperDB = new WhisperDB(this);
        SQLiteDatabase sqLiteDatabase = whisperDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        for(int i = 0; i < friends.size(); i++) {
            values.put(FriendsTable.TableDfn.COLUMN_NAME_FBID,friends.get(i).getId());
            values.put(FriendsTable.TableDfn.COLUMN_NAME_DISPLAY_NAME,friends.get(i).getFirstName() + " " +friends.get(i).getLastName());
            values.put(FriendsTable.TableDfn.COLUMN_NAME_PROFILE_PIC,friends.get(i).getPicture());
            sqLiteDatabase.insert(FriendsTable.TableDfn.TABLE_NAME, null, values);
        }
        goToShowFriendsActivity("fb");
    }

    private void goToShowFriendsActivity(final String fbOrInsta) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPref = FindFriendsSetup.this.getSharedPreferences(getString(R.string.user_login_details), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.isFindFnds), "true");
                editor.commit();
                Intent intent;
                intent = new Intent(FindFriendsSetup.this, ShowFriends.class);
                intent.putExtra(fbOrInsta, "true");
                finish();
                startActivity(intent);
            }
        });

    }

    private void goToLoginActivity() {
        Intent intent;
        intent = new Intent(FindFriendsSetup.this, LoginActivity.class);
        finish();
        startActivity(intent);
    }


}
