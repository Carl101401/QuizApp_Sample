package com.example.myquizapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
                            storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                @Override
                                public void onSuccess(StorageMetadata storageMetadata) {
                                    String imageName = storageMetadata.getName();
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();
                                            TeacherImage teacherImage = new TeacherImage(imageName, imageUrl);
                                            arrayList.add(teacherImage);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle failure to get download URL
                                            Log.e("TeacherImageReviewer", "Failed to get download URL for image: " + imageName, e);
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure to get metadata
                                    Log.e("TeacherImageReviewer", "Failed to get metadata for image", e);
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





    public void onBackPressed() {
        // Do nothing or add a message if you want
        // Toast.makeText(TeacherImageReviewer.this, "Choose back", Toast.LENGTH_SHORT).show();
    }
}
