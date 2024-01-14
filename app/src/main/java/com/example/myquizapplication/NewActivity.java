package com.example.myquizapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        EditText createusername, createpassword, retypepassword;
        Button Registerlogin;
        DBhelper DB;

        createusername = (EditText) findViewById(R.id.createusername);
        createpassword = (EditText) findViewById(R.id.createpassword);
        retypepassword = (EditText) findViewById(R.id.retypepassword);
        Registerlogin = (Button) findViewById(R.id.RegisterLogin);
        DB = new DBhelper(this);

        Registerlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = createusername.getText().toString();
                String pass = createpassword.getText().toString();
                String retype = retypepassword.getText().toString();
                String score = null;



                if(user.equals("")|| pass.equals("")|| retype.equals(""))
                    Toast.makeText(NewActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                else {
                    if (pass.equals(retype)){
                        Boolean checkuser = DB.checkusername(user);
                        if (checkuser==false){
                            Boolean insert = DB.insertData(user,pass);
                            if (insert==true){
                                Toast.makeText(NewActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(NewActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(NewActivity.this, "User already exists! please sign in", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(NewActivity.this, "Passwords does not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}