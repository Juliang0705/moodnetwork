package com.moodnetwork.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.*;
import android.os.IBinder;
import android.util.Log;
import android.os.CountDownTimer;

import com.moodnetwork.MoodNetworkApplication;
import com.moodnetwork.database.MongoDB;



public class AccelerometerService extends Service {
    public static final String TAG = AccelerometerService.class.getCanonicalName();

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private class AcclerometerEventListener extends CountDownTimer implements SensorEventListener {

        private static final double MAGNITUDE_THRESHOLD = 0.7;
        private static final long TIMEOUT_IN_SECS = 60;
        private double mXSum = 0;
        private double mYSum = 0;
        private double mZSum = 0;
        private int mCount = 0;

        AcclerometerEventListener() {
            super(TIMEOUT_IN_SECS * 1000, TIMEOUT_IN_SECS * 1000);
            start();
        }
        @Override
        public void onFinish() {
            double xAverage = 0;
            double yAverage = 0;
            double zAverage = 0;
            synchronized (this) {
                xAverage = mXSum / mCount;
                yAverage = mYSum / mCount;
                zAverage = mZSum / mCount;
                mXSum = 0;
                mYSum = 0;
                mZSum = 0;
                mCount = 0;
            }
            double magnitudeAverage = Math.sqrt(xAverage * xAverage + yAverage * yAverage + zAverage * zAverage);
            Log.i(TAG, "Acceleration for the past 1 mins is " + magnitudeAverage);
            if (magnitudeAverage >= MAGNITUDE_THRESHOLD) {
                MongoDB.getInstance().insertAccelerometerData(xAverage, yAverage, zAverage);
            }
            start();
        }
        @Override
        public void onTick(long millisUntilFinished) {
            //ignore
        }
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            synchronized (this) {
                mXSum += x;
                mYSum += y;
                mZSum += z;
                mCount += 1;
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.i(TAG, "Accelerometer accurancy changed: " + accuracy);
        }
    }

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
        mSensorManager.registerListener(new AcclerometerEventListener(),
                                        mSensor,
                                        SensorManager.SENSOR_DELAY_NORMAL);
    }
    public static void startService() {
        Context context = MoodNetworkApplication.getContext();
        context.startService(new Intent(context,AccelerometerService.class));
    }
    public static void stopService() {
        Context context = MoodNetworkApplication.getContext();
        context.stopService(new Intent(context, AccelerometerService.class));
    }
}

