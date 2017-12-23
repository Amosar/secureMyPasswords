package com.securemypasswords.securemypasswords;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("com.securemypassword", MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        File file = new File(getApplicationContext().getFilesDir(), "securedMyPassword.smp");

        Intent intent;
        if(file.exists()) {
            intent = new Intent(this, unlock_storage.class);
        }else{
            intent = new Intent(this, SetupActivity.class);
        }
        startActivity(intent);
        finish();
        /*if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs

            Intent intent = new Intent(this, SetupActivity.class);
            startActivity(intent);
            finish();
            //prefs.edit().putBoolean("firstrun", false).commit();
        }else{
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }*/
    }
}