package com.moodnetwork.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import java.io.*;
import android.net.Uri;

import com.moodnetwork.MoodNetworkApplication;
import com.moodnetwork.database.MongoDB;
import java.util.Random;


public class MicrophoneService extends Service implements MediaRecorder.OnInfoListener {
    public static final String TAG = MicrophoneService.class.getCanonicalName();
    private static final int MAX_RECORD_DURATION_IN_SECS = 60;
    private static final long RECORD_GAP_IN_SECS[] = {60,120,180,240,300,360,420,480,540,600};
    private MediaRecorder mRecorder;
    private static boolean running = false;

    @Override
    public void onCreate() {
        Log.i(TAG, "Microphone Service is created");
        mRecorder = new MediaRecorder();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Microphone Service is running");
        startRecording();
        return START_STICKY;
    }
    @Override
    public void onDestroy(){
    }
    private void playAudio(String filePath) {
        MediaPlayer player = MediaPlayer.create(this, Uri.parse(filePath));
        player.start();
    }
    private String getOutputFilePath() {
        File outputFile = new File(getApplicationContext().getFilesDir(), "TestAudio.amr");
        return outputFile.getAbsolutePath();
    }
    private void startRecording() {
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setOutputFile(getOutputFilePath());
        mRecorder.setAudioChannels(1);
        mRecorder.setMaxDuration(MAX_RECORD_DURATION_IN_SECS * 1000);
        mRecorder.setOnInfoListener(this);
        try {
            mRecorder.prepare();
            mRecorder.start();
            Log.i(TAG, "Starting recording microphone now");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }
    private void stopRecording() {
        Log.i(TAG, "Stop recording microphone now");
        mRecorder.stop();
        mRecorder.reset();
        //playAudio(getOutputFilePath());
        saveAudioToDatabase(getOutputFilePath());
        rescheduleRecording();
    }
    private void rescheduleRecording(){
        new Thread(){
            @Override
            public void run(){
                try {
                    long waitTime = RECORD_GAP_IN_SECS[new Random().nextInt(RECORD_GAP_IN_SECS.length)];
                    Thread.sleep(waitTime * 1000);

                    Log.i(TAG, "Rescheduling recording, wait for " + waitTime);
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
                startRecording();
            }
        }.start();
    }
    private void saveAudioToDatabase(String filepath) {
        File audioFile = new File(filepath);
        if (audioFile.exists()) {
            Log.i(TAG, "Length of file is " + audioFile.length());
            try {
                byte[] fileData = new byte[(int) audioFile.length()];
                DataInputStream dis = new DataInputStream(new FileInputStream(audioFile));
                dis.readFully(fileData);
                dis.close();
                MongoDB.getInstance().insertMicrophoneData(fileData);
                //playAudio(filepath);
                audioFile.delete();

            }catch(IOException e ) {
                Log.e(TAG, e.getMessage());
            }
        } else{
            Log.e(TAG, "File doesn't exist");
        }
    }
    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            stopRecording();
        }
    }
    public static void startService() {
        Context context = MoodNetworkApplication.getContext();
        context.startService(new Intent(context,MicrophoneService.class));
        running = true;
    }
    public static void stopService() {
        Context context = MoodNetworkApplication.getContext();
        context.stopService(new Intent(context, MicrophoneService.class));
        running = false;
    }

    public static boolean isRunning() {
        return running;
    }
}
