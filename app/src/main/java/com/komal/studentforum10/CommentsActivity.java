package com.komal.studentforum10;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class CommentsActivity extends AppCompatActivity {

    private TextView commentPostName;
    private TextView commentPostDesc;

    private String threadPageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Bundle bundle = getIntent().getExtras();
        threadPageId = bundle.getString("threadPageId");


        //commentPostName.setText(threadPageId);
    }
}
