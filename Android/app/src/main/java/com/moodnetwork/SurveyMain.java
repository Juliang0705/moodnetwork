package com.moodnetwork;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SurveyMain extends AppCompatActivity {
    int[] used = {0,0,0,0};

    //private variables
    private int questionToGet = 0; //represents the question we want to get
    private boolean tips = true; //turn off the tips for the rest of the survey
    private int numQuestions = 4;//change to 10 for final application

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_main);
    }

    //reset the background color of the buttons to normal
    protected void resetbuttons(){
        findViewById(R.id.button2).setBackgroundColor(getResources().getColor(R.color.button));
        findViewById(R.id.button3).setBackgroundColor(getResources().getColor(R.color.button));
        findViewById(R.id.button4).setBackgroundColor(getResources().getColor(R.color.button));
        findViewById(R.id.button5).setBackgroundColor(getResources().getColor(R.color.button));
    }

    //handle the data from the questionaire
    protected void handleData(){
        //get the question and answer and store both in the backend
    }

    //get a new question and display it, increment progress bar
    protected void getQuestion(){
        resetbuttons();
        ProgressBar p = (ProgressBar) findViewById(R.id.progress);
        int progressIncrement = 100/numQuestions; //this should be a whole number every time
        p.setProgress(p.getProgress() + progressIncrement);

        TextView question = (TextView) findViewById(R.id.textView);
        String text = "";
        switch(questionToGet){
            case 0:
                text = getString(R.string.q1);
                break;
            case 1:
                text = getString(R.string.q2);
                break;
            case 2:
                text = getString(R.string.q3);
                break;
            case 3:
                text = getString(R.string.q4);
                break;

        }
        question.setText(text);
    }

    //handle button clicks
    public void onButtonClick(View view){
        //save the value for the survey
       ColorDrawable button =  (ColorDrawable) view.getBackground();
        if( button.getColor() == getResources().getColor(R.color.colorAccent)){
            //save value and go to the next question.
            handleData();
            getQuestion();
            questionToGet++;
            if(questionToGet > (numQuestions-1)){
                Intent intent = new Intent(this, SurveyFinished.class);
                startActivity(intent);
            }

        }
        else {
            resetbuttons();
            view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            if (tips) {
                Context context = getApplicationContext();
                CharSequence text = "Double tap to submit answer";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                tips = false;
            }
        }

    }
}
