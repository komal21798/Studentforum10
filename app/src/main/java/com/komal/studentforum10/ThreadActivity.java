package com.komal.studentforum10;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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

    private TextView threadDesc;

    private RecyclerView threadPageView;
    private List<ThreadPage> threadPageList;

    private ThreadPageRecyclerAdapter threadPageRecyclerAdapter;

    private FirebaseFirestore firebaseFirestore;

    public FirebaseAuth firebaseAuth;

    private DocumentSnapshot lastVisible;

    private Boolean isFirstPageFirstLoaded = true;

    public static String CategoryId;
    public static String CategoryDesc;

    private String user_id;
    private int subscribersCount;

    private ActionBar actionBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        actionBar = getSupportActionBar();
        actionBar.hide();

        //Get categoryId from previous activity i.e. category fragment
        Bundle bundle = getIntent().getExtras();
        CategoryId = bundle.getString("CategoryId");
        CategoryDesc = bundle.getString("CategoryDesc");

        threadName = findViewById(R.id.threadName);
        threadName.setText(CategoryId);

        threadDesc = findViewById(R.id.threadDesc);

        threadDesc.setText(CategoryDesc);

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
