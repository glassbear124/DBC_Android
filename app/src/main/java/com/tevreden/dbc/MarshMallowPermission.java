package com.tevreden.dbc;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class MarshMallowPermission {
    Activity activity;

    public MarshMallowPermission(Activity activity) {
        this.activity = activity;
    }

    public boolean checkPermissionForReadExternalStorage() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    public boolean checkPermissionForReadPhoneState() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    //<uses-permission android:name="android.permission.READ_PHONE_STATE" />

    public boolean checkPermissionForExternalStorage() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForCamera() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForRecordAudio() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void requestPermissionForReadExternalStorage() {
       /* if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(activity, "Microphone permission needed for recording. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {*/
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, C.REQ_READ_EXTERNAL_STORAGE);
        //}
    }

    public void requestPermissionForExternalStorage() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//            Toast.makeText(activity, "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
//        } else {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, C.REQ_WRITE_EXTERNAL_STORAGE);
//        }
    }

    public boolean checkPermissionForLocation() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForReadCalendar() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForWriteCalendar() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void requestPermissionForCamera() {
        // Should we show an explanation?
//        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)){
        // Show an expanation to the user asynchronously -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.
//            Toast.makeText(activity, "Camera permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
//        } else {
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, C.REQ_CAMERA_CLICK);
//        }
    }

    public void requestPermissionForLocation() {
       /* if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(activity, "Microphone permission needed for recording. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {*/
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, C.REQ_ACCESS_FINE_LOCATION);
        //}
    }


    public void requestPermissionForPhoneState() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, C.READ_PHONE_STATE);
    }

    public void requestPermissionForReadCalendar() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALENDAR}, C.REQ_READ_CALENDAR);
    }
    public void requestPermissionForWriteCalendar() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_CALENDAR}, C.REQ_WRITE_CALENDAR);
    }
}
