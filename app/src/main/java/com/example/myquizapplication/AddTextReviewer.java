package com.example.myquizapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTextReviewer extends AppCompatActivity {

    LinearProgressIndicator progressIndicator;
    EditText textInput;
    MaterialButton uploadText;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text_reviewer);

        progressIndicator = findViewById(R.id.process2);
        textInput = findViewById(R.id.textInput);
        uploadText = findViewById(R.id.uploadText);

        FirebaseApp.initializeApp(this);
        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Set the button visible by default
        uploadText.setVisibility(View.VISIBLE);

        com.google.android.material.button.MaterialButton viewTextButton = findViewById(R.id.ViewTextButton);
        viewTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Define the Intent to start a new activity (replace NewActivity.class with your desired activity)
                Intent intent = new Intent(AddTextReviewer.this, TeacherTextReviewer.class);

                // Add any extras or data you want to pass to the new activity
                // intent.putExtra("key", "value");

                // Start the new activity
                startActivity(intent);
            }
        });
        textInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Enable or disable the button based on whether there is text
                uploadText.setEnabled(charSequence.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed
            }
        });



        uploadText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = textInput.getText().toString().trim();
                if (!text.isEmpty()) {
                    uploadText(text);
                } else {
                    Toast.makeText(AddTextReviewer.this, "Please enter text", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadText(String text) {

        String key = databaseReference.push().getKey();
        databaseReference.child("Reviewer").child("Text").child(key).setValue(text)
                .addOnCompleteListener(task -> {
                    progressIndicator.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(AddTextReviewer.this, "Text uploaded successfully", Toast.LENGTH_SHORT).show();
                        textInput.setText(""); // Clear the EditText
                    } else {
                        Toast.makeText(AddTextReviewer.this, "Failed to upload text", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void onBackPressed() {
        // Do nothing or add a message if you want
        //Toast.makeText(Reviewer.this, "Choose back", Toast.LENGTH_SHORT).show();
    }

}
