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

public class InboxActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String user_id;

    private RecyclerView inboxPageView;
    private List<InboxPage> inboxPageList;

    private InboxPageRecyclerAdapter inboxPageRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        inboxPageView = findViewById(R.id.inboxPageView);
        inboxPageList = new ArrayList<>();

        inboxPageRecyclerAdapter = new InboxPageRecyclerAdapter(inboxPageList);
        inboxPageView.setLayoutManager(new LinearLayoutManager(this));
        inboxPageView.setAdapter(inboxPageRecyclerAdapter);

        Query query = firebaseFirestore.collection("Users").document("M4S0hiNILmTuj1nEKp3NCGvfiiF2")
                .collection("Inbox");

        query.addSnapshotListener(InboxActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String inboxPageId = doc.getDocument().getId();
                        InboxPage inboxPage = doc.getDocument()
                                .toObject(InboxPage.class).withId(inboxPageId);

                        inboxPageList.add(inboxPage);

                        inboxPageRecyclerAdapter.notifyDataSetChanged();
                    }
                }

            }
        });
    }
}
