package com.securemypasswords.secureMyPasswords;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.securemypasswords.secureMyPasswords.passwordsStorage.Password;
import com.securemypasswords.secureMyPasswords.secure.SetupPassword;

import java.io.Serializable;

public class PasswordManage extends AppCompatActivity {

    private EditText title, url, name, password, note;
    private int position = -1;
    private RelativeLayout activity_passwordManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_password_manage);

        activity_passwordManage = findViewById(R.id.activity_passwordManage_layout);

        title = findViewById(R.id.et_passManage_title);
        url = findViewById(R.id.et_passManage_url);
        name = findViewById(R.id.et_passManage_userName);
        password = findViewById(R.id.et_passManage_password);
        note = findViewById(R.id.et_passManage_note);

        Serializable intentExtra = getIntent().getSerializableExtra("appElements");
        position = getIntent().getIntExtra("position", -1);
        if(intentExtra instanceof  Password){
            Password element = (Password) intentExtra;
            title.setText(element.getName());
            name.setText(element.getUserName());
            password.setText(element.getPassword());
            url.setText(element.getUrl());
            note.setText(element.getNote());
        }

        initSaveListener();
        initFollowUrlListener();
        initPasswordChangeListener();
    }

    private void initPasswordChangeListener() {
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(PasswordManage.this);
                builder.setTitle("Change the Password");

                View view = LayoutInflater.from(PasswordManage.this).inflate(R.layout.dialog_change_password, activity_passwordManage, false);
                builder.setView(view);

                final TextView passwordStrengthIndicator = view.findViewById(R.id.tv_passwordStrength_changePassword);
                final EditText newPassword = view.findViewById(R.id.et_newPassword_changePassword);
                final EditText confirmNewPassword = view.findViewById(R.id.et_confirmNewPassword_changePassword);


                newPassword.setText(password.getText().toString());
                confirmNewPassword.setText(password.getText().toString());

                TextWatcher watcher = SetupPassword.getPasswordTextWatcher(getApplicationContext(), newPassword, passwordStrengthIndicator, confirmNewPassword);
                newPassword.addTextChangedListener(watcher);

                builder.setPositiveButton("OK", null);

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                final AlertDialog alertDialog = builder.create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(final DialogInterface dialog) {

                        Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                if (newPassword.getText().toString().equals(confirmNewPassword.getText().toString())) {
                                    password.setText(newPassword.getText().toString());
                                    dialog.dismiss();
                                }else{
                                    newPassword.setError(getString(R.string.password_not_match_error));
                                    confirmNewPassword.setError(getString(R.string.password_not_match_error));
                                }
                            }
                        });
                    }
                });

                alertDialog.show();
            }
        });
        password.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", password.getText().toString());
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.copiedToClipBoard), Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
    }

    private void initFollowUrlListener() {

        ImageButton bt = findViewById(R.id.bt_passManage_url);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textUrl = url.getText().toString();
                Uri uri;
                if (textUrl.contains("http") || textUrl.contains("https")) {
                    uri = Uri.parse(textUrl);
                } else {
                    uri = Uri.parse("https://" + textUrl);
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }

    private void initSaveListener() {
        Button save = findViewById(R.id.entry_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validForm()){
                    Intent intent = new Intent();
                    intent.putExtra("appElement", new Password(password.getText().toString(),
                            title.getText().toString(),
                            url.getText().toString(),
                            name.getText().toString(),
                            note.getText().toString()));
                    if(position != -1){
                        intent.putExtra("position",position);
                    }
                    setResult(AppRequestVariables.RESULT_OK.getValue(), intent);
                    finish();
                }
            }
        });

    }

    private boolean validForm() {
        boolean cancel = false;
        if(title.getText().toString().equalsIgnoreCase("")){
            title.setError(getApplicationContext().getString(R.string.error_titleIsEmpty));
            title.requestFocus();
            cancel = true;
        }
        return !cancel;
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
}
