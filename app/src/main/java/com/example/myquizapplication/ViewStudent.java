package com.example.myquizapplication;

import static com.example.myquizapplication.DBstudent.TABLENAME;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewStudent extends AppCompatActivity {
DBstudent dBstudent;
SQLiteDatabase sqLiteDatabase;
RecyclerView recyclerView;
MyAdapter myAdapter;
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_view_student);
        dBstudent = new DBstudent(this);
        findid();
        viewdata();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void viewdata() {
        sqLiteDatabase = dBstudent.getReadableDatabase();
        Cursor cursor =sqLiteDatabase.rawQuery("select *from " + TABLENAME + "", null);
        ArrayList<Model>modelArrayList=new ArrayList<>();
        while (cursor.moveToNext()){
            int id=cursor.getInt(0);
            String user= cursor.getString(1);
            String pass= cursor.getString(2);
            String fname= cursor.getString(3);
            String lname= cursor.getString(4);
            String ysection= cursor.getString(5);
            modelArrayList.add(new Model(id, user, pass, fname, lname, ysection));
        }
        cursor.close();
        myAdapter=new MyAdapter(this,R.layout.singledata,modelArrayList,sqLiteDatabase);
        recyclerView.setAdapter(myAdapter);
    }

    private void findid() { recyclerView = findViewById(R.id.rv);
    }
    public void onBackPressed() {
        // Do nothing or add a message if you want
        //Toast.makeText(Reviewer.this, "Choose back", Toast.LENGTH_SHORT).show();
    }
}