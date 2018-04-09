package com.moodnetwork;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Switch;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //Get widgets reference from XML layout
        Switch locButton = (Switch) findViewById(R.id.locationSwitch);
        Switch microButton = (Switch) findViewById(R.id.microphoneSwitch);
        Switch pushButton = (Switch) findViewById(R.id.pushNotifSwitch);

        //Saves the state of the switch buttons
        SharedPreferences settings = getSharedPreferences("com.mobileapp.smartapplocker", 0);
        //creates the locations setting from the shared preferences
        boolean silentLoc = settings.getBoolean("lockey", false);
        //Get the initial state of location permissions by checking the permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            silentLoc = settings.getBoolean("lockey", true);

        }
        //creates the microphone setting from the shared preferences
        boolean silentMicro = settings.getBoolean("microkey", false);
        //creates the push notification setting from the shared preferences
        boolean silentPush = settings.getBoolean("pushkey", false);
        locButton.setChecked(silentLoc);
        microButton.setChecked(silentMicro);
        pushButton.setChecked(silentPush);


        //Set a Click Listener for location switch Button
        locButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                //Is the switch is on?
                boolean on = ((Switch) v).isChecked();
                if(on)
                {
                    //Do something when switch is on/checked
                    //toast notification to let user know what they are changing
                    Context context = getApplicationContext();
                    CharSequence text = "Location tracking on";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    GPSService.startService();
                }
                else
                {
                    //Do something when switch is off/unchecked
                    //toast notification to let user know what they are changing
                    Context context = getApplicationContext();
                    CharSequence text = "Location tracking off";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    GPSService.stopService();
                }
                //Saves state to the shared preferences when the button is clicked
                SharedPreferences settings = getSharedPreferences("com.mobileapp.smartapplocker", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("lockey", on);
                editor.apply();
            }
        });
        //Set a Click Listener for microphone switch Button
        microButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                //Is the switch is on?
                boolean on = ((Switch) v).isChecked();
                if(on)
                {
                    //Do something when switch is on/checked
                    //toast notification to let user know what they are changing
                    Context context = getApplicationContext();
                    CharSequence text = "Microphone listening on";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    MicrophoneService.startService();
                }
                else
                {
                    //Do something when switch is off/unchecked
                    //toast notification to let user know what they are changing
                    Context context = getApplicationContext();
                    CharSequence text = "Microphone listening off";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    MicrophoneService.stopService();
                }
                //Saves state to the shared preferences when the button is clicked
                SharedPreferences settings = getSharedPreferences("com.mobileapp.smartapplocker", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("microkey", on);
                editor.apply();
            }
        });
        //Set a Click Listener for push notification switch Button
        pushButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                //Is the switch is on?
                boolean on = ((Switch) v).isChecked();
                if(on)
                {
                    //Do something when switch is on/checked
                    //toast notification to let user know what they are changing
                    Context context = getApplicationContext();
                    CharSequence text = "Push notifications on";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    NotificationService.startService();
                }
                else
                {
                    //Do something when switch is off/unchecked
                    //toast notification to let user know what they are changing
                    Context context = getApplicationContext();
                    CharSequence text = "Push notifications off";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    NotificationService.stopService();
                }
                //Saves state to the shared preferences when the button is clicked
                SharedPreferences settings = getSharedPreferences("com.mobileapp.smartapplocker", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("pushkey", on);
                editor.apply();
            }
        });
    }
}
