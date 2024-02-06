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

public class AddImageReviewer extends AppCompatActivity {
    StorageReference storageReference;
    LinearProgressIndicator progressIndicator;
    Uri image;
    MaterialButton selectImage, uploadImage;
    ImageView imageView;
    private int imageCounter = 1;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK){
                if (result.getData() != null){
                    uploadImage.setEnabled(true);
                    image = result.getData().getData();
                    Glide.with(AddImageReviewer.this).load(image).into(imageView);
                }
            } else {
                Toast.makeText(AddImageReviewer.this, "Please Select an Image", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addimagereviewer);

        FirebaseApp.initializeApp(this);
        storageReference = FirebaseStorage.getInstance().getReference();

        imageView = findViewById(R.id.addImageView);
        progressIndicator = findViewById(R.id.process2);
        selectImage = findViewById(R.id.selectImage);
        uploadImage = findViewById(R.id.uploadImage);

        com.google.android.material.button.MaterialButton viewTextButton = findViewById(R.id.ViewImageButton);
        viewTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Define the Intent to start a new activity (replace NewActivity.class with your desired activity)
                Intent intent = new Intent(AddImageReviewer.this, TeacherImageReviewer.class);

                // Add any extras or data you want to pass to the new activity
                // intent.putExtra("key", "value");

                // Start the new activity
                startActivity(intent);
            }
        });
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
                uploadImage.setVisibility(View.VISIBLE);
            }
        });
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(image);
            }
        });
    }

    private void uploadImage(Uri uri) {

        SharedPreferences preferences = getSharedPreferences("ImageCounterPrefs", MODE_PRIVATE);
        final int[] imageCounter = {preferences.getInt("imageCounter", 1)}; // Default value is 1
        String customImageName = "Quiz_Reviewer_Number_" + imageCounter[0];

        StorageReference reference = storageReference.child("images/" + customImageName);

        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddImageReviewer.this, "Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                imageCounter[0]++;

                // Save the updated videoCounter value to SharedPreferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("imageCounter", imageCounter[0]);
                editor.apply();
                // Reset image and clear ImageView
                image = null;
                Glide.with(AddImageReviewer.this).clear(imageView);
                uploadImage.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddImageReviewer.this, "Failed To Upload Image", Toast.LENGTH_SHORT).show();
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
