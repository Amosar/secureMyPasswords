package com.securemypasswords.secureMyPasswords;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;

import java.io.File;

public class Util {

    public static void removePasswordFile(){
        Context context = InstrumentationRegistry.getTargetContext();
        File file = new File(context.getFilesDir(), "securedMyPassword.smp");
        file.deleteOnExit();
    }
}
