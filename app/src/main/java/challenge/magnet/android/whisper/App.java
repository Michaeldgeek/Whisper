package challenge.magnet.android.whisper;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.FacebookSdk;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMXUser;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

import challenge.magnet.android.whisper.activities.LoginActivity;


public class App extends Application {

    private static App Instance;
    public static volatile Handler applicationHandler = null;

    @Override
    public void onCreate() {
        super.onCreate();
        MMX.init(this, R.raw.whisper);
        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(getString(R.string.app_id))
                .setNamespace(Constants.APP_NS)
                .setPermissions(Constants.permissions)
                .build();
        SimpleFacebook.setConfiguration(configuration);
        Instance=this;
        applicationHandler = new Handler(getInstance().getMainLooper());
        NativeLoader.initNativeLibs(App.getInstance());
        MMX.registerWakeupBroadcast(new Intent("MY_WAKEUP_ACTION"));
        MMX.registerListener(new MMX.EventListener() {
            @Override
            public boolean onMessageReceived(MMXMessage mmxMessage) {
               // sendNotification(mmxMessage);
                return true;
            }

            @Override
            public boolean onLoginRequired(MMX.LoginReason reason) {
                if (reason == MMX.LoginReason.SERVICE_UNAVAILABLE)
                    attemptLogin();
                else if (reason == MMX.LoginReason.DISCONNECTED)
                    attemptLogin();
                else if (reason == MMX.LoginReason.SERVICE_AVAILABLE) {
                    ;
                } else if (reason == MMX.LoginReason.CREDENTIALS_EXPIRED)
                    goToLoginActivity();
                return true;
            }
        });
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(getInstance(), LoginActivity.class);
        startActivity(intent);
    }

    private void attemptLogin() {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_login_details), Context.MODE_PRIVATE);
        String username = sharedPref.getString(getString(R.string.username), "notfound");
        String password_string = sharedPref.getString(getString(R.string.password),"notfound");
        if(username.contains("notfound")){
        }
        else {
            byte[] password = password_string.getBytes(); // since the password in pref file is string, conversion is necessary.
            MMX.login(username, password, new MMX.OnFinishedListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    MMX.start();
                }

                @Override
                public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
                   if(failureCode == MMX.FailureCode.DEVICE_CONCURRENT_LOGIN){;}
                    else if(failureCode == MMX.FailureCode.SERVICE_UNAVAILABLE)
                       attemptLogin();
                    else if(failureCode == MMX.FailureCode.SERVER_AUTH_FAILED){;}
                    else if(failureCode == MMX.FailureCode.SERVER_ERROR)
                       attemptLogin();
                }
            });
        }
    }

    /**private void sendNotification(MMXMessage mmxMessage) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getInstance())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText(mmxMessage.getContent().get("message"))
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setTicker(mmxMessage.getContent().get("message"))
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        NotificationManager mNotificationManager =
                (NotificationManager) getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    } **/

    public static App getInstance()
    {
        return Instance;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // unregister listener
    }
}
