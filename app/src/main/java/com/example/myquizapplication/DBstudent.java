package com.example.myquizapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBstudent extends SQLiteOpenHelper {
    public static final String DBNAME = "dbStudent";
    public static final String TABLENAME = "students";
    public static final int  VER=2;
    String query;
    public DBstudent(@Nullable Context context) {
        super(context, DBNAME, null, VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DBstudent", "onCreate called");
        query = "create table " + TABLENAME + "(id integer primary key , user text , pass text , fname text, lname text, ysection text)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion){
        query = " drop table if exists " + TABLENAME ;
        db.execSQL(query);
        onCreate(db);
    }

    public Boolean checkUserPass(String user, String pass) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME + " WHERE user=? AND pass=?", new String[]{user, pass});
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

}