package challenge.magnet.android.whisper.activities;

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
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXUser;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import challenge.magnet.android.whisper.R;

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
                .coordinatorLayout);
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
                        attemptRegister(username, password, true);
                    }
                }
            });
    }


    private void attemptRegister(final String user, final byte[] pass, final boolean isNewUser) {
        MMXUser mmxUser = new MMXUser.Builder().username(user).displayName(user).build();
        mmxUser.register(pass, new MMXUser.OnFinishedListener<Void>() {
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "attemptRegister() success");
                mLoginSuccess.set(true);
                attemptLogin(user, pass);
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

                    } else {
                        attemptLogin(user, pass);
                    }
                }
                mLoginSuccess.set(false);
            }
        });
    }


    private void attemptLogin(String user, byte[] pass) {
        Intent intent = new Intent(this,LoginLoadingActivity.class);
        intent.putExtra("username",user);
        intent.putExtra("password",pass);
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
