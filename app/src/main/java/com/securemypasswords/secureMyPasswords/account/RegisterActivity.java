package com.securemypasswords.secureMyPasswords.account;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.securemypasswords.secureMyPasswords.R;
import com.securemypasswords.secureMyPasswords.WebAuth;
import com.securemypasswords.secureMyPasswords.secure.SetupPassword;

public class RegisterActivity extends AppCompatActivity implements authActivity {

    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private EditText mEmailView;
    private View mProgressView;
    private View mLoginFormView;
    private WebAuth mAuthTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.activity_register);

        mEmailView = findViewById(R.id.et_email_register);
        mPasswordView = findViewById(R.id.et_password_register);
        mConfirmPasswordView = findViewById(R.id.et_confirmPassword_register);

        mLoginFormView = findViewById(R.id.ll_register_form);
        mProgressView = findViewById(R.id.register_progress);

        initCheckPasswordStrengthListener();
        initConfirmListener();
        initAlreadyAnAccountButton();
    }

    private void initAlreadyAnAccountButton() {
        Button goToLoginScreen = findViewById(R.id.bt_loginFrom_register);
        goToLoginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runLoginActivity();
            }
        });
    }


    private void initCheckPasswordStrengthListener() {
        TextView passwordStrength = findViewById(R.id.et_setup_passwordStrength);
        TextWatcher textWatcher = SetupPassword.getPasswordTextWatcher(getApplicationContext(),
                mPasswordView, passwordStrength, mConfirmPasswordView);

        mPasswordView.addTextChangedListener(textWatcher);
    }

    private void initConfirmListener() {
        final Button confirmButton = findViewById(R.id.bt_confirm_register);

        EditText.OnEditorActionListener onEditorActionListener = new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                attemptRegister();

                return false;
            }

        };
        mConfirmPasswordView.setOnEditorActionListener(onEditorActionListener);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
    }

    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !SetupPassword.validThePassword(mPasswordView, mConfirmPasswordView)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new WebAuth(this, "register", email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private void runLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void authSuccess(String apiKey) {
        runLoginActivity();
    }

    @Override
    public void setError(String msg) {
        mPasswordView.setError(msg);
        mPasswordView.requestFocus();
    }

    @Override
    public void sendMsg(String msg) {
        //Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
    }
}
