package com.securemypasswords.secureMyPasswords.passwordsStorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.securemypasswords.secureMyPasswords.passwordListActivity.PasswordListActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class PasswordDownload extends AsyncTask<Void, Void, Boolean> {

    private String defaultURL = "https://www.maxime-cassina.pro/private/projects/secureMyPasswords/v1";
    private PasswordListActivity parent;

    public PasswordDownload(PasswordListActivity parent) {
        this.parent = parent;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        SharedPreferences authSetting = parent.getApplicationContext().getSharedPreferences("auth", 0);
        String apiKey = authSetting.getString("apiKey", null);
        try {
            URL url = new URL(defaultURL + "/getPasswordsFile");
            File file = new File(parent.getApplicationContext().getFilesDir(), "securedMyPassword.smp");

            URLConnection connection = url.openConnection();
            connection.addRequestProperty("auth", apiKey);


            InputStream inputStream = connection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            FileOutputStream fileOutputStream = parent.openFileOutput(file.getName(), Context.MODE_PRIVATE);

            byte[] buffer = new byte[4096];//Set buffer type
            int current;//init length
            while ((current = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, current);//Write new file
            }

            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();

            parent.updateElements();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
