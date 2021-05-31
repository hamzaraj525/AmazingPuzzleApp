package com.learning.amazingpuzzleapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class quiz_activity extends AppCompatActivity {
    private static final long COUNTDOWN_MILLIS = 30000;
    TextView textViewTimer, textViewQuestionCount, textViewQuestions;

    Button btnConfirmNext;
    RadioGroup radioGroup;
    RadioButton rb1, rb2, rb3, rb4;

    private ColorStateList textColourDefault;
    private ColorStateList textColourDefaultCD;

    private CountDownTimer countDownTimer;
    private long timeLeftMillis;

    private List<Question> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    private boolean answered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity);

        textViewTimer = findViewById(R.id.textViewTimer);
        textViewQuestionCount = findViewById(R.id.textViewQuestionCount);
        textViewQuestions = findViewById(R.id.textViewQuestions);
        radioGroup = findViewById(R.id.rdGroup1);
        rb1 = findViewById(R.id.btn1);
        rb2 = findViewById(R.id.btn2);
        rb3 = findViewById(R.id.btn3);
        rb4 = findViewById(R.id.btn4);
        btnConfirmNext = findViewById(R.id.btn);

        textColourDefault = rb1.getTextColors();
        textColourDefaultCD = textViewTimer.getTextColors();

        QuizDbHelper dbHelper = new QuizDbHelper(this);
        questionList = dbHelper.getAllQuestions();

        questionCountTotal = questionList.size();
        Collections.shuffle(questionList);

        showNextQuestion();

        btnConfirmNext.setOnClickListener(v -> {
            if (!answered) {
                if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {
                    checkAnswer();

                } else {
                    Toast.makeText(quiz_activity.this, "PLEASE SELECT ANY OPTION ", Toast.LENGTH_SHORT).show();
                }
            } else {
                showNextQuestion();
            }

        });

    }

    private void showNextQuestion() {

        rb1.setTextColor(textColourDefault);
        rb2.setTextColor(textColourDefault);
        rb3.setTextColor(textColourDefault);
        rb4.setTextColor(textColourDefault);
        radioGroup.clearCheck();

        if (questionCounter < questionCountTotal) {

            currentQuestion = questionList.get(questionCounter);
            textViewQuestions.setText(currentQuestion.getQuestion());

            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());
            rb4.setText(currentQuestion.getOption4());
            questionCounter++;
            textViewQuestionCount.setText("" + questionCounter + "/" + questionCountTotal);
            answered = false;
            btnConfirmNext.setText("Correct");

            timeLeftMillis = COUNTDOWN_MILLIS;
            startCountDown();
        } else {
            finishQuiz();
        }
    }

    private void startCountDown() {

        countDownTimer = new CountDownTimer(timeLeftMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMillis = millisUntilFinished;
                updateCountDownText();

            }

            private void updateCountDownText() {
                int mint = (int) (timeLeftMillis / 1000 / 60);
                int sec = (int) (timeLeftMillis / 1000) % 60;
                String timeFormat = String.format(Locale.getDefault(), "%02d:%02d", mint, sec);
                textViewTimer.setText(timeFormat);
                if (timeLeftMillis < 10000) {
                    textViewTimer.setTextColor(Color.RED);

                } else {
                    textViewTimer.setTextColor(textColourDefaultCD);
                }
            }

            @Override
            public void onFinish() {
                timeLeftMillis = 0;
                updateCountDownText();
                checkAnswer();

            }
        }.start();
    }

    private void checkAnswer() {
        answered = true;
        countDownTimer.cancel();

        RadioButton selected = findViewById(radioGroup.getCheckedRadioButtonId());
        showAnswer();
    }

    private void showAnswer() {

        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);
        rb4.setTextColor(Color.RED);

        switch (currentQuestion.getAnswerNr()) {

            case 1:
                rb1.setTextColor(Color.GREEN);
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                break;
            case 4:
                rb4.setTextColor(Color.GREEN);
                break;
        }

        if (questionCounter < questionCountTotal) {
            btnConfirmNext.setText("Next");
        } else {
            btnConfirmNext.setText("Finish");
        }
    }

    private void finishQuiz() {
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(quiz_activity.this, QuestionTypeActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}