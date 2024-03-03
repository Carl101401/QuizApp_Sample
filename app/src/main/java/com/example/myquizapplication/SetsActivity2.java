package com.example.myquizapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myquizapplication.Adapters.GrideAdapter2;
import com.example.myquizapplication.databinding.ActivitySets2Binding;
import com.google.firebase.database.FirebaseDatabase;
public class SetsActivity2 extends AppCompatActivity {
    ActivitySets2Binding binding;
    FirebaseDatabase database;
    GrideAdapter2 adapter;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySets2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        database = FirebaseDatabase.getInstance();
        key = getIntent().getStringExtra("key");
        adapter = new GrideAdapter2(getIntent().getIntExtra("sets", 0),
                getIntent().getStringExtra("category"));
        binding.gridView.setAdapter(adapter);
        ImageView setBackArrow = findViewById(R.id.imageStudentSetBack);
        setBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate to another activity (replace YourTargetActivity.class with your target activity)
                Intent intent = new Intent(SetsActivity2.this, Students2.class);
                startActivity(intent);
            }
        });
    }
    public void onBackPressed() {
        // Do nothing or add a message if you want
        //Toast.makeText(Reviewer.this, "Choose back", Toast.LENGTH_SHORT).show();
    }
}