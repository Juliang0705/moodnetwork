package com.moodnetwork;

import android.app.Application;
import android.content.Context;

public class MoodNetworkApplication extends Application {

    private static MoodNetworkApplication sInstance;

    @Override
    public void onCreate(){
        super.onCreate();
        sInstance = this;
    }

    public static Context getContext(){
        return sInstance.getApplicationContext();
    }
}
