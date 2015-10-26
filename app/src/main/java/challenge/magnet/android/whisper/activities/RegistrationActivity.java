
/*
This activity has different states
1. User provides details
2. Attempt registration
3. if user registration is successful store user details in online db
4. if user registration fails, retry.
5. if user details is stored online. Store user details in preference file and start login activity.
 */
package challenge.magnet.android.whisper.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceActivity;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.api.client.json.webtoken.JsonWebToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.magnet.mmx.client.api.MMXUser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import challenge.magnet.android.whisper.Constants;
import challenge.magnet.android.whisper.R;
import cz.msebera.android.httpclient.Header;

public class RegistrationActivity extends AppCompatActivity {

    final String TAG = "RegistrationActivity";

    EditText email, userName, passWord, confirmPassword;
    ActionProcessButton btnSignUp;
    CoordinatorLayout coordinatorLayout;
    private AtomicBoolean mLoginSuccess = new AtomicBoolean(false);
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout); /* needed for snack bar*/
            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null) {
                actionBar.hide();
            }
            email = (EditText) findViewById(R.id.signup_email_input);
            userName = (EditText) findViewById(R.id.signup_username_input);
            passWord = (EditText) findViewById(R.id.signup_password_input);
            confirmPassword = (EditText) findViewById(R.id.signup_confirm_password_input);
            btnSignUp = (ActionProcessButton) findViewById(R.id.button_signup);
            btnSignUp.setMode(ActionProcessButton.Mode.ENDLESS);
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* fetch user details and validate it*/
                    if (email.getText().toString().length() == 0 || TextUtils.isEmpty(email.getText().toString()))
                        email.setError("An email is required");
                    else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                        email.setError("Invalid email");
                    } else if (userName.getText().toString().length() == 0 || TextUtils.isEmpty(userName.getText().toString())
                            || userName.getText().toString().equals(" "))
                        userName.setError("A username is required");
                    else if (passWord.getText().toString().length() == 0 || TextUtils.isEmpty(passWord.getText().toString()))
                        passWord.setError("A password is required");
                    else if (passWord.getText().toString().length() < 6) {
                        passWord.setError("Password should exceed six characters");
                    } else if (!passWord.getText().toString().equals(confirmPassword.getText().toString()))
                        confirmPassword.setError("Passwords are not the same");

                    else {
                        // set progress > 0 to start progress indicator animation
                        btnSignUp.setProgress(1);
                        String username = userName.getText().toString().trim();
                        byte[] password = passWord.getText().toString().getBytes();
                        attemptRegister(username, password, true); // attempt registration. State 2

                    }
                }
            });
    }


    private void attemptRegister(final String user, final byte[] pass, final boolean isNewUser) {
        final MMXUser mmxUser = new MMXUser.Builder().username(user).displayName(user).build();
        mmxUser.register(pass, new MMXUser.OnFinishedListener<Void>() {
            public void onSuccess(Void aVoid) {
                // successful, store user details in online db state 3
                Log.e(TAG, "attemptRegister() success");
                mLoginSuccess.set(true);
                registerUserOnline(user, passWord.getText().toString(), email.getText().toString(), user);

            }

            public void onFailure(MMXUser.FailureCode failureCode, Throwable throwable) {
                if (MMXUser.FailureCode.REGISTRATION_INVALID_USERNAME.equals(failureCode)) {
                    Log.e(TAG, "attemptRegister() error: " + failureCode, throwable);
                    showRetry("Sorry, that's not a valid username.", btnSignUp);

                }
                if (MMXUser.FailureCode.REGISTRATION_USER_ALREADY_EXISTS.equals(failureCode)) {
                    if (isNewUser) {
                        Log.e(TAG, "attemptRegister() error: " + failureCode, throwable);
                        showRetry("Sorry, this user already exists.", btnSignUp);

                    }
                }
                if ((MMXUser.FailureCode.SERVICE_UNAVAILABLE).equals(failureCode)) {
                    Log.e(TAG, "attemptRegister() error: " + failureCode, throwable);
                    showRetry("Check network and retry", btnSignUp);
                }
                if ((MMXUser.FailureCode.SERVER_ERROR).equals(failureCode)) {
                    Log.e(TAG, "attemptRegister() error: " + failureCode, throwable);
                    showRetry("Check network and retry", btnSignUp);
                }
                mLoginSuccess.set(false);
            }
        });
    }

    private void registerUserOnline(final String username, final String password,String email, String displayname) {
        // store in online db
        final AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("username", username.toString());
        params.put("password",password.toString());
        params.put("email", email.toString());
        params.put("displayname", displayname.toString());
        client.setEnableRedirects(true);
        client.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1");
        client.post(RegistrationActivity.this, Constants.WHISPER_URL_REGISTER, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String s = new String(responseBody, "UTF-8");
                    Log.i("success", s);
                    /*
                    User details was saved online successfully. move to state 5
                     */
                    storeLoginDetailsInPref(username.toString(), passWord.getText().toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showOnlineRegFailed("Registration not completed", btnSignUp);
            }
        });
    }

    private void showOnlineRegFailed(final String message, final ActionProcessButton btnSignUp) {
        RegistrationActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar
                        .make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                registerUserOnline(userName.getText().toString(),passWord.getText().toString(),
                                        email.getText().toString(),userName.getText().toString());
                                snackbar.dismiss();
                                if(btnSignUp.getProgress() == 0){
                                    btnSignUp.setProgress(1);
                                }
                            }
                        });
                // Changing message text color
                snackbar.setActionTextColor(Color.RED);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                btnSignUp.setProgress(0);
                snackbar.show();
            }
        });

    }

    private void storeLoginDetailsInPref(String user_name, String password) {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_login_details), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.username),user_name);
        editor.putString(getString(R.string.password),password);
        editor.commit();
        // user login details saved in global preference. attempt Login
        attemptLogin(user_name, passWord.getText().toString().getBytes());

    }


    private void attemptLogin(String user, byte[] pass) {
        finish();
        Intent intent = new Intent(this,LoginLoadingActivity.class);
        startActivity(intent);

    }

    private void showRetry(final String message, final ActionProcessButton btnSignUp) {
        RegistrationActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar
                        .make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Okay", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });
                // Changing message text color
                snackbar.setActionTextColor(Color.RED);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                btnSignUp.setProgress(0);
                snackbar.show();
            }
        });

    }

}
