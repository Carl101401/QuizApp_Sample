package com.example.myquizapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentLogin extends AppCompatActivity {

    EditText username, password;
    Button btnLogin;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.buttonLogin);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                if (user.equals("") || pass.equals("")) {
                    Toast.makeText(StudentLogin.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Check the username and password in the "Students" collection
                    checkStudentCredentials(user, pass);
                }
            }
        });
    }

    private void checkStudentCredentials(String user, String pass) {
        db.collection("Students")
                .whereEqualTo("username", user)
                .whereEqualTo("password", pass)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Authentication successful
                            Toast.makeText(StudentLogin.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), PlayQuiz.class);
                            startActivity(intent);
                            finish(); // Close the current activity to prevent going back to login
                        } else {
                            // Invalid Credentials
                            Toast.makeText(StudentLogin.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle task failure
                        Toast.makeText(StudentLogin.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
