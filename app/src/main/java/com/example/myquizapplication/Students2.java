package com.example.myquizapplication;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.myquizapplication.Adapters.CategoryAdapter2;
import com.example.myquizapplication.Models.CategoryModel2;
import com.example.myquizapplication.databinding.ActivityStudents2Binding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
public class Students2 extends AppCompatActivity {
    ActivityStudents2Binding binding;
    FirebaseDatabase database;
    ArrayList<CategoryModel2> list;
    CategoryAdapter2 adapter;
    ProgressDialog progressDialog;
    Dialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudents2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        database = FirebaseDatabase.getInstance();
        list = new ArrayList<>();
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        if (loadingDialog.getWindow()!=null){
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
        }
        loadingDialog.show();
        ImageView setBackArrow = findViewById(R.id.imageCategorySetBack);
        setBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate to another activity (replace YourTargetActivity.class with your target activity)
                Intent intent = new Intent(Students2.this, QuizReviewer.class);
                startActivity(intent);
            }
        });
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.recyCategory.setLayoutManager(layoutManager);
        adapter = new CategoryAdapter2(this,list);
        binding.recyCategory.setAdapter(adapter);
        database.getReference().child("categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        list.add(new CategoryModel2(
                                dataSnapshot.child("categoryName").getValue().toString(),
                                dataSnapshot.child("categoryImage").getValue().toString(),
                                dataSnapshot.getKey(),
                                Integer.parseInt(dataSnapshot.child("setNum").getValue().toString())
                        ));
                    }
                    adapter.notifyDataSetChanged();
                    loadingDialog.dismiss();
                }
                else{
                    Toast.makeText(Students2.this, "Category Not Exist", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Students2.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Inside Students2 activity

    // Add this method to fetch categories and listen for changes
    private void fetchCategoriesAndListenForChanges() {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String categoryId = snapshot.getKey();
                    String categoryName = snapshot.child("categoryName").getValue(String.class);
                    String categoryImage = snapshot.child("categoryImage").getValue(String.class);
                    int setNum = snapshot.child("setNum").getValue(Integer.class);
                    boolean locked = snapshot.child("locked").getValue(Boolean.class);

                    // Update UI based on lock status
                    if (locked) {
                        // Category is locked
                        // Show a message to unlock the category to proceed
                        Toast.makeText(Students2.this, "Category '" + categoryName + "' is locked. Please unlock to proceed.", Toast.LENGTH_SHORT).show();
                        // Optionally, you can disable any UI elements related to this category
                        // For example, if there's a button to select this category, you can disable it
                    } else {
                        // Category is unlocked
                        // Add category to list
                        list.add(new CategoryModel2(categoryName, categoryImage, categoryId, setNum));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });
    }

    public void onBackPressed() {
    }
}