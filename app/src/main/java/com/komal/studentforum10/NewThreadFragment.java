package com.komal.studentforum10;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class NewThreadFragment extends Fragment {

    private EditText newThreadName;
    private EditText newThreadDesc;
    private Button newThreadSubmitBtn;
    private ProgressBar newThreadProgress;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public String username;

    private String user_id;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public NewThreadFragment() {
        // Required empty public constructor
    }

    public static NewThreadFragment newInstance(String param1, String param2) {
        NewThreadFragment fragment = new NewThreadFragment();
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
        View v;
        v = inflater.inflate(R.layout.fragment_new_thread, container, false);

        newThreadName = v.findViewById(R.id.newThreadName);
        newThreadDesc = v.findViewById(R.id.newThreadDesc);
        newThreadSubmitBtn = v.findViewById(R.id.newThreadSubmitBtn);
        newThreadProgress = v.findViewById(R.id.newThreadProgress);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();

        newThreadSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String threadName = newThreadName.getText().toString();
                final String threadDesc = newThreadDesc.getText().toString();

                if (firebaseAuth.getCurrentUser().isAnonymous()){
                    Toast.makeText(getActivity(),"First Login In",Toast.LENGTH_LONG).show();
                }

              else  if(!TextUtils.isEmpty(threadName) && !TextUtils.isEmpty(threadDesc)) {

                    newThreadProgress.setVisibility(View.VISIBLE);

                    firebaseFirestore.collection("Users").document(user_id)
                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                                    username = documentSnapshot.getString("username");

                                    Map<String, Object> threadMap = new HashMap<>();
                                    threadMap.put("username", username);
                                    threadMap.put("thread_name", threadName);
                                    threadMap.put("thread_desc", threadDesc);

                                    //submit thread to admin for approval
                                    firebaseFirestore.collection("Users").document("M4S0hiNILmTuj1nEKp3NCGvfiiF2").collection("Inbox")
                                            .document(threadName).set(threadMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                Intent myIntent = new Intent(getActivity(), ThreadSubmittedActivity.class);
                                                startActivity(myIntent);

                                            } else{

                                                String error = task.getException().getMessage();
                                                Toast.makeText(getActivity(), "Thread posting error:" + error, Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });


                                }
                            });



                } else {

                    Toast.makeText(getActivity(), "Enter all the details.", Toast.LENGTH_SHORT).show();

                }

            }
        });



        return v;
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
