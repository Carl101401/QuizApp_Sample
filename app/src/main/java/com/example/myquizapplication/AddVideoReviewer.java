package com.example.myquizapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddVideoReviewer extends AppCompatActivity {


    StorageReference storageReference;
    LinearProgressIndicator progressIndicator;
    Uri video;
    MaterialButton selectVideo, uploadVideo;
    ImageView imageView;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {


        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK){
                if (result.getData() != null){
                    uploadVideo.setEnabled(true);
                    video = result.getData().getData();
                    Glide.with(AddVideoReviewer.this).load(video).into(imageView);
                }
            }else Toast.makeText(AddVideoReviewer.this, "Please Select A Video", Toast.LENGTH_SHORT).show();

        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addvideoreviewer);

        FirebaseApp.initializeApp(this);
        storageReference = FirebaseStorage.getInstance().getReference();

        imageView = findViewById(R.id.imageViews);
        progressIndicator = findViewById(R.id.process);
        selectVideo = findViewById(R.id.selectVideo);
        uploadVideo = findViewById(R.id.uploadVideo);

        com.google.android.material.button.MaterialButton viewTextButton = findViewById(R.id.ViewVideoButton);
        viewTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Define the Intent to start a new activity (replace NewActivity.class with your desired activity)
                Intent intent = new Intent(AddVideoReviewer.this, TeacherVideoReviewer.class);

                // Add any extras or data you want to pass to the new activity
                // intent.putExtra("key", "value");

                // Start the new activity
                startActivity(intent);
            }
        });

        selectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*");
                activityResultLauncher.launch(intent);
                uploadVideo.setVisibility(View.VISIBLE);
            }
        });
        uploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadVideo(video);

            }
        });
    }

    private void uploadVideo(Uri uri) {
        // Retrieve the current videoCounter value from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("VideoCounterPrefs", MODE_PRIVATE);
        final int[] videoCounter = {preferences.getInt("videoCounter", 1)}; // Default value is 1


        final String[] customVideoName = {"Quiz Reviewer      Number " + videoCounter[0]};

        StorageReference reference = storageReference.child("video/" + customVideoName[0]);

        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddVideoReviewer.this, "Video Uploaded Successfully!", Toast.LENGTH_SHORT).show();

                // Increment videoCounter
                videoCounter[0]++;

                // Save the updated videoCounter value to SharedPreferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("videoCounter", videoCounter[0]);
                editor.apply();

                video = null;
                Glide.with(AddVideoReviewer.this).clear(imageView);
                uploadVideo.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddVideoReviewer.this, "Failed To Upload Video", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressIndicator.setVisibility(View.VISIBLE);
                progressIndicator.setMax(Math.toIntExact(snapshot.getTotalByteCount()));
                progressIndicator.setProgress(Math.toIntExact(snapshot.getBytesTransferred()));
            }
        });
    }

    public void onBackPressed() {
        // Do nothing or add a message if you want
        //Toast.makeText(Reviewer.this, "Choose back", Toast.LENGTH_SHORT).show();
    }

}