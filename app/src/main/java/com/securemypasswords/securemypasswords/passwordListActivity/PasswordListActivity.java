package com.securemypasswords.securemypasswords.passwordListActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.securemypasswords.securemypasswords.PasswordManage;
import com.securemypasswords.securemypasswords.R;
import com.securemypasswords.securemypasswords.passwordsStorage.AppElements;
import com.securemypasswords.securemypasswords.passwordsStorage.Group;
import com.securemypasswords.securemypasswords.passwordsStorage.Password;

import java.util.ArrayList;
import java.util.List;

public class PasswordListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String password;
    private ArrayList<Password> passwords;
    private ArrayList<Group> groups;

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
        setContentView(R.layout.activity_password_list);
        Intent intent = getIntent();
        password = intent.getStringExtra("PASSWORD");
        Object[] elements = (Object[]) intent.getExtras().get("ELEMENTS");
        passwords = (ArrayList<Password>) elements[0];
        groups = (ArrayList<Group>) elements[1];

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

        initRecyclerView(elements);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initAddPasswordListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView(Object elements) {
        mRecyclerView = findViewById(R.id.rv_passlist);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
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

        List<AppElements> elements = new ArrayList<>();
        elements.add(socialMedia);
        elements.add(useless);
        elements.add(computerPassword);
        elements.add(cb_PIN);
        elements.add(dropBox_password);*/

        //List<AppElements> elements = new ArrayList<>();

        //mAdapter = new PasswordListAdaptater(elements);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.password_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {

        } else if (id == R.id.nav_passGenerator) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_cloapp) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        startActivity(intent);
    }
}
