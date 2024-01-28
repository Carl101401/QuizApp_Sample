    package com.example.myquizapplication;

    import android.animation.Animator;
    import android.content.Intent;
    import android.media.MediaPlayer;
    import android.os.Bundle;
    import android.os.CountDownTimer;

    import android.view.View;
    import android.view.animation.DecelerateInterpolator;
    import android.widget.Button;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;

    import com.example.myquizapplication.Models.QuestionModel2;
    import com.example.myquizapplication.databinding.ActivityQuestion2Binding;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.util.ArrayList;

    public class QuestionActivity2 extends AppCompatActivity {
        ActivityQuestion2Binding binding;
        private ArrayList<QuestionModel2>list;
        private int count = 0;
        private int position = 0;
        private int score = 0;
        CountDownTimer timer;
        private TextView numIndicator;


        FirebaseDatabase database;
        String categoryName;

        private int set;
        private MediaPlayer correctSound;
        private MediaPlayer wrongSound;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityQuestion2Binding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            getSupportActionBar().hide();

            database = FirebaseDatabase.getInstance();
            categoryName = getIntent().getStringExtra("categoryName");
            set = getIntent().getIntExtra("setNum", 1);
            list = new ArrayList<>();

            numIndicator = findViewById(R.id.numIndicator);
            correctSound = MediaPlayer.create(this, R.raw.correct);
            wrongSound = MediaPlayer.create(this, R.raw.wrong);


            resetTimer();
            timer.start();


            database.getReference().child("Sets").child(categoryName).child("questions")
                    .orderByChild("setNum").equalTo(set)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();

                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    QuestionModel2 model = dataSnapshot.getValue(QuestionModel2.class);
                                    list.add(model);
                                }

                                if (list.size() > 0) {
                                    int numberOfOptions = getNumberOfOptions(list.get(position));

                                    for (int i = 0; i < 4; i++) {
                                        Button optionButton = (Button) binding.optionContainer.getChildAt(i);

                                        if (i < numberOfOptions) {
                                            optionButton.setVisibility(View.VISIBLE);

                                            String optionText = "";
                                            switch (i) {
                                                case 0:
                                                    optionText = list.get(position).getOptionA();
                                                    break;
                                                case 1:
                                                    optionText = list.get(position).getOptionB();
                                                    break;
                                                case 2:
                                                    optionText = list.get(position).getOptionC();
                                                    break;
                                                case 3:
                                                    optionText = list.get(position).getOptionD();
                                                    break;
                                            }

                                            if (!optionText.isEmpty()) {
                                                // Set the actual option text
                                                optionButton.setText(optionText);
                                                optionButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        checkAnsw((Button) view);
                                                    }
                                                });
                                            } else {
                                                // Hide the button if there is no data
                                                optionButton.setVisibility(View.GONE);
                                            }
                                        } else {
                                            optionButton.setVisibility(View.GONE);
                                        }
                                    }

                                    binding.btnNext.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            binding.btnNext.setEnabled(false);
                                            binding.btnNext.setAlpha(0.3f);

                                            enableOption(true);
                                            position++;

                                            if (position == list.size()) {
                                                Intent intent = new Intent(QuestionActivity2.this, ScoreActivity.class);
                                                intent.putExtra("correctAnsw", score);
                                                intent.putExtra("totalQuestion", list.size());
                                                startActivity(intent);
                                                finish();
                                                return;
                                            }

                                            count = 0;
                                            playAnimation(binding.question, 0, list.get(position).getQuestion());
                                            resetTimer();
                                            updateQuestionIndicator();
                                        }
                                    });

                                    // Play the animation for the first question
                                    playAnimation(binding.question, 0, list.get(position).getQuestion());
                                    updateQuestionIndicator();
                                } else {
                                    Toast.makeText(QuestionActivity2.this, "Question Not Exist", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(QuestionActivity2.this, "Question Not Exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(QuestionActivity2.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });





        }

        private void updateQuestionIndicator() {
            if (numIndicator != null) {
                numIndicator.setText((position + 1) + "/" + list.size());
            }
        }


        private int getNumberOfOptions(QuestionModel2 question) {
            int count = 0;

            // Check if each option is not empty and count them
            if (!question.getOptionA().isEmpty()) {
                count++;
            }
            if (!question.getOptionB().isEmpty()) {
                count++;
            }
            if (!question.getOptionC().isEmpty()) {
                count++;
            }
            if (!question.getOptionD().isEmpty()) {
                count++;
            }

            return count;
        }


        public void onBackPressed() {
            // Do nothing or add a message if you want
            Toast.makeText(QuestionActivity2.this, "Please Complete The Quiz.", Toast.LENGTH_SHORT).show();
        }

        private void resetTimer() {
            if (timer != null) {
                timer.cancel(); // Cancel the current timer if it exists
            }

            timer = new CountDownTimer(31000, 1000) {
                @Override
                public void onTick(long l) {
                    binding.timer.setText(String.valueOf(l / 1000));
                }

                @Override
                public void onFinish() {
                    // Check if the quiz has ended
                    if (position < list.size()) {
                        Toast.makeText(QuestionActivity2.this, "Time Out", Toast.LENGTH_SHORT).show();

                        // Simulate a click on the "Next" button
                        binding.btnNext.performClick();
                    }
                }

            };

            timer.start(); // Start the new timer
        }

        private void enableOption(boolean enable) {

            for (int i=0; i<4; i++){

                binding.optionContainer.getChildAt(i).setEnabled(enable);

                if (enable){

                    binding.optionContainer.getChildAt(i).setBackgroundResource(R.drawable.btn_option_back2);

                }

            }

        }

        private void playAnimation(View view, int value, String data) {
            view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                    .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(@NonNull Animator animator) {
                            if (value == 0 && count < 4) {
                                String option = "";
                                if (count == 0) {
                                    option = list.get(position).getOptionA();
                                } else if (count == 1) {
                                    option = list.get(position).getOptionB();
                                } else if (count == 2) {
                                    option = list.get(position).getOptionC();
                                } else if (count == 3) {
                                    option = list.get(position).getOptionD();
                                }

                                playAnimation(binding.optionContainer.getChildAt(count), 0, option);
                                count++;
                            }
                        }

                        @Override
                        public void onAnimationEnd(@NonNull Animator animator) {
                            if (value == 0) {
                                try {
                                    if (view instanceof Button) {
                                        Button optionButton = (Button) view;
                                        if (data.isEmpty()) {
                                            optionButton.setVisibility(View.GONE); // Use GONE instead of INVISIBLE
                                        } else {
                                            optionButton.setVisibility(View.VISIBLE);
                                            optionButton.setText(data);
                                            optionButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    checkAnsw((Button) view);
                                                }
                                            });
                                        }
                                    } else if (view instanceof TextView) {
                                        TextView questionText = (TextView) view;
                                        if (data.isEmpty()) {
                                            questionText.setVisibility(View.GONE); // Use GONE instead of INVISIBLE
                                        } else {
                                            questionText.setVisibility(View.VISIBLE);
                                            questionText.setText(data);
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                view.setTag(data);
                                playAnimation(view, 1, data);
                            }
                        }

                        @Override
                        public void onAnimationCancel(@NonNull Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(@NonNull Animator animator) {
                        }
                    });
        }

        private void checkAnsw(Button selectedOption) {

            enableOption(false);

            binding.btnNext.setEnabled(true);
            binding.btnNext.setAlpha(1);

            //String selectedAnswer = selectedOption.getText().toString().trim();
           // String correctAnswer = list.get(position).getCorrectAnsw().trim();

            // if (selectedAnswer.equalsIgnoreCase(correctAnswer)) {
             //   score++;
              //  selectedOption.setBackgroundResource(R.drawable.right_answ2);
          //  } else {
              //  selectedOption.setBackgroundResource(R.drawable.wrong_answ2);

               // Button correctOption = (Button) binding.optionContainer.findViewWithTag(correctAnswer);
               // correctOption.setBackgroundResource(R.drawable.right_answ2);
           // }


            if (selectedOption.getText().toString().equals(list.get(position).getCorrectAnsw())){

                score ++;
                selectedOption.setBackgroundResource(R.drawable.right_answ2);
                playSound(correctSound);

            }
            else {

                selectedOption.setBackgroundResource(R.drawable.wrong_answ2);

                Button correctOption = (Button) binding.optionContainer.findViewWithTag(list.get(position).getCorrectAnsw());
                correctOption.setBackgroundResource(R.drawable.right_answ2);
                playSound(wrongSound);

            }

        }

        private void playSound(MediaPlayer mediaPlayer) {
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        }
        @Override
        protected void onDestroy() {
            super.onDestroy();

            // Release MediaPlayer resources
            if (correctSound != null) {
                correctSound.release();
            }
            if (wrongSound != null) {
                wrongSound.release();
            }
        }


    }