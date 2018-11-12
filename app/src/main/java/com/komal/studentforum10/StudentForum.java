package com.komal.studentforum10;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StudentForum extends AppCompatActivity {

    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;

    private FirebaseAuth mAuth;

    private FirebaseUser firebaseUser;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    HomeFragment homeFragment = new HomeFragment();
                    FragmentManager manager1 = getFragmentManager();
                    manager1.beginTransaction().replace(R.id.contentLayout, homeFragment, homeFragment.getTag()).commit();
                    return true;

                case R.id.navigation_explore:
                    ExploreFragment exploreFragment = new ExploreFragment();
                    FragmentManager manager2 = getFragmentManager();
                    manager2.beginTransaction().replace(R.id.contentLayout, exploreFragment, exploreFragment.getTag()).commit();
                    return true;

                case R.id.navigation_addNew:
                    Intent myIntent = new Intent(StudentForum.this, AddNewActivity.class);
                    StudentForum.this.startActivity(myIntent);
                    return true;

                case R.id.navigation_categories:
                    CategoriesFragment categoriesFragment = new CategoriesFragment();
                    FragmentManager manager4 = getFragmentManager();
                    manager4.beginTransaction().replace(R.id.contentLayout, categoriesFragment, categoriesFragment.getTag()).commit();
                    return true;

                case R.id.navigation_notifications:
                    NotifsFragment notifsFragment = new NotifsFragment();
                    FragmentManager manager5 = getFragmentManager();
                    manager5.beginTransaction().replace(R.id.contentLayout, notifsFragment, notifsFragment.getTag()).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentforum);

        mAuth = FirebaseAuth.getInstance();

        firebaseUser = mAuth.getCurrentUser();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView);
        BottomNavViewHelper.disableShiftMode(bottomNavigationView);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        HomeFragment homeFragment = new HomeFragment();
        FragmentManager manager1 = getFragmentManager();
        manager1.beginTransaction().replace(R.id.contentLayout, homeFragment, homeFragment.getTag()).commit();

        dl = (DrawerLayout) findViewById(R.id.dl);
        abdt = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(abdt);
        abdt.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);

        //to check for internet connection
        if(!isConnected(StudentForum.this)) {

            buildDialog(StudentForum.this).show();

        }

        //to check if email is verified
        if(!firebaseUser.isEmailVerified()) {

            Toast.makeText(this, "Please verify your email to login.", Toast.LENGTH_LONG).show();
            goToLogin();

        }

        else {

            nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Intent myIntent;

                    int id = item.getItemId();

                    if(id == R.id.my_profile){

                        setupAccount();

                    }

                    else if(id == R.id.settings){
                        settings();
                    }

                    else if(id == R.id.logout){

                        logout();

                    }

                    return true;
                }
            });

        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public void goToLogin(){

        Intent loginIntent = new Intent(StudentForum.this,LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    public void logout(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(StudentForum.this);
        builder.setMessage(" Are you sure you want to logout ?");
        builder.setCancelable(true);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth.signOut();
                goToLogin();
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    public void setupAccount() {

        Intent myIntent = new Intent(StudentForum.this,AccountSetupActivity.class);
        startActivity(myIntent);

    }

    public void settings() {
        Intent myIntent = new Intent(StudentForum.this, SettingsActivity.class);
        startActivity(myIntent);
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;
    }

}