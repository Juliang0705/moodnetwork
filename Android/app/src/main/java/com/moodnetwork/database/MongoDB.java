package com.moodnetwork.database;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.auth.anonymous.AnonymousAuthProvider;
import com.mongodb.stitch.android.services.mongodb.*;
import com.mongodb.stitch.android.StitchClient;
import org.bson.Document;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.util.Log;

import java.text.DateFormat;
import java.util.Locale;
import java.util.UUID;
import java.util.Date;
import java.text.SimpleDateFormat;

import com.moodnetwork.MoodNetworkApplication;

public class MongoDB {
    private static final String TAG = MongoDB.class.getCanonicalName();

    private static final String STITCH_APP_ID = "moonnetwork-cpgmz";
    private static final String MONGO_SERVICE_PROVIDER = "mongodb-atlas";
    private static final String DATABASE_NAME = "moonnetwork";
    private static final String USER_ID = getUserID();

    private static final String DB_COLLECTION_GPS = "GPS";
    private static final String DB_COLLECTION_ACCELEROMETER = "Accelerometer";

    private static final DateFormat sDateFormatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US);

    private static MongoDB sInstance = new MongoDB();

    private StitchClient mStitchClient;
    private MongoClient mMongoClient;
    private MongoClient.Database mDatabase;
    private interface OnCompleteHandler{
        void handle();
    }
    public static MongoDB getInstance() {
        return sInstance;
    }
    private MongoDB() {
        mStitchClient = new StitchClient(MoodNetworkApplication.getContext(), STITCH_APP_ID);
        mMongoClient = new MongoClient(mStitchClient, MONGO_SERVICE_PROVIDER);
        mDatabase = mMongoClient.getDatabase(DATABASE_NAME);
        Log.i(TAG, "MongoDB instance created");
    }

    private static String getUserID(){
        SharedPreferences settings = MoodNetworkApplication.getContext().getSharedPreferences(TAG, 0);
        String userId = "";
        userId = settings.getString("userId", "");
        if (userId.isEmpty()) {
            userId = UUID.randomUUID().toString();
            Editor editor = settings.edit();
            editor.putString("userId", userId);
            editor.commit();
        }
        return userId;
    }

    private static Document getNewDocument(){
        Document new_doc = new Document();
        new_doc.put("userId", USER_ID);
        new_doc.put("date", sDateFormatter.format(new Date()));
        return new_doc;
    }

    private void accessMongoDB(final OnCompleteHandler handler){
        if (mStitchClient.isAuthenticated()){
            handler.handle();
        }else{
            //for development purpose there is no password for the db
            //@TODO add authentication
            mStitchClient.logInWithProvider(new AnonymousAuthProvider())
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "Log into MongoDB successfully");
                                handler.handle();
                            }else{
                                Log.e(TAG, "Log into MongoDB failed: " + task.getException().getMessage());
                            }
                        }
                    });
        }
    }

    public void insertGPSData(final double latitude, final double longitude) {
        accessMongoDB(new OnCompleteHandler() {
            @Override
            public void handle() {
                final Document new_doc = getNewDocument();
                new_doc.put("latitude", latitude);
                new_doc.put("longitude", longitude);
                mDatabase.getCollection(DB_COLLECTION_GPS)
                        .insertOne(new_doc)
                        .addOnCompleteListener(new OnCompleteListener<Document>() {
                            @Override
                            public void onComplete(Task<Document> task) {
                                if (!task.isSuccessful()) {
                                    Log.e(TAG, "InsertGPSDATA: " + task.getException().getMessage());
                                }
                            }
                        });
            }
        });
    }
    public void insertAccelerometerData(final double x, final double y, final double z){
        accessMongoDB(new OnCompleteHandler() {
            @Override
            public void handle() {
                final Document new_doc = getNewDocument();
                new_doc.put("x", x);
                new_doc.put("y", y);
                new_doc.put("z", z);
                mDatabase.getCollection(DB_COLLECTION_ACCELEROMETER)
                        .insertOne(new_doc)
                        .addOnCompleteListener(new OnCompleteListener<Document>() {
                            @Override
                            public void onComplete(@NonNull Task<Document> task) {
                                if (!task.isSuccessful()) {
                                    Log.e(TAG, "insertAccelerometerData: " + task.getException().getMessage());
                                }
                            }
                        });
            }
        });
    }

}
