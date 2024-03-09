package com.example.myquizapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class Scoreboard extends AppCompatActivity implements ScoreAdapter.OnLongItemClickListener {

    private static final String TAG = "Scoreboard";

    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private Spinner sortSpinner1, sortSpinner2;
    private Button deleteButton;
    private boolean isLongPressed = false;
    private ScoreAdapter adapter; // Declare the adapter as a member variable
    private List<Score> scoreList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        sortSpinner1 = findViewById(R.id.sortSpinner1);
        sortSpinner2 = findViewById(R.id.sortSpinner2);

        adapter = new ScoreAdapter(scoreList, this);
        recyclerView.setAdapter(adapter);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // spinner 1
        List<String> sortOptions = new ArrayList<>();
        sortOptions.add("Quiz Number ↑ ");
        sortOptions.add("Quiz Number ↓");
        //spinner 2
        List<String> sortOptions2 = new ArrayList<>();
        sortOptions2.add(""); // Blank option
        sortOptions2.add("First Name");
        sortOptions2.add("Last Name");

        // Convert the list to an array
        String[] sortOptionsArray = sortOptions.toArray(new String[0]);
        String[] sortOptionsArray2 = sortOptions2.toArray(new String[0]);
        // Set up the adapter for the Spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sortOptionsArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner1.setAdapter(spinnerAdapter);
        ArrayAdapter<String> spinnerAdapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sortOptionsArray2);
        spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner2.setAdapter(spinnerAdapter2);

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
        sortSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle sorting based on the selected option
                if (position != 0) {
                    retrieveScoreboardData();
                }
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
        recyclerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongPressed = true;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isLongPressed) {
                            // Get the selected item from the RecyclerView
                            int position = recyclerView.getChildAdapterPosition(v);
                            Score selectedScore = adapter.getItem(position);

                            // Extract firstName, lastName, and quizNumber from the selected item
                            String firstName = selectedScore.getFirstName();
                            String lastName = selectedScore.getLastName();
                            String quizNumber = String.valueOf(selectedScore.getQuizNumber());

                            // Show the edit confirmation dialog
                            showEditConfirmationDialog(firstName, lastName, quizNumber);
                        }
                    }
                }, 5000); // 5 seconds delay
                return true;
            }
        });

        recyclerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongPressed = true;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isLongPressed) {
                            // Get the selected item from the RecyclerView
                            int position = recyclerView.getChildAdapterPosition(v);
                            Score selectedScore = adapter.getItem(position);

                            // Extract firstName, lastName, and quizNumber from the selected item
                            String firstName = selectedScore.getFirstName();
                            String lastName = selectedScore.getLastName();
                            String quizNumber = String.valueOf(selectedScore.getQuizNumber());

                            // Show the edit confirmation dialog
                            showEditConfirmationDialog(firstName, lastName, quizNumber);
                        }
                    }
                }, 5000); // 5 seconds delay
                return true;
            }
        });

        // Retrieve scoreboard data
        retrieveScoreboardData();

    }
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        builder.setTitle("Delete User");
        builder.setMessage("Enter first name, last name, and quiz number:");

        // Set up the input
        final EditText inputFirstName = new EditText(Scoreboard.this);
        inputFirstName.setHintTextColor(Color.BLACK);
        inputFirstName.setTextColor(Color.BLACK);
        inputFirstName.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));

        final EditText inputLastName = new EditText(Scoreboard.this);
        inputLastName.setHintTextColor(Color.BLACK);
        inputLastName.setTextColor(Color.BLACK);
        inputLastName.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));

        final EditText inputQuizNumber = new EditText(Scoreboard.this);
        inputQuizNumber.setHintTextColor(Color.BLACK);
        inputQuizNumber.setTextColor(Color.BLACK);
        inputQuizNumber.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));

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

                // Check if any of the fields are blank
                if (firstName.isEmpty() || lastName.isEmpty() || quizNumber.isEmpty()) {
                    // Display a toast message or handle the case where any field is blank
                    Toast.makeText(Scoreboard.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform deletion logic here
                    deleteUser(firstName, lastName, quizNumber);
                }
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
    private void showEditConfirmationDialog(String firstName, String lastName, String quizNumber) {
        // Retrieve the data to display in the dialog
        firestore.collection("StudentsScore")
                .whereEqualTo("firstName", firstName)
                .whereEqualTo("lastName", lastName)
                .whereEqualTo("quizNumber", Integer.parseInt(quizNumber))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Extract section and correct from the document
                        String section = documentSnapshot.getString("section");
                        int correct = documentSnapshot.getLong("correct").intValue();

                        // Create a Score object
                        Score score = new Score(firstName, lastName, Integer.parseInt(quizNumber), correct, section, "00:00:00"); // Pass time as a string

                        // Build the dialog with the retrieved data
                        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
                        builder.setTitle("Edit Confirmation");
                        builder.setMessage("First Name: " + firstName + "\n" +
                                "Last Name: " + lastName + "\n" +
                                "Quiz Number: " + quizNumber + "\n" +
                                "Section: " + section + "\n" +
                                "Score: " + correct);

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Show the dialog for editing the data
                                showEditDialog(firstName, lastName, quizNumber, score);
                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Scoreboard.this, "Error retrieving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showEditDialog(String firstName, String lastName, String quizNumber, Score score) {
        // Create an AlertDialog for editing data
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        builder.setTitle("Edit Student Data");

        // Set up the input fields
        final EditText editFirstName = new EditText(this);
        editFirstName.setHintTextColor(Color.BLACK);
        editFirstName.setTextColor(Color.BLACK);
        editFirstName.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        editFirstName.setInputType(InputType.TYPE_CLASS_TEXT);  // Set input type to text

        final EditText editLastName = new EditText(this);
        editLastName.setHintTextColor(Color.BLACK);
        editLastName.setTextColor(Color.BLACK);
        editLastName.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        editFirstName.setInputType(InputType.TYPE_CLASS_TEXT);  // Set input type to text

        final EditText editQuizNumber = new EditText(this);
        editQuizNumber.setHintTextColor(Color.BLACK);
        editQuizNumber.setTextColor(Color.BLACK);
        editQuizNumber.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        Object pe;
        editFirstName.setInputType(InputType.TYPE_CLASS_TEXT);  // Set input type to text

        final EditText editSection = new EditText(this);  // Add EditText for section
        editSection.setHintTextColor(Color.BLACK);
        editSection.setTextColor(Color.BLACK);
        editSection.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        editFirstName.setInputType(InputType.TYPE_CLASS_TEXT);  // Set input type to text

        // Set the initial values
        editFirstName.setText(firstName);
        editLastName.setText(lastName);
        editQuizNumber.setText(quizNumber);
        editSection.setText(score.getSection()); // Set section value from Score object

        // Add EditText fields to the dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editFirstName);
        layout.addView(editLastName);
        layout.addView(editQuizNumber);
        layout.addView(editSection);  // Add EditText for section
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Confirm Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the edited values
                String newFirstName = editFirstName.getText().toString().trim();
                String newLastName = editLastName.getText().toString().trim();
                String newQuizNumber = editQuizNumber.getText().toString().trim();
                String newSection = editSection.getText().toString().trim();  // Retrieve section value

                // Check if any of the fields are blank
                if (newFirstName.isEmpty() || newLastName.isEmpty() || newQuizNumber.isEmpty() || newSection.isEmpty()) {
                    // Display a toast message or handle the case where any field is blank
                    Toast.makeText(Scoreboard.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform the edit action
                    editUser(firstName, lastName, quizNumber, newFirstName, newLastName, newQuizNumber, newSection);
                }
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

    private void editUser(String oldFirstName, String oldLastName, String oldQuizNumber, String newFirstName, String newLastName, String newQuizNumber, String newSection) {
        // Update the document in Firestore with the new values
        firestore.collection("StudentsScore")
                .whereEqualTo("firstName", oldFirstName)
                .whereEqualTo("lastName", oldLastName)
                .whereEqualTo("quizNumber", Integer.parseInt(oldQuizNumber))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        documentSnapshot.getReference().update("firstName", newFirstName,
                                        "lastName", newLastName,
                                        "quizNumber", Integer.parseInt(newQuizNumber),
                                        "section", newSection)  // Update section field
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(Scoreboard.this, "Data updated successfully", Toast.LENGTH_SHORT).show();
                                    retrieveScoreboardData(); // Refresh scoreboard data after editing
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Scoreboard.this, "Error updating data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Scoreboard.this, "Error updating data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void retrieveScoreboardData() {
        firestore.collection("StudentsScore")
                .orderBy("quizNumber", Query.Direction.DESCENDING) // Order the data by quizNumber in descending order
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
                        String section = documentSnapshot.getString("section"); // Added section field
                        String finishTimeStr = documentSnapshot.getString("finishTime"); // Retrieve finishTime as string
                        // Parse the finishTime string back to a Date object
                        Date finishTime = null;
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                            finishTime = dateFormat.parse(finishTimeStr);
                        } catch (ParseException e) {
                            Log.e(TAG, "Error parsing finishTime: " + e.getMessage());
                        }
                        // Format the finishTime back to a string
                        finishTimeStr = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(finishTime);
                        Score scoreboardItem = new Score(firstName, lastName, quizNumber, correct, section, finishTimeStr);

                        // Add the scoreboard item to the list
                        scoreList.add(scoreboardItem);
                    }

                    // Log the size of the scoreList
                    Log.d(TAG, "Score List Size: " + scoreList.size());

                    applySorting(scoreList);
                    applySortingForSpinner2(scoreList);
                    // Display the data in a RecyclerView
                    adapter = new ScoreAdapter(scoreList, this);
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
    private void applySortingForSpinner2(List<Score> scoreList) {
        if (sortSpinner2 != null) {
            // Sort the scoreList based on the selected sorting criteria from spinner2
            Collections.sort(scoreList, new Score.ScoreComparator2(sortSpinner2.getSelectedItemPosition()));

            // Get the existing adapter from the RecyclerView
            ScoreAdapter adapter = (ScoreAdapter) recyclerView.getAdapter();

            // Notify the adapter that the data set has changed
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            } else {
                Log.e(TAG, "Adapter is null");
            }
        } else {
            // Handle the case where sortSpinner2 is null
            Log.e(TAG, "Sort spinner 2 is null");
            Toast.makeText(Scoreboard.this, "Error applying sorting", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLongItemClick(Score score) {
        // Implement the logic to show the edit confirmation dialog
        showEditConfirmationDialog(score.getFirstName(), score.getLastName(), String.valueOf(score.getQuizNumber()));
    }
}

