package challenge.magnet.android.whisper.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.magnet.mmx.client.api.MMX;

import org.w3c.dom.Text;

import challenge.magnet.android.whisper.R;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * This activity has different states.
 * 1. Fetch data from preference file.
 * 2. If data is fetched successfully, attempt login else take user to login activity
 * 3. if user is logged in successfully, move to find friends setup activity.
 *
 */
public class LoginLoadingActivity extends AppCompatActivity {

    final String TAG = "LoginLoadingActivity";
    Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    Button retryBtn;
    TextView loggingInTxt;
    SmoothProgressBar progressBar;
    String user;
    byte[] pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_loading_activity);
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                    .coordinatorLayout);

            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null) {
                actionBar.hide();
            }
            retryBtn = (Button)findViewById(R.id.retry);
            loggingInTxt = (TextView)findViewById(R.id.login_text);
            progressBar = (SmoothProgressBar) findViewById(R.id.login_pbar);
            SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_login_details), Context.MODE_PRIVATE);
            // state 1. read user details from preference file
            user = sharedPref.getString(getString(R.string.username),"notfound");
            String password_string = sharedPref.getString(getString(R.string.password),"notfound");
            if(user.contains("notfound")){
                // state 2. user details not found. go to login activity
                goToLoginActivity();
            }
            else {
                pass = password_string.getBytes(); // since the password in pref file is string, conversion is necessary.
                attemptLogin(user, pass);
            }

    }

    private void goToLoginActivity() {
        finish();
        Intent intent;
        intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void goToFindFriendsSelectActivity(String username) {
        finish();
        Intent intent;
        intent = new Intent(this, FindFriendsSetup.class);
        startActivity(intent);
    }

    private void showRetry(final TextView retryTxt, final Button retryBtn, final SmoothProgressBar progressBar) {
        LoginLoadingActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                retryTxt.setText("Check your network connection and try again.");
                retryTxt.setTextSize(16);
                progressBar.setVisibility(View.INVISIBLE);
                retryBtn.setVisibility(View.VISIBLE);
                retryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attemptLogin(user, pass);
                        progressBar.setVisibility(View.VISIBLE);
                        retryBtn.setVisibility(View.GONE);
                        retryTxt.setText("Logging In");
                        retryTxt.setTextSize(24);
                        ;
                    }
                });
            }
        });

    }

    private void attemptLogin(final String user, byte[] password) {
        MMX.login(user, password, new MMX.OnFinishedListener<Void>() {
            public void onSuccess(Void aVoid) {
                //if an EventListener has already been registered, start receiving messages
                MMX.start();
                goToFindFriendsSelectActivity(user);

            }

            public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
                Log.e(TAG, "attemptLogin() error: " + failureCode, throwable);
                if (MMX.FailureCode.SERVICE_UNAVAILABLE.equals(failureCode)) {
                    //login failed, network issue
                    showRetry(loggingInTxt,retryBtn,progressBar);
                }
                else if (MMX.FailureCode.SERVER_ERROR.equals(failureCode)) {
                    //login failed, network issue
                    showRetry(loggingInTxt,retryBtn,progressBar);
                }
                else if (MMX.FailureCode.SERVER_AUTH_FAILED.equals(failureCode)) {
                    //login failed, network issue
                    showRetry(loggingInTxt,retryBtn,progressBar);
                }
            }
        });
    }
}
