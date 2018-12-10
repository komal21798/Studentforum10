package com.komal.studentforum10;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ClipData;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class StudentForum extends AppCompatActivity {

    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;

    private FirebaseAuth mAuth;

    private FirebaseUser firebaseUser;

    private FirebaseFirestore firebaseFirestore;

    private String user_id;

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
                    setTitle("Home");
                    return true;

                case R.id.navigation_explore:
                    ExploreFragment exploreFragment = new ExploreFragment();
                    FragmentManager manager2 = getFragmentManager();
                    manager2.beginTransaction().replace(R.id.contentLayout, exploreFragment, exploreFragment.getTag()).commit();
                    setTitle("Explore");
                    return true;

                case R.id.navigation_addNew:
                    Intent myIntent = new Intent(StudentForum.this, AddNewActivity.class);
                    StudentForum.this.startActivity(myIntent);
                    return true;

                case R.id.navigation_categories:
                    CategoriesFragment categoriesFragment = new CategoriesFragment();
                    FragmentManager manager4 = getFragmentManager();
                    manager4.beginTransaction().replace(R.id.contentLayout, categoriesFragment, categoriesFragment.getTag()).commit();
                    setTitle("Categories");
                    return true;

                case R.id.navigation_notifications:

                    Intent notifsIntent = new Intent(StudentForum.this, NotifsActivity.class);
                    StudentForum.this.startActivity(notifsIntent);
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

        firebaseFirestore = FirebaseFirestore.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        final NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);

        //inbox for admin
        if(!user_id.equals("M4S0hiNILmTuj1nEKp3NCGvfiiF2"))
        {
            Menu nav_menu = nav_view.getMenu();
            nav_menu.findItem(R.id.inbox).setVisible(false);
        }

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

        //to check for internet connection
        if (!isConnected(StudentForum.this)) {

            buildDialog(StudentForum.this).show();

        }

        // guestlogin
        if (mAuth.getCurrentUser().isAnonymous()) {

            nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Intent myIntent;

                    int id = item.getItemId();

                    if (id == R.id.logout) {

                        logout();

                    }

                    return true;
                }
            });
        }


        //to check if email is verified
        if (!firebaseUser.isAnonymous()) {

            if (!firebaseUser.isEmailVerified()) {

                Toast.makeText(this, "Please verify your email to login.", Toast.LENGTH_LONG).show();
                goToLogin();

            }

            nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Intent myIntent;

                    int id = item.getItemId();

                    if (id == R.id.my_profile) {

                        setupAccount();

                    } else if (id == R.id.settings) {

                        settings();

                    } else if (id == R.id.logout) {

                        logout();

                    } else if(id == R.id.inbox) {

                        inbox();

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

    public void goToLogin() {

        Intent loginIntent = new Intent(StudentForum.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    public void inbox() {

        Intent inboxIntent = new Intent(StudentForum.this, InboxActivity.class);
        startActivity(inboxIntent);

    }

    public void logout() {

        if (firebaseUser.isAnonymous()) {

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
                    firebaseUser.delete();
                    goToLogin();
                    finish();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        } else {

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

                    Map<String,Object> tokenMap = new HashMap<>();
                    tokenMap.put("token_id", "");

                    firebaseFirestore.collection("Users").document(user_id)
                            .update(tokenMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                mAuth.signOut();
                                goToLogin();
                                finish();

                            } else {

                                String error = task.getException().getMessage();
                                Toast.makeText(StudentForum.this, "Error: " + error, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });


                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }

    }

    public void setupAccount() {

        Intent myIntent = new Intent(StudentForum.this, AccountSetupActivity.class);
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

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        } else
            return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No internet connection");
        builder.setMessage("Please check your internet connection. Click OK to exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;
    }


}