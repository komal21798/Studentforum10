package com.komal.studentforum10;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class NotifsInboxActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String user_id;

    private RecyclerView notifsInboxView;
    private List<NotifsInboxPage> notifsInboxPageList;

    private NotifsInboxPageRecyclerAdapter notifsInboxPageRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifs_inbox);
        setTitle("Notifications");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();

        notifsInboxView = (RecyclerView) findViewById(R.id.notifsInboxView);
        notifsInboxPageList = new ArrayList<>();

        notifsInboxPageRecyclerAdapter = new NotifsInboxPageRecyclerAdapter(notifsInboxPageList);
        notifsInboxView.setLayoutManager(new LinearLayoutManager(this));
        notifsInboxView.setAdapter(notifsInboxPageRecyclerAdapter);

        Query query = firebaseFirestore.collection("Users")
                .document(user_id)
                .collection("Notifications")
                .orderBy("message", Query.Direction.ASCENDING);

        query.addSnapshotListener(NotifsInboxActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String notifsInboxPageId = doc.getDocument().getId();
                        NotifsInboxPage notifsInboxPage = doc.getDocument()
                                .toObject(NotifsInboxPage.class).withId(notifsInboxPageId);

                        notifsInboxPageList.add(notifsInboxPage);

                        notifsInboxPageRecyclerAdapter.notifyDataSetChanged();
                    }
                }

            }
        });
    }
}
