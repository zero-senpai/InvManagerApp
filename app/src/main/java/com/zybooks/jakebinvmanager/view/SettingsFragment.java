package com.zybooks.jakebinvmanager.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.Manifest;

import com.zybooks.jakebinvmanager.R;
import com.zybooks.jakebinvmanager.controller.MainActivity;

public class SettingsFragment extends androidx.fragment.app.Fragment {

    private static final String PREF_SMS_NOTIFICATION = "sms_notification";
    private static final int SMS_PERMISSION_REQUEST_CODE = MainActivity.SMS_PERMISSION_REQUEST_CODE;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100; // Custom request code for notification permission

    private Switch smsNotificationSwitch;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.settings_fragment, container, false);

        // Set up UI elements
        smsNotificationSwitch = rootView.findViewById(R.id.smsNotificationSwitch);
        Button closeButton = rootView.findViewById(R.id.closeButton);

        // Set up close button listener
        closeButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                MainActivity activity = (MainActivity) getActivity();
                activity.closeSettingsFragment();
            }
        });

        // Set the switch state from preferences
        SharedPreferences prefs = getActivity().getSharedPreferences("app_preferences", AppCompatActivity.MODE_PRIVATE);
        boolean isSmsEnabled = prefs.getBoolean(PREF_SMS_NOTIFICATION, false);
        smsNotificationSwitch.setChecked(isSmsEnabled);

        // Set up listener for SMS notification preference
        smsNotificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save the preference to SharedPreferences
            savePreference(PREF_SMS_NOTIFICATION, isChecked);

            // Trigger SMS sending if enabled
            if (isChecked) {
                // Check if SMS permission is granted
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    sendSMS("1234567890", "Low inventory alert: Your item quantity is running low!");
                } else {
                    // Request permission if not granted
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
                }

                // Check and request POST_NOTIFICATIONS permission for Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Request permission if not granted
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
                    }
                }
            }
        });

        // Return the root view to be displayed
        return rootView;
    }


    private void sendSMS(String phoneNumber, String message) {
        // Create an Intent to send an SMS
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("sms:" + phoneNumber));  // Specify the phone number
        smsIntent.putExtra("sms_body", message);  // Set the message body

        // Check if there is an app that can handle the SMS action
        if (smsIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(smsIntent);  // Send the SMS

            // Show a notification after sending the SMS
            showSmsSentNotification();
        } else {
            Toast.makeText(getContext(), "No SMS app found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSmsSentNotification() {
        // Create the notification manager
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the notification channel (required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "sms_notification_channel";
            CharSequence name = "SMS Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        Notification notification = new NotificationCompat.Builder(getContext(), "sms_notification_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Add your notification icon
                .setContentTitle("SMS Sent")
                .setContentText("Your SMS message has been sent.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        // Show the notification
        notificationManager.notify(1, notification);
    }

    private void savePreference(String key, boolean value) {
        SharedPreferences prefs = getActivity().getSharedPreferences("app_preferences", AppCompatActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    // Handle the result of permission requests (for SMS and notification permissions)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "SMS permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "SMS permission denied!", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Notification permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Notification permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
