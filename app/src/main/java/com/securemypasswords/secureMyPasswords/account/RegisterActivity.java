package com.securemypasswords.secureMyPasswords.account;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.securemypasswords.secureMyPasswords.R;
import com.securemypasswords.secureMyPasswords.secure.SetupPassword;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private EditText mEmailView;
    private View mProgressView;
    private View mRegisterFormView;

    private RequestQueue requestQueue;


    private String defaultURL = "https://maxime-cassina.pro/private/projects/secureMyPasswords/v1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.activity_register);

        mEmailView = findViewById(R.id.et_email_register);
        mPasswordView = findViewById(R.id.et_password_register);
        mConfirmPasswordView = findViewById(R.id.et_confirmPassword_register);

        mRegisterFormView = findViewById(R.id.ll_register_form);
        mProgressView = findViewById(R.id.register_progress);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

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

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !SetupPassword.validThePassword(mPasswordView, mConfirmPasswordView)) {
            mPasswordView.setError(getString(R.string.error_toShort_password));
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
            //mAuthTask = new WebAuth(this, "register", email, password);
            //mAuthTask.execute((Void) null);
            checkRegister(email, password);
        }
    }

    private void checkRegister(final String email, final String password) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                defaultURL + "/register", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("SecureMypasswords", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean resultSuccess = !jsonObject.getBoolean("error");
                    if (resultSuccess) {
                        runLoginActivity();
                    } else {
                        String errorMessage = jsonObject.getString("message");
                        if (errorMessage.contains("email")) {
                            mEmailView.setError(errorMessage);
                            mEmailView.requestFocus();
                        } else {
                            Snackbar.make(findViewById(R.id.sv_login_form), errorMessage, Snackbar.LENGTH_INDEFINITE).show();
                        }
                        showProgress(false);
                    }
                } catch (JSONException e) {
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), getString(R.string.server_answer_error), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connMgr != null) {
                    NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Snackbar.make(findViewById(R.id.sv_register_form), error.getCause().getMessage(), Snackbar.LENGTH_INDEFINITE).show();
                    } else {
                        Snackbar.make(findViewById(R.id.sv_register_form), getString(R.string.turnOnNetwork), Snackbar.LENGTH_INDEFINITE).show();

                    }
                }
                showProgress(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        requestQueue.add(strReq);
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
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

}
