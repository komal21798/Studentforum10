package com.komal.studentforum10;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CategoriesFeedRecyclerAdapter extends RecyclerView.Adapter<CategoriesFeedRecyclerAdapter.ViewHolder> {

    private List<CategoriesFeed> categoriesFeedList;
    private FirebaseFirestore firebaseFirestore;
    private Context context;


    public CategoriesFeedRecyclerAdapter(List<CategoriesFeed> categoriesFeedList) {

        this.categoriesFeedList = categoriesFeedList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_category_item, parent, false);
        context = parent.getContext();

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final String CategoryId = categoriesFeedList.get(position).CategoryId;

        final String threadDesc = categoriesFeedList.get(position).getThread_desc();

        final String threadNameData = categoriesFeedList.get(position).getThread_name();
        holder.setThreadName(threadNameData);

        holder.threadCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ThreadActivity.class);
                intent.putExtra("CategoryId", CategoryId);
                intent.putExtra("ThreadDesc", threadDesc);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesFeedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView threadName;
        private ImageView threadArrowBtn;
        private CardView threadCardView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            threadArrowBtn = mView.findViewById(R.id.threadArrow);
            threadCardView = mView.findViewById(R.id.threadCardView);
        }

        public void setThreadName(String threadNameText) {

            threadName = mView.findViewById(R.id.threadNameCategories);
            threadName.setText(threadNameText);

        }

    }
}
