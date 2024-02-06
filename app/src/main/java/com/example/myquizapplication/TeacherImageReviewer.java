package com.example.myquizapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
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
public class TeacherImageReviewer extends AppCompatActivity {

    private ArrayList<TeacherImage> arrayList;
    private TeacherImageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_image_reviewer);

        FirebaseApp.initializeApp(this);

        RecyclerView recyclerView = findViewById(R.id.teacherImageRecycler);
        FirebaseStorage.getInstance().getReference().child("images").listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        arrayList = new ArrayList<>();
                        adapter = new TeacherImageAdapter(TeacherImageReviewer.this, arrayList);
                        adapter.setOnItemClickListener(new TeacherImageAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(TeacherImage teacherImage) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.parse(teacherImage.getUrl()), "image/*");
                                startActivity(intent);
                            }
                        });
                        adapter.setOnItemLongClickListener(new TeacherImageAdapter.OnItemLongClickListener() {
                            @Override
                            public void onLongClick(TeacherImage teacherImage) {
                                // Handle image long click
                                showDeleteDialog(teacherImage);
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
                            TeacherImage teacherImage = new TeacherImage();
                            teacherImage.setTitle(storageReference.getName());
                            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String url = "https://" + task.getResult().getEncodedAuthority() +
                                            task.getResult().getEncodedPath() + "?alt=media&token=" +
                                            task.getResult().getQueryParameters("token").get(0);
                                    teacherImage.setUrl(url);
                                    arrayList.add(teacherImage);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TeacherImageReviewer.this, "Failed To Retrieve Images", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showDeleteDialog(TeacherImage teacherImage) {
        // Implement dialog for deletion here
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Image");
        builder.setMessage("Are you sure you want to delete this image?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call method to delete the image
                deleteImage(teacherImage);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    private void deleteImage(TeacherImage teacherImage) {
        // Get a reference to the image in Firebase Storage
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("images").child(teacherImage.getTitle());

        // Delete the image from Firebase Storage
        imageRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Image deleted successfully
                        // Remove the image from the list and update the RecyclerView
                        arrayList.remove(teacherImage);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(TeacherImageReviewer.this, "Image deleted", Toast.LENGTH_SHORT).show();
                        updateImageCounter();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to delete the image
                        Toast.makeText(TeacherImageReviewer.this, "Failed to delete image", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateImageCounter() {
        int lastImageNumber = 0;

        // Find the highest image number among existing images
        for (TeacherImage image : arrayList) {
            String title = image.getTitle();
            if (title.startsWith("Quiz_Reviewer_Number_")) {
                try {
                    int number = Integer.parseInt(title.substring("Quiz_Reviewer_Number_".length()));
                    if (number > lastImageNumber) {
                        lastImageNumber = number;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace(); // Handle invalid number format if necessary
                }
            }
        }

        // Renumber the images sequentially starting from the next number after the last image number
        for (int i = 0; i < arrayList.size(); i++) {
            TeacherImage image = arrayList.get(i);
            if (image.getTitle().startsWith("Quiz_Reviewer_Number_")) {
                // If the title already has a number, keep it unchanged
                continue;
            }
            image.setTitle("Quiz_Reviewer_Number_" + (++lastImageNumber));
        }

        // Save the updated imageCounter value to SharedPreferences
        SharedPreferences preferences = getSharedPreferences("ImageCounterPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("imageCounter", lastImageNumber);
        editor.apply();
    }




    public void onBackPressed() {
        // Do nothing or add a message if you want
        // Toast.makeText(TeacherImageReviewer.this, "Choose back", Toast.LENGTH_SHORT).show();
    }
}
