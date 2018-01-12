package com.securemypasswords.secureMyPasswords.passwordsStorage;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class PasswordsUpload extends AsyncTask<Void, Void, Boolean> {

    private String defaultURL = "https://www.maxime-cassina.pro/private/projects/secureMyPasswords/v1";
    private String apiKey;
    private Activity parent;

    public PasswordsUpload(Activity parent) {
        this.parent = parent;

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        SharedPreferences serverSetting = parent.getApplicationContext().getSharedPreferences("serverSetting", 0);
        String serverUrl = serverSetting.getString("url", defaultURL);
        SharedPreferences authSetting = parent.getApplicationContext().getSharedPreferences("auth", 0);
        apiKey = authSetting.getString("apiKey", null);

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;

        URL serverURL;
        try {
            File file = new File(parent.getApplicationContext().getFilesDir(), "securedMyPassword.smp");
            FileInputStream fileInputStream = new FileInputStream(file);

            serverURL = new URL(serverUrl + "/sendPasswordsFile");
            HttpsURLConnection connection = (HttpsURLConnection) serverURL.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("ENCTYPE", "multipart/form-data");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            connection.setRequestProperty("uploaded_file", file.getName());
            connection.setRequestProperty("auth", apiKey);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                    + file.getName() + "\"" + lineEnd);

            dataOutputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dataOutputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            if (serverResponseCode == 200) {
                Log.d("SecureMyPassword", serverResponseMessage);
            }

            fileInputStream.close();
            dataOutputStream.flush();
            dataOutputStream.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return true;
    }
}
