package com.moodnetwork;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.moodnetwork.activity.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SurveyFinished extends AppCompatActivity {
    public static final String TIME_STAMP = "com.moodnetwork.TIME";
    private File f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_finished);
    }
    public void onButtonClick(View view){
        Intent intent = new Intent(this, MainActivity.class);
        String message = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
        //write the date to the file and send in intent as backup
        try {
            //create the file if it does not exist and print the date into it
            f = new File(getApplicationContext().getFilesDir(), "lastsurvey.txt");
            PrintWriter writer = new PrintWriter(f);
            writer.print(message);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.putExtra(TIME_STAMP, message);
        startActivity(intent);

        Context context = getApplicationContext();
        CharSequence text = "Survey Completed!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //prevent user from going back into the survey after completing it
    @Override
    public void onBackPressed() {
        onButtonClick(findViewById(R.id.okaySurveyFinished));
    }
}
