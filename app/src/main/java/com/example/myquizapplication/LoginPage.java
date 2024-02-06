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

public class LoginPage extends AppCompatActivity {

    EditText username, password;
    Button btnLogin, btnCreateAccount;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        FirebaseApp.initializeApp(this);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.buttonLogin);
        btnCreateAccount = findViewById(R.id.buttonCreateAccount);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                if (user.equals("") || pass.equals("")) {
                    Toast.makeText(LoginPage.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                } else {
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
        String customEmail = user + "@yourdomain.com";

        firebaseAuth.signInWithEmailAndPassword(customEmail, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Check if the authenticated user exists in the "Users" collection
                        checkIfUserExistsInUsersCollection(user);
                    } else {
                        Toast.makeText(LoginPage.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkIfUserExistsInUsersCollection(String username) {
        FirebaseFirestore.getInstance().collection("users")
                .document(username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            // User exists in the "Users" collection
                            Toast.makeText(LoginPage.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Teachers.class);
                            startActivity(intent);
                            finish(); // Close the current activity to prevent going back to login
                        } else {
                            // User doesn't exist in the "Users" collection
                            Toast.makeText(LoginPage.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            // You might want to sign out the user or handle this case accordingly
                        }
                    } else {
                        Toast.makeText(LoginPage.this, "Error checking user in Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onBackPressed() {
        // Do nothing or add a message if you want
        // Toast.makeText(Students.this, "Choose back", Toast.LENGTH_SHORT).show();
    }
}
