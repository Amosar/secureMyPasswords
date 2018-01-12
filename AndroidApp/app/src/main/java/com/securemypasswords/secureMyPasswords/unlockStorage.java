package com.securemypasswords.secureMyPasswords;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.securemypasswords.secureMyPasswords.passwordListActivity.PasswordListActivity;
import com.securemypasswords.secureMyPasswords.passwordsStorage.AppElements;
import com.securemypasswords.secureMyPasswords.passwordsStorage.FileParser;
import com.securemypasswords.secureMyPasswords.secure.CryptManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;

public class unlockStorage extends AppCompatActivity {

    EditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_storage);
        passwordView = findViewById(R.id.et_login_password);
        String s = getIntent().getStringExtra("PASSWORD");
        ArrayList<AppElements> elements = unlockPasswordFile(s);
        if(elements != null){
            Intent intent = new Intent(this, PasswordListActivity.class);
            intent.putExtra("ELEMENTS", elements);
            intent.putExtra("PASSWORD", s);
            startActivity(intent);
            finish();
        }else{
            initConfirmButtonListener();
        }
    }

    private void initConfirmButtonListener() {
        Button confirm = findViewById(R.id.bt_unlockPassword_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText passwordView = findViewById(R.id.et_login_password);
                String password = passwordView.getText().toString();
                ArrayList<AppElements> elements = unlockPasswordFile(password);
                if(elements != null){
                    Intent intent = new Intent(unlockStorage.this, PasswordListActivity.class);
                    intent.putExtra("ELEMENTS", elements);
                    intent.putExtra("PASSWORD", password);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }


    private ArrayList<AppElements> unlockPasswordFile(String password){
        passwordView.setError(null);
        CryptManager cryptManager = new CryptManager("AES");
        ArrayList<AppElements> dataObjects = null;
        try {
            FileInputStream fileInputStream = openFileInput("securedMyPassword.smp");
            StringBuilder fileContent = new StringBuilder("");
            byte[] buffer = new byte[1024];

            int n;
            while ((n = fileInputStream.read(buffer)) != -1)
            {
                fileContent.append(new String(buffer, 0, n));
            }
            String data = cryptManager.decrypt(fileContent.toString(),password);
            FileParser fileParser = new FileParser();
            dataObjects = fileParser.xmlToObject(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            if(e instanceof InvalidKeyException || e instanceof  BadPaddingException){
                passwordView.setError(getApplicationContext().getString(R.string.error_incorrect_password));
                passwordView.requestFocus();
            }
        }
        return dataObjects;
    }


}
