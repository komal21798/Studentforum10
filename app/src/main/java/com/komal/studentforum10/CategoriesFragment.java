package com.komal.studentforum10;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.CardView;
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
public class CategoriesFragment extends Fragment {

    private RecyclerView categoriesFeedView;
    private List<CategoriesFeed> categoriesFeedList;
    private CategoriesFeedRecyclerAdapter categoriesFeedRecyclerAdapter;

    private FirebaseFirestore firebaseFirestore;

    private FirebaseAuth firebaseAuth;


    public CategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.fragment_categories, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        categoriesFeedView = (RecyclerView) v.findViewById(R.id.categoriesFeedView);

        categoriesFeedList = new ArrayList<>();

        categoriesFeedRecyclerAdapter = new CategoriesFeedRecyclerAdapter(categoriesFeedList);
        categoriesFeedView.setLayoutManager(new LinearLayoutManager(getActivity()));
        categoriesFeedView.setAdapter(categoriesFeedRecyclerAdapter);

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore.collection("Threads").addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if(!queryDocumentSnapshots.isEmpty()){

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String categoryId = doc.getDocument().getId();

                                CategoriesFeed categoriesFeed = doc.getDocument().toObject(CategoriesFeed.class).withId(categoryId);
                                categoriesFeedList.add(categoriesFeed);

                                categoriesFeedRecyclerAdapter.notifyDataSetChanged();

                            }

                        }

                    }

                }
            });
        }

        return v;
    }

}
