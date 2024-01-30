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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewStudent extends AppCompatActivity {

    private static final String TAG = "ViewStudent";
    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private static final int EDIT_STUDENT_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student);

        firestore = FirebaseFirestore.getInstance();
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
        DocumentReference studentRef = firestore.collection("Students").document(student.getDocumentId());

        // Perform the delete operation
        studentRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Refresh the data after deletion
                        retrieveData();
                        Toast.makeText(ViewStudent.this, "Student deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error deleting student", e);
                        Toast.makeText(ViewStudent.this, "Error deleting student", Toast.LENGTH_SHORT).show();
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
