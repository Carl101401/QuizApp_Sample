package com.example.myquizapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;  // Import this
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginPage extends AppCompatActivity {

    EditText username, password;
    Button btnLogin, btnCreateAccount;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.buttonLogin);
        btnCreateAccount = findViewById(R.id.buttonCreateAccount);
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                if (user.equals("") || pass.equals("")) {
                    Toast.makeText(LoginPage.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Check the username and password in the "users" collection
                    checkUserCredentials(user, pass);
                }
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkUserCredentials(String user, String pass) {
        db.collection("users")
                .whereEqualTo("username", user)
                .whereEqualTo("password", pass)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Authentication successful
                            Toast.makeText(LoginPage.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Teachers.class);
                            startActivity(intent);
                            finish(); // Close the current activity to prevent going back to login
                        } else {
                            // Invalid Credentials
                            Toast.makeText(LoginPage.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle task failure
                        Toast.makeText(LoginPage.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
