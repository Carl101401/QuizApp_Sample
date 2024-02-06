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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentLogin extends AppCompatActivity {

    EditText username, password;
    Button btnLogin;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        FirebaseApp.initializeApp(this);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.buttonLogin);
        firebaseAuth = FirebaseAuth.getInstance();

        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                if (user.equals("") || pass.equals("")) {
                    Toast.makeText(StudentLogin.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    checkStudentCredentials(user, pass);
                }
            }
        });
    }

    private void checkStudentCredentials(String user, String pass) {
        firebaseAuth.signInWithEmailAndPassword(user + "@yourdomain.com", pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Check if the authenticated user exists in the "Students" collection
                        checkIfUserExistsInStudentsCollection(user);
                    } else {
                        Toast.makeText(StudentLogin.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkIfUserExistsInStudentsCollection(String username) {
        FirebaseFirestore.getInstance().collection("Students")
                .document(username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            // User exists in the "Students" collection
                            Toast.makeText(StudentLogin.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), PlayQuiz.class);
                            startActivity(intent);
                            finish(); // Close the current activity to prevent going back to login
                        } else {
                            // User doesn't exist in the "Students" collection
                            Toast.makeText(StudentLogin.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            // You might want to sign out the user or handle this case accordingly
                        }
                    } else {
                        Toast.makeText(StudentLogin.this, "Error checking user in Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onBackPressed() {
        // Do nothing or add a message if you want
        // Toast.makeText(Students.this, "Choose back", Toast.LENGTH_SHORT).show();
    }
}