package com.ats.shivshambhuweighingapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ats.shivshambhuweighingapp.R;
import com.ats.shivshambhuweighingapp.fragment.AddPoklenReadingFragment;
import com.ats.shivshambhuweighingapp.fragment.AddWeighingFragment;
import com.ats.shivshambhuweighingapp.fragment.HomeFragment;
import com.ats.shivshambhuweighingapp.fragment.PoklenReadingListFragment;
import com.ats.shivshambhuweighingapp.fragment.UserFragment;
import com.ats.shivshambhuweighingapp.fragment.WeighingListFragment;
import com.ats.shivshambhuweighingapp.model.User;
import com.ats.shivshambhuweighingapp.util.CustomSharedPreference;
import com.ats.shivshambhuweighingapp.util.PermissionsUtil;
import com.google.gson.Gson;

import java.io.File;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    File folder = new File(Environment.getExternalStorageDirectory() + File.separator, "ShivShambhu");
    File f;

    User user;
    int userType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String userStr = CustomSharedPreference.getString(this, CustomSharedPreference.KEY_USER);
        Gson gson = new Gson();
        user = gson.fromJson(userStr, User.class);
        Log.e("HOME_ACTIVITY : ", "--------USER-------" + user);

        if (user == null) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }

        userType = CustomSharedPreference.getInt(HomeActivity.this, CustomSharedPreference.KEY_USER_TYPE);

        if (PermissionsUtil.checkAndRequestPermissions(this)) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        TextView tvName = header.findViewById(R.id.tvUser);
        TextView tvMobile = header.findViewById(R.id.tvMobile);

        if (user != null) {
            tvName.setText("" + user.getUsrName());
            tvMobile.setText("" + user.getUsrMob());
        }

        createFolder();

        int userType = CustomSharedPreference.getInt(HomeActivity.this, CustomSharedPreference.KEY_USER_TYPE);
        if (userType == 1) {
            if (savedInstanceState == null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, new PoklenReadingListFragment(), "Exit");
                ft.commit();
            }
        } else if (userType == 2) {
            if (savedInstanceState == null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, new WeighingListFragment(), "Exit");
                ft.commit();
            }
        } else {
            if (savedInstanceState == null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, new WeighingListFragment(), "Exit");
                ft.commit();
            }

        }


    }

    public void createFolder() {
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    @Override
    public void onBackPressed() {

        Fragment exit = getSupportFragmentManager().findFragmentByTag("Exit");
        Fragment homeFragment = getSupportFragmentManager().findFragmentByTag("HomeFragment");
        Fragment poklenReadingListFragment = getSupportFragmentManager().findFragmentByTag("PoklenReadingListFragment");
        Fragment weighingListFragment = getSupportFragmentManager().findFragmentByTag("WeighingListFragment");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else if (exit instanceof HomeFragment && exit.isVisible() ||
                exit instanceof PoklenReadingListFragment && exit.isVisible() ||
                exit instanceof WeighingListFragment && exit.isVisible()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, R.style.AlertDialogTheme);
            builder.setMessage("Exit Application ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        } else if (homeFragment instanceof AddPoklenReadingFragment && homeFragment.isVisible() ||
                homeFragment instanceof PoklenReadingListFragment && homeFragment.isVisible() ||
                homeFragment instanceof AddWeighingFragment && homeFragment.isVisible() ||
                homeFragment instanceof UserFragment && homeFragment.isVisible() ||
                homeFragment instanceof WeighingListFragment && homeFragment.isVisible()) {

            int userType = CustomSharedPreference.getInt(HomeActivity.this, CustomSharedPreference.KEY_USER_TYPE);
            Log.e("USER TYPE :", " ------------- " + userType);
            if (userType == 1) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, new PoklenReadingListFragment(), "Exit");
                ft.commit();

            } else if (userType == 2) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, new WeighingListFragment(), "Exit");
                ft.commit();

            } else {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, new WeighingListFragment(), "Exit");
                ft.commit();

            }


        } else if (poklenReadingListFragment instanceof AddPoklenReadingFragment && poklenReadingListFragment.isVisible()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new PoklenReadingListFragment(), "Exit");
            ft.commit();

        } else if (weighingListFragment instanceof AddWeighingFragment && weighingListFragment.isVisible()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new WeighingListFragment(), "Exit");
            ft.commit();

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_add_poklen_reading) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new AddPoklenReadingFragment(), "HomeFragment");
            ft.commit();

        } else if (id == R.id.nav_poklen_read_list) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new PoklenReadingListFragment(), "Exit");
            ft.commit();

        } else if (id == R.id.nav_add_weighing) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new AddWeighingFragment(), "HomeFragment");
            ft.commit();

        }else if (id == R.id.nav_weighing_list) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new WeighingListFragment(), "Exit");
            ft.commit();

        } else if (id == R.id.nav_user) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new UserFragment(), "HomeFragment");
            ft.commit();

        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, R.style.AlertDialogTheme);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to logout?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    CustomSharedPreference.deletePreference(HomeActivity.this);
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
