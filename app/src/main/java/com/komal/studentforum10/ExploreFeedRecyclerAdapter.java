package com.komal.studentforum10;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExploreFeedRecyclerAdapter extends RecyclerView.Adapter<ExploreFeedRecyclerAdapter.ViewHolder> {

    public List<ExploreFeed> exploreFeedList;
    private FirebaseFirestore firebaseFirestore;
    private Context context;

    public ExploreFeedRecyclerAdapter(List<ExploreFeed> exploreFeedList) {

        this.exploreFeedList = exploreFeedList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_post_item, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        context = parent.getContext();
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        String postNameData = exploreFeedList.get(position).getPost_name();
        holder.setPostName(postNameData);

        String user_id = exploreFeedList.get(position).getUser_id();

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                String postUsername;
                String postUserimage;
                if(task.isSuccessful()){

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

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
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
    }
}

