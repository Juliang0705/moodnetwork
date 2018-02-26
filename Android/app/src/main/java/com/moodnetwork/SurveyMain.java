package com.moodnetwork;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SurveyMain extends AppCompatActivity {
    int[] used = {0,0,0,0};
    int x = 0;
    boolean tips = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_main);
    }
    private void resetbuttons(){
        findViewById(R.id.button2).setBackgroundColor(getResources().getColor(R.color.button));
        findViewById(R.id.button3).setBackgroundColor(getResources().getColor(R.color.button));
        findViewById(R.id.button4).setBackgroundColor(getResources().getColor(R.color.button));
        findViewById(R.id.button5).setBackgroundColor(getResources().getColor(R.color.button));
    }
    private void getQuestion(int x){
        TextView question = (TextView) findViewById(R.id.textView);

        String text = "";
        switch(x){
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
    public void onButtonClick(View view){
        //save the value for the survey
       ColorDrawable button =  (ColorDrawable) view.getBackground();
        if( button.getColor() == getResources().getColor(R.color.colorAccent)){
            //save value and go to the next question.
            System.out.println("color same");
            getQuestion(x);
            x++;
            if(x > 3){
                Intent intent = new Intent(this, SurveyFinished.class);
                startActivity(intent);
            }
            resetbuttons();
        }
        resetbuttons();
        view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        if(tips){
            Context context = getApplicationContext();
            CharSequence text = "Double tap to submit answer";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            tips = false;
        }

    }
}
