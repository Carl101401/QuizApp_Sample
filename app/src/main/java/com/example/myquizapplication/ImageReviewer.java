package com.example.myquizapplication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
public class ImageReviewer extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_reviewer);
        FirebaseApp.initializeApp(this);
        RecyclerView recyclerView = findViewById(R.id.imageRecycler);
        FirebaseStorage.getInstance().getReference().child("images").listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        ArrayList<Image> arrayList = new ArrayList<>();
                        ImageAdapter adapter = new ImageAdapter(ImageReviewer.this, arrayList);
                        adapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(Image image) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.parse(image.getUrl()), "image/*");
                                startActivity(intent);
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
                                            Image image = new Image(imageName, imageUrl);
                                            arrayList.add(image);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle failure to get download URL
                                            Log.e("ImageReviewer", "Failed to get download URL for image: " + imageName, e);
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure to get metadata
                                    Log.e("ImageReviewer", "Failed to get metadata for image", e);
                                }
                            });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ImageReviewer.this, "Failed To Retrieve Images", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void onBackPressed() {
    }
}
