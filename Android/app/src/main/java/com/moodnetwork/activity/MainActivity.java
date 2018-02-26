package com.moodnetwork.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.moodnetwork.R;
import com.moodnetwork.Settings;
import com.moodnetwork.SurveyFinished;
import com.moodnetwork.SurveyMain;
import com.moodnetwork.service.AccelerometerService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.moodnetwork.SurveyFinished.TIME_STAMP;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getCanonicalName();
    // Not sure what this return code is actually supposed to be besides a final int that's >= 0
    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private boolean cameraFunctionality = false;
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.

                    startBackgroundServices();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    // tell the user they need to accept the permissions to use the app?
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    cameraFunctionality = true;

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    cameraFunctionality = false;
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {
            // Permission has already been granted
            startBackgroundServices();
        }

        //this will be pulled from the database instead of an intent
        String message = "No Data";
        Intent intent = getIntent();
        if(intent.hasExtra(TIME_STAMP)) {
            message = intent.getStringExtra(TIME_STAMP);
        }
        //this will stay the same when pulled from database
        TextView textView = (TextView) findViewById(R.id.dateText);
        textView.setText(message);

    }
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    //handles the image data
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            // do whatever you want with the image now}
        }

        Context context = getApplicationContext();
        CharSequence text = "Image Saved!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //opens the camera
    public void openCamera(View view){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                //do we need to do anything here?
            }
            else {
                //request permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
        else{
            //already have permission
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
            //this below saves the file to the users phone
                 /*File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        System.out.println("photo created.");
                        Uri photoURI = FileProvider.getUriForFile(this,
                                "com.moodnetwork.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }*/

        }


    }

    //start survey activity
    public void goToSurvey(View view){
        Intent intent = new Intent(this, SurveyMain.class);
        startActivity(intent);
    }

    //start settings activity
    public void goToSettings(View view){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
    private void startBackgroundServices() {
        Log.i(TAG,"Started background service");
        //getApplicationContext().startService(new Intent(this, GPSService.class));
        getApplicationContext().startService(new Intent(this,AccelerometerService.class));
    }
}