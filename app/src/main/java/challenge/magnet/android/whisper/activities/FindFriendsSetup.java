package challenge.magnet.android.whisper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXUser;

import challenge.magnet.android.whisper.ChatActivity;
import challenge.magnet.android.whisper.R;

public class FindFriendsSetup extends ActionBarActivity {
    String username = "michael";
    byte[] password = "android".getBytes();
    MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final MMXUser user = new MMXUser.Builder().username(username).build();
        materialDialog = new MaterialDialog.Builder(this)
                .title("Register as Michael")
                .content("Please wait")
                .progress(true, 0)
                .cancelable(false)
                .show();
        user.register(password, new MMXUser.OnFinishedListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }

            @Override
            public void onFailure(MMXUser.FailureCode failureCode, Throwable throwable) {
                FindFriendsSetup.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(MMX.getCurrentUser() == null) {
                            materialDialog.setTitle("Logging in as Michael ");
                            MMX.login(username, password, new MMX.OnFinishedListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    materialDialog.dismiss();
                                    startActivity(new Intent(FindFriendsSetup.this, ChatActivity.class));
                                }

                                @Override
                                public void onFailure(final MMX.FailureCode failureCode, Throwable throwable) {
                                    Toast.makeText(getApplicationContext(), "Unexpected error " + failureCode.toString()+". Exit app and try again!", Toast.LENGTH_LONG).show();
                                    materialDialog.dismiss();
                                    FindFriendsSetup.this.finish();
                                }
                            });

                        }
                        else {
                            materialDialog.dismiss();
                            MMX.start();
                            startActivity(new Intent(FindFriendsSetup.this, ChatActivity.class));
                            FindFriendsSetup.this.finish();
                        }
                    }
                });

            }
        });

    }

}
