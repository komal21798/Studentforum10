package com.komal.studentforum10;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class ThreadActivity extends AppCompatActivity {

    private TextView threadName;
    private TextView threadSubscribers;
    private Button subscribeBtn;
    private Button unsubscribeBtn;

    private RecyclerView threadPageView;
    private List<ThreadPage> threadPageList;

    private ThreadPageRecyclerAdapter threadPageRecyclerAdapter;

    private FirebaseFirestore firebaseFirestore;

    private FirebaseAuth firebaseAuth;

    private DocumentSnapshot lastVisible;

    private String CategoryId;
    private String user_id;
    private int subscribersCount = 0;

    private Boolean isFirstPageFirstLoaded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);


        //Get categoryId from previous activity i.e. category fragment
        Bundle bundle = getIntent().getExtras();
        CategoryId = bundle.getString("CategoryId");
        Toast.makeText(this, "Category id: " + CategoryId, Toast.LENGTH_SHORT).show();


        threadName = findViewById(R.id.threadName);
        threadName.setText(CategoryId);
        threadSubscribers = findViewById(R.id.threadSubscribers);
        subscribeBtn = findViewById(R.id.subsrcibeBtn);
        unsubscribeBtn = findViewById(R.id.unsubscribeBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();

        threadPageView = (RecyclerView) findViewById(R.id.threadPageView);
        threadPageList = new ArrayList<>();

        threadPageRecyclerAdapter = new ThreadPageRecyclerAdapter(threadPageList);
        threadPageView.setLayoutManager(new LinearLayoutManager(this));
        threadPageView.setAdapter(threadPageRecyclerAdapter);

        firebaseFirestore.collection("Threads/" + CategoryId + "/Subscribers")
                .document(user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (!task.getResult().exists()) {

                            subscribeBtn.setVisibility(View.VISIBLE);

                        } else {

                            unsubscribeBtn.setVisibility(View.VISIBLE);

                        }

                    }
                });


        //for loading the posts
        if (firebaseAuth.getCurrentUser() != null) {

            threadPageView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if (reachedBottom) {

                        loadMorePost();

                    }
                }
            });

            //for getting the number of subscribers
            firebaseFirestore.collection("Threads/" + CategoryId + "/Subscribers")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                            if (!queryDocumentSnapshots.isEmpty()) {

                                subscribersCount = queryDocumentSnapshots.size();

                            }

                            //setting the subscribers count
                            threadSubscribers.setText(subscribersCount + " subscribers");

                        }
                    });


            Query firstQuery = firebaseFirestore.collection("Threads")
                    .document(CategoryId)
                    .collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(15);

            firstQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {

                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        if (isFirstPageFirstLoaded) {

                            // Get the last visible document
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                        }

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                ThreadPage threadPage = doc.getDocument().toObject(ThreadPage.class);
                                if (isFirstPageFirstLoaded) {

                                    threadPageList.add(threadPage);

                                } else {

                                    threadPageList.add(0, threadPage);

                                }

                                threadPageRecyclerAdapter.notifyDataSetChanged();

                            }

                        }

                        isFirstPageFirstLoaded = false;

                    }

                }
            });


            //jugaad - subscribing to a thread - not implemented yet
            subscribeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    subscribeBtn.setVisibility(View.INVISIBLE);
                    unsubscribeBtn.setVisibility(View.VISIBLE);

                    firebaseFirestore.collection("Threads/" + CategoryId + "/Subscribers")
                            .document(user_id)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (!task.getResult().exists()) {

                                        Map<String, Object> subscribersMap = new HashMap<>();
                                        subscribersMap.put("timestamp", FieldValue.serverTimestamp());

                                        firebaseFirestore.collection("Threads/" + CategoryId + "/Subscribers").document(user_id).set(subscribersMap);

                                    }
                                }
                            });

                }
            });

            //unsubscribing - not implemented yet
            unsubscribeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    unsubscribeBtn.setVisibility(View.INVISIBLE);
                    subscribeBtn.setVisibility(View.VISIBLE);

                    firebaseFirestore.collection("Threads/" + CategoryId + "/Subscribers")
                            .document(user_id)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (task.getResult().exists()) {

                                        firebaseFirestore.collection("Threads/" + CategoryId + "/Subscribers").document(user_id).delete();
                                        subscribersCount--;
                                        threadSubscribers.setText(subscribersCount);

                                    }
                                }
                            });
                }
            });
        }

    }

    public void loadMorePost() {

        Query nextQuery = firebaseFirestore.collection("Threads")
                .document(CategoryId)
                .collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(15);

        nextQuery.addSnapshotListener(ThreadActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()) {

                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            ThreadPage threadPage = doc.getDocument().toObject(ThreadPage.class);
                            threadPageList.add(threadPage);

                            threadPageRecyclerAdapter.notifyDataSetChanged();

                        }
                    }
                }
            }
        });
    }
}
