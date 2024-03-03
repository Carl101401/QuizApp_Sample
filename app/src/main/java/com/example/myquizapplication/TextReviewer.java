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
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String text = snapshot.getValue(String.class);
                    textList.add(text);
                }
                textAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
                Toast.makeText(TextReviewer.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
        textAdapter.setOnItemClickListener(new TextViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                viewTextDialog(position);
            }
        });
    }
    private void viewTextDialog(int position) {
        if (position >= 0 && position < textList.size()) {
            String text = textList.get(position);
            Dialog dialog = new Dialog(TextReviewer.this, R.style.TransparentDialog);
            dialog.setContentView(R.layout.dialog_view_text);
            TextView dialogTextContent = dialog.findViewById(R.id.dialogTextContent);
            Button dialogCloseButton = dialog.findViewById(R.id.dialogCloseButton);
            dialogTextContent.setText(text);
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
    }
}
