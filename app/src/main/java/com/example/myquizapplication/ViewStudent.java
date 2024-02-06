package com.example.myquizapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewStudent extends AppCompatActivity {

    private static final String TAG = "ViewStudent";
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    private RecyclerView recyclerView;
    private static final int EDIT_STUDENT_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student);

        // Initialize Firebase Authentication and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Check if the user is authenticated
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            // If not authenticated, redirect to the login screen
            startActivity(new Intent(ViewStudent.this, Teachers.class));
            finish(); // Close the current activity
            return;
        }

        recyclerView = findViewById(R.id.recyclerView);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        retrieveData();
    }

    private void retrieveData() {
        firestore.collection("Students")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Student> studentList = new ArrayList<>();

                        // Inside the for loop where you create Student objects in ViewStudent activity
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String username = documentSnapshot.getString("username");
                            String password = documentSnapshot.getString("password");
                            String firstname = documentSnapshot.getString("firstname");
                            String lastname = documentSnapshot.getString("lastname");
                            String yearSection = documentSnapshot.getString("yearSection");

                            // Get the document ID
                            String documentId = documentSnapshot.getId();

                            // Create a Student object and set the documentId
                            Student student = new Student(username, password, firstname, lastname, yearSection);
                            student.setDocumentId(documentId);

                            // Add the student to the list
                            studentList.add(student);
                        }

                        // Display the data in a RecyclerView
                        StudentAdapter adapter = new StudentAdapter(studentList, new StudentAdapter.OnItemClickListener() {
                            @Override
                            public void onEditClick(int position) {
                                // Handle Edit button click
                                launchEditActivity(studentList.get(position));
                            }

                            @Override
                            public void onDeleteClick(int position) {
                                // Handle Delete button click (e.g., delete the student)
                                deleteStudent(studentList.get(position));
                            }
                        });

                        recyclerView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error retrieving data", e);
                        Toast.makeText(ViewStudent.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteStudent(Student student) {
        // Get the Firestore instance
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Get the reference to the document you want to delete
        DocumentReference studentRef = firestore.collection("Students").document(student.getUsername());

        // Perform the delete operation
        studentRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Delete the corresponding authentication account
                        firebaseAuth.signInWithEmailAndPassword(student.getUsername() + "@yourdomain.com", student.getPassword())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        if (user != null) {
                                            user.delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Data and authentication account deleted successfully
                                                            Toast.makeText(ViewStudent.this, "Student Data is Deleted ", Toast.LENGTH_SHORT).show();
                                                            Toast.makeText(ViewStudent.this, "Add Student to Proceed View Student Again ", Toast.LENGTH_SHORT).show();
                                                            // Refresh the student data after deletion
                                                            retrieveData();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.e(TAG, "Error deleting authentication account", e);
                                                            Toast.makeText(ViewStudent.this, "Error deleting authentication account", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Error signing in to delete authentication account", e);
                                        Toast.makeText(ViewStudent.this, "Error signing in to delete authentication account", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error deleting student data", e);
                        Toast.makeText(ViewStudent.this, "Error deleting student data", Toast.LENGTH_SHORT).show();
                    }
                });
    }




    private void launchEditActivity(Student student) {
        // Intent to launch the EditActivity with the selected student details
        Intent intent = new Intent(ViewStudent.this, EditStudent.class);

        // Pass the document ID as an extra
        intent.putExtra("documentId", student.getDocumentId());

        // Pass the rest of the student details
        intent.putExtra("username", student.getUsername());
        intent.putExtra("password", student.getPassword());
        intent.putExtra("firstname", student.getFirstname());
        intent.putExtra("lastname", student.getLastname());
        intent.putExtra("yearSection", student.getYearSection());

        startActivityForResult(intent, EDIT_STUDENT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is from the EditStudent activity and indicates a successful modification
        if (requestCode == EDIT_STUDENT_REQUEST_CODE && resultCode == RESULT_OK) {
            // Refresh the data
            retrieveData();
        }
    }
}
