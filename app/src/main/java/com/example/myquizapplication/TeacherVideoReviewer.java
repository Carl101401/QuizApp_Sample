package com.example.myquizapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeacherVideoReviewer extends AppCompatActivity {

    private ArrayList<TeacherVideo> arrayList; // Declare arrayList as a class-level variable
    private TeacherVideoAdapter adapter; // Declare adapter as a class-level variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoreviewer);

        FirebaseApp.initializeApp(this);
        SharedPreferences preferences = getSharedPreferences("VideoCounterPrefs", MODE_PRIVATE);
        final int videoCounter = preferences.getInt("videoCounter", 1); // Default value is 1

        RecyclerView recyclerView = findViewById(R.id.recycler);
        FirebaseStorage.getInstance().getReference().child("video").listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        arrayList = new ArrayList<>(); // Initialize arrayList
                        adapter = new TeacherVideoAdapter(TeacherVideoReviewer.this, arrayList); // Initialize adapter
                        adapter.setOnItemClickListener(new TeacherVideoAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(TeacherVideo video) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getUrl()));
                                intent.setDataAndType(Uri.parse(video.getUrl()), "video/*");
                                startActivity(intent);
                            }
                        });
                        adapter.setOnItemLongClickListener(new TeacherVideoAdapter.OnItemLongClickListener() {
                            @Override
                            public void onLongClick(TeacherVideo video) {
                                showDeleteConfirmationDialog(video);
                            }
                        });
                        recyclerView.setAdapter(adapter);
                        List<StorageReference> sortedItems = new ArrayList<>(listResult.getItems());

                        Collections.sort(sortedItems, (o1, o2) -> {
                            Task<StorageMetadata> metadataTask1 = o1.getMetadata();
                            Task<StorageMetadata> metadataTask2 = o2.getMetadata();

                            try {
                                // Wait for both tasks to complete
                                Tasks.await(metadataTask1);
                                Tasks.await(metadataTask2);

                                // Get creation time
                                long time1 = metadataTask1.getResult().getCreationTimeMillis();
                                long time2 = metadataTask2.getResult().getCreationTimeMillis();

                                // Compare based on creation time
                                return Long.compare(time1, time2);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return 0;
                            }
                        });

                        for (StorageReference storageReference : sortedItems) {
                            TeacherVideo video = new TeacherVideo();
                            video.setTitle(storageReference.getName());
                            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String url = "https://" + task.getResult().getEncodedAuthority() +
                                            task.getResult().getEncodedPath() + "?alt=media&token=" +
                                            task.getResult().getQueryParameters("token").get(0);
                                    video.setUrl(url);
                                    arrayList.add(video);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TeacherVideoReviewer.this, "Failed To Retrieve Videos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDeleteConfirmationDialog(TeacherVideo video) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Delete Video");
        builder.setMessage("Are you sure you want to delete this video?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteVideo(video);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing, dismiss dialog
            }
        });
        builder.show();
    }

    private void deleteVideo(TeacherVideo teacherVideo) {
        // Get a reference to the video in Firebase Storage
        StorageReference videoRef = FirebaseStorage.getInstance().getReference().child("video").child(teacherVideo.getTitle());

        // Delete the video from Firebase Storage
        videoRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Video deleted successfully
                        // Remove the video from the list and update the RecyclerView
                        arrayList.remove(teacherVideo);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(TeacherVideoReviewer.this, "Video deleted", Toast.LENGTH_SHORT).show();
                        updateVideoCounter();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to delete the video
                        Toast.makeText(TeacherVideoReviewer.this, "Failed to delete video", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateVideoCounter() {
        int lastVideoNumber = 0;

        // Find the highest video number among existing videos
        for (TeacherVideo video : arrayList) {
            String title = video.getTitle();
            if (title.startsWith("Quiz_Reviewer_Number_")) {
                try {
                    int number = Integer.parseInt(title.substring("Quiz_Reviewer_Number_".length()));
                    if (number > lastVideoNumber) {
                        lastVideoNumber = number;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace(); // Handle invalid number format if necessary
                }
            }
        }

        // Renumber the videos sequentially starting from the next number after the last video number
        for (int i = 0; i < arrayList.size(); i++) {
            TeacherVideo video = arrayList.get(i);
            if (video.getTitle().startsWith("Quiz_Reviewer_Number_")) {
                // If the title already has a number, keep it unchanged
                continue;
            }
            video.setTitle("Quiz_Reviewer_Number_" + (++lastVideoNumber));
        }

        // Save the updated videoCounter value to SharedPreferences
        SharedPreferences preferences = getSharedPreferences("VideoCounterPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("videoCounter", lastVideoNumber);
        editor.apply();
    }


    @Override
    public void onBackPressed() {
        // Do nothing or add a message if you want
        //Toast.makeText(TeacherVideoReviewer.this, "Choose back", Toast.LENGTH_SHORT).show();
    }
}
