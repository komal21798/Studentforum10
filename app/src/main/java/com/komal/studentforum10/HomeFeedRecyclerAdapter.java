package com.komal.studentforum10;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class HomeFeedRecyclerAdapter extends RecyclerView.Adapter<HomeFeedRecyclerAdapter.ViewHolder> {

    public List<HomeFeed> homeFeedList;

    public HomeFeedRecyclerAdapter(List<HomeFeed> homeFeedList) {

        this.homeFeedList = homeFeedList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_feed_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String postNameData = homeFeedList.get(position).getPost_name();
        holder.setPostName(postNameData);

    }

    @Override
    public int getItemCount() {
        return homeFeedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView postName;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setPostName(String postText) {

            postName = mView.findViewById(R.id.postName);
            postName.setText(postText);

        }
    }
}
