package com.example.myquizapplication;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private TextViewAdapter textAdapter; // Updated to use TextViewAdapter
    private TextView textViewTextTitle;
    private List<String> textList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_reviewer);

        textRecycler = findViewById(R.id.textRecycler);
        textViewTextTitle = findViewById(R.id.textViewTextTitle);

        textList = new ArrayList<>();
        textAdapter = new TextViewAdapter(this, textList); // Updated to use TextViewAdapter
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

        // Set item click listener for TextViewAdapter
        textAdapter.setOnItemClickListener(new TextViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                viewTextDialog(position);
            }
        });
    }

    private void viewTextDialog(int position) {
        // Ensure the position is within bounds
        if (position >= 0 && position < textList.size()) {
            // Get the text corresponding to the clicked position
            String text = textList.get(position);

            // Create a dialog to display the text content
            Dialog dialog = new Dialog(TextReviewer.this, R.style.TransparentDialog);
            dialog.setContentView(R.layout.dialog_view_text);

            TextView dialogTextContent = dialog.findViewById(R.id.dialogTextContent);
            Button dialogCloseButton = dialog.findViewById(R.id.dialogCloseButton);

            // Set the text content in the dialog
            dialogTextContent.setText(text);

            // Set a click listener for the close button
            dialogCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; // R.style.DialogAnimation should be defined in your styles.xml

            // Show the dialog
            dialog.show();
        } else {
            // Handle an invalid position (out of bounds)
            Toast.makeText(TextReviewer.this, "Invalid position", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        // Do nothing or add a message if you want
        // Toast.makeText(Reviewer.this, "Choose back", Toast.LENGTH_SHORT).show();
    }
}
