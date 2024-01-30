package com.example.myquizapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewActivity extends AppCompatActivity {

    EditText createusername, createpassword, retypepassword;
    Button Registerlogin;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        createusername = findViewById(R.id.createusername);
        createpassword = findViewById(R.id.createpassword);
        retypepassword = findViewById(R.id.retypepassword);
        Registerlogin = findViewById(R.id.RegisterLogin);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // ...

        // ...

        Registerlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = createusername.getText().toString();
                String pass = createpassword.getText().toString();
                String retype = retypepassword.getText().toString();

                if (user.equals("") || pass.equals("") || retype.equals("")) {
                    Toast.makeText(NewActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (pass.equals(retype)) {
                        // Store username and password in the "users" collection
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("username", user);
                        userMap.put("password", pass);

                        db.collection("users")
                                .document(user)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult().exists()) {
                                        // User already exists, show message
                                        Toast.makeText(NewActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // User does not exist, proceed with registration
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
                                                });
                                    }
                                });
                    } else {
                        Toast.makeText(NewActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

// ...



    }
}
