package com.komal.studentforum10;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class ThreadActivity extends AppCompatActivity {

    private TextView threadName;

    private RecyclerView threadPageView;
    private List<ThreadPage> threadPageList;

    private ThreadPageRecyclerAdapter threadPageRecyclerAdapter;

    private FirebaseFirestore firebaseFirestore;

    private FirebaseAuth firebaseAuth;

    private DocumentSnapshot lastVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        threadPageView = (RecyclerView) findViewById(R.id.threadPageView);
        threadPageList = new ArrayList<>();

        threadPageRecyclerAdapter = new ThreadPageRecyclerAdapter(threadPageList);
        threadPageView.setLayoutManager(new LinearLayoutManager(this));
        threadPageView.setAdapter(threadPageRecyclerAdapter);

        threadName = findViewById(R.id.threadNameCategories);

        if (firebaseAuth.getCurrentUser() != null) {

            String thread_name = threadName.getText().toString();

            threadPageView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if(reachedBottom){
                        loadMorePost();
                    }
                }
            });

            Query firstQuery = firebaseFirestore.collection("Posts")
                    .orderBy("timestamp",Query.Direction.DESCENDING)
                    .limit(15);

            firstQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {

                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    // Get the last visible document
                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() -1);

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String threadPageId = doc.getDocument().getId();
                            ThreadPage threadPage = doc.getDocument().toObject(ThreadPage.class).withId(threadPageId);
                            threadPageList.add(threadPage);

                            threadPageRecyclerAdapter.notifyDataSetChanged();

                        }

                    }

                }
            });
        }

    }

    public void loadMorePost(){

        Query nextQuery = firebaseFirestore.collection("Posts")
                .orderBy("timestamp",Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(15);

        nextQuery.addSnapshotListener(ThreadActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(!queryDocumentSnapshots.isEmpty()) {

                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String threadPageId = doc.getDocument().getId();
                            ThreadPage threadPage = doc.getDocument().toObject(ThreadPage.class).withId(threadPageId);
                            threadPageList.add(threadPage);

                            threadPageRecyclerAdapter.notifyDataSetChanged();

                        }

                    }
                }
            }
        });
    }
}
