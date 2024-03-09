package com.example.myquizapplication;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.myquizapplication.Adapters.CategoryAdapter;
import com.example.myquizapplication.Models.CategoryModel;
import com.example.myquizapplication.databinding.ActivityAddQuizBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

        fetchCategories();


        ImageView imageView = findViewById(R.id.Delete);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteCategoryDialog();
            }
        });

        ImageView setBackArrow = findViewById(R.id.imageAddQuizBack);
        setBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate to another activity (replace YourTargetActivity.class with your target activity)
                Intent intent = new Intent(AddQuiz.this, Teachers.class);

                // Optionally, you can add flags or extras to the intent
                // intent.putExtra("key", "value");

                // Start the new activity
                startActivity(intent);
            }
        });


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
        ImageView LockAndUnlockImage = findViewById(R.id.LockAndUnlock);
        LockAndUnlockImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChoiceDialog();
            }
        });


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

        });
    }
    private void showImageChoiceDialog() {
        // Call fetchSets directly when the image is clicked
        fetchSets();
    }
    private void fetchCategories() {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String categoryId = categorySnapshot.getKey();
                    String categoryName = categorySnapshot.child("categoryName").getValue(String.class);
                    Boolean locked = categorySnapshot.child("locked").getValue(Boolean.class);

                    if (locked != null && locked.booleanValue()) {
                        // Category is locked, show a message
                        Toast.makeText(AddQuiz.this, "Category is locked: " + categoryName, Toast.LENGTH_SHORT).show();

                        // You can also display a dialog or message to prompt the user to unlock the category to proceed
                        // For example:
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddQuiz.this);
                        builder.setTitle("Category Locked");
                        builder.setMessage("This category is locked. Please unlock it to proceed.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Optionally handle the click event
                                dialog.dismiss(); // Dismiss the dialog
                            }
                        });
                        builder.show();
                    } else {
                        // Category is unlocked, proceed with displaying the category or performing any related actions
                        // Implement your logic here
                        // For example, you can show the category details or allow the user to select the category
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });
    }
    private void showDeleteCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        builder.setTitle("Delete Category");
        builder.setMessage("Enter the category name to delete:");

        // Create an EditText for user input
        final EditText input = new EditText(this);
        input.setHint("Category Name");
        input.setHintTextColor(Color.BLACK);
        input.setTextColor(Color.BLACK);
        input.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        // Set up the buttons for dialog
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String categoryName = input.getText().toString().trim();
                if (!categoryName.isEmpty()) {
                    deleteCategory(categoryName);
                } else {
                    Toast.makeText(AddQuiz.this, "Please enter a category name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void deleteCategory(String categoryName) {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");

        // Query to find the category with the matching name
        Query categoryQuery = categoriesRef.orderByChild("categoryName").equalTo(categoryName);

        categoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        String categoryKey = categorySnapshot.getKey();

                        // Delete the category
                        categorySnapshot.getRef().removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(AddQuiz.this, "Category deleted successfully", Toast.LENGTH_SHORT).show();
                                        // Also delete associated sets
                                        deleteSetsForCategory(categoryKey);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddQuiz.this, "Failed to delete category: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(AddQuiz.this, "Category not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddQuiz.this, "Failed to delete category: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteSetsForCategory(String categoryKey) {
        DatabaseReference setsRef = FirebaseDatabase.getInstance().getReference().child("sets");

        // Query to find sets with the matching category key
        Query setsQuery = setsRef.orderByChild("category").equalTo(categoryKey);

        setsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot setSnapshot : dataSnapshot.getChildren()) {
                        // Delete each set
                        setSnapshot.getRef().removeValue()
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddQuiz.this, "Failed to delete set: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddQuiz.this, "Failed to delete sets: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

                        Toast.makeText(AddQuiz.this, "Image Uploaded", Toast.LENGTH_SHORT).show();

                        CategoryModel categoryModel = new CategoryModel();
                        categoryModel.setCategoryName(inputCategoryName.getText().toString());
                        categoryModel.setSetNum(0);
                        categoryModel.setCategoryImage(uri.toString());

                        database.getReference().child("categories")
                                .push()
                                .setValue(categoryModel)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
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
    private void fetchSets() {
        DatabaseReference setsRef = FirebaseDatabase.getInstance().getReference().child("Sets");

        setsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Create a dialog builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddQuiz.this);
                    builder.setTitle("Lessons");

                    // Create a StringBuilder to hold the category names
                    StringBuilder categoryNames = new StringBuilder();

                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        String categoryName = categorySnapshot.getKey();
                        categoryNames.append(categoryName).append("\n");
                    }

                    // Set the categoryNames to the dialog message
                    builder.setMessage(categoryNames.toString());

                    // Add a button to dismiss the dialog
                    builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    // Show the dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    // Handle the case where no sets exist
                    Toast.makeText(AddQuiz.this, "No sets found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
                Toast.makeText(AddQuiz.this, "Failed to fetch sets: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        // Do nothing or add a message if you want
        //Toast.makeText(Reviewer.this, "Choose back", Toast.LENGTH_SHORT).show();
    }


}



