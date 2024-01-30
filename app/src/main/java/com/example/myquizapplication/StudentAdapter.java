package com.example.myquizapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Inside StudentAdapter.java
public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public StudentAdapter(List<Student> studentList, OnItemClickListener listener) {
        this.studentList = studentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);

        holder.usernameTextView.setText("Username: " + student.getUsername());
        holder.firstnameTextView.setText("First Name: " + student.getFirstname());
        holder.lastnameTextView.setText("Last Name: " + student.getLastname());
        holder.yearSectionTextView.setText("Year & Section: " + student.getYearSection());
        
        String hiddenPassword = generateHiddenPassword(student.getPassword());
        holder.passwordTextView.setText("Password: " + hiddenPassword);

        // Set click listeners for Edit and Delete buttons
        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(position);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(position);
            }
        });
    }

    private String generateHiddenPassword(String password) {
        // Replace the actual password with asterisks or any other character
        int passwordLength = password.length();
        StringBuilder hiddenPassword = new StringBuilder();

        for (int i = 0; i < passwordLength; i++) {
            hiddenPassword.append("*");
        }

        return hiddenPassword.toString();
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, passwordTextView, firstnameTextView, lastnameTextView, yearSectionTextView;
        Button editButton, deleteButton;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            passwordTextView = itemView.findViewById(R.id.passwordTextView);
            firstnameTextView = itemView.findViewById(R.id.firstnameTextView);
            lastnameTextView = itemView.findViewById(R.id.lastnameTextView);
            yearSectionTextView = itemView.findViewById(R.id.yearSectionTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
