package com.example.myquizapplication;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;

public class DBhelper extends SQLiteOpenHelper {

    public static final String DBNAME = "login.db";
    public  static final String users = "users";
    public  static final String COL1 = "ID";
    public  static final String COL2 = "username";
    public  static final String COL3 = "password";
    public  static final String COL4 = "score";

    public DBhelper( Context context ) {
        super(context, "login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("create Table users(ID INTEGER primary key autoincrement,username TEXT, password TEXT, retype TEXT )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int oldVersion, int newVersion) {
        MyDB.execSQL("drop Table if exists users");

    }
    public Boolean insertData(String username, String password){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, username);
        contentValues.put(COL3, password);

        long result = MyDB.insert("users", null, contentValues);
        if (result==-1) return false;
        else
            return true;

    }

    public Boolean checkusername(String username){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where username = ?", new String[] {username});
        if (cursor.getCount()>0)
            return true;
        else
            return false;
    }
    public Boolean checkusernamepassword(String username, String password){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where username = ? and password = ? ", new String[] {username,password});
        if (cursor.getCount()>0)
            return true;
        else
            return false;

    }
    public Boolean score (String score){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from user where score = ?", new String[]{score});
        if (cursor.getCount()>0)
            return true;
        else
            return false;
    }

    public Cursor getStudentList(){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor data = MyDB.rawQuery("SELECT * FROM " + users,null );
        return data;
    }}
