    package com.example.myquizapplication;

    import android.animation.Animator;
    import android.content.Intent;
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

        FirebaseDatabase database;
        String categoryName;

        private int set;


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

            resetTimer();
            timer.start();


            database.getReference().child("Sets").child(categoryName).child("questions")
                    .orderByChild("setNum").equalTo(set)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            list.clear();
                            if (snapshot.exists()){

                                for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                                    QuestionModel2 model = dataSnapshot.getValue(QuestionModel2.class);
                                    list.add(model);

                                }
                                if (list.size()>0){

                                    for (int i=0; i<4; i++){

                                        binding.optionContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                checkAnsw((Button)view);

                                            }
                                        });
                                    }

                                    playAnimation(binding.question,0,list.get(position).getQuestion());


                                    binding.btnNext.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            binding.btnNext.setEnabled(false);
                                            binding.btnNext.setAlpha(0.3f);

                                            enableOption(true);
                                            position ++;

                                            if (position==list.size()){

                                                Intent intent = new Intent(QuestionActivity2.this,ScoreActivity.class);
                                                intent.putExtra("correctAnsw",score);
                                                intent.putExtra("totalQuestion",list.size());
                                                startActivity(intent);
                                                finish();

                                                return;
                                            }

                                            count = 0;
                                            playAnimation(binding.question,0,list.get(position).getQuestion());

                                            resetTimer();

                                        }
                                    });

                                }
                                else {

                                    Toast.makeText(QuestionActivity2.this, "Question Not Exist", Toast.LENGTH_SHORT).show();

                                }

                            }

                            else {
                                Toast.makeText(QuestionActivity2.this, "Question Not Exist", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(QuestionActivity2.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });



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
           // Log.d("FirebaseData", "Option " + count + ": " + data);

            view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                    .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(@NonNull Animator animator) {

                            if (value==0 &&count<4){

                                String option = "";

                                if (count==0){
                                    option = list.get(position).getOptionA();
                                }
                                else if (count==1) {
                                    option = list.get(position).getOptionB();
                                }
                                else if (count==2) {
                                    option = list.get(position).getOptionC();
                                }
                                else if (count==3) {
                                    option = list.get(position).getOptionD();
                                }
                                playAnimation(binding.optionContainer.getChildAt(count),0,option);
                                count++;
                            }

                        }

                        @Override
                        public void onAnimationEnd(@NonNull Animator animator) {

                            if (value==0){

                                try {

                                    ((TextView)view).setText(data);
                                    binding.numIndicator.setText(position+1+"/"+list.size());

                                }
                                catch (Exception e){

                                    ((Button)view).setText(data);

                                }

                                view.setTag(data);
                                playAnimation(view,1,data);


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

            }
            else {

                selectedOption.setBackgroundResource(R.drawable.wrong_answ2);

                Button correctOption = (Button) binding.optionContainer.findViewWithTag(list.get(position).getCorrectAnsw());
                correctOption.setBackgroundResource(R.drawable.right_answ2);

            }

        }


    }