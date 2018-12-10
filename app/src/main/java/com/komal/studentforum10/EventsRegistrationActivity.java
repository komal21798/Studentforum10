package com.komal.studentforum10;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EventsRegistrationActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;

    private TextView registerFname;
    private TextView registerLname;
    private TextView registerContact;
    private TextView registerStream;
    private TextView registerContribute;
    private Button registerSubmitBtn;
    private ProgressBar registerProgressBar;


    private String eventRegisterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_registration);

        Bundle bundle = getIntent().getExtras();
        eventRegisterId = bundle.getString("threadPageId");

        registerFname = findViewById(R.id.registerFnameText);
        registerLname = findViewById(R.id.registerLnameText);
        registerContact = findViewById(R.id.registerContactText);
        registerStream = findViewById(R.id.registerStreamText);
        registerContribute = findViewById(R.id.registerContributeText);
        registerSubmitBtn = findViewById(R.id.registerSubmitBtn);
        registerProgressBar = findViewById(R.id.registerProgressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        user_id = firebaseAuth.getCurrentUser().getUid();

        registerSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (firebaseAuth.getCurrentUser().isAnonymous()) {

                    Toast.makeText(EventsRegistrationActivity.this, "Please Login to access this functionality", Toast.LENGTH_SHORT).show();

                } else {

                    String register_fname = registerFname.getText().toString();
                    String register_lname = registerLname.getText().toString();
                    String register_contact = registerContact.getText().toString();
                    String register_stream = registerStream.getText().toString();
                    String register_contribute = registerContribute.getText().toString();

                    if (!TextUtils.isEmpty(register_fname) && !TextUtils.isEmpty(register_lname) && !TextUtils.isEmpty(register_contact) && !TextUtils.isEmpty(register_stream) && !TextUtils.isEmpty(register_contribute)) {

                        registerProgressBar.setVisibility(View.VISIBLE);

                        Map<String, Object> eventRegisterMap = new HashMap<>();
                        eventRegisterMap.put("first_name", register_fname);
                        eventRegisterMap.put("last_name", register_lname);
                        eventRegisterMap.put("contact", register_contact);
                        eventRegisterMap.put("stream", register_stream);
                        eventRegisterMap.put("contribute", register_contribute);
                        eventRegisterMap.put("event", eventRegisterId);
                        eventRegisterMap.put("timestamp", FieldValue.serverTimestamp());

                        firebaseFirestore.collection("Register").document(eventRegisterId).collection("Registrations").document(user_id).set(eventRegisterMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText(EventsRegistrationActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                    goToHome();
                                } else {

                                    String error = task.getException().getMessage();
                                    Toast.makeText(EventsRegistrationActivity.this, "Error:" + error, Toast.LENGTH_SHORT).show();

                                }
                            }
                        });


                    } else {

                        Toast.makeText(EventsRegistrationActivity.this, "Please fill all the details.", Toast.LENGTH_SHORT).show();

                    }

                }

            }
        });

    }

    public void goToHome() {

        Intent homeIntent = new Intent(EventsRegistrationActivity.this, StudentForum.class);
        startActivity(homeIntent);
        //finish();

    }
}
