package com.komal.studentforum10;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SendNotifsActivity extends AppCompatActivity {

    private EditText notifsMessage;
    private Button sendNotifBtn;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    private String user_id;

    private String toUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notifs);

        Bundle bundle = getIntent().getExtras();
        toUserId = bundle.getString("toUserId");

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        notifsMessage = findViewById(R.id.notifsMessage);
        sendNotifBtn = findViewById(R.id.sendNotifBtn);

        sendNotifBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(user_id.equals("M4S0hiNILmTuj1nEKp3NCGvfiiF2")) {

                    String notifMessageText = notifsMessage.getText().toString();

                    if(!TextUtils.isEmpty(notifMessageText)){

                        Map<String,Object> notifMap = new HashMap<>();
                        notifMap.put("notif_message", notifMessageText);
                        notifMap.put("from_user_id", user_id);

                        firebaseFirestore.collection("Users").document(toUserId)
                                .collection("Notifications").add(notifMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                if(task.isSuccessful()){
                                    Toast.makeText(SendNotifsActivity.this, "Message sent!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SendNotifsActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        Toast.makeText(SendNotifsActivity.this, "Please enter some message!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(SendNotifsActivity.this, "You can't send notif", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
