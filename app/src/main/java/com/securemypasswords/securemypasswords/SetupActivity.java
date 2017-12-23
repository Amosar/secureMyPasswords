package com.securemypasswords.securemypasswords;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.securemypasswords.securemypasswords.passwordsStorage.FileParser;
import com.securemypasswords.securemypasswords.secure.CryptManager;
import com.securemypasswords.securemypasswords.secure.SetupPassword;

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

                if(SetupPassword.validThePassword(mPasswordView,mConfirmPasswordView)) {
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
        TextView passwordStrength = findViewById(R.id.et_setup_passwordStrength);
        TextWatcher textWatcher = SetupPassword.getPasswordTextWatcher(getApplicationContext(),
                mPasswordView,passwordStrength,mConfirmPasswordView);

        mPasswordView.addTextChangedListener(textWatcher);
        mConfirmPasswordView.setOnEditorActionListener(onEditorActionListener);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SetupPassword.validThePassword(mPasswordView,mConfirmPasswordView)) {
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
}
