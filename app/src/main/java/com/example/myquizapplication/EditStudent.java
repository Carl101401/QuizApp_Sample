package com.example.myquizapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditStudent extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, firstnameEditText, lastnameEditText, yearSectionEditText;
    private Button saveButton;
    private CheckBox showPasswordCheckBox;
    private Student student; // Declare student variable in the class scope

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        usernameEditText = findViewById(R.id.editUsernameEditText);
        passwordEditText = findViewById(R.id.editPasswordEditText);
        firstnameEditText = findViewById(R.id.editFirstnameEditText);
        lastnameEditText = findViewById(R.id.editLastnameEditText);
        yearSectionEditText = findViewById(R.id.editYearSectionEditText);
        showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox);

        saveButton = findViewById(R.id.saveButton);

        Intent intent = getIntent();
        String documentId = intent.getStringExtra("documentId");
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        String firstname = intent.getStringExtra("firstname");
        String lastname = intent.getStringExtra("lastname");
        String yearSection = intent.getStringExtra("yearSection");
        // Create a Student object with the retrieved details
        student = new Student(username, password, firstname, lastname, yearSection);

        student.setDocumentId(documentId);
        // Retrieve the student details from the intent


        if (student != null) {
            // Set the existing details in the EditText fields
            usernameEditText.setText(student.getUsername());
            passwordEditText.setText(student.getPassword());
            firstnameEditText.setText(student.getFirstname());
            lastnameEditText.setText(student.getLastname());
            yearSectionEditText.setText(student.getYearSection());

            // Make sure the documentId is set
            if (student.getDocumentId() == null || student.getDocumentId().isEmpty()) {
                // Handle the case where documentId is not set
                Toast.makeText(this, "Error: Document ID is not set", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                togglePasswordVisibility(isChecked);
            }
        });
        togglePasswordVisibility(showPasswordCheckBox.isChecked());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save the modified details to Firestore or perform necessary actions
                saveModifiedDetails();
            }
        });
    }

    private void togglePasswordVisibility(boolean showPassword) {
        int inputType = showPassword ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
        passwordEditText.setInputType(inputType);
        // Move cursor to the end of the text to apply the new input type
        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    private void saveModifiedDetails() {
        // Retrieve modified details from EditText fields
        String modifiedUsername = usernameEditText.getText().toString();
        String modifiedPassword = passwordEditText.getText().toString();
        String modifiedFirstname = firstnameEditText.getText().toString();
        String modifiedLastname = lastnameEditText.getText().toString();
        String modifiedYearSection = yearSectionEditText.getText().toString();

        // Validate the modified details if needed

        // Perform actions to update the details in Firestore or your data source
        updateStudentInFirestore(student.getDocumentId(), modifiedUsername, modifiedPassword, modifiedFirstname, modifiedLastname, modifiedYearSection);

        // Show a toast message indicating success
        Toast.makeText(EditStudent.this, "Details saved successfully", Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK);
        // Finish the activity
        finish();
    }

    private void updateStudentInFirestore(String documentId, String modifiedUsername, String modifiedPassword, String modifiedFirstname, String modifiedLastname, String modifiedYearSection) {
        // Get the Firestore instance
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Get the reference to the document you want to update
        DocumentReference studentRef = firestore.collection("Students").document(documentId);

        // Create a map with the updated data
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("username", modifiedUsername);
        updatedData.put("password", modifiedPassword);
        updatedData.put("firstname", modifiedFirstname);
        updatedData.put("lastname", modifiedLastname);
        updatedData.put("yearSection", modifiedYearSection);

        // Perform the update operation
        studentRef.update(updatedData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Log success or perform additional actions if needed
                        Log.d("EditStudent", "DocumentSnapshot successfully updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Log failure or handle the error
                        Log.w("EditStudent", "Error updating document", e);
                    }
                });
    }
}
