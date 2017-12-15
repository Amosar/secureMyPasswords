package com.securemypasswords.securemypasswords;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.securemypasswords.securemypasswords.passwordListActivity.PasswordListActivity;
import com.securemypasswords.securemypasswords.passwordsStorage.FileParser;
import com.securemypasswords.securemypasswords.secure.CryptManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SetupActivity extends AppCompatActivity {

    //UI References
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;

    private String cryptAlgorithm = "AES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mPasswordView = findViewById(R.id.et_setup_password);
        mConfirmPasswordView = findViewById(R.id.et_setup_confirmpassword);


        listenEditorAction();

    }

    private void listenEditorAction(){
        final Button confirmButton = findViewById(R.id.bt_setup_confirm);

        EditText.OnEditorActionListener onEditorActionListener = new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if(validThePassword()) {
                    try {
                        initPasswordFile();
                        runPasswordListActivity();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    handled = true;
                }
                return handled;
            }

        };
        TextWatcher textWatcher = getPasswordTextWatcher();

        mPasswordView.addTextChangedListener(textWatcher);
        mConfirmPasswordView.setOnEditorActionListener(onEditorActionListener);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validThePassword()) {
                    try {
                        initPasswordFile();
                        runPasswordListActivity();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private boolean validThePassword() {
        final String password = (mPasswordView).getText().toString();
        final String confirmPassword = (mConfirmPasswordView).getText().toString();
        boolean cancel = false;
        if(password.length() < 6 || getRating(password)<1){
            mPasswordView.setError("The password is to small");
            mPasswordView.requestFocus();
            cancel = true;
        }else if(!password.equals(confirmPassword)){
            mPasswordView.setError("The two password does not match");
            mConfirmPasswordView.setError("The two password does not match");
            mConfirmPasswordView.requestFocus();
            cancel = true;
        }
        return !cancel;
        /*return */
    }

    private void initPasswordFile() throws IOException{
        File file = new File(getApplicationContext().getFilesDir(), "securedMyPassword.smp");

        file.createNewFile();
        FileOutputStream fileOutputStream = openFileOutput(file.getName(), Context.MODE_PRIVATE);

        FileParser fileParser = new FileParser();
        String xmlVersion = fileParser.objectToXml(null,null);


        /*//Todo sert seulement pour test
        Group socialMedia = new Group("social media");
        Group useless = new Group("useless");

        Password twitter = new Password("123456","Twitter","twitter.com","Jaque","");
        Password google = new Password("123456","Google","google.com","Jaque","");
        Password facebook = new Password("123456","Facebook","facebook.com","Jaque","");
        Password computerPassword = new Password("123456","Computer password","","Jaque","");
        Password cb_PIN = new Password("1234","CB PIN","bank.fr","","");
        Password dropBox_password = new Password("123456","DropBox password","dropbox.com","Jaque","");

        socialMedia.addPassword(twitter);
        socialMedia.addPassword(google);
        socialMedia.addPassword(facebook);
        socialMedia.addGroup(useless);


        String xmlVersion = fileParser.objectToXml(new Group[]{socialMedia},new Password[]{computerPassword,cb_PIN,dropBox_password});*/
        try {
            CryptManager cryptManager = new CryptManager(cryptAlgorithm);
            String encrypt = cryptManager.encrypt(xmlVersion, mPasswordView.getText().toString());
            fileOutputStream.write(encrypt.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.error,Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }



    private void runPasswordListActivity(){
        Intent intent = new Intent(this, unlock_storage.class);
        intent.putExtra("PASSWORD", mPasswordView.getText().toString());
        startActivity(intent);
        finish();
    }

    @NonNull
    private TextWatcher getPasswordTextWatcher() {
        return new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (_ignore)
                        return;

                    _ignore = true; // prevent infinite loop

                    // Reset errors.
                    mPasswordView.setError(null);
                    mConfirmPasswordView.setError(null);

                    String passwordText = s.toString();
                    TextView passwordStrength = findViewById(R.id.et_setup_passwordStrenght);

                    if (s.length() < 6 || getRating(passwordText) == 0) {
                        passwordStrength.setText(R.string.tooSmall_password);
                        passwordStrength.setTextColor(getResources().getColor(R.color.EASY));
                        if(Build.VERSION.SDK_INT >= 21) {
                            changeBackGroundTint(mPasswordView, getResources().getColor(R.color.EASY));
                        }
                    } else if (getRating(passwordText) == 1) {
                        passwordStrength.setText(R.string.medium_password);
                        passwordStrength.setTextColor(getResources().getColor(R.color.EASY));
                        if(Build.VERSION.SDK_INT >= 21) {
                            changeBackGroundTint(mPasswordView, getResources().getColor(R.color.EASY));
                        }
                    } else if (getRating(passwordText) == 2) {
                        passwordStrength.setText(R.string.medium_password);
                        passwordStrength.setTextColor(getResources().getColor(R.color.MEDIUM));
                        if(Build.VERSION.SDK_INT >= 21) {
                            changeBackGroundTint(mPasswordView, getResources().getColor(R.color.MEDIUM));
                        }
                    } else if (getRating(passwordText) == 3) {
                        passwordStrength.setText(R.string.strong_password);
                        passwordStrength.setTextColor(getResources().getColor(R.color.STRONG));
                        if(Build.VERSION.SDK_INT >= 21) {
                            changeBackGroundTint(mPasswordView, getResources().getColor(R.color.STRONG));
                        }
                    } else {
                        passwordStrength.setText(R.string.strongest_password);
                        passwordStrength.setTextColor(getResources().getColor(R.color.STRONGEST));
                        if(Build.VERSION.SDK_INT >= 21) {
                            changeBackGroundTint(mPasswordView, getResources().getColor(R.color.STRONGEST));
                        }
                    }
                    _ignore = false; // release, so the TextWatcher start to listen again.
                }

                boolean _ignore = false; // indicates if the change was made by the TextWatcher itself.
            };
    }

    @TargetApi(21)
    private void changeBackGroundTint(EditText password, int Color){
        password.setBackgroundTintList(ColorStateList.valueOf(Color));
    }


    private float getRating(String password) throws IllegalArgumentException {
        if (password == null) {throw new IllegalArgumentException();}
        int passwordStrength = 0;
        if (password.length() > 5) {passwordStrength++;} // minimal pw length of 6
        if (!password.toLowerCase().equals(password)) {passwordStrength++;} // lower and upper case
        if (password.length() > 8) {passwordStrength++;} // good pw length of 9+
        int numDigits= getNumberDigits(password);
        if (numDigits > 0 && numDigits != password.length()) {passwordStrength++;} // contains digits and non-digits
        return passwordStrength;
    }

    private int getNumberDigits(String inString){
        if (isEmpty(inString)) {
            return 0;
        }
        int numDigits= 0;
        int length= inString.length();
        for (int i = 0; i < length; i++) {
            if (Character.isDigit(inString.charAt(i))) {
                numDigits++;
            }
        }
        return numDigits;
    }

    private static boolean isEmpty(String inString) {
        return inString == null || inString.length() == 0;
    }
}
