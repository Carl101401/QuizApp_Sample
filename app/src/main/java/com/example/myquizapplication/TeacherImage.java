package com.example.myquizapplication;

public class TeacherImage {
    private String title; // Image name
    private String url;   // Image URL

    public TeacherImage(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
