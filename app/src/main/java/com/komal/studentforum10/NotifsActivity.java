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

public class NotifsActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String user_id;

    private RecyclerView notifsPageView;
    private List<NotifsPage> notifsPageList;

    private NotifsPageRecyclerAdpater notifsPageRecyclerAdpater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifs);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();

        notifsPageView = (RecyclerView) findViewById(R.id.notifsView);
        notifsPageList = new ArrayList<>();

        notifsPageRecyclerAdpater = new NotifsPageRecyclerAdpater(notifsPageList);
        notifsPageView.setLayoutManager(new LinearLayoutManager(this));
        notifsPageView.setAdapter(notifsPageRecyclerAdpater);


        Query query = firebaseFirestore.collection("Users")
                .orderBy("username", Query.Direction.ASCENDING)
                .limit(15);

        query.addSnapshotListener(NotifsActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String notifsPageId = doc.getDocument().getId();
                        NotifsPage notifsPage = doc.getDocument()
                                .toObject(NotifsPage.class).withId(notifsPageId);

                        notifsPageList.add(notifsPage);

                        notifsPageRecyclerAdpater.notifyDataSetChanged();
                    }
                }

            }
        });
    }
}
