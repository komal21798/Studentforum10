package com.komal.studentforum10;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText login_email;
    private EditText login_password;
    private Button login_btn;
    private Button login_signup_btn;
    private ProgressBar login_progress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");


        FirebaseApp.initializeApp(this);

        login_email = (EditText) findViewById(R.id.login_email);
        login_password = (EditText) findViewById(R.id.login_password);
        login_btn = (Button) findViewById(R.id.login_btn);
        login_signup_btn = (Button) findViewById(R.id.login_signup_btn);
        login_progress = (ProgressBar) findViewById(R.id.login_progress);

        mAuth = FirebaseAuth.getInstance();

        login_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String loginEmail = login_email.getText().toString();
                String loginPassword = login_password.getText().toString();

                if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPassword)) {

                    //login_progress.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                Intent mainIntent = new Intent(LoginActivity.this,StudentForum.class);
                                startActivity(mainIntent);
                                finish();

                            } else {

                                Toast.makeText(LoginActivity.this, "Error!", Toast.LENGTH_LONG).show();

                            }

                            //login_progress.setVisibility(View.INVISIBLE);

                        }
                    });

                }

            }
        });

        login_signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(myIntent);

            }
        });

    }
}
