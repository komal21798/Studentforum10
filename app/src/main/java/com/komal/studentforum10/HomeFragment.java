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
import com.google.firebase.firestore.DocumentSnapshot;
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
public class HomeFragment extends Fragment {

    private RecyclerView homeFeedView;
    private List<HomeFeed> homeFeedList;

    private HomeFeedRecyclerAdapter homeFeedRecyclerAdapter;

    private FirebaseFirestore firebaseFirestore;

    private FirebaseAuth firebaseAuth;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        homeFeedView = (RecyclerView) v.findViewById(R.id.homeFeedView);
        homeFeedList = new ArrayList<>();

        homeFeedRecyclerAdapter = new HomeFeedRecyclerAdapter(homeFeedList);
        homeFeedView.setLayoutManager(new LinearLayoutManager(getActivity()));
        homeFeedView.setAdapter(homeFeedRecyclerAdapter);

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            HomeFeed homeFeed = doc.getDocument().toObject(HomeFeed.class);
                            homeFeedList.add(homeFeed);

                            homeFeedRecyclerAdapter.notifyDataSetChanged();

                        }

                    }

                }
            });
        }

        return v;
    }

}
