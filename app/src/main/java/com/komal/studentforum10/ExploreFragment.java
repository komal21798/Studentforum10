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
import com.google.firebase.firestore.Query;
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

    private DocumentSnapshot lastVisible;

    private Boolean isFirstPageFirstLoaded = true;

    public ExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
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

            exploreFeedView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if (reachedBottom) {
                        loadMorePost();
                    }
                }
            });

            Query firstQuery = firebaseFirestore.collection("Posts")
                    .orderBy("position", Query.Direction.DESCENDING)
                    .limit(15);

            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {

                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        if (isFirstPageFirstLoaded) {

                            // Get the last visible document
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                        }

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String exploreFeedId = doc.getDocument().getId();
                                ExploreFeed exploreFeed = doc.getDocument().toObject(ExploreFeed.class).withId(exploreFeedId);

                                if (isFirstPageFirstLoaded) {
                                    exploreFeedList.add(exploreFeed);

                                } else {

                                    exploreFeedList.add(0, exploreFeed);

                                }

                                exploreFeedRecyclerAdapter.notifyDataSetChanged();

                            }

                        }

                    }

                }
            });

        }

        return v;
    }

    public void loadMorePost() {

        Query nextQuery = firebaseFirestore.collection("Posts")
                .orderBy("position", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(15);

        nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()) {

                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            ExploreFeed exploreFeed = doc.getDocument().toObject(ExploreFeed.class);
                            exploreFeedList.add(exploreFeed);

                            exploreFeedRecyclerAdapter.notifyDataSetChanged();

                        }

                    }
                }
            }
        });
    }

}
