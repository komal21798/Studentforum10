package com.komal.studentforum10;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends AppCompatActivity {


    private EditText ResetEmailInput;
    private Button ResetPasswordButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        ResetEmailInput = (EditText) findViewById(R.id.ResetEmailInput);
        ResetPasswordButton = (Button) findViewById(R.id.ResetPasswordButton);
        firebaseAuth = FirebaseAuth.getInstance();

        ResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail= ResetEmailInput.getText().toString().trim();

                if (useremail.equals("")){
                    Toast.makeText(PasswordActivity.this,"Please enter your registered email", Toast.LENGTH_SHORT).show();



                } else{
                    firebaseAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PasswordActivity.this, "Password Reset Email Link Sent", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(PasswordActivity.this, MainActivity.class));
                            }
                            else {
                                Toast.makeText(PasswordActivity.this, "Error in sending password reset link", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }

            }
        });
    }
}
