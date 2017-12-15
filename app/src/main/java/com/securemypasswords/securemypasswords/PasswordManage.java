package com.securemypasswords.securemypasswords;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PasswordManage extends AppCompatActivity {

    private EditText title, url, name, password, note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_manage);
        title = findViewById(R.id.et_passManage_titleContent);
        url = findViewById(R.id.et_passManage_urlContent);
        name = findViewById(R.id.et_passManage_userNameContent);
        password = findViewById(R.id.et_passManage_passwordContent);
        note = findViewById(R.id.et_passManage_noteContent);

        initSaveListener();
    }

    public void initSaveListener(){
        Button save = findViewById(R.id.entry_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validForm()){
                    finish();
                }
            }
        });

    }

    public boolean validForm(){
        boolean cancel = false;
        if(title.getText().toString().equalsIgnoreCase("")){
            title.setError("The title cannot be empty");
            cancel = true;
        }

        return !cancel;
    }
}
