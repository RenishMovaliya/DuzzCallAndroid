package com.logycraft.duzzcalll.helper.compatibility;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Person;
import android.app.RemoteInput;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.duzzcall.duzzcall.R;
import com.logycraft.duzzcalll.helper.notifications.Notifiable;
import com.logycraft.duzzcalll.helper.notifications.NotifiableMessage;
import com.logycraft.duzzcalll.helper.notifications.NotificationBroadcastReceiver;

/* loaded from: classes2.dex */
public class ApiTwentyEightPlus {
    ApiTwentyEightPlus() {
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static Notification createMessageNotification(Context context, Notifiable notif, Bitmap contactIcon, PendingIntent intent) {
         Person me = new Person.Builder().setName(notif.getMyself()).build();
        Notification.MessagingStyle style = new Notification.MessagingStyle(me);
        for (NotifiableMessage message : notif.getMessages()) {
            Icon userIcon = null;
            if (message.getSenderBitmap() != null) {
                userIcon = Icon.createWithBitmap(message.getSenderBitmap());
            }
            Person.Builder builder = new Person.Builder().setName(message.getSender());
            if (userIcon != null) {
                builder.setIcon(userIcon);
            }
            Person user = builder.build();
            Notification.MessagingStyle.Message msg = new Notification.MessagingStyle.Message(message.getMessage(), message.getTime(), user);
            if (message.getFilePath() != null) {
                msg.setData(message.getFileMime(), message.getFilePath());
            }
            style.addMessage(msg);
        }
        if (notif.isGroup()) {
            style.setConversationTitle(notif.getGroupTitle());
        }
        style.setGroupConversation(notif.isGroup());
        return new Notification.Builder(context, context.getString(R.string.notification_channel_id)).setSmallIcon(R.drawable.app_logo).setAutoCancel(true).setContentIntent(intent).setDefaults(7).setLargeIcon(contactIcon).setCategory(NotificationCompat.CATEGORY_MESSAGE).setGroup(Compatibility.CHAT_NOTIFICATIONS_GROUP).setVisibility(0).setPriority(1).setNumber(notif.getMessages().size()).setWhen(System.currentTimeMillis()).setShowWhen(true).setColor(context.getColor(R.color.notification_led_color)).setStyle(style).addAction(Compatibility.getReplyMessageAction(context, notif)).addAction(Compatibility.getMarkMessageAsReadAction(context, notif)).build();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static boolean isAppUserRestricted(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.isBackgroundRestricted();
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static int getAppStandbyBucket(Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        return usageStatsManager.getAppStandbyBucket();
    }

    public static String getAppStandbyBucketNameFromValue(int bucket) {
        if (bucket != 10) {
            if (bucket == 20) {
                return "STANDBY_BUCKET_WORKING_SET";
            }
            if (bucket == 30) {
                return "STANDBY_BUCKET_FREQUENT";
            }
            if (bucket == 40) {
                return "STANDBY_BUCKET_RARE";
            }
            return null;
        }
        return "STANDBY_BUCKET_ACTIVE";
    }

    public static Notification.Action getReplyMessageAction(Context context, Notifiable notif) {
        String replyLabel = context.getResources().getString(R.string.notification_reply_label);
        RemoteInput remoteInput = new RemoteInput.Builder(Compatibility.KEY_TEXT_REPLY).setLabel(replyLabel).build();
        Intent replyIntent = new Intent(context, NotificationBroadcastReceiver.class);
        replyIntent.setAction(Compatibility.INTENT_REPLY_NOTIF_ACTION);
        replyIntent.putExtra(Compatibility.INTENT_NOTIF_ID, notif.getNotificationId());
        replyIntent.putExtra(Compatibility.INTENT_LOCAL_IDENTITY, notif.getLocalIdentity());
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context, notif.getNotificationId(), replyIntent, 134217728);
        return new Notification.Action.Builder((int) R.drawable.chat_send_over, context.getString(R.string.notification_reply_label), replyPendingIntent).addRemoteInput(remoteInput).setAllowGeneratedReplies(true).setSemanticAction(1).build();
    }

    public static Notification.Action getMarkMessageAsReadAction(Context context, Notifiable notif) {
        Intent markAsReadIntent = new Intent(context, NotificationBroadcastReceiver.class);
        markAsReadIntent.setAction(Compatibility.INTENT_MARK_AS_READ_ACTION);
        markAsReadIntent.putExtra(Compatibility.INTENT_NOTIF_ID, notif.getNotificationId());
        markAsReadIntent.putExtra(Compatibility.INTENT_LOCAL_IDENTITY, notif.getLocalIdentity());
        PendingIntent markAsReadPendingIntent = PendingIntent.getBroadcast(context, notif.getNotificationId(), markAsReadIntent, 134217728);
        return new Notification.Action.Builder((int) R.drawable.chat_send_over, context.getString(R.string.notification_mark_as_read_label), markAsReadPendingIntent).setSemanticAction(2).build();
    }
}
