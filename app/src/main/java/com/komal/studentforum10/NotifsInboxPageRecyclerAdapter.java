package com.komal.studentforum10;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class NotifsInboxPageRecyclerAdapter extends RecyclerView.Adapter<NotifsInboxPageRecyclerAdapter.ViewHolder> {

    public List<NotifsInboxPage> notifsInboxPageList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private Context context;

    public NotifsInboxPageRecyclerAdapter(List<NotifsInboxPage> notifsInboxPageList) {
        this.notifsInboxPageList = notifsInboxPageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_inbox_item, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        String message = notifsInboxPageList.get(position).getMessage();
        holder.setMessage(message);

    }

    @Override
    public int getItemCount() {
        return notifsInboxPageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mview;
        private TextView message;

        public ViewHolder(View itemView)
        {
            super(itemView);

            mview = itemView;
        }

        public void setMessage(String messageText)
        {
            message = mview.findViewById(R.id.messageText);
            message.setText(messageText);
        }
    }
}
