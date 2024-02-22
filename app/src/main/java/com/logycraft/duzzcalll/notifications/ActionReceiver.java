package com.logycraft.duzzcalll.notifications;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.logycraft.duzzcalll.Activity.DashboardActivity;
import com.logycraft.duzzcalll.Activity.Verify_PhoneActivity;
import com.logycraft.duzzcalll.helper.notifications.NotificationsManager;

public class ActionReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(NotificationsManager.MISSED_CALLS_NOTIF_ID)) {
            int notificationId = 1;
            if (notificationId != -1) {
                // Handle notification dismissal
                // This could be used to update your app's state or perform some other action
                Log.d("NotificationDismiss", "Notification dismissed with ID: " + notificationId);
            }
        }
    }

}