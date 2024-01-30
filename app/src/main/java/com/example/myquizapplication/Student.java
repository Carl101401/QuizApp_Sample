package com.example.myquizapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable {
    private String documentId; // Add documentId field
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String yearSection;

    // Empty constructor required by Firestore
    public Student() {
    }

    public Student(String username, String password, String firstname, String lastname, String yearSection) {
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.yearSection = yearSection;
    }

    protected Student(Parcel in) {
        documentId = in.readString();
        username = in.readString();
        password = in.readString();
        firstname = in.readString();
        lastname = in.readString();
        yearSection = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(firstname);
        dest.writeString(lastname);
        dest.writeString(yearSection);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    // Getters and Setters
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getYearSection() {
        return yearSection;
    }

    public void setYearSection(String yearSection) {
        this.yearSection = yearSection;
    }
}
