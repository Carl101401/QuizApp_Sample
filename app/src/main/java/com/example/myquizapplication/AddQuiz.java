package com.example.myquizapplication;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.myquizapplication.Adapters.CategoryAdapter;
import com.example.myquizapplication.Models.CategoryModel;
import com.example.myquizapplication.databinding.ActivityAddQuizBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddQuiz extends AppCompatActivity {

    ActivityAddQuizBinding binding;

    FirebaseDatabase database;
    FirebaseStorage storage;
    CircleImageView categoryImage;
    ImageView addCategory;
    EditText inputCategoryName;
    Button UploadCategory;
    Dialog dialog;
    View fetchImage;
    Uri ImageUri;
    int i = 0;
    ArrayList<CategoryModel>list;
    CategoryAdapter adapter;
    ProgressDialog progressDialog;
    private static final String STATE_IMAGE_URI = "ImageUri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        list = new ArrayList<>();

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_add_category_dialog);


        if (savedInstanceState != null) {
            // Restore the image URI if available
            ImageUri = savedInstanceState.getParcelable(STATE_IMAGE_URI);
            if (ImageUri != null) {
                categoryImage.setImageURI(ImageUri);
            }
        }


        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please Wait");

        UploadCategory = dialog.findViewById(R.id.btnUpload);
        inputCategoryName = dialog.findViewById(R.id.InputCategoryName);
        categoryImage = dialog.findViewById(R.id.quizImage);
        fetchImage = dialog.findViewById(R.id.fetchImage);
        addCategory = dialog.findViewById(R.id.addCategory);


        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.recyCategory.setLayoutManager(layoutManager);
        adapter = new CategoryAdapter(this, list);
        binding.recyCategory.setAdapter(adapter);

        database.getReference().child("categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if (snapshot.exists()){

                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        
                        list.add(new CategoryModel(
                           dataSnapshot.child("categoryName").getValue().toString(),
                           dataSnapshot.child("categoryImage").getValue().toString(),
                           dataSnapshot.getKey(),
                           Integer.parseInt(dataSnapshot.child("setNum").getValue().toString())

                        ));

                    }
                    adapter.notifyDataSetChanged();

                }
                else{
                    Toast.makeText(AddQuiz.this, "Category Not Exist", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(AddQuiz.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



        binding.addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });


        fetchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);

            }
        });

        UploadCategory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final StorageReference reference = storage.getReference().child("category").child(new Date().getTime() + "");

                String name = inputCategoryName.getText().toString();

                if (ImageUri == null) {
                    Toast.makeText(AddQuiz.this, "Please Upload Category Image", Toast.LENGTH_SHORT).show();
                } else if (name.isEmpty()) {
                    inputCategoryName.setError("Enter Category Name");
                } else {
                    progressDialog.show();
                    uploadData();
                }

            }


            private void UploadCategory() {
                final String categoryName = inputCategoryName.getText().toString().trim();

                // Check if the category name already exists
                boolean categoryExists = checkIfCategoryExists(categoryName);

                if (categoryExists) {
                    // Display a message or handle accordingly (category already exists)
                    Toast.makeText(AddQuiz.this, "Category already exists", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }
                final StorageReference reference = storage.getReference().child("category")
                        .child(new Date().getTime() + "");
                reference.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                CategoryModel categoryModel = new CategoryModel();
                                categoryModel.setCategoryName(inputCategoryName.getText().toString());
                                categoryModel.setSetNum(0);
                                categoryModel.setCategoryImage(uri.toString());

                                int categoryCount = list.size();
                                int newIndex = categoryCount;
                                list.add(categoryModel);
                                adapter.notifyItemInserted(newIndex);

                                Picasso.get().load(uri.toString()).into(categoryImage);

                                // Create or get the unique ID for the category
                                String categoryId = database.getReference().child("categories").push().getKey();
                                categoryModel.setKey(categoryId); // Set the unique ID

                                database.getReference().child("categories").child(categoryId)
                                        .setValue(categoryModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(AddQuiz.this, "Data Uploaded", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AddQuiz.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        });

                    }
                });
            }
        });
    }

    private void uploadData() {

        final StorageReference reference = storage.getReference().child("category").child(new Date().getTime()+"");
        reference.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        CategoryModel categoryModel = new CategoryModel();
                        categoryModel.setCategoryName(inputCategoryName.getText().toString());
                        categoryModel.setSetNum(0);
                        categoryModel.setCategoryImage(uri.toString());

                        database.getReference().child("categories").child("category"+i++)
                                .setValue(categoryModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AddQuiz.this, "Data Uploaded", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddQuiz.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1){

            if (data != null ){

                ImageUri = data.getData();
                categoryImage.setImageURI(ImageUri);

            }

        }

    }
    private boolean checkIfCategoryExists(String categoryName) {
        for (CategoryModel model : list) {
            if (model.getCategoryName().equalsIgnoreCase(categoryName)) {
                return true; // Category already exists
            }
        }
        return false; // Category does not exist
    }
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the image URI if it's not null
        if (ImageUri != null) {
            outState.putParcelable(STATE_IMAGE_URI, ImageUri);
        }
    }

}



