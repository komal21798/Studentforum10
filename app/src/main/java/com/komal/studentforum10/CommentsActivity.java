package com.komal.studentforum10;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class CommentsActivity extends AppCompatActivity {

    private TextView commentName;
    private ImageView addCommentButton;
    private EditText commentsEditText;

    private RecyclerView commentsPageView;
    private List<CommentsFeed> commentsPageList;

    //private CommentsFeedRecyclerAdapter commentsPageRecyclerAdapter;

    private FirebaseFirestore firebaseFirestore;

    private FirebaseAuth firebaseAuth;

    private DocumentSnapshot lastVisible;

    private Boolean isFirstPageFirstLoaded = true;

    public static String threadPageId;
    public static String CategoryId;
    private String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Bundle bundle = getIntent().getExtras();
        threadPageId = bundle.getString("threadPageId");
        CategoryId = bundle.getString("postThreadId");

        commentName = findViewById(R.id.commentName);
        addCommentButton = findViewById(R.id.addCommentButton);
        commentsEditText = findViewById(R.id.commentsEditText);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();

        commentsPageView = (RecyclerView) findViewById(R.id.commentsPageView);
        commentsPageList = new ArrayList<>();

        //commentsPageRecyclerAdapter = new CommentsFeedRecyclerAdapter(commentsPageList);
        //commentsPageView.setLayoutManager(new LinearLayoutManager(this));
        //commentsPageView.setAdapter(commentsPageRecyclerAdapter);

        //for adding new comment
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String comments_editext = commentsEditText.getText().toString();

                if(!TextUtils.isEmpty(comments_editext))
                {
                    Map<String, Object> commentsMap = new HashMap<>();
                    commentsMap.put("user_id", user_id);
                    commentsMap.put("comment", comments_editext);
                    commentsMap.put("timestamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Posts/" + threadPageId + "/Comments").document().set(commentsMap);
                    firebaseFirestore.collection("Threads/" + CategoryId + "/Posts/" + threadPageId  + "/Comments").document().set(commentsMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        Toast.makeText(CommentsActivity.this, "Comment posted successfully!", Toast.LENGTH_SHORT).show();

                                    }
                                    else{

                                        String error = task.getException().getMessage();
                                        Toast.makeText(CommentsActivity.this, "Error:" + error, Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                } else {

                    Toast.makeText(CommentsActivity.this, "Please fill all the details.", Toast.LENGTH_SHORT).show();

                }


            }
        });

        /*//for loading the posts
        if (firebaseAuth.getCurrentUser() != null) {

            commentsPageView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

            firstQuery.addSnapshotListener(CommentsActivity.this, new EventListener<QuerySnapshot>() {
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

                                String commentsFeedId = doc.getDocument().getId();
                                CommentsFeed commentsFeed = doc.getDocument()
                                        .toObject(CommentsFeed.class).withId(commentsFeedId);
                                if (isFirstPageFirstLoaded) {

                                    commentsPageList.add(commentsFeed);

                                } else {

                                    commentsPageList.add(0, commentsFeed);

                                }

                                //commentsPageRecyclerAdapter.notifyDataSetChanged();
                            }
                        }

                        isFirstPageFirstLoaded = false;
                    }
                }
            });
        }*/
    }

    public void loadMorePost() {

        Query nextQuery = firebaseFirestore.collection("Threads/" + CategoryId + "/Subscribers")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(15);

        nextQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()) {

                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String commentsFeedId = doc.getDocument().getId();
                            CommentsFeed commentsFeed = doc.getDocument().toObject(CommentsFeed.class).withId(commentsFeedId);
                            commentsPageList.add(commentsFeed);
                            //commentsPageRecyclerAdapter.notifyDataSetChanged();

                        }

                    }
                }
            }
        });
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popupactions, popup.getMenu());
        popup.show();
    }


}
