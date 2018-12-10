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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private EditText signup_email;
    private EditText signup_password;
    private EditText signup_confirm_password;
    private Button signup_btn;
    private ProgressBar signup_progress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");

        mAuth = FirebaseAuth.getInstance();

        signup_email = (EditText) findViewById(R.id.signup_email);
        signup_password = (EditText) findViewById(R.id.signup_password);
        signup_confirm_password = (EditText) findViewById(R.id.signup_confirm_password);
        signup_btn = (Button) findViewById(R.id.signup_btn);
        signup_progress = (ProgressBar) findViewById(R.id.signup_progress);

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = signup_email.getText().toString();
                String password = signup_password.getText().toString();
                String confirm_password = signup_confirm_password.getText().toString();

                if(!email.endsWith("@nuv.ac.in")) {

                    Toast.makeText(SignUpActivity.this, "Users can only register with nuv.ac.in emails", Toast.LENGTH_SHORT).show();

                } else {

                    if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirm_password)){

                        signup_progress.setVisibility(View.VISIBLE);

                        if(password.equals(confirm_password)){

                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){

                                        sendEmailVerification();
                                        Intent myIntent = new Intent(SignUpActivity.this,LoginActivity.class);
                                        startActivity(myIntent);
                                        finish();

                                    } else {

                                        signup_progress.setVisibility(View.INVISIBLE);
                                        String error = task.getException().getMessage();
                                        Toast.makeText(SignUpActivity.this, error, Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });

                        } else {

                            Toast.makeText(SignUpActivity.this, "Confirm password and password don't match!", Toast.LENGTH_SHORT).show();
                            signup_progress.setVisibility(View.INVISIBLE);

                        }

                    } else {

                        Toast.makeText(SignUpActivity.this, "Please enter all the details!", Toast.LENGTH_SHORT).show();
                        signup_progress.setVisibility(View.INVISIBLE);

                    }

                }

            }
        });
    }

    public void sendToMain(){

        Intent myIntent = new Intent(SignUpActivity.this,MainActivity.class);
        startActivity(myIntent);
        finish();

    }

    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){

            sendToMain();
            finish();

        }
    }

    private void sendEmailVerification() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SignUpActivity.this,"Check your Email for Verification",Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            });
        }
    }
}
