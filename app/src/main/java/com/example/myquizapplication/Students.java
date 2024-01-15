package com.example.myquizapplication;

import static com.example.myquizapplication.DBstudent.TABLENAME;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Students extends AppCompatActivity {
DBstudent dbStudent;
SQLiteDatabase sqLiteDatabase;
TextView studentList;
EditText User, Pass, Fname, Lname, Ysection;
Button Add, View, Edit;
int id = 0 ;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_students);
        dbStudent =new DBstudent (this);
        findid ();
        insertData();
        editData();
    }

    private void editData() {
    if (getIntent().getBundleExtra("userdata")!=null){
        Bundle bundle=getIntent().getBundleExtra("userdata");
        id=bundle.getInt("id");
        User.setText(bundle.getString("user"));
        Pass.setText(bundle.getString("pass"));
        Fname.setText(bundle.getString("fname"));
        Lname.setText(bundle.getString("lname"));
        Ysection.setText(bundle.getString("ysection"));
        Edit.setVisibility(View.VISIBLE);
        Add.setVisibility(View.GONE);
    }
    }

    private void insertData() {
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                ContentValues cv = new ContentValues();
                cv.put("user", User.getText().toString());
                cv.put("pass", Pass.getText().toString());
                cv.put("fname", Fname.getText().toString());
                cv.put("lname", Lname.getText().toString());
                cv.put("ysection", Ysection.getText().toString());
                sqLiteDatabase = dbStudent.getWritableDatabase();
                long recinsert = sqLiteDatabase.insert(TABLENAME, null, cv);
                if (recinsert != -1) {
                    Toast.makeText(Students.this, "Successfully Inserted Data", Toast.LENGTH_SHORT).show();
                    User.setText("");
                    Pass.setText("");
                    Fname.setText("");
                    Lname.setText("");
                    Ysection.setText("");
                } else {
                    Toast.makeText(Students.this, "Something Wrong Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent=new Intent(Students.this,ViewStudent.class);
                startActivity(intent);
            }
        });
        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                ContentValues cv=new ContentValues();
                cv.put("user",User.getText().toString());
                cv.put("pass",Pass.getText().toString());
                cv.put("fname",Fname.getText().toString());
                cv.put("lname",Lname.getText().toString());
                cv.put("ysection",Ysection.getText().toString());

                sqLiteDatabase=dbStudent.getReadableDatabase();
                String whereClause = "id=?";
                String[] whereArgs = {String.valueOf(id)};
                long recedit = sqLiteDatabase.update(TABLENAME, cv, whereClause, whereArgs);
                if (recedit!=-1){
                    Toast.makeText(Students.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                    Add.setVisibility(View.VISIBLE);
                    Edit.setVisibility(View.GONE);
                }else
                    Toast.makeText(Students.this, "Something Wrong Try Again", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void findid() {
        User=(EditText) findViewById(R.id.name);
        Pass=(EditText) findViewById(R.id.pass);
        Fname=(EditText) findViewById(R.id.firstname);
        Lname=(EditText) findViewById(R.id.lastname);
        Ysection=(EditText) findViewById(R.id.ysection);
        Add = findViewById(R.id.addbutton);
        View= findViewById(R.id.viewbutton);
        Edit = findViewById(R.id.editbutton);

    }
}
