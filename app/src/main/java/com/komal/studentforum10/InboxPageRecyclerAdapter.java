package com.komal.studentforum10;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InboxPageRecyclerAdapter extends RecyclerView.Adapter<InboxPageRecyclerAdapter.ViewHolder> {

    public List<InboxPage> inboxPageList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private Context context;

    public InboxPageRecyclerAdapter(List<InboxPage> inboxPageList) {
        this.inboxPageList = inboxPageList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_inbox_item, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        context = parent.getContext();
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String user_id = inboxPageList.get(position).inboxPageId;

        final String username = inboxPageList.get(position).getUsername();
        holder.setUsername(username);

        final String threadName = inboxPageList.get(position).getThread_name();
        final String threadDesc = inboxPageList.get(position).getThread_desc();
        holder.setThreadDetails(threadName, threadDesc);


        //admin approves thread
        holder.approveThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> threadMap = new HashMap<>();
                threadMap.put("username", username);
                threadMap.put("thread_name", threadName);
                threadMap.put("thread_desc", threadDesc);

                firebaseFirestore.collection("Threads").document(threadName).set(threadMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            Toast.makeText(context, "Posted!", Toast.LENGTH_SHORT).show();
                            firebaseFirestore.collection("Users").document("M4S0hiNILmTuj1nEKp3NCGvfiiF2").collection("Inbox")
                                    .document(threadName).delete();
                            removeAt(holder.getAdapterPosition());
                            notifyDataSetChanged();

                        } else{

                            String error = task.getException().getMessage();
                            Toast.makeText(context, "Thread posting error:" + error, Toast.LENGTH_SHORT).show();

                        }

                    }
                });

            }
        });


        //admin discards thread
        holder.discardThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Users").document("M4S0hiNILmTuj1nEKp3NCGvfiiF2").collection("Inbox")
                        .document(threadName).delete();
                Toast.makeText(context, "Discarded!", Toast.LENGTH_SHORT).show();
                removeAt(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return inboxPageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView username;
        private TextView threadname;
        private TextView threaddesc;
        private ImageView approveThread;
        private ImageView discardThread;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            approveThread = mView.findViewById(R.id.approveThread);
            discardThread = mView.findViewById(R.id.discardThread);
        }

        private void setUsername(String usernameText) {

            username = mView.findViewById(R.id.usernameInbox);
            username.setText(usernameText);

        }

        private void setThreadDetails(String name, String desc) {

            threadname = mView.findViewById(R.id.threadnameInbox);
            threaddesc = mView.findViewById(R.id.threaddescInbox);

            threadname.setText(name);
            threaddesc.setText(desc);

        }
    }

    //removing discarded thread or approvedh from recycler view
    public void removeAt(int position) {
        inboxPageList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, inboxPageList.size());
    }

}
