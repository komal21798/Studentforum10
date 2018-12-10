package com.komal.studentforum10;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DeletePost {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    boolean temp = false;

    public boolean deletePost(final String postId, final String threadId, final Context context) {

        firebaseFirestore = FirebaseFirestore.getInstance();

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(" Are you sure you want to delete the post?");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                temp = true;
                Toast.makeText(context, "Post id: " + postId + " Thread name: " + threadId, Toast.LENGTH_SHORT).show();

                firebaseFirestore.collection("Posts").document(postId).delete();
                firebaseFirestore.collection("Threads/" + threadId + "/Posts/").document(postId).delete();

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        return temp;

    }

}
