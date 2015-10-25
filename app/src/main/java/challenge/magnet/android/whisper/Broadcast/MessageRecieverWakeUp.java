package challenge.magnet.android.whisper.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;

import challenge.magnet.android.whisper.R;

public class MessageRecieverWakeUp extends BroadcastReceiver {
    @Override
    // called when the device sleep is interrupted by gcm
    public void onReceive(Context context, Intent intent) {
        attemptLogin(context);

    }

    private void attemptLogin(final Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.user_login_details), Context.MODE_PRIVATE);
        String username = sharedPref.getString(context.getString(R.string.username), "notfound");
        String password_string = sharedPref.getString(context.getString(R.string.password),"notfound");
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
                        attemptLogin(context);
                    else if(failureCode == MMX.FailureCode.SERVER_AUTH_FAILED){;}
                    else if(failureCode == MMX.FailureCode.SERVER_ERROR)
                        attemptLogin(context);
                }
            });
        }
    }
}
