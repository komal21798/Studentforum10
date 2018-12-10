package com.komal.studentforum10;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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

    private ImageView addCommentButton;
    private EditText commentsEditText;

    private TextView commentPostName;
    private TextView commentPostDesc;

    private RecyclerView commentsPageView;
    private List<CommentsFeed> commentsPageList;

    private CommentsFeedRecyclerAdapter commentsFeedRecyclerAdapter;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public static String postId;
    public static String CategoryId;
    public static String postDesc;
    private String user_id;

    private DocumentSnapshot lastVisible;

    private Boolean isFirstPageFirstLoaded = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Bundle bundle = getIntent().getExtras();
        postId = bundle.getString("threadPageId");
        CategoryId = bundle.getString("postThreadId");
        postDesc = bundle.getString("postDesc");

        commentPostName = findViewById(R.id.commentPostName);
        commentPostName.setText(postId);
        commentPostDesc = findViewById(R.id.commentPostDesc);
        commentPostDesc.setText(postDesc);

        addCommentButton = findViewById(R.id.addCommentButton);
        commentsEditText = findViewById(R.id.commentsEditText);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();

        commentsPageView = (RecyclerView) findViewById(R.id.commentsPageView);
        commentsPageList = new ArrayList<>();

        commentsFeedRecyclerAdapter = new CommentsFeedRecyclerAdapter(commentsPageList);
        commentsPageView.setLayoutManager(new LinearLayoutManager(this));
        commentsPageView.setAdapter(commentsFeedRecyclerAdapter);

        //for adding new comment
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String comments_editext = commentsEditText.getText().toString();

                if (!TextUtils.isEmpty(comments_editext)) {

                    Map<String, Object> commentsMap = new HashMap<>();
                    commentsMap.put("user_id", user_id);
                    commentsMap.put("comment", comments_editext);
                    commentsMap.put("timestamp", FieldValue.serverTimestamp());
                    commentsMap.put("post_name", postId);
                    commentsMap.put("thread_name", CategoryId);

                    firebaseFirestore.collection("Posts/" + postId + "/Comments").document().set(commentsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(CommentsActivity.this, "Comment posted successfully!", Toast.LENGTH_SHORT).show();

                            } else {

                                String error = task.getException().getMessage();
                                Toast.makeText(CommentsActivity.this, "Error:" + error, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                    firebaseFirestore.collection("Threads/" + CategoryId + "/Posts/" + postId + "/Comments").document().set(commentsMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        Toast.makeText(CommentsActivity.this, "Comment posted successfully!", Toast.LENGTH_SHORT).show();

                                    } else {

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

        //for loading more comments
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


            //for loading comments

            Query firstQuery = firebaseFirestore.collection("Posts/" + postId + "/Comments")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .limit(15);

            firstQuery.addSnapshotListener(CommentsActivity.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        if (isFirstPageFirstLoaded) {

                            // Get the last visible document
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                        }

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String commentFeedId = doc.getDocument().getId();
                                CommentsFeed commentsFeed = doc.getDocument()
                                        .toObject(CommentsFeed.class).withId(commentFeedId);
                                if (isFirstPageFirstLoaded) {

                                    commentsPageList.add(commentsFeed);

                                } else {

                                    commentsPageList.add(0, commentsFeed);

                                }

                                commentsFeedRecyclerAdapter.notifyDataSetChanged();

                            }
                        }

                        isFirstPageFirstLoaded = false;

                    }


                }
            });
        }
    }

    public void loadMorePost() {

        Query nextQuery = firebaseFirestore.collection("Posts")
                .document(postId)
                .collection("Comments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .startAfter(lastVisible)
                .limit(15);

        nextQuery.addSnapshotListener(CommentsActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()) {

                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String commentsFeedId = doc.getDocument().getId();
                            CommentsFeed commentsFeed = doc.getDocument().toObject(CommentsFeed.class)
                                    .withId(commentsFeedId);
                            commentsPageList.add(commentsFeed);
                            commentsFeedRecyclerAdapter.notifyDataSetChanged();

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
