package com.example.myquizapplication;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Comparator;
import java.util.Date;

public class Score implements Parcelable {

    private String firstName;
    private String lastName;
    private int quizNumber;
    private int correct;
    private String section; // Add section field
    private String finishTime; // Change type to String
    private String documentId; // Add this field

    public Score(String firstName, String lastName, int quizNumber, int correct, String section, String finishTime) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.quizNumber = quizNumber;
        this.correct = correct;
        this.section = section;
        this.finishTime = finishTime;
    }

    protected Score(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        quizNumber = in.readInt();
        correct = in.readInt();
        section = in.readString();
        finishTime = in.readString();
    }

    public static final Creator<Score> CREATOR = new Creator<Score>() {
        @Override
        public Score createFromParcel(Parcel in) {
            return new Score(in);
        }

        @Override
        public Score[] newArray(int size) {
            return new Score[size];
        }
    };

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getQuizNumber() {
        return quizNumber;
    }

    public int getCorrect() {
        return correct;
    }

    public String getSection() {
        return section;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeInt(quizNumber);
        dest.writeInt(correct);
        dest.writeString(section);
        dest.writeString(finishTime);
    }

    // ScoreComparator class
    public static class ScoreComparator implements Comparator<Score> {

        private int sortingOption;

        public ScoreComparator(int sortingOption) {
            this.sortingOption = sortingOption;
        }

        @Override
        public int compare(Score score1, Score score2) {
            int quizNumber1 = score1.getQuizNumber();
            int quizNumber2 = score2.getQuizNumber();

            // Handle the special case for sorting "Quiz1" to "Quiz100"
            if (sortingOption == 0) { // Ascending order
                return Integer.compare(quizNumber1, quizNumber2);
            } else if (sortingOption == 1) { // Descending order
                return Integer.compare(quizNumber2, quizNumber1);
            } else {
                // Handle other sorting options as needed
                return 0;
            }
        }
    }

    public static class ScoreComparator2 implements Comparator<Score> {

        private int sortingOption;

        public ScoreComparator2(int sortingOption) {
            this.sortingOption = sortingOption;
        }

        @Override
        public int compare(Score score1, Score score2) {
            switch (sortingOption) {
                case 0: // First Name Ascending
                    return score1.getFirstName().compareToIgnoreCase(score2.getFirstName());
                case 1: // Last Name Ascending
                    return score1.getLastName().compareToIgnoreCase(score2.getLastName());
                default:
                    return 0;
            }
        }
    }
}
