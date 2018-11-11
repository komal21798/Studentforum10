package com.komal.studentforum10;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewPostFragment extends Fragment {

    public String[] Threads = new String[]{
            "thread1","thread2","thread3","thread4"
    };

    private AutoCompleteTextView newPostThread;
    private EditText newPostName;
    private EditText newPostDesc;
    private Button newPostBtn;
    private ProgressBar newPostProgress;
    private ArrayAdapter<String> adapter;

    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    private String user_id;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;



    public NewPostFragment() {

        // Required empty public constructor
    }

    public static NewPostFragment newInstance(String param1, String param2) {

        NewPostFragment fragment = new NewPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v;
        v = inflater.inflate(R.layout.fragment_new_post, container, false);

        newPostThread = (AutoCompleteTextView) v.findViewById(R.id.newPostThread);
        newPostName = (EditText) v.findViewById(R.id.newPostName);
        newPostDesc = (EditText) v.findViewById(R.id.newPostDesc);
        newPostBtn = (Button) v.findViewById(R.id.newPostBtn);
        newPostProgress = (ProgressBar) v.findViewById(R.id.newPostProgress);
       // newresultlist = (RecyclerView) v.findViewById(R.id.resultlist);

        adapter= new ArrayAdapter<String>(getActivity(),android.R.layout.select_dialog_item, Threads);
        newPostThread.setThreshold(1);
        newPostThread.setAdapter(adapter);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        user_id = firebaseAuth.getCurrentUser().getUid();

        //only adds text post and not images, gifs
        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String post_thread = newPostThread.getText().toString();
                String post_name = newPostName.getText().toString();
                String post_desc = newPostDesc.getText().toString();

                if(!TextUtils.isEmpty(post_desc) && !TextUtils.isEmpty(post_name) /*&& !TextUtils.isEmpty(post_thread)*/) {

                    newPostProgress.setVisibility(View.VISIBLE);

                    Map<String, Object> postMap = new HashMap<>();
                    postMap.put("user_id", user_id);
                    postMap.put("post_thread", post_thread);
                    postMap.put("post_name", post_name);
                    postMap.put("post_desc", post_desc);
                    postMap.put("timestamp", FieldValue.serverTimestamp());

                    firebaseFirestore/*.collection("Threads").document(post_thread)*/.collection("Posts").document(post_name).set(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Toast.makeText(getActivity(), "Posted successfully!", Toast.LENGTH_SHORT).show();
                                goToHome();
                                //doesnt finish the activity. Work on that

                            } else {

                                String error = task.getException().getMessage();
                                Toast.makeText(getActivity(), "New Post Error:" + error, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                } else {

                    Toast.makeText(getActivity(), "Please fill all the details.", Toast.LENGTH_SHORT).show();

                }

            }
        });

        return  v;


    }


    public void goToHome() {

        Intent homeIntent = new Intent(getActivity(), StudentForum.class);
        startActivity(homeIntent);
        //finish();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }






}
