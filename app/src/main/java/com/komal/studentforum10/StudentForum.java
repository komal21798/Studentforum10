package com.komal.studentforum10;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Intent myIntent;

                int id = item.getItemId();

                if(id == R.id.my_profile){

                    setupAccount();

                }

                else if(id == R.id.settings){
                    Toast.makeText(StudentForum.this, "Settings", Toast.LENGTH_SHORT).show();
                }

                else if(id == R.id.logout){

                    logout();

                }

                return true;
            }
        });
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

        mAuth.signOut();
        goToLogin();
        finish();

    }

    public void setupAccount() {

        Intent myIntent = new Intent(StudentForum.this,AccountSetupActivity.class);
        startActivity(myIntent);

    }
}
