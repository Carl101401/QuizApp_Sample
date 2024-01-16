package com.example.myquizapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Teachers extends AppCompatActivity {
    TextView teachers, Teacher;
    Button btnstudent, btnscore, btnaddquiz, btnaddreviewer, btnsignout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers);
        btnstudent = (Button) findViewById(R.id.addStudent);
        btnscore = (Button) findViewById(R.id.studentScore);
        btnaddquiz = (Button) findViewById(R.id.Addquiz);
        btnaddreviewer= (Button) findViewById(R.id.Addreviewer);
        btnsignout= (Button) findViewById(R.id.Signout);

        btnstudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Students.class);
                startActivity(intent);
            }

        });
        btnscore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),NewActivity.class);
                startActivity(intent);
            }

        });
        btnaddquiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddQuiz.class);
                startActivity(intent);
            }

        });
        btnaddreviewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddReviewer.class);
                startActivity(intent);
            }

        });
        btnsignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }

        });

    }
}