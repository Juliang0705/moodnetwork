package com.moodnetwork;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.moodnetwork.activity.MainActivity;

public class SurveyFinished extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_finished);
    }
    public void onButtonClick(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        Context context = getApplicationContext();
        CharSequence text = "Survey Completed!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
