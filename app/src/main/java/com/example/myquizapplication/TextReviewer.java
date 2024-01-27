package com.example.myquizapplication;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class TextReviewer extends AppCompatActivity {

    private RecyclerView textRecycler;
    private TextView textViewTextTitle;
    private List<String> textList;
    private TextAdapter textAdapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_reviewer);

        textRecycler = findViewById(R.id.textRecycler);
        textViewTextTitle = findViewById(R.id.textViewTextTitle);

        textList = new ArrayList<>();
        textAdapter = new TextAdapter(this, textList);
        textRecycler.setLayoutManager(new LinearLayoutManager(this));
        textRecycler.setAdapter(textAdapter);

        textViewTextTitle.setText("Text Reviewer");

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Reviewer").child("Text");

        // Listen for changes in the data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the existing list
                textList.clear();

                // Iterate through each data snapshot and add text to the list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String text = snapshot.getValue(String.class);
                    textList.add(text);
                }

                // Notify the adapter that data has changed
                textAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
                Toast.makeText(TextReviewer.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });

        // Set item click listener for delete functionality
        textAdapter.setOnItemClickListener(new TextAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                deleteText(position);
            }
        });
    }

    private void deleteText(int position) {
        // Get the selected text from the list
        String textToDelete = textList.get(position);

        // Remove the item from the RecyclerView
        textAdapter.deleteItem(position);

        // Remove the item from Firebase Database
        databaseReference.orderByValue().equalTo(textToDelete).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
                Toast.makeText(TextReviewer.this, "Failed to delete data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Do nothing or add a message if you want
        //Toast.makeText(Reviewer.this, "Choose back", Toast.LENGTH_SHORT).show();
    }
}
