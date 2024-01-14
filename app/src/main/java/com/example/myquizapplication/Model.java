package com.example.myquizapplication;

public class Model {
    private int id;
    private String user;
    private String pass;
    private String fname;
    private String lname;
    private String ysection;

    public Model(int id, String user, String pass, String fname, String lname, String ysection) {
        this.id = id;
        this.user = user;
        this.pass = pass;
        this.fname = fname;
        this.lname = lname;
        this.ysection = ysection;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getYsection() {
        return ysection;
    }

    public void setYsection(String ysection) {
        this.ysection = ysection;
    }
}