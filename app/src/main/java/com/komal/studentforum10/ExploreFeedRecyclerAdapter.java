package com.komal.studentforum10;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExploreFeedRecyclerAdapter extends RecyclerView.Adapter<ExploreFeedRecyclerAdapter.ViewHolder> {

    public List<ExploreFeed> exploreFeedList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private Context context;
    private int count;

    public ExploreFeedRecyclerAdapter(List<ExploreFeed> exploreFeedList) {

        this.exploreFeedList = exploreFeedList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_post_item, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        context = parent.getContext();
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String exploreFeedId = exploreFeedList.get(position).exploreFeedId;
        final String explorePostDesc = exploreFeedList.get(position).getPost_desc();
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        final String postThread = exploreFeedList.get(position).getPost_thread();


        final int likes_count = exploreFeedList.get(position).getLikes_count();

        String postNameData = exploreFeedList.get(position).getPost_name();
        holder.setPostName(postNameData);

        String user_id = exploreFeedList.get(position).getUser_id();

        firebaseFirestore.collection("Users").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        String postUsername;
                        String postUserimage;
                        if (task.isSuccessful()) {

                            postUsername = task.getResult().getString("username");
                            postUserimage = task.getResult().getString("profile_image");

                            holder.setUsername(postUsername);
                            holder.setUserimage(postUserimage);

                        } else {

                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

                        }

                    }
                });

        try {

            long millisecond = exploreFeedList.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("dd/MM/yyyy", new Date(millisecond)).toString();
            holder.setPostDate(dateString);

        } catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        //Get Comments Count
        firebaseFirestore.collection("/Posts/" + exploreFeedId + "/Comments")
                .addSnapshotListener((Activity) context, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            int count = queryDocumentSnapshots.size();

                            holder.updateCommentCount(count);

                        } else {

                            holder.updateCommentCount(0);

                        }
                    }
                });

        //Get Likes Counts
        firebaseFirestore.collection("Posts/" + exploreFeedId + "/Likes")
                .addSnapshotListener((Activity) context, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (!queryDocumentSnapshots.isEmpty()) {

                            count = queryDocumentSnapshots.size();
                            holder.updateLikeCount(count);

                        } else {

                            holder.updateLikeCount(0);

                        }
                    }
                });


        //Get Likes

        firebaseFirestore.collection("Posts/" + exploreFeedId + "/Likes")
                .document(currentUserId).addSnapshotListener((Activity) context, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (documentSnapshot.exists()) {
                    holder.postLikeBtn.setImageDrawable(context.getDrawable(R.drawable.action_like_accent));
                    holder.postLikeCount.setTextColor(ContextCompat.getColor(context, R.color.Like_Accent));
                } else {
                    holder.postLikeBtn.setImageDrawable(context.getDrawable(R.drawable.action_like_gray));
                    holder.postLikeCount.setTextColor(ContextCompat.getColor(context, R.color.Like_Gray));
                }

            }
        });


        //Likes Feature
        holder.postLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (firebaseAuth.getCurrentUser().isAnonymous()) {

                    Toast.makeText(context, "Please login to access this functionality.", Toast.LENGTH_LONG).show();

                } else {

                    firebaseFirestore.collection("Posts/" + exploreFeedId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (!task.getResult().exists()) {
                                Map<String, Object> likesMap = new HashMap<>();
                                likesMap.put("timestamp", FieldValue.serverTimestamp());

                                firebaseFirestore.collection("Posts/" + exploreFeedId + "/Likes").document(currentUserId).set(likesMap);
                                firebaseFirestore.collection("Threads/" + postThread + "/Posts/" + exploreFeedId
                                        + "/Likes").document(currentUserId).set(likesMap);


                            } else {

                                firebaseFirestore.collection("Posts/" + exploreFeedId + "/Likes").document(currentUserId).delete();
                                firebaseFirestore.collection("Threads/" + postThread + "/Posts/" + exploreFeedId
                                        + "/Likes").document(currentUserId).delete();

                            }
                        }
                    });
                }
            }
        });

        //going to comments activity
        holder.postCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (postThread.equals("Register")) {

                    Intent registerIntent = new Intent(context, EventsRegistrationActivity.class);
                    registerIntent.putExtra("threadPageId", exploreFeedId);
                    context.startActivity(registerIntent);

                } else {

                    Intent commentsIntent = new Intent(context, CommentsActivity.class);
                    commentsIntent.putExtra("threadPageId", exploreFeedId);
                    commentsIntent.putExtra("postDesc", explorePostDesc);
                    context.startActivity(commentsIntent);

                }

            }
        });

        //for showing delete/report popup menu
        if (!currentUserId.equals("M4S0hiNILmTuj1nEKp3NCGvfiiF2")) {
            holder.deleteReportPost.setVisibility(View.INVISIBLE);
        }


        //To not show comments on Registration Posts
        if (postThread.equals("Register")) {
            holder.postCommentBtn.setVisibility(View.INVISIBLE);
            holder.postCommentCount.setVisibility(View.INVISIBLE);
            holder.postRegistrationView.setVisibility(View.VISIBLE);
        }

        //deleting posts
        holder.deleteReportPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.popupactions, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        boolean choice;
                        if (item.getTitle().equals("Delete")) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(" Are you sure you want to delete the post?");
                            builder.setCancelable(true);
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    firebaseFirestore.collection("Posts").document(exploreFeedId).delete();
                                    firebaseFirestore.collection("Threads/" + postThread + "/Posts/").document(exploreFeedId).delete();
                                    removeAt(holder.getAdapterPosition());
                                    notifyDataSetChanged();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                        return true;
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return exploreFeedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView postName;
        private TextView postUsername;
        private CircleImageView postUserimage;
        private TextView postDate;
        private ImageView postLikeBtn;
        private TextView postLikeCount;
        private TextView postCommentCount;
        private ImageView postCommentBtn;
        private ImageView postRegistrationView;
        private CardView postCardView;
        private ImageView deleteReportPost;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            postLikeBtn = mView.findViewById(R.id.postLikeBtn);
            postLikeCount = mView.findViewById(R.id.postLikeCount);

            postCommentCount = mView.findViewById(R.id.postCommentCount);
            postCommentBtn = mView.findViewById(R.id.postCommentBtn);
            postRegistrationView = mView.findViewById(R.id.postRegistrationView);

            postCardView = mView.findViewById(R.id.postCardView);

            deleteReportPost = mView.findViewById(R.id.deleteReportPost);
        }

        public void setPostName(String postText) {

            postName = mView.findViewById(R.id.postName);
            postName.setText(postText);

        }

        public void setUsername(String postUsernameText) {

            postUsername = mView.findViewById(R.id.postUsername);
            postUsername.setText(postUsernameText);

        }

        public void setUserimage(String postUserimageText) {

            postUserimage = mView.findViewById(R.id.postUserImage);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.mipmap.ic_launcher_foreground);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(postUserimageText).into(postUserimage);
        }

        public void setPostDate(String postDateText) {

            postDate = mView.findViewById(R.id.postDate);
            postDate.setText(postDateText);
        }

        public void updateLikeCount(int count) {
            postLikeCount.setText(count + " "); //Space so no error while converting to string
        }

        public void updateCommentCount(int count) {
            postCommentCount.setText(count + " "); //Space so no error while converting to string
        }

    }

    //removing deleted posts from recycler view
    public void removeAt(int position) {
        exploreFeedList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, exploreFeedList.size());
    }


}

