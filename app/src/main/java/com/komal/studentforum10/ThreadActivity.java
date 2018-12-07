package com.komal.studentforum10;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
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

    public FirebaseAuth firebaseAuth;

    private DocumentSnapshot lastVisible;

    private Boolean isFirstPageFirstLoaded = true;

    private String CategoryId;
    private String user_id;
    private int subscribersCount;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);


        //Get categoryId from previous activity i.e. category fragment
        Bundle bundle = getIntent().getExtras();
        CategoryId = bundle.getString("CategoryId");

        threadName = findViewById(R.id.threadName);
        threadName.setText(CategoryId);
        threadSubscribers = findViewById(R.id.threadSubscribers);
        subscribeBtn = findViewById(R.id.subscribeBtn);
        unsubscribeBtn = findViewById(R.id.unsubscribeBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();

        threadPageView = (RecyclerView) findViewById(R.id.threadPageView);
        threadPageList = new ArrayList<>();

        threadPageRecyclerAdapter = new ThreadPageRecyclerAdapter(threadPageList);
        threadPageView.setLayoutManager(new LinearLayoutManager(this));
        threadPageView.setAdapter(threadPageRecyclerAdapter);
        threadName = findViewById(R.id.threadName);

        //anonymous login
        if (firebaseAuth.getCurrentUser().isAnonymous()) {


        }



        {


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

            //showing the subscribe or unsubscribe button
            firebaseFirestore.collection("Threads/" + CategoryId + "/Subscribers").document(user_id)
                    .addSnapshotListener(ThreadActivity.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                            if (documentSnapshot.exists()) {

                                unsubscribeBtn.setVisibility(View.VISIBLE);

                            } else {

                                subscribeBtn.setVisibility(View.VISIBLE);

                            }
                        }
                    });

            //subscribing
            subscribeBtn.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {


                    subscribeBtn.setVisibility(View.INVISIBLE);

                    Map<String, Object> subscribeMap = new HashMap<>();
                    subscribeMap.put("timestamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Threads/" + CategoryId + "/Subscribers")
                            .document(user_id).set(subscribeMap);

                    firebaseFirestore.collection("Users/" + user_id + "/Subs")
                            .document(CategoryId).set(subscribeMap);

                }
            });


            //unsubscribing
            unsubscribeBtn.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {

                    unsubscribeBtn.setVisibility(View.INVISIBLE);

                    firebaseFirestore.collection("Threads/" + CategoryId + "/Subscribers")
                            .document(user_id).delete();

                    firebaseFirestore.collection("Users/" + user_id + "/Subs")
                            .document(CategoryId).delete();

                }
            });

        }

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
                .addSnapshotListener(ThreadActivity.this, new EventListener<QuerySnapshot>() {

                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            subscribersCount = queryDocumentSnapshots.size();

                        } else {

                            subscribersCount = 0;

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

        firstQuery.addSnapshotListener(ThreadActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                // Get the last visible document
                if (!queryDocumentSnapshots.isEmpty()) {

                    if (isFirstPageFirstLoaded) {

                        // Get the last visible document
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                    }

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String threadPageId = doc.getDocument().getId();
                            ThreadPage threadPage = doc.getDocument()
                                    .toObject(ThreadPage.class).withId(threadPageId);
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
    }

}

    public void loadMorePost () {

        Query nextQuery = firebaseFirestore.collection("Threads/" + CategoryId + "/Subscribers")
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

    public void showPopup (View v){
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popupactions, popup.getMenu());
        popup.show();
    }



}
