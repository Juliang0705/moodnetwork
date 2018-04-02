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

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Scanner;

public class SurveyMain extends AppCompatActivity {


    //private variables
    private int questionToGet = 0; //represents the question we want to get
    private int curQuestion = 0; //current question we are on (0-9)
    private boolean tips = true; //turn off the tips for the rest of the survey
    private int numQuestions = 10;//change to 10 for final application
    private char[] usedIndexes;
    private File f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_main);

        try {
            f = new File(getApplicationContext().getFilesDir(), "save.txt");
            FileInputStream is;
            if (!f.exists()) {
                FileWriter writer = new FileWriter(f);
                writer.append("000000000000000000000000000000000000000");
                writer.flush();
                writer.close();
                System.out.println("New file");
            }
            is = new FileInputStream(f);
            Scanner s = new Scanner(is).useDelimiter("\\A");
            String save = s.hasNext() ? s.next() : "";
            usedIndexes = save.toCharArray();
            System.out.println("Existing file");
            System.out.println(usedIndexes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setQuestion();
        curQuestion++;
    }

    //reset the background color of the buttons to normal
    protected void resetbuttons(){
        findViewById(R.id.button2).setBackgroundColor(getResources().getColor(R.color.button));
        findViewById(R.id.button3).setBackgroundColor(getResources().getColor(R.color.button));
        findViewById(R.id.button4).setBackgroundColor(getResources().getColor(R.color.button));
        findViewById(R.id.button5).setBackgroundColor(getResources().getColor(R.color.button));
    }

    public static int getStringIdentifier(Context context, String name) {
        return context.getResources().getIdentifier(name, "string", context.getPackageName());
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

        setQuestion();
    }

    //sets the current question
    protected void setQuestion(){
        // More efficient way? Does this suffice?
        do{
            questionToGet = (int)(Math.random()*39);
        }while(usedIndexes[questionToGet] == '1');
        usedIndexes[questionToGet] = '1';
        if((new String(usedIndexes)).equals("111111111111111111111111111111111111111")){
            usedIndexes = "000000000000000000000000000000000000000".toCharArray();
        }

        TextView question = (TextView) findViewById(R.id.textView);
        String text = "";
        text = getString(getStringIdentifier(getApplicationContext(),"q"+questionToGet));
        question.setText((curQuestion+1) + ". " + text);
    }

    //handle button clicks
    public void onButtonClick(View view){
        //save the value for the survey
       ColorDrawable button =  (ColorDrawable) view.getBackground();
        if( button.getColor() == getResources().getColor(R.color.colorAccent)){
            //save value and go to the next question.
            handleData();
            getQuestion();
            curQuestion++;

            //TODO: Fix 11th question temporarily showing
            if(curQuestion > (numQuestions)){
                //finished questionnaire
                Intent intent = new Intent(this, SurveyFinished.class);
                startActivity(intent);

                //only save when we reach the end, else answered questions of an unfinished survey will still be available
                String save = new String(usedIndexes);
                try {
                    FileWriter writer = new FileWriter(f);
                    writer.write(save);
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
