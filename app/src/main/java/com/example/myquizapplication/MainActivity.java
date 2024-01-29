package com.example.myquizapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnStudent, btnTeacher;
    private MediaPlayer mediaPlayer;
    private boolean welcomeSoundPlayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStudent = (Button) findViewById(R.id.Student);
        btnTeacher = (Button) findViewById(R.id.Teacher);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        welcomeSoundPlayed = preferences.getBoolean("welcomeSoundPlayed", false);

        mediaPlayer = MediaPlayer.create(this, R.raw.welcome);

        if (!welcomeSoundPlayed) {
            mediaPlayer.start();
            welcomeSoundPlayed = true; // Set the flag to true after playing the sound
            preferences.edit().putBoolean("welcomeSoundPlayed", true).apply();
        }

        btnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),StudentLogin.class);
                startActivity(intent);
            }
        });
        btnTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginPage.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}