package com.securemypasswords.secureMyPasswords.passwordListActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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

import com.securemypasswords.secureMyPasswords.AppRequestVariables;
import com.securemypasswords.secureMyPasswords.PasswordManage;
import com.securemypasswords.secureMyPasswords.R;
import com.securemypasswords.secureMyPasswords.account.ManageStorage;
import com.securemypasswords.secureMyPasswords.passwordsStorage.AppElements;
import com.securemypasswords.secureMyPasswords.passwordsStorage.FileParser;
import com.securemypasswords.secureMyPasswords.secure.CryptManager;
import com.securemypasswords.secureMyPasswords.secure.SetupPassword;
import com.securemypasswords.secureMyPasswords.settings.SettingsActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class PasswordListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String password;
    private ArrayList<AppElements> elements;

    private DrawerLayout activityMainLayout;

    private RecyclerView.Adapter mAdapter;


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

        activityMainLayout = findViewById(R.id.dl_passwordList_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, activityMainLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        activityMainLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initAddPasswordListener();
        initAddGroupListener();
    }

    private void initRecyclerView() {
        RecyclerView mRecyclerView = findViewById(R.id.rv_passwordList);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PasswordListAdapter(elements, this);
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
        DrawerLayout drawer = findViewById(R.id.dl_passwordList_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_onlineStorage) {
            Intent intent = new Intent(this, ManageStorage.class);
            startActivity(intent);
            //Toast.makeText(getApplicationContext(),"Not implemented yet",Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_passGenerator) {
            //TODO add password generator
            Toast.makeText(getApplicationContext(),"Not implemented yet",Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_updateMasterKey) {
            final Builder builder = new Builder(PasswordListActivity.this);
            builder.setTitle("Change master key");

            View view = LayoutInflater.from(this).inflate(R.layout.activity_change_password, activityMainLayout, false);

            builder.setView(view);

            final EditText oldPassword = view.findViewById(R.id.et_oldPassword_updateMasterKey);
            final TextView passwordStrengthIndicator = view.findViewById(R.id.tv_passwordStrength_updateMasterKey);
            final EditText newPassword = view.findViewById(R.id.et_newPassword_updateMasterKey);
            final EditText confirmNewPassword = view.findViewById(R.id.et_confirmNewPassword_updateMasterKey);

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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.dl_passwordList_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Todo Implement this (group with multiple levels)
    private void initAddGroupListener() {
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

    /*public void addGroup(String name){
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
    }*/


    private void initAddPasswordListener() {
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
