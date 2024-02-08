package com.example.myquizapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scoreboard extends AppCompatActivity {

    private static final String TAG = "Scoreboard";

    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private Spinner sortSpinner1;
    private Button deleteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        sortSpinner1 = findViewById(R.id.sortSpinner1);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Generate items from Quiz1 to Quiz100 for the Spinner
        List<String> sortOptions = new ArrayList<>();
        sortOptions.add("Asc");
        sortOptions.add("Desc");

        // Convert the list to an array
        String[] sortOptionsArray = sortOptions.toArray(new String[0]);

        // Set up the adapter for the Spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sortOptionsArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner1.setAdapter(spinnerAdapter);

        // Set up a listener for Spinner item selection
        sortSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle sorting based on the selected option
                retrieveScoreboardData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing or provide a default sorting option
            }
        });
        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
        // Retrieve scoreboard data
        retrieveScoreboardData();
    }
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(Scoreboard.this);
        builder.setTitle("Delete User");
        builder.setMessage("Enter first name, last name, and quiz number:");

        // Set up the input
        final EditText inputFirstName = new EditText(Scoreboard.this);
        final EditText inputLastName = new EditText(Scoreboard.this);
        final EditText inputQuizNumber = new EditText(Scoreboard.this);

        // Specify the type of input expected
        inputFirstName.setHint("First Name");
        inputLastName.setHint("Last Name");
        inputQuizNumber.setHint("Quiz Number");

        // Add EditText fields to the dialog
        LinearLayout layout = new LinearLayout(Scoreboard.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputFirstName);
        layout.addView(inputLastName);
        layout.addView(inputQuizNumber);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String firstName = inputFirstName.getText().toString().trim();
                String lastName = inputLastName.getText().toString().trim();
                String quizNumber = inputQuizNumber.getText().toString().trim();
                // Perform deletion logic here
                deleteUser(firstName, lastName, quizNumber);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    private void deleteUser(String firstName, String lastName, String quizNumber) {
        // Delete the user from Firestore database
        firestore.collection("StudentsScore")
                .whereEqualTo("firstName", firstName)
                .whereEqualTo("lastName", lastName)
                .whereEqualTo("quizNumber", Integer.parseInt(quizNumber))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        documentSnapshot.getReference().delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Scoreboard.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                                        retrieveScoreboardData(); // Refresh scoreboard data after deletion
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Scoreboard.this, "Error deleting user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Scoreboard.this, "Error deleting user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }




    private void retrieveScoreboardData() {
        firestore.collection("StudentsScore")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Score> scoreList = new ArrayList<>();

                    // Log the number of documents in the snapshot
                    Log.d(TAG, "Number of documents: " + queryDocumentSnapshots.size());

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Log the document data
                        Log.d(TAG, "Document data: " + documentSnapshot.getData());

                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        int quizNumber = documentSnapshot.getLong("quizNumber").intValue();
                        int correct = documentSnapshot.getLong("correct").intValue();

                        // Create a Scores object
                        Score scoreboardItem = new Score(firstName, lastName, quizNumber, correct);

                        // Add the scoreboard item to the list
                        scoreList.add(scoreboardItem);
                    }

                    // Log the size of the scoreList
                    Log.d(TAG, "Score List Size: " + scoreList.size());

                    applySorting(scoreList);

                    // Display the data in a RecyclerView
                    ScoreAdapter adapter = new ScoreAdapter(scoreList, new ScoreAdapter.ScoreClickListener() {
                        @Override
                        public void onDeleteClick(Score score) {
                            // Handle delete click

                        }
                    });
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error retrieving scoreboard data", e);
                    Toast.makeText(Scoreboard.this, "Error retrieving scoreboard data", Toast.LENGTH_SHORT).show();
                });
    }

    private void applySorting(List<Score> scoreList) {
        if (sortSpinner1 != null) {
            // Sort the scoreList based on the selected sorting criteria
            Collections.sort(scoreList, new Score.ScoreComparator(sortSpinner1.getSelectedItemPosition()));

            // Get the existing adapter from the RecyclerView
            ScoreAdapter adapter = (ScoreAdapter) recyclerView.getAdapter();

            // Notify the adapter that the data set has changed
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            } else {
                Log.e(TAG, "Adapter is null");
            }
        } else {
            // Handle the case where sortSpinner1 is null
            // For example, you can log an error or display a message
            Log.e(TAG, "Sort spinner is null");
            Toast.makeText(Scoreboard.this, "Error applying sorting", Toast.LENGTH_SHORT).show();
        }
    }
}

