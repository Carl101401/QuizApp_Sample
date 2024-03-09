package com.example.myquizapplication;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myquizapplication.databinding.ActivityScore2Binding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
public class ScoreActivity extends AppCompatActivity {
    ActivityScore2Binding binding;
    private FirebaseFirestore firestore;
    private int correct;
    private int totalQuestion;
    private int wrong;
    private MediaPlayer scoreBelow50Sound;
    private MediaPlayer scoreAbove50Sound;
    private MediaPlayer score50Sound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScore2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        firestore = FirebaseFirestore.getInstance();
        correct = getIntent().getIntExtra("correctAnsw", 0);
        totalQuestion = getIntent().getIntExtra("totalQuestion", 0);
        wrong = totalQuestion - correct;
        binding.totalRight.setText(String.valueOf(correct));
        binding.totalWrong.setText(String.valueOf(wrong));
        binding.totalQuestion.setText(String.valueOf(totalQuestion));
        binding.progressBar.setProgress(totalQuestion);
        binding.progressBar.setProgress(correct);
        binding.progressBar.setProgressMax(totalQuestion);
        initializeSounds();
        checkAndPlayScoreSound();
        binding.btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNameInputDialog();
            }
        });
    }
    private void initializeSounds() {
        // Assuming you have sound files in the raw folder
        scoreBelow50Sound = MediaPlayer.create(this, R.raw.betterlucknextime);
        scoreAbove50Sound = MediaPlayer.create(this, R.raw.good);
        score50Sound = MediaPlayer.create(this, R.raw.notbad);
    }
    private void checkAndPlayScoreSound() {
        int threshold = totalQuestion / 2;

        if (correct < threshold) {
            playSound(scoreBelow50Sound);
        } else if (correct > threshold) {
            playSound(scoreAbove50Sound);
        } else {
            playSound(score50Sound);
        }
    }
    private void playSound(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
    private void showNameInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogStyle);
        builder.setTitle("Submit Your Name, Section, and Quiz Number");
        View view = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        builder.setView(view);
        EditText inputFirstName = view.findViewById(R.id.editTextFirstName);
        EditText inputLastName = view.findViewById(R.id.editTextLastName);
        EditText inputQuizNumber = view.findViewById(R.id.editTextQuizNumber);
        EditText inputSection = view.findViewById(R.id.editTextSection); // New EditText for Section
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long finishTime = System.currentTimeMillis();

                String firstName = inputFirstName.getText().toString().trim();
                String lastName = inputLastName.getText().toString().trim();
                String quizNumberText = inputQuizNumber.getText().toString().trim();
                String section = inputSection.getText().toString().trim(); // Get section text
                if (!firstName.isEmpty() && !lastName.isEmpty() && !quizNumberText.isEmpty() && !section.isEmpty()) {
                    try {
                        int quizNumber = Integer.parseInt(quizNumberText);
                        // Store the score data in Firestore
                        storeScoreInFirestore(firstName, lastName, quizNumber, correct, totalQuestion, wrong, section, finishTime); // Pass section to method
                        Toast.makeText(ScoreActivity.this, "Your score is submitted!", Toast.LENGTH_SHORT).show();
                        // Navigate to the desired activity
                        Intent intent = new Intent(ScoreActivity.this, QuizReviewer.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } catch (NumberFormatException e) {
                        Toast.makeText(ScoreActivity.this, "Invalid Quiz Number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ScoreActivity.this, "Please enter your first name, last name, section, and quiz number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void storeScoreInFirestore(String firstName, String lastName, int quizNumber, int correct, int totalQuestion, int wrong, String section, long finishTime) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String formattedFinishTime = dateFormat.format(new Date(finishTime));
        if (currentUser != null) {
            String username = currentUser.getEmail().replace("@yourdomain.com", "");
            // Use a combination of firstName and lastName as the document ID
            String uniqueDocumentId = firstName + "_" + lastName + "_" + System.currentTimeMillis();
            DocumentReference userScoreRef = firestore.collection("StudentsScore")
                    .document(uniqueDocumentId);
            // Fetch the current quizNumber for the user or initialize to 1
            userScoreRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    int currentQuizNumber = 1;  // Default quiz number for a new user
                    if (task.getResult() != null && task.getResult().exists()) {
                        // Document already exists, retrieve the existing quizNumber and increment it
                        currentQuizNumber = task.getResult().get("quizNumber", Integer.class) + 1;
                    }
                    // Create a new scoreMap for the quiz
                    Map<String, Object> scoreMap = new HashMap<>();
                    scoreMap.put("quizNumber", quizNumber); // Store the provided quiz number
                    scoreMap.put("correct", correct);
                    scoreMap.put("totalQuestion", totalQuestion);
                    scoreMap.put("wrong", wrong);
                    scoreMap.put("firstName", firstName);
                    scoreMap.put("lastName", lastName);
                    scoreMap.put("section", section); // Add section to the scoreMap
                    scoreMap.put("finishTime", formattedFinishTime);

                    // Set the scoreMap directly to the document using merge option
                    userScoreRef.set(scoreMap, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                Log.d("ScoreActivity", "Score data successfully stored/updated in Firestore");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("ScoreActivity", "Error storing/updating score data in Firestore", e);
                            });
                } else {
                    Log.e("ScoreActivity", "Error checking document existence in Firestore", task.getException());
                }
            });
        } else {
            Log.e("ScoreActivity", "User not authenticated");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseSounds();
    }
    private void releaseSounds() {
        if (scoreBelow50Sound != null) {
            scoreBelow50Sound.release();
        }
        if (scoreAbove50Sound != null) {
            scoreAbove50Sound.release();
        }
        if (score50Sound != null) {
            score50Sound.release();
        }
    }
    @Override
    public void onBackPressed() {
        // Do nothing or add a message if you want
        // Intercept the Back button press
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Intercept the Home button press
        if ((keyCode == KeyEvent.KEYCODE_HOME)) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
