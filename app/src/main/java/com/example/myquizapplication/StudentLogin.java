package com.example.myquizapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class StudentLogin extends AppCompatActivity {

    EditText username, password;
    Button btnLogin;
    DBstudent DB;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.buttonLogin);
        DB = new DBstudent(this);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();




                if (user.equals("")||pass.equals(""))
                    Toast.makeText(StudentLogin.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                else {
                    Boolean checkuserpass = DB.checkUserPass(user,pass);
                    if (checkuserpass==true) {
                        Toast.makeText(StudentLogin.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),PlayQuiz.class);
                        startActivity(intent);
                    } else{
                        Toast.makeText(StudentLogin.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });


    }
    public void onBackPressed() {
        // Do nothing or add a message if you want
        //Toast.makeText(Reviewer.this, "Choose back", Toast.LENGTH_SHORT).show();
    }
}