package com.moodnetwork;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SurveyMain extends AppCompatActivity {

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
    public void onbuttonClick(View view){
        //save the value for the survey
        if(view.getSolidColor() == getResources().getColor(R.color.colorAccent)){

        }
        resetbuttons();
        view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }
}
