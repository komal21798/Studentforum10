package com.komal.studentforum10;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String current_user_id;

    public void onBttnClick(View view){


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {

            goToLogin();
            finish();

        } else {

            goToFeed();

            current_user_id = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()) {

                        if(!task.getResult().exists()) {

                            Intent setupIntent = new Intent(MainActivity.this, AccountSetupActivity.class);
                            startActivity(setupIntent);
                            finish();

                        }
                        finish();

                    } else {

                        String error = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Main activity error:" + error, Toast.LENGTH_SHORT).show();

                    }

                }

            });

        }

    }

    public void goToLogin(){

        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    public void goToFeed(){

        Intent myIntent = new Intent(MainActivity.this, StudentForum.class);
        startActivity(myIntent);
        finish();

    }
}
