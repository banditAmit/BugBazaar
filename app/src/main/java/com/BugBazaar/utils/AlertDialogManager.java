package com.BugBazaar.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;

import com.BugBazaar.ui.SplashActivity;

public class AlertDialogManager {


    // Constructor to set the Context


    public void showRootedDeviceAlert(Context context, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("This device is rooted.");
        alertDialogBuilder.setMessage("Found=>> "+ message +"!Exiting in 3 Seconds.");
        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        // Schedule a task to exit the application after 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss(); // Dismiss the dialog if it's still visible
                System.exit(0);

            }
        }, 3000);
    }
}