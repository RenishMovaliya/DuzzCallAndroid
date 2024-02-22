package com.logycraft.duzzcalll

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class YourNotificationListenerService : NotificationListenerService() {

    companion object {
        const val NOTIFICATION_DISMISS_ACTION = "com.duzzcall.duzzcall.NOTIFICATION_DISMISSED"
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Notification dismissed, send a broadcast
        sendNotificationDismissedBroadcast()
    }

    private fun sendNotificationDismissedBroadcast() {
        val intent = Intent(NOTIFICATION_DISMISS_ACTION)
        sendBroadcast(intent)
    }

    // Rest of your NotificationListenerService code...
}
