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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSetupActivity extends AppCompatActivity {

    private CircleImageView setup_profile_pic;
    private EditText setup_name;
    private Button setup_save_details_btn;
    private ProgressBar setup_progress;

    private Uri mainImageURI = null;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);

        setup_profile_pic = (CircleImageView) findViewById(R.id.setup_profile_pic);
        setup_name = (EditText) findViewById(R.id.setup_name);
        setup_save_details_btn = (Button) findViewById(R.id.setup_save_details_btn);
        setup_progress = (ProgressBar) findViewById(R.id.setup_progress);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        setup_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if(ContextCompat.checkSelfPermission(AccountSetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        ActivityCompat.requestPermissions(AccountSetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(AccountSetupActivity.this);

                    }

                }

            }
        });

        setup_save_details_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user_name = setup_name.getText().toString();

                if(TextUtils.isEmpty(user_name)) {

                    setup_progress.setVisibility(View.VISIBLE);

                    String user_id = firebaseAuth.getCurrentUser().getUid();

                    StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                    image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if(task.isSuccessful()) {

                                //Uri uri = task.getResult().getDownloadUrl();
                                Toast.makeText(AccountSetupActivity.this, "Done!", Toast.LENGTH_SHORT).show();

                            } else {

                                String error = task.getException().getMessage();
                                Toast.makeText(AccountSetupActivity.this, error, Toast.LENGTH_SHORT).show();

                            }
                            setup_progress.setVisibility(View.INVISIBLE);

                        }
                    });

                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                setup_profile_pic.setImageURI(mainImageURI);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }
}
