package com.example.myquizapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class QuizReviewer extends AppCompatActivity {

    ImageView quizLogo;
    Button btnQuiz, btnReviewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_reviewer);

        btnQuiz = (Button) findViewById(R.id.Chapter);
        btnReviewer = (Button) findViewById(R.id.Review);

        btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Students2.class);
                startActivity(intent);            }
        });
        btnReviewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DifferentReviewers.class);
                startActivity(intent);
            }
        });
    }




}