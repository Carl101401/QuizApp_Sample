package com.example.myquizapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private List<Score> scoreList;
    private OnLongItemClickListener longItemClickListener;
    private OnLongItemClickListener itemLongClickListener;



    public ScoreAdapter(List<Score> scoreList, OnLongItemClickListener listener) {
        this.scoreList = scoreList;
        this.longItemClickListener = listener;
    }


    public interface OnLongItemClickListener {
        void onLongItemClick(Score score);
    }

    public interface ScoreClickListener {
        void onDeleteClick(Score score);
    }

    public class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView firstNameTextView;
        TextView lastNameTextView;
        TextView quizNumberTextView;
        TextView scoreTextView;


        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            firstNameTextView = itemView.findViewById(R.id.firstNameTextView);
            lastNameTextView = itemView.findViewById(R.id.lastNameTextView);
            quizNumberTextView = itemView.findViewById(R.id.quizNumberTextView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);

        }

        public void bind(Score score) {
            firstNameTextView.setText("First Name: " + score.getFirstName());
            lastNameTextView.setText("Last Name: " + score.getLastName());
            quizNumberTextView.setText("Quiz Number: " + score.getQuizNumber());
            scoreTextView.setText("Score: " + score.getCorrect());
        }
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        Score score = scoreList.get(position);
        holder.bind(score);

        Score currentScore = scoreList.get(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Notify the listener about the long click event
                longItemClickListener.onLongItemClick(currentScore);
                return true;
            }
        });
        holder.firstNameTextView.setText(score.getFirstName());
        holder.lastNameTextView.setText(score.getLastName());
        holder.quizNumberTextView.setText("Quiz Number: " + score.getQuizNumber());
        holder.scoreTextView.setText("Score: " + score.getCorrect());




    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }
    public Score getItem(int position) {
        return scoreList.get(position);
    }

}

