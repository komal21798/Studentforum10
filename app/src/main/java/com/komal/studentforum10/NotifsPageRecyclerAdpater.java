package com.komal.studentforum10;

import android.app.NotificationChannelGroup;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotifsPageRecyclerAdpater extends RecyclerView.Adapter<NotifsPageRecyclerAdpater.ViewHolder> {


    public List<NotifsPage> notifsPageList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private Context context;


    public NotifsPageRecyclerAdpater(List<NotifsPage> notifsPageList) {
        this.notifsPageList = notifsPageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_item, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String user_id = notifsPageList.get(position).notifsPageId;

        String username = notifsPageList.get(position).getUsername();
        String profile_image = notifsPageList.get(position).getProfile_image();
        holder.setUserData(username, profile_image);

        //go to send notification activity
        holder.userCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendNotifsIntent = new Intent(context, SendNotifsActivity.class);
                sendNotifsIntent.putExtra("toUserId", user_id);
                context.startActivity(sendNotifsIntent);

            }
        });



    }

    @Override
    public int getItemCount() {
        return notifsPageList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private CircleImageView userImage;
        private TextView username;
        private CardView userCardView;


        public ViewHolder(View itemView){
            super(itemView);

            mView = itemView;

            userCardView = mView.findViewById(R.id.userCardView);
        }

        public void setUserData(String name, String image){

            username = mView.findViewById(R.id.username);
            userImage = mView.findViewById(R.id.userImage);

            username.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.ic_launcher_foreground);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(userImage);

        }
    }
}
