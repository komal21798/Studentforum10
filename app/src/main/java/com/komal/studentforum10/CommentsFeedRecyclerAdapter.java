package com.komal.studentforum10;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsFeedRecyclerAdapter extends RecyclerView.Adapter<CommentsFeedRecyclerAdapter.ViewHolder> {

    public List<CommentsFeed> commentsFeedList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private Context context;

    public CommentsFeedRecyclerAdapter(List<CommentsFeed> commentsFeedList) {

        this.commentsFeedList = commentsFeedList;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_comment_item, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        context = parent.getContext();
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String commentsFeedId = commentsFeedList.get(position).commentsFeedId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String comment = commentsFeedList.get(position).getComment();
        holder.setComment(comment);

        String user_id = commentsFeedList.get(position).getUser_id();

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

    }

    @Override
    public int getItemCount() {
        return commentsFeedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView comment;
        private CircleImageView commentUserimage;
        private TextView commentUsername;


        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setComment(String commentText){

            comment = mView.findViewById(R.id.commentName);
            comment.setText(commentText);

        }

        public void setUserData(String name, String image){

            commentUsername = mView.findViewById(R.id.commentUserName);
            commentUserimage = mView.findViewById(R.id.commentUserImage);

            commentUsername.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.ic_launcher_foreground);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(commentUserimage);

        }
    }
}