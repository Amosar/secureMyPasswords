package com.securemypasswords.secureMyPasswords.account;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
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

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private RequestQueue requestQueue;


    private String defaultURL = "https://maxime-cassina.pro/private/projects/secureMyPasswords/v1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupActionBar();

        mEmailView = findViewById(R.id.et_email_login);

        mPasswordView = findViewById(R.id.et_password_login);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.bt_confirm_login);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.ll_login_form);
        mProgressView = findViewById(R.id.login_progress);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        initConfirmListener();
        initAlreadyAnAccountButton();

    }

    private void initConfirmListener() {
        final Button confirmButton = findViewById(R.id.bt_confirm_login);

        EditText.OnEditorActionListener onEditorActionListener = new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                attemptLogin();

                return false;
            }

        };
        mPasswordView.setOnEditorActionListener(onEditorActionListener);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void initAlreadyAnAccountButton() {
        Button goToLoginScreen = findViewById(R.id.bt_registerFrom_login);
        goToLoginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runRegisterActivity();
            }
        });
    }

    private void runRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        // Show the Up button in the action bar.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !SetupPassword.validThePassword(getApplicationContext(), mPasswordView, mPasswordView)) {
            cancel = true;
        }

        // Check for a valid email address.
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
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            showProgress(true);
            //mAuthTask = new WebAuth(this, "login", email, password);
            //mAuthTask.execute((Void) null);
            checkLogin(email, password);
        }
    }

    private void checkLogin(final String email, final String password) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                defaultURL + "/login", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("SecureMypasswords", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean resultSuccess = !jsonObject.getBoolean("error");
                    if (resultSuccess) {
                        if (jsonObject.has("apiKey")) {
                            String apiKey = jsonObject.getString("apiKey");
                            authSuccess(apiKey);
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.loginWithoutApiKey), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        CharSequence errorMessage = jsonObject.getString("message");
                        Snackbar.make(findViewById(R.id.sv_login_form), errorMessage, Snackbar.LENGTH_INDEFINITE).show();
                        showProgress(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
                        if(error.getCause() == null){
                            mEmailView.setError(getString(R.string.wrongLogin));
                        }else{
                            Snackbar.make(findViewById(R.id.sv_login_form), error.getCause().getMessage(), Snackbar.LENGTH_INDEFINITE).show();
                        }
                    } else {
                        Snackbar.make(findViewById(R.id.sv_login_form), getString(R.string.turnOnNetwork), Snackbar.LENGTH_INDEFINITE).show();

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

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
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

    public void authSuccess(String apiKey) {
        SharedPreferences authPref = getSharedPreferences("auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = authPref.edit();
        editor.putString("apiKey", apiKey);
        editor.apply();
        editor.commit();
        Toast.makeText(getApplicationContext(), getString(R.string.select_online_account_again), Toast.LENGTH_LONG).show();
        finish();

    }
}

