package com.moodnetwork;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.moodnetwork.activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SurveyFinished extends AppCompatActivity {
    public static final String TIME_STAMP = "com.moodnetwork.TIME";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_finished);
    }
    public void onButtonClick(View view){
        Intent intent = new Intent(this, MainActivity.class);
        String message = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
        intent.putExtra(TIME_STAMP, message);
        startActivity(intent);

        Context context = getApplicationContext();
        CharSequence text = "Survey Completed!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
