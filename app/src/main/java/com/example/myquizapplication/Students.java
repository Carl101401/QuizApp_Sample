package com.example.myquizapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Students extends AppCompatActivity {
    FirebaseFirestore firestore;
    EditText User, Pass, Fname, Lname, Ysection;
    Button Add, View, Edit;
    int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        firestore = FirebaseFirestore.getInstance();

        findId();
        insertData();
        editData();
    }

    private void editData() {
        if (getIntent().getBundleExtra("userdata") != null) {
            Bundle bundle = getIntent().getBundleExtra("userdata");
            id = bundle.getInt("id");
            User.setText(bundle.getString("user"));
            Pass.setText(bundle.getString("pass"));
            Fname.setText(bundle.getString("fname"));
            Lname.setText(bundle.getString("lname"));
            Ysection.setText(bundle.getString("ysection"));
            Edit.setVisibility(View.VISIBLE);
            Add.setVisibility(View.GONE);
        }
    }

    private void insertData() {
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = User.getText().toString();
                String password = Pass.getText().toString();
                String firstname = Fname.getText().toString();
                String lastname = Lname.getText().toString();
                String yearSection = Ysection.getText().toString();

                if (username.equals("") || password.equals("") || firstname.equals("") || lastname.equals("") || yearSection.equals("")) {
                    Toast.makeText(Students.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> studentMap = new HashMap<>();
                    studentMap.put("username", username);
                    studentMap.put("password", password);
                    studentMap.put("firstname", firstname);
                    studentMap.put("lastname", lastname);
                    studentMap.put("yearSection", yearSection);

                    firestore.collection("Students")
                            .document(firstname)
                            .set(studentMap)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(Students.this, "Successfully Inserted Data", Toast.LENGTH_SHORT).show();
                                clearInputFields();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(Students.this, "Error Inserting Data", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });

        View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Students.this, ViewStudent.class);
                startActivity(intent);
            }
        });

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = User.getText().toString();
                String password = Pass.getText().toString();
                String firstname = Fname.getText().toString();
                String lastname = Lname.getText().toString();
                String yearSection = Ysection.getText().toString();

                if (username.equals("") || password.equals("") || firstname.equals("") || lastname.equals("") || yearSection.equals("")) {
                    Toast.makeText(Students.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> studentMap = new HashMap<>();
                    studentMap.put("username", username);
                    studentMap.put("password", password);
                    studentMap.put("firstname", firstname);
                    studentMap.put("lastname", lastname);
                    studentMap.put("yearSection", yearSection);

                    DocumentReference docRef = firestore.collection("Students").document(String.valueOf(id));
                    docRef.set(studentMap)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(Students.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                                Add.setVisibility(View.VISIBLE);
                                Edit.setVisibility(View.GONE);
                                clearInputFields();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(Students.this, "Error Updating Data", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });
    }

    private void findId() {
        User = findViewById(R.id.name);
        Pass = findViewById(R.id.pass);
        Fname = findViewById(R.id.firstname);
        Lname = findViewById(R.id.lastname);
        Ysection = findViewById(R.id.ysection);

        Add = findViewById(R.id.addbutton);
        View = findViewById(R.id.viewbutton);
        Edit = findViewById(R.id.editbutton);
    }

    private void clearInputFields() {
        User.setText("");
        Pass.setText("");
        Fname.setText("");
        Lname.setText("");
        Ysection.setText("");
    }

    @Override
    public void onBackPressed() {
        // Do nothing or add a message if you want
        // Toast.makeText(Students.this, "Choose back", Toast.LENGTH_SHORT).show();
    }
}
