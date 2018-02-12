package com.moodnetwork.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.*;
import android.os.IBinder;
import android.util.Log;

import com.moodnetwork.database.MongoDB;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class AccelerometerService extends Service {
    public static final String TAG = AccelerometerService.class.getCanonicalName();

    private SensorManager mSensorManager;
    private Sensor mSensor;
    @Override
    public void onCreate() {
        Log.i(TAG, "Accelerometer service created");
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager == null) {
            throw new RuntimeException("No sensor available");
        }
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (mSensor == null) {
            throw new RuntimeException("No Accelerometer sensor available");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Accelerometer service started");
        startRecordingAcceleration();
        // restart the service if get killed
        return START_STICKY;
    }
    @Override
    public void onDestroy(){
    }
    private void startRecordingAcceleration(){
        mSensorManager.registerListener(new SensorEventListener() {
            private static final double MAGNITUDE_THRESHOLD = 5;
            private double mPreviousMagnitude = 0;
            private ArrayList<Double> previousMagnitudes = new ArrayList<Double>();
            private boolean isMoving = false;
            private final DateFormat sDateFormatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US);
            private String startDate;
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                double magnitude = Math.sqrt(x * x + y * y + z * z);
                if (Math.abs(magnitude - mPreviousMagnitude) >= MAGNITUDE_THRESHOLD){
                    Log.i(TAG, "Accelerometer: " + magnitude);
                    if (isMoving) {
                        previousMagnitudes.add(magnitude);
                    } else {
                        startDate = sDateFormatter.format(new Date());
                        isMoving = true;
                    }
                    // MongoDB.getInstance().insertAccelerometerData(magnitude);
                }
                else {
                    if (isMoving) {
                        double averageMagnitude = 0;
                        for (Double mag : previousMagnitudes) {
                            averageMagnitude += mag.doubleValue();
                        }
                        averageMagnitude /= previousMagnitudes.size();

                        MongoDB.getInstance().insertAccelerometerData(averageMagnitude, startDate);

                        previousMagnitudes.clear();
                        isMoving = false;
                    }
                }
                mPreviousMagnitude = magnitude;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.i(TAG, "Accelerometer accurancy changed: " + accuracy);
            }
        }, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}

