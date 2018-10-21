package com.komal.studentforum10;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SignUpActivity extends AppCompatActivity {

    public void onSubmitClick(View view){
        Intent myIntent = new Intent(SignUpActivity.this, LoginActivity.class);
        SignUpActivity.this.startActivity(myIntent);

        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }
}
