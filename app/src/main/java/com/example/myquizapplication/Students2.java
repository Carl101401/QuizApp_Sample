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

                // Optionally, you can add flags or extras to the intent
                // intent.putExtra("key", "value");

                // Start the new activity
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
    public void onBackPressed() {
        // Do nothing or add a message if you want
        //Toast.makeText(Reviewer.this, "Choose back", Toast.LENGTH_SHORT).show();
    }
}