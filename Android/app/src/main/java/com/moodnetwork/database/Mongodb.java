package com.moodnetwork.database;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.auth.anonymous.AnonymousAuthProvider;
import com.mongodb.stitch.android.services.mongodb.*;
import com.mongodb.stitch.android.StitchClient;
import org.bson.Document;

import android.telephony.TelephonyManager;
import android.content.Context;
import android.util.Log;

import com.moodnetwork.MoodNetworkApplication;

public class MongoDB {
    private static final String TAG = MongoDB.class.getCanonicalName();

    private static final String STITCH_APP_ID = "moonnetwork-cpgmz";
    private static final String MONGO_SERVICE_PROVIDER = "mongodb-atlas";
    private static final String DATABASE_NAME = "moonnetwork";
    private static final String DEVICE_IMEI = getDeviceIMEI();

    private static final String DB_COLLECTION_GPS = "GPS";

    private static MongoDB sInstance = new MongoDB();

    private StitchClient mStitchClient;
    private MongoClient mMongoClient;
    private MongoClient.Database mDatabase;

    public static MongoDB getInstance() {
        return sInstance;
    }
    private MongoDB(){
        mStitchClient = new StitchClient(MoodNetworkApplication.getContext(), STITCH_APP_ID);
        mMongoClient = new MongoClient(mStitchClient, MONGO_SERVICE_PROVIDER);
        mDatabase = mMongoClient.getDatabase(DATABASE_NAME);
    }

    private static String getDeviceIMEI(){
        Context context = MoodNetworkApplication.getContext();
        TelephonyManager teleManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        return teleManager.getDeviceId();
    }

    private void loginToMongoDB(){
        //for development purpose there is no password for the db
        //@TODO add authentication
        mStitchClient.logInWithProvider(new AnonymousAuthProvider());

    }

    public void insertGPSData(final float latitude, final float longitude) {
        final Document new_doc = new Document();
        new_doc.put("userId", DEVICE_IMEI);
        new_doc.put("latitude", latitude);
        new_doc.put("longitude", longitude);
        mDatabase.getCollection(DB_COLLECTION_GPS).insertOne(new_doc)
                .addOnCompleteListener(new OnCompleteListener<Document>() {
                    @Override
                    public void onComplete(Task<Document> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "InsertGPSDATA: " + task.getException().getMessage());
                        }
                    }
                });
    }

}
