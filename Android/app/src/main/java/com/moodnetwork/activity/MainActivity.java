package com.moodnetwork.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.content.SharedPreferences;
import android.app.usage.*;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.*;

import com.moodnetwork.database.Model.Questionnaire;
import com.moodnetwork.database.MongoDB;
import com.moodnetwork.service.*;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout;

import com.moodnetwork.HelpPage;
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
    private File f;
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

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Permission Needed")
                                .setMessage("We recommend you allow camera permissions to assist with our research.")
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ActivityCompat.requestPermissions(MainActivity.this,
                                                        new String[]{Manifest.permission.CAMERA},
                                                        MY_PERMISSIONS_REQUEST_CAMERA);
                                            }
                                        })
                                .setNegativeButton("Cancel", null)
                                .create()
                                .show();
                    }
                    else{
                        // "Never ask again"
                        Context context = getApplicationContext();
                        CharSequence text = "Never ask again!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        Snackbar s = Snackbar.make(findViewById(R.id.main), "Camera permissions denied", Snackbar.LENGTH_LONG);
                        s.setAction("LEARN MORE",
                            new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setMessage("Open Settings, and turn on camera permissions for MoodChecker.")
                                            .setPositiveButton("Open Settings",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                                        }
                                                    })
                                            .setNegativeButton("Cancel", null)
                                            .create()
                                            .show();
                                }
                        });
                        s.show();

                    }
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
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
           || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO},
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

        String message = "No Data";
        //get date from file saved on device
        try {
            f = new File(getApplicationContext().getFilesDir(), "lastsurvey.txt");
            FileInputStream is = new FileInputStream(f);
            Scanner s = new Scanner(is);
            message = s.hasNext() ? s.nextLine() : "No Data"; //if the file is empty put no data
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //set the date on the screen
        TextView textView = (TextView) findViewById(R.id.dateText);
        textView.setText(message);
        removeAllNotifications();

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
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                MongoDB.getInstance().insertSelfieData(image);
            }
            // do whatever you want with the image now}

            Context context = getApplicationContext();
            CharSequence text = "Image Saved!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    //opens the camera
    public void openCamera(View view){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                //request permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
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
    //start help page activity
    public void goToHelpPage(View view){
        Intent intent = new Intent(this, HelpPage.class);
        startActivity(intent);
    }
    private void startBackgroundServices() {
        Log.i(TAG,"Started background service");

        SharedPreferences settings = getSharedPreferences("com.mobileapp.smartapplocker", 0);
        //creates the locations setting from the shared preferences
        boolean locationKey = settings.getBoolean("lockey", false);
        //creates the microphone setting from the shared preferences
        boolean microphoneKey = settings.getBoolean("microkey", false);
        //creates the push notification setting from the shared preferences
        boolean pushNotificationKey = settings.getBoolean("pushkey", false);

        getApplicationContext().startService(new Intent(this,AccelerometerService.class));
        AppUsageService.startService();
        if(locationKey == true) {
            GPSService.startService();
        }
        if(pushNotificationKey == true) {
            NotificationService.startService();
        }
        if(microphoneKey == true) {
            MicrophoneService.startService();
        }
    }
    private void removeAllNotifications() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }


}