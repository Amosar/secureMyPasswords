package com.securemypasswords.secureMyPasswords;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();

        File file = new File(getApplicationContext().getFilesDir(), "securedMyPassword.smp");

        Intent intent;
        if (file.exists()) {
            intent = new Intent(this, unlockStorage.class);
        } else {
            intent = new Intent(this, SetupActivity.class);
        }
        startActivity(intent);
        finish();
    }
}