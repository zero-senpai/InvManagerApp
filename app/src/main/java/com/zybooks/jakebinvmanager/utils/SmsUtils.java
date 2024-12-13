package com.zybooks.jakebinvmanager.utils;

import android.telephony.SmsManager;
import android.util.Log;

/**
 * SMSUtils handles the function logic for sending the SMS to the user's device
 */
public class SmsUtils {

    public static void sendSmsNotification(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.d("SmsUtils", "SMS sent successfully");
        } catch (Exception e) {
            Log.e("SmsUtils", "Error sending SMS", e);
        }
    }
}
