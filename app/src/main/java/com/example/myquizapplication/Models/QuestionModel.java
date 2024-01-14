package com.example.myquizapplication.Models;

public class QuestionModel {

    private String question, optionA,optionB,optionC,optionD,correctAnsw;
    private String key;
    private int setNum;

    public QuestionModel(String question, String optionA, String optionB, String optionC, String optionD, String correctAnsw, String key, int setNum) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnsw = correctAnsw;
        this.key = key;
        this.setNum = setNum;
    }

    public QuestionModel() {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectAnsw() {
        return correctAnsw;
    }

    public void setCorrectAnsw(String correctAnsw) {
        this.correctAnsw = correctAnsw;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getSetNum() {
        return setNum;
    }

    public void setSetNum(int setNum) {
        this.setNum = setNum;
    }
}
