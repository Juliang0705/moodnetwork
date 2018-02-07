package com.moodnetwork.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.*;
import android.os.IBinder;
import android.util.Log;

import com.moodnetwork.database.MongoDB;


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
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                Log.i(TAG, "Accelerometer: x=" + x + " y=" + y + " z=" + z);
                MongoDB.getInstance().insertAccelerometerData(x, y, z);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.i(TAG, "Accelerometer accurancy changed: " + accuracy);
            }
        }, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}

