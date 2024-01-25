package com.example.myquizapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DifferentReviewers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_different_reviewers);

        Button btnVideoReviewer = findViewById(R.id.btnVideoReviewer);
        Button btnImageReviewer = findViewById(R.id.btnImageReviewer);
        Button btnTextReviewer = findViewById(R.id.btnTextReviewer);

        btnVideoReviewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Video Reviewer Activity
                Intent intent = new Intent(DifferentReviewers.this, VideoReviewer.class);
                startActivity(intent);
            }
        });

        btnImageReviewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Image Reviewer Activity
                Intent intent = new Intent(DifferentReviewers.this, ImageReviewer.class);
                startActivity(intent);
            }
        });

        btnTextReviewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Text Reviewer Activity
                Intent intent = new Intent(DifferentReviewers.this, TextReviewer.class);
                startActivity(intent);
            }
        });
    }
}
