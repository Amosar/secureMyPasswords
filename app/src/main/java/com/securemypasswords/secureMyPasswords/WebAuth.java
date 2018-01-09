package com.securemypasswords.secureMyPasswords;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.securemypasswords.secureMyPasswords.account.authActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class WebAuth extends AsyncTask<Void, Void, Boolean> {

    private final String mEmail;
    private final String mPassword;
    private final authActivity parent;
    private final String action;
    private String defaultURL = "https://www.maxime-cassina.pro/private/projects/secureMyPasswords/v1";
    private String apiKey;

    public WebAuth(authActivity parent, String action, String email, String password) {
        mEmail = email;
        mPassword = password;
        this.action = action;
        this.parent = parent;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean resultSuccess = false;
        String resultMsg = "";
        SharedPreferences serverSetting = parent.getApplicationContext().getSharedPreferences("serverSetting", 0);
        String serverUrl = serverSetting.getString("url", defaultURL);
        try {
            URL serverURL = new URL(serverUrl + "/" + action);
            HttpsURLConnection connection = (HttpsURLConnection) serverURL.openConnection();

            connection.setRequestMethod("POST");

            connection.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            String data = "email=" + mEmail + "&password=" + mPassword;
            wr.write(data);
            wr.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == 201 || responseCode == 200) {
                InputStream responseBody = connection.getInputStream();
                InputStreamReader responseBodyReader =
                        new InputStreamReader(responseBody, "UTF-8");

                BufferedReader reader = new BufferedReader(responseBodyReader);
                String inputLine;
                StringBuilder stringBuilder = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                resultSuccess = !jsonObject.getBoolean("error");
                if (jsonObject.has("message")) {
                    resultMsg = jsonObject.getString("message");
                }
                if (resultSuccess) {
                    if (jsonObject.has("apiKey")) {
                        apiKey = jsonObject.getString("apiKey");
                    } else {
                        resultMsg = "no Api key";
                    }
                }


                responseBodyReader.close();
                responseBody.close();
                connection.disconnect();
            }

        } catch (MalformedURLException e) {
            parent.setError(parent.getApplicationContext().getString(R.string.malformedURL));
            resultSuccess = false;
        } catch (ProtocolException e) {
            parent.setError(parent.getApplicationContext().getString(R.string.connectionError));
            resultSuccess = false;
        } catch (IOException e) {
            parent.setError(parent.getApplicationContext().getString(R.string.connectionError));
            resultSuccess = false;
        } catch (JSONException e) {
            e.printStackTrace();
            resultSuccess = false;
        }

        parent.sendMsg(resultMsg);
        return resultSuccess;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        parent.showProgress(false);

        if (success) {
            parent.authSuccess(apiKey);
        } else {
            parent.setError(parent.getApplicationContext().getString(R.string.error_incorrect_password));
        }
    }

    @Override
    protected void onCancelled() {
        parent.showProgress(false);
    }
}
