package com.komal.studentforum10;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    public void onSignUpClick(View view){

        Intent myIntent = new Intent(LoginActivity.this, SignUpActivity.class);
        LoginActivity.this.startActivity(myIntent);

        finish();
    }

    public void onLoginClick(View view){
        Intent myIntent = new Intent(LoginActivity.this, StudentForum.class);
        LoginActivity.this.startActivity(myIntent);

        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
