package com.securemypasswords.secureMyPasswords.account;

import android.content.Context;

public interface authActivity {

    void showProgress(final boolean show);

    void authSuccess(String apiKey);

    void setError(String msg);

    void sendMsg(String msg);

    Context getApplicationContext();
}
