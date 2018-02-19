package com.moodnetwork.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import java.io.*;
import android.net.Uri;


public class MicrophoneService extends Service implements MediaRecorder.OnInfoListener {
    public static final String TAG = MicrophoneService.class.getCanonicalName();
    private static final int MAX_RECORD_DURATION_IN_SECS = 10;
    private MediaRecorder mRecorder;
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
        File outputFile = new File(getApplicationContext().getFilesDir(), "TestAudio.mp3");
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
        mRecorder.stop();
        mRecorder.reset();
        playAudio(getOutputFilePath());
        Log.i(TAG, "Stop recording microphone now");
    }
    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            stopRecording();
        }
    }
}
