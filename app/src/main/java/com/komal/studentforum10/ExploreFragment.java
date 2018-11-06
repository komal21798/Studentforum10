package com.komal.studentforum10;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {

    private RecyclerView exploreFeedView;
    private List<ExploreFeed> exploreFeedList;

    private ExploreFeedRecyclerAdapter exploreFeedRecyclerAdapter;

    private FirebaseFirestore firebaseFirestore;

    private FirebaseAuth firebaseAuth;



    public ExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.fragment_explore, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        exploreFeedView = (RecyclerView) v.findViewById(R.id.exploreFeedView);
        exploreFeedList = new ArrayList<>();

        exploreFeedRecyclerAdapter = new ExploreFeedRecyclerAdapter(exploreFeedList);
        exploreFeedView.setLayoutManager(new LinearLayoutManager(getActivity()));
        exploreFeedView.setAdapter(exploreFeedRecyclerAdapter);

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore.collection("Posts").addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            ExploreFeed exploreFeed = doc.getDocument().toObject(ExploreFeed.class);
                            exploreFeedList.add(exploreFeed);

                            exploreFeedRecyclerAdapter.notifyDataSetChanged();

                        }

                    }

                }
            });
        }

        return v;
    }

}
