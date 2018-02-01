package com.moodnetwork.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

import com.moodnetwork.service.*;

import com.moodnetwork.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBackgroundServices();
    }

    private void startBackgroundServices() {
        getApplicationContext().startService(new Intent(this, GPSService.class));
    }
}
