package com.securemypasswords.securemypasswords.passwordListActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.securemypasswords.securemypasswords.AppRequestVariables;
import com.securemypasswords.securemypasswords.PasswordManage;
import com.securemypasswords.securemypasswords.R;
import com.securemypasswords.securemypasswords.passwordsStorage.AppElements;
import com.securemypasswords.securemypasswords.passwordsStorage.FileParser;
import com.securemypasswords.securemypasswords.passwordsStorage.Group;
import com.securemypasswords.securemypasswords.secure.CryptManager;
import com.securemypasswords.securemypasswords.secure.SetupPassword;
import com.securemypasswords.securemypasswords.settings.SettingsActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class PasswordListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String password;
    private ArrayList<AppElements> elements;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private FloatingActionButton fabAdd, fabAddEntry, fabAddGroup;
    private LinearLayout subFabEntry, subFabGroup;
    private Animation fabOpen, fabClose, rotateForward, rotateBackward;
    private boolean isOpen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_password_list);
        Intent intent = getIntent();
        password = intent.getStringExtra("PASSWORD");
        elements = (ArrayList<AppElements>) intent.getSerializableExtra("ELEMENTS");
        //passwords = (ArrayList<Password>) elements[0];
        //groups = (ArrayList<Group>) elements[1];

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabAdd = findViewById(R.id.fab_passwordList_add);
        fabAddEntry = findViewById(R.id.fab_passwordList_addEntry);
        subFabEntry = findViewById(R.id.linearLayout_passwordList_addEntry);
        fabAddGroup = findViewById(R.id.fab_passwordList_addGroup);
        subFabGroup = findViewById(R.id.linearLayout_passwordList_addGroup);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animatedFab();
            }
        });

        initRecyclerView();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initAddPasswordListener();
        initAddGroupListener();
    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.rv_passlist);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        /*//Todo only for testing
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

        List<AppElements> elements = new ArrayList<>();
        elements.add(socialMedia);
        elements.add(useless);
        elements.add(computerPassword);
        elements.add(cb_PIN);
        elements.add(dropBox_password);*/

  //      List<AppElements> elements = new ArrayList<>();

        mAdapter = new PasswordListAdaptater(elements, this);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mRecyclerView.setAdapter(mAdapter);
    }

    private void animatedFab(){
        if(isOpen){
            fabAdd.startAnimation(rotateBackward);
            subFabEntry.startAnimation(fabClose);
            subFabGroup.startAnimation(fabClose);
            fabAddEntry.setClickable(false);
            fabAddGroup.setClickable(false);
        }else{
            fabAdd.startAnimation(rotateForward);
            subFabEntry.startAnimation(fabOpen);
            subFabGroup.startAnimation(fabOpen);
            fabAddEntry.setClickable(true);
            fabAddGroup.setClickable(true);
        }
        isOpen = !isOpen;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Toast.makeText(getApplicationContext(),"Not implemented yet",Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_passGenerator) {
            Toast.makeText(getApplicationContext(),"Not implemented yet",Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_updateMasterKey) {
            final Builder builder = new Builder(PasswordListActivity.this);
            builder.setTitle("Change master key");

            final LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            final EditText oldPassword = new EditText(PasswordListActivity.this);
            oldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            oldPassword.setHint("old password");
            final TextView passwordStrengthIndicator = new TextView(PasswordListActivity.this);
            passwordStrengthIndicator.setGravity(Gravity.CENTER);
            final EditText newPassword = new EditText(PasswordListActivity.this);
            newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            newPassword.setHint("new password");
            final EditText confirmNewPassword = new EditText(PasswordListActivity.this);
            confirmNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            confirmNewPassword.setHint("Confirm password");

            layout.addView(oldPassword);
            layout.addView(passwordStrengthIndicator);
            layout.addView(newPassword);
            layout.addView(confirmNewPassword);

            builder.setView(layout);

            TextWatcher watcher = SetupPassword.getPasswordTextWatcher(getApplicationContext(),newPassword,passwordStrengthIndicator,confirmNewPassword);
            newPassword.addTextChangedListener(watcher);

            builder.setPositiveButton("OK", null);

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            final AlertDialog alertDialog = builder.create();

            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(final DialogInterface dialog) {

                    Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            if(oldPassword.getText().toString().equals(password)) {
                                if (SetupPassword.validThePassword(newPassword, confirmNewPassword)) {
                                    password = newPassword.getText().toString();
                                    updateElements();
                                    dialog.dismiss();
                                }
                            }else{
                                oldPassword.setError("Wrong Password");
                            }
                        }
                    });
                }
            });

            alertDialog.show();

        } else if (id == R.id.nav_settings) {
            Toast.makeText(getApplicationContext(),"Not implemented yet",Toast.LENGTH_LONG).show();

//            Intent intent = new Intent(this, SettingsActivity.class);
//            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Implement this
    public void initAddGroupListener(){
        FloatingActionButton passwordFab = findViewById(R.id.fab_passwordList_addGroup);
        passwordFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animatedFab();

                Toast.makeText(getApplicationContext(),"Not implemented yet",Toast.LENGTH_LONG).show();
                /*AlertDialog.Builder builder = new AlertDialog.Builder(PasswordListActivity.this);
                builder.setTitle("Name of the Group");

                final EditText input = new EditText(PasswordListActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("group");
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addGroup(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();*/
            }
        });
    }


    public void initAddPasswordListener(){
        FloatingActionButton passwordFab = findViewById(R.id.fab_passwordList_addEntry);
        passwordFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animatedFab();
                runManagePasswordActivity();
            }
        });

    }

    private void runManagePasswordActivity(){
        Intent intent = new Intent(this, PasswordManage.class);
        startActivityForResult(intent,AppRequestVariables.passAddRequestCode.getValue());
    }

    public void runManagePasswordActivity(AppElements appElements,int position){
        Intent intent = new Intent(this, PasswordManage.class);
        intent.putExtra("appElements",appElements);
        intent.putExtra("position", position);
        startActivityForResult(intent,AppRequestVariables.passModificationRequestCode.getValue());
    }

    public void addGroup(String name){
        elements.add(new Group(name));
        try {
            CryptManager cryptManager = new CryptManager("AES");
            FileParser fileParser = new FileParser();
            String encrypt = cryptManager.encrypt(fileParser.objectToXml(elements), password);
            File file = new File(getApplicationContext().getFilesDir(), "securedMyPassword.smp");
            FileOutputStream fileOutputStream = openFileOutput(file.getName(), Context.MODE_PRIVATE);
            fileOutputStream.write(encrypt.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.error,Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        mAdapter.notifyDataSetChanged();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(data != null) {
            if (requestCode == AppRequestVariables.passAddRequestCode.getValue()) {
                if (resultCode == AppRequestVariables.RESULT_OK.getValue()) {
                    elements.add((AppElements) (data.getSerializableExtra("appElement")));
                    updateElements();
                }
            } else if (requestCode == AppRequestVariables.passModificationRequestCode.getValue()) {
                int position = data.getIntExtra("position", -1);
                if (position != -1) {
                    AppElements password = (AppElements) (data.getSerializableExtra("appElement"));
                    elements.set(position, password);
                    updateElements();
                }
            }
        }

    }




    public void updateElements(){
        try {
            CryptManager cryptManager = new CryptManager("AES");
            FileParser fileParser = new FileParser();
            String encrypt = cryptManager.encrypt(fileParser.objectToXml(elements), password);
            File file = new File(getApplicationContext().getFilesDir(), "securedMyPassword.smp");
            FileOutputStream fileOutputStream = openFileOutput(file.getName(), Context.MODE_PRIVATE);
            fileOutputStream.write(encrypt.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.error,Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        mAdapter.notifyDataSetChanged();
    }
}
