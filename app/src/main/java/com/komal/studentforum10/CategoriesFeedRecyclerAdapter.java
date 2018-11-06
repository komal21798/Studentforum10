package com.komal.studentforum10;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CategoriesFeedRecyclerAdapter extends RecyclerView.Adapter<CategoriesFeedRecyclerAdapter.ViewHolder> {

    private List<CategoriesFeed> categoriesFeedList;
    private FirebaseFirestore firebaseFirestore;
    private Context context;

    public CategoriesFeedRecyclerAdapter(List<CategoriesFeed> categoriesFeedList){

        this.categoriesFeedList = categoriesFeedList;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_category_item, parent, false);
        //firebaseFirestore = FirebaseFirestore.getInstance();
        //context = parent.getContext();
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String threadNameData = categoriesFeedList.get(position).getThread_name();
        holder.setThreadName(threadNameData);

    }

    @Override
    public int getItemCount() {
        return categoriesFeedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView threadName;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setThreadName(String threadNameText) {

            threadName = mView.findViewById(R.id.threadName);
            threadName.setText(threadNameText);

        }
    }
}
