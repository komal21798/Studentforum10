package com.komal.studentforum10;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
public class HomeFragment extends Fragment {

    private RecyclerView homeFeedView;
    private List<HomeFeed> homeFeedList;

    private HomeFeedRecyclerAdapter homeFeedRecyclerAdapter;

    private FirebaseFirestore firebaseFirestore;

    private FirebaseAuth firebaseAuth;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoaded = true;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
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

            homeFeedView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if(reachedBottom){
                        loadMorePost();
                    }
                }
            });

            //To order the posts according to the Timestamp added a first Query and added a limit to load 15 posts at a time (Changeable)
            Query firstQuery = firebaseFirestore.collection("Posts")
                    .orderBy("timestamp",Query.Direction.DESCENDING)
                    .limit(15);

            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
              
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                  if (isFirstPageFirstLoaded) {

                      // Get the last visible document
                      lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                  }
                      for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                          if (doc.getType() == DocumentChange.Type.ADDED) {

                              String homeFeedId = doc.getDocument().getId();
                              HomeFeed homeFeed = doc.getDocument().toObject(HomeFeed.class).withId(homeFeedId);
                                if (isFirstPageFirstLoaded) {

                                    homeFeedList.add(homeFeed);

                                } else {

                                    homeFeedList.add(0,homeFeed);

                                }

                                homeFeedRecyclerAdapter.notifyDataSetChanged();

                          }

                      }

                      isFirstPageFirstLoaded = false;

                }

            });

        }

        return v;
    }

    public void loadMorePost(){

        Query nextQuery = firebaseFirestore.collection("Posts")
                .orderBy("timestamp",Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(15);

        nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(!queryDocumentSnapshots.isEmpty()) {

                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String homeFeedId = doc.getDocument().getId();
                            HomeFeed homeFeed = doc.getDocument().toObject(HomeFeed.class).withId(homeFeedId);
                            homeFeedList.add(homeFeed);

                            homeFeedRecyclerAdapter.notifyDataSetChanged();

                        }

                    }
                }
            }
        });
    }

}
