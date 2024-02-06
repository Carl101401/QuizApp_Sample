package com.example.myquizapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class NewActivity extends AppCompatActivity {

    // UI elements
    EditText createusername, createpassword, retypepassword;
    Button Registerlogin;

    // Firebase Authentication and Firestore instances
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        // Initialize UI elements
        createusername = findViewById(R.id.createusername);
        createpassword = findViewById(R.id.createpassword);
        retypepassword = findViewById(R.id.retypepassword);
        Registerlogin = findViewById(R.id.RegisterLogin);

        // Initialize Firebase Authentication and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Set click listener for the Register button
        Registerlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input
                String user = createusername.getText().toString();
                String pass = createpassword.getText().toString();
                String retype = retypepassword.getText().toString();

                // Check if fields are empty
                if (user.equals("") || pass.equals("") || retype.equals("")) {
                    Toast.makeText(NewActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if passwords match
                    if (pass.equals(retype)) {
                        // Check if password meets minimum length requirement
                        if (pass.length() >= 6) {
                            // Use a custom email pattern for authentication
                            String customEmail = user + "@yourdomain.com";

                            // Create a user with email and password
                            firebaseAuth.createUserWithEmailAndPassword(customEmail, pass)
                                    .addOnCompleteListener(NewActivity.this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // User registration successful, proceed with Firestore data storage

                                                // Store username and password in the "users" collection
                                                Map<String, Object> userMap = new HashMap<>();
                                                userMap.put("username", user);
                                                userMap.put("password", pass);

                                                db.collection("users")
                                                        .document(user)
                                                        .set(userMap)
                                                        .addOnSuccessListener(aVoid -> {
                                                            // Registration successful
                                                            Toast.makeText(NewActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                                                            // Clear EditText fields
                                                            createusername.getText().clear();
                                                            createpassword.getText().clear();
                                                            retypepassword.getText().clear();

                                                            // Navigate back to the LoginPage
                                                            Intent loginIntent = new Intent(getApplicationContext(), LoginPage.class);
                                                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(loginIntent);
                                                            finish(); // Close the current activity to prevent going back to registration
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            // Registration failed
                                                            Toast.makeText(NewActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                                            Log.e("NewActivity", "Registration failed", e);
                                                        });
                                            } else {
                                                // Registration failed
                                                Toast.makeText(NewActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();

                                                // Log the error
                                                if (task.getException() instanceof FirebaseAuthException) {
                                                    FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                                    Log.e("NewActivity", "FirebaseAuthException: " + e.getErrorCode() + ", " + e.getMessage());
                                                }
                                            }
                                        }
                                    });
                        } else {
                            // Password length is less than 6 characters
                            Toast.makeText(NewActivity.this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Passwords do not match
                        Toast.makeText(NewActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
