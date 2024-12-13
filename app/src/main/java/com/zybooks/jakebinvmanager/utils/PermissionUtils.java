package com.zybooks.jakebinvmanager.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class PermissionUtils {

    // Check if the app has permission to send SMS
    public static boolean hasSmsPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    // Request permission to send SMS
    public static void requestSmsPermission(AppCompatActivity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.SEND_SMS}, 1);
    }
}
