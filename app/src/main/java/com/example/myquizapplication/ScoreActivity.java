package com.example.myquizapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myquizapplication.databinding.ActivityScore2Binding;

public class ScoreActivity extends AppCompatActivity {
    ActivityScore2Binding binding;
    private MediaPlayer scoreBelow50Sound;
    private MediaPlayer scoreAbove50Sound;
    private MediaPlayer score50Sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScore2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        int correct = getIntent().getIntExtra("correctAnsw",0);
        int totalQuestion = getIntent().getIntExtra("totalQuestion",0);
        int wrong = totalQuestion - correct;

        binding.totalRight.setText(String.valueOf(correct));
        binding.totalWrong.setText(String.valueOf(wrong));
        binding.totalQuestion.setText(String.valueOf(totalQuestion));

        binding.progressBar.setProgress(totalQuestion);
        binding.progressBar.setProgress(correct);

        binding.progressBar.setProgressMax(totalQuestion);
        initializeSounds();
        checkAndPlayScoreSound();


        binding.btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ScoreActivity.this,Students2.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
        binding.btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ScoreActivity.this, QuizReviewer.class);
                startActivity(intent);

                finish();
            }
        });
    }
    private void initializeSounds() {
        scoreBelow50Sound = MediaPlayer.create(this, R.raw.betterlucknextime);
        scoreAbove50Sound = MediaPlayer.create(this, R.raw.good);
        score50Sound = MediaPlayer.create(this, R.raw.notbad);
    }
    private void checkAndPlayScoreSound() {
        int correct = getIntent().getIntExtra("correctAnsw", 0);
        int totalQuestion = getIntent().getIntExtra("totalQuestion", 0);
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
    public void onBackPressed() {
        // Do nothing or add a message if you want
        //Toast.makeText(ScoreActivity.this, "Choose Retry or Quit", Toast.LENGTH_SHORT).show();
    }
}