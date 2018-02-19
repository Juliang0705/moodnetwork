package com.moodnetwork.service;

import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.moodnetwork.MoodNetworkApplication;
import com.moodnetwork.database.MongoDB;

/**
 * This is GPS service runs in the background collecting user's location
 */
public class GPSService extends Service {

    public static final String TAG = GPSService.class.getCanonicalName();
    public static final int GPS_UPDATE_INTERVAL = 60000; //every minute
    public static final float GPS_MIN_DISTANCE = 10; // meters

    private LocationManager mLocationManager;

    private LocationListener mNetworkLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //@TODO save the data
            Log.i(TAG, "GPS: " + location.toString());
            MongoDB.getInstance().insertGPSData(location.getLatitude(), location.getLongitude());
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "onStatusChanged: " + provider + " " + status + " " + extras.toString());
        }
        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderEnabled: " + provider);
        }
        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG, "onProviderDisabled: " + provider);
        }
    };

    @Override
    public void onCreate() {
        Log.i(TAG, "GPS service created");
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "GPS service started");
        startRecordingLocation();
        // restart the service if get killed
        return START_STICKY;
    }
    @Override
    public void onDestroy(){
    }
    private void startRecordingLocation() {
        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                                    GPS_UPDATE_INTERVAL,
                                                    GPS_MIN_DISTANCE,
                                                    mNetworkLocationListener);
        }catch (SecurityException e) {
            Log.e(TAG, "Failed to request location: " + e.getMessage());
        }catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
}
