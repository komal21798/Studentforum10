package com.komal.studentforum10;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSetupActivity extends AppCompatActivity {

    private CircleImageView setup_profile_pic;
    private EditText setup_name;
    private Button setup_save_details_btn;
    private ProgressBar setup_progress;

    private boolean isChanged = false; //to see if profile_pic is changed or not

    private String user_id;

    private Uri mainImageURI = null;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);

        setup_profile_pic = (CircleImageView) findViewById(R.id.setup_profile_pic);
        setup_name = (EditText) findViewById(R.id.setup_username);
        setup_save_details_btn = (Button) findViewById(R.id.setup_save_details_btn);
        setup_progress = (ProgressBar) findViewById(R.id.setup_progress);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        user_id = firebaseAuth.getCurrentUser().getUid();

        //to retrieve user data from firebase(image retrieval not working)
        setup_progress.setVisibility(View.VISIBLE);
        setup_save_details_btn.setEnabled(false);

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    //to check if user image already exists or not
                    if (task.getResult().exists()) {

                        String user_name = task.getResult().getString("username");
                        String profile_image = task.getResult().getString("profile_image");

                        mainImageURI = Uri.parse(profile_image);

                        setup_name.setText(user_name);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.mipmap.ic_launcher_foreground);

                        Glide.with(AccountSetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(profile_image).into(setup_profile_pic);

                    }

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(AccountSetupActivity.this, "Firestore retrieve error: " + error, Toast.LENGTH_SHORT).show();

                }

                setup_progress.setVisibility(View.INVISIBLE);
                setup_save_details_btn.setEnabled(true);

            }
        });

        setup_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(AccountSetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AccountSetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        cropImage();

                    }

                } else {

                    cropImage();

                }

            }
        });

        setup_save_details_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user_name = setup_name.getText().toString();
                setup_progress.setVisibility(View.VISIBLE);

                if (isChanged) {

                    if (!TextUtils.isEmpty(user_name)) {

                        final String user_id = firebaseAuth.getCurrentUser().getUid();

                        final StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");

                        image_path.putFile(mainImageURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                return image_path.getDownloadUrl();

                            }

                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {

                                    storeFirestore(task, user_name);

                                } else {

                                    String error = task.getException().getMessage();
                                    Toast.makeText(AccountSetupActivity.this, "File upload error: " + error, Toast.LENGTH_SHORT).show();
                                    setup_progress.setVisibility(View.INVISIBLE);

                                }
                            }
                        });

                    }
                } else {

                    storeFirestore(null,user_name);

                }
            }
        });
    }

    private void storeFirestore(Task<Uri> task, String user_name) {

        Uri downloadUri;
        if(task != null) {

           downloadUri  = task.getResult();

        } else{

            downloadUri = mainImageURI;

        }
        Map<String, String> userMap = new HashMap<>();
        userMap.put("username", user_name);
        userMap.put("profile_image", downloadUri.toString());

        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(AccountSetupActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(AccountSetupActivity.this, StudentForum.class);
                    startActivity(myIntent);
                    finish();

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(AccountSetupActivity.this, "Firebase firestore: " + error, Toast.LENGTH_SHORT).show();

                }
                setup_progress.setVisibility(View.INVISIBLE);
            }

        });

    }

    private void cropImage() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(AccountSetupActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                setup_profile_pic.setImageURI(mainImageURI);

                isChanged = true;


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }
}
