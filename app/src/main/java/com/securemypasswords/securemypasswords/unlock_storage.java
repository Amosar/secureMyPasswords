package com.securemypasswords.securemypasswords;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.securemypasswords.securemypasswords.passwordListActivity.PasswordListActivity;
import com.securemypasswords.securemypasswords.passwordsStorage.FileParser;
import com.securemypasswords.securemypasswords.secure.CryptManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Arrays;

public class unlock_storage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_storage);
        String s = getIntent().getStringExtra("PASSWORD");
        Object[] elements = unlockPasswordFile(s);
        if(elements != null){
            Intent intent = new Intent(this, PasswordListActivity.class);
            intent.putExtra("ELEMENTS", elements);
            intent.putExtra("PASSWORD", s);
            startActivity(intent);
            finish();
        }
    }


    private Object[] unlockPasswordFile(String password){
        CryptManager cryptManager = new CryptManager("AES");
        Object[] dataObjects = null;
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
            e.printStackTrace();
            if(e instanceof InvalidKeyException){
                Toast.makeText(getApplicationContext(),"The password is wrong",Toast.LENGTH_LONG);
            }
        }
        return dataObjects;
    }


}
