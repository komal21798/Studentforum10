package com.komal.studentforum10;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class ThreadPageRecyclerAdapter extends RecyclerView.Adapter<ThreadPageRecyclerAdapter.ViewHolder> {

    public List<ThreadPage> threadPageList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private Context context;


    public ThreadPageRecyclerAdapter(List<ThreadPage> threadPageList) {

        this.threadPageList = threadPageList;

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

        final String threadPageId = threadPageList.get(position).threadPageId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();
        final String postThread = threadPageList.get(position).getPost_thread();

        String postName = threadPageList.get(position).getPost_name();
        holder.setPostName(postName);

        String user_id = threadPageList.get(position).getUser_id();

        firebaseFirestore.collection("Users").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()) {

                            String username = task.getResult().getString("username");
                            String userImage = task.getResult().getString("profile_image");

                            holder.setUserData(username, userImage);

                        } else {

                            Intent newIntent = new Intent(context, StudentForum.class);
                            context.startActivity(newIntent);

                        }

                    }
                });

        /*//User Data will be retrieved here...
        firebaseFirestore.collection("Users").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String userName = task.getResult().getString("username");
                    String userImage = task.getResult().getString("profile_image");

                    holder.setUsername(userName);
                    holder.setUserimage(userImage);


                } else {

                    Intent newIntent = new Intent(context, StudentForum.class);
                    context.startActivity(newIntent);

                }

            }
        });*/

        /*firebaseFirestore.collection("Users").document(user_id).get()
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
*/
        try {

            long millisecond = threadPageList.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("dd/MM/yyyy", new Date(millisecond)).toString();
            holder.setPostDate(dateString);

        } catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        //Get Likes Counts
        firebaseFirestore.collection("Threads/" + postThread + "/Posts/" + threadPageId + "/Likes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    int count = queryDocumentSnapshots.size();

                    holder.updateLikeCount(count);

                } else {

                    holder.updateLikeCount(0);

                }
            }
        });

        //Get Likes
        firebaseFirestore.collection("Threads/" + postThread + "/Posts/" + threadPageId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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

            if (firebaseAuth.getCurrentUser().isAnonymous()){
                Toast.makeText(context,"not allowed to like",Toast.LENGTH_SHORT).show();
            }
            else {
                //Likes Feature
                holder.postLikeBtn.setOnClickListener(new View.OnClickListener() {


                firebaseFirestore.collection("Threads/" + postThread + "/Posts/" + threadPageId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onClick(View view) {


                        firebaseFirestore.collection("Posts/" + threadPageId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (!task.getResult().exists()) {
                                    Map<String, Object> likesMap = new HashMap<>();
                                    likesMap.put("timestamp", FieldValue.serverTimestamp());


                            firebaseFirestore.collection("Threads/" + postThread + "/Posts/" + threadPageId + "/Likes").document(currentUserId).set(likesMap);
                            firebaseFirestore.collection("Posts/" + threadPageId + "/Likes").document(currentUserId).set(likesMap);

                                } else {

                            firebaseFirestore.collection("Threads/" + postThread + "/Posts/" + threadPageId + "/Likes").document(currentUserId).delete();
                            firebaseFirestore.collection("Posts/" + threadPageId + "/Likes").document(currentUserId).delete();


                        }
                    }
                });
            }

        //going to comments activity
        holder.postCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(postThread.equals("Register")) {

                    Intent registerIntent = new Intent(context, EventsRegistrationActivity.class);
                    registerIntent.putExtra("threadPageId", threadPageId);
                    context.startActivity(registerIntent);

                } else {

                    Intent commentsIntent = new Intent(context, CommentsActivity.class);
                    commentsIntent.putExtra("postThreadId", postThread);
                    commentsIntent.putExtra("threadPageId", threadPageId);
                    context.startActivity(commentsIntent);

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return threadPageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView postName;
        private TextView postUsername;
        private CircleImageView postUserimage;
        private TextView postDate;
        private ImageView postLikeBtn;
        private TextView postLikeCount;
        private ImageButton postDelete;
        private CardView postCardView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            postLikeBtn = mView.findViewById(R.id.postLikeBtn);
            postLikeCount = mView.findViewById(R.id.postLikeCount);

            postDelete = mView.findViewById(R.id.delete_post);

            postCardView = mView.findViewById(R.id.postCardView);
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

        public void setUserData(String name, String image){

            postUsername = mView.findViewById(R.id.postUsername);
            postUserimage = mView.findViewById(R.id.postUserImage);

            postUsername.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.ic_launcher_foreground);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(postUserimage);

        }

        public void setPostDate(String postDateText) {

            postDate = mView.findViewById(R.id.postDate);
            postDate.setText(postDateText);

        }

        public void updateLikeCount(int count) {
            postLikeCount.setText(count + " "); //Space so no error while converting to string
        }

    }

}
