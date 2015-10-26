/*
* This activity has the following states
 * 1. User provides details
 * 2. attempt login. If successful, save user login details in pref file else show retry
 * 3. check if find friends has run b4. if yes, take user to main activity else take user to find friends
*/
package challenge.magnet.android.whisper.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;
import com.magnet.mmx.client.api.MMX;

import challenge.magnet.android.whisper.MainActivity;
import challenge.magnet.android.whisper.R;

public class LoginActivity extends AppCompatActivity {

    final String TAG = "LoginActivity";

    ActionProcessButton btnLogIn;
    Button buttonSignUp;
    EditText userName, password;
    CoordinatorLayout coordinatorLayout;
    String username_str;
    byte[] password_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
         coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null) {
                actionBar.hide();
            }
        //state 1 fetch user details.
            userName = (EditText) findViewById(R.id.login_username_input);
            password = (EditText) findViewById(R.id.login_password_input);

            btnLogIn = (ActionProcessButton) findViewById(R.id.button_login);
            // you can display endless google like progress indicator
            btnLogIn.setMode(ActionProcessButton.Mode.ENDLESS);
            btnLogIn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (userName.getText().toString().length() == 0 || TextUtils.isEmpty(userName.getText().toString())
                                                        ||userName.getText().toString().equals(" "))
                                                    userName.setError("Please enter your username first");
                                                else if (password.getText().toString().length() == 0 || TextUtils.isEmpty(password.getText().toString()))
                                                    password.setError("Please enter your password");
                                                else {
                                                    // set progress > 0 to start progress indicator animation
                                                    btnLogIn.setProgress(1);
                                                    username_str = userName.getText().toString().trim();
                                                    password_str = password.getText().toString().getBytes();
                                                    attemptLogin(username_str, password_str); // state 2 attempt login
                                                }
                                            }
                                        }
            );


            buttonSignUp = (Button) findViewById(R.id.signup_button);
            buttonSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                    startActivity(i);
                }
            });
    }

    private void attemptLogin(final String user, final byte[] pass) {
        MMX.login(user, pass, new MMX.OnFinishedListener<Void>() {
            public void onSuccess(Void aVoid) {

                // save user login details in pref file
                storeLoginDetailsInPref(username_str, password.getText().toString());
                if(isFindFriendRun()){
                    // state 3. user already has friends. go to Mainactivity
                    goToMainActivity();
                }
                else{
                    // state 4. user has no friends. go to find friends activity
                    goToFindFriendsActivity();
                }
            }

            public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
                Log.e(TAG, "attemptLogin() error: " + failureCode, throwable);
                if (MMX.FailureCode.SERVER_AUTH_FAILED == (failureCode)) {
                    //login failed, probably an incorrect password

                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showRetry("Incorrect username and/or password ",userName.getText().toString(),password.getText().toString().getBytes(),btnLogIn);
                            btnLogIn.setProgress(0);
                            // show snack bar for user to retry
                        }
                    });

                } else if (failureCode == MMX.FailureCode.SERVICE_UNAVAILABLE) {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnLogIn.setProgress(0);
                            showRetry("Network Isuses",user,pass,btnLogIn);
                            // show snack bar for user to retry
                        }
                    });
                } else if (failureCode == MMX.FailureCode.DEVICE_CONCURRENT_LOGIN)

                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnLogIn.setProgress(0);
                        showRetry("Multiple login attempted", user, pass,btnLogIn);
                        // show snack bar for user to retry
                    }
                });
            }
        });
    }

    private void goToFindFriendsActivity() {
        finish();
        Intent intent;
        intent = new Intent(LoginActivity.this, FindFriendsSetup.class);
        startActivity(intent);
    }

    private void goToMainActivity() {
        finish();
        Intent intent;
        intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private boolean isFindFriendRun() {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_login_details), Context.MODE_PRIVATE);
        String s = sharedPref.getString(getString(R.string.isFindFnds), "false");
        if(s.contains("true")){
            return true;
        }
        else {
            return false;
        }
    }

    private void showRetry(String message, final String user, final byte[] pass, final ActionProcessButton btnLogIn) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attemptLogin(user, pass);
                        btnLogIn.setProgress(1);
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

    public void goToWhispererSelectActivity() {
        Intent intent;
        intent = new Intent(LoginActivity.this, WhisperersActivity.class);
        startActivity(intent);
    }

    private void storeLoginDetailsInPref(String user_name, String password) {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_login_details), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.username),user_name);
        editor.putString(getString(R.string.password), password);
        editor.commit();


    }

}
