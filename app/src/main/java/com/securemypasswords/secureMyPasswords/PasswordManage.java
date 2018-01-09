package com.securemypasswords.secureMyPasswords;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.securemypasswords.secureMyPasswords.passwordsStorage.Password;

import java.io.Serializable;

public class PasswordManage extends AppCompatActivity {

    private EditText title, url, name, password, note;
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_password_manage);
        title = findViewById(R.id.et_passManage_titleContent);
        url = findViewById(R.id.et_passManage_urlContent);
        name = findViewById(R.id.et_passManage_userNameContent);
        password = findViewById(R.id.et_passManage_passwordContent);
        note = findViewById(R.id.et_passManage_noteContent);

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
            title.setError("The title cannot be empty");
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
