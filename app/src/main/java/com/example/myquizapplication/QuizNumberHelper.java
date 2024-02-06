package com.example.myquizapplication;

import android.util.Log;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class QuizNumberHelper {

    private FirebaseFirestore firestore;

    // Constructor
    public QuizNumberHelper() {
        firestore = FirebaseFirestore.getInstance();
    }

    public interface OnQuizNumberDeterminedListener {
        void onQuizNumberDetermined(int quizNumber);
    }

    public void determineQuizNumber(String username, OnQuizNumberDeterminedListener listener) {
        firestore.collection("StudentsScore")
                .document(username)
                .collection("Scores")
                .orderBy("quizNumber", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Retrieve the highest quiz number and increment it
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        Long highestQuizNumber = documentSnapshot.getLong("quizNumber");

                        int quizNumber;
                        if (highestQuizNumber != null) {
                            quizNumber = highestQuizNumber.intValue() + 1;
                        } else {
                            // If the user has not taken any quizzes, start with quiz number 1
                            quizNumber = 1;
                        }

                        // Notify the listener with the determined quiz number
                        listener.onQuizNumberDetermined(quizNumber);
                    } else {
                        // If the user has not taken any quizzes, start with quiz number 1
                        listener.onQuizNumberDetermined(1);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("QuizNumberHelper", "Error determining quiz number", e);
                });
    }
}
