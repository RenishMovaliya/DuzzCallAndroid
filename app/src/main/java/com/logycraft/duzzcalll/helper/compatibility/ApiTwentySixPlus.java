package com.logycraft.duzzcalll.helper.compatibility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.duzzcall.duzzcall.R;
import com.logycraft.duzzcalll.helper.notifications.Notifiable;
import com.logycraft.duzzcalll.helper.notifications.NotifiableMessage;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ApiTwentySixPlus {
    ApiTwentySixPlus() {
    }

    public static String getDeviceName(Context context) {
        BluetoothAdapter adapter;
        String name = Settings.Global.getString(context.getContentResolver(), "device_name");
        if (name == null && (adapter = BluetoothAdapter.getDefaultAdapter()) != null) {
//            name = adapter.getName();
        }
        if (name == null) {
            name = Settings.Secure.getString(context.getContentResolver(), "bluetooth_name");
        }
        if (name == null) {
            return Build.MANUFACTURER + " " + Build.MODEL;
        }
        return name;
    }

    public static Notification createRepliedNotification(Context context, String reply) {
        return new Notification.Builder(context, context.getString(R.string.notification_channel_id)).setSmallIcon(R.drawable.app_logo).setContentText(context.getString(R.string.notification_replied_label).replace("%s", reply)).build();
    }


    public static void createServiceChannel(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String id = context.getString(R.string.notification_service_channel_id);
        CharSequence name = context.getString(R.string.content_title_notification_service);
        String description = context.getString(R.string.content_title_notification_service);
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
        channel.setDescription(description);
        channel.enableVibration(false);
        channel.enableLights(false);
        channel.setShowBadge(false);
        notificationManager.createNotificationChannel(channel);
    }

    public static void createMessageChannel(Context context) {
        @SuppressLint("WrongConstant") NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        String id = context.getString(R.string.notification_channel_id);
        String name = context.getString(R.string.content_title_notification);
        String description = context.getString(R.string.content_title_notification);
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(description);
        channel.setLightColor(context.getColor(R.color.notification_led_color));
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setShowBadge(true);
        notificationManager.createNotificationChannel(channel);
    }

    public static Notification createMessageNotification(Context context, Notifiable notif, Bitmap contactIcon, PendingIntent intent) {
        Notification.MessagingStyle style = new Notification.MessagingStyle(notif.getMyself());
        for (NotifiableMessage message : notif.getMessages()) {
            Notification.MessagingStyle.Message msg = new Notification.MessagingStyle.Message(message.getMessage(), message.getTime(), message.getSender());
            if (message.getFilePath() != null) {
                msg.setData(message.getFileMime(), message.getFilePath());
            }
            style.addMessage(msg);
        }
        if (notif.isGroup()) {
            style.setConversationTitle(notif.getGroupTitle());
        }
        return new Notification.Builder(context, context.getString(R.string.notification_channel_id)).setSmallIcon(R.drawable.app_logo).setAutoCancel(true).setContentIntent(intent).setDefaults(7).setLargeIcon(contactIcon).setCategory(NotificationCompat.CATEGORY_MESSAGE).setGroup(Compatibility.CHAT_NOTIFICATIONS_GROUP).setVisibility(Notification.VISIBILITY_PRIVATE).setPriority(Notification.PRIORITY_HIGH).setNumber(notif.getMessages().size()).setWhen(System.currentTimeMillis()).setShowWhen(true).setColor(context.getColor(R.color.notification_led_color)).setStyle(style).addAction(Compatibility.getReplyMessageAction(context, notif)).addAction(Compatibility.getMarkMessageAsReadAction(context, notif)).build();
    }

    public static Notification createInCallNotification(Context context, int callId, String msg, int iconID, Bitmap contactIcon, String contactName, PendingIntent intent) {
        return new NotificationCompat.Builder(context, context.getString(R.string.notification_service_channel_id)).setContentTitle(contactName).setContentText(msg).setSmallIcon(iconID).setAutoCancel(false).setContentIntent(intent).setLargeIcon(contactIcon).setCategory(NotificationCompat.CATEGORY_CALL).setVisibility(Notification.VISIBILITY_PUBLIC).setPriority(Notification.PRIORITY_LOW).setWhen(System.currentTimeMillis()).setShowWhen(true).setOngoing(true).setColor(context.getColor(R.color.notification_led_color)).addAction(Compatibility.getCallDeclineAction(context, callId)).build();
    }

    public static Notification createIncomingCallNotification(Context context, int callId, Bitmap contactIcon, String contactName, String sipUri, PendingIntent intent) {
        RemoteViews notificationLayoutHeadsUp = new RemoteViews(context.getPackageName(), (int) R.layout.call_incoming_notification_heads_up);
        notificationLayoutHeadsUp.setTextViewText(R.id.caller, contactName);
        notificationLayoutHeadsUp.setTextViewText(R.id.sip_uri, sipUri);
        notificationLayoutHeadsUp.setTextViewText(R.id.incoming_call_info, context.getString(R.string.incall_notif_incoming));
        if (contactIcon != null) {
            notificationLayoutHeadsUp.setImageViewBitmap(R.id.caller_picture, contactIcon);
        }
        return new NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id)).setStyle(new NotificationCompat.DecoratedCustomViewStyle()).setSmallIcon(R.drawable.app_logo).setContentTitle(contactName).setContentText(context.getString(R.string.incall_notif_incoming)).setContentIntent(intent).setCategory(NotificationCompat.CATEGORY_CALL).setVisibility(Notification.VISIBILITY_PUBLIC).setPriority(Notification.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis()).setAutoCancel(false).setShowWhen(true).setOngoing(true)
                .setColor(context.getColor(R.color.notification_led_color)).setFullScreenIntent(intent, true)
                .addAction(Compatibility.getCallDeclineAction(context, callId)).addAction(Compatibility.getCallAnswerAction(context, callId))
                .setCustomHeadsUpContentView(notificationLayoutHeadsUp).build();
    }

    public static Notification createNotification(Context context, String title, String message, int icon, int level, Bitmap largeIcon, PendingIntent intent, int priority, boolean ongoing) {
        if (largeIcon != null) {
            return new Notification.Builder(context, context.getString(R.string.notification_service_channel_id)).setContentTitle(title).setContentText(message).setSmallIcon(icon, level).setLargeIcon(largeIcon).setContentIntent(intent).setCategory(NotificationCompat.CATEGORY_SERVICE).setVisibility(Notification.VISIBILITY_SECRET).setPriority(priority).setWhen(System.currentTimeMillis()).setShowWhen(true).setOngoing(ongoing).setColor(context.getColor(R.color.notification_led_color)).build();
        }
        return new Notification.Builder(context, context.getString(R.string.notification_service_channel_id)).setContentTitle(title).setContentText(message).setSmallIcon(icon, level).setContentIntent(intent).setCategory(NotificationCompat.CATEGORY_SERVICE).setVisibility(Notification.VISIBILITY_SECRET).setPriority(priority).setWhen(System.currentTimeMillis()).setShowWhen(true).setColor(context.getColor(R.color.notification_led_color)).build();
    }

    public static Notification createMissedCallNotification(Context context, String title, String text, PendingIntent intent, int count) {
        return new Notification.Builder(context, context.getString(R.string.notification_channel_id)).setContentTitle(title).setContentText(text).setSmallIcon(R.drawable.call_status_missed).setAutoCancel(true).setContentIntent(intent).setDefaults(3).setCategory(NotificationCompat.CATEGORY_EVENT).setVisibility(Notification.VISIBILITY_PRIVATE).setPriority(Notification.PRIORITY_HIGH).setWhen(System.currentTimeMillis()).setShowWhen(true).setNumber(count).setColor(context.getColor(R.color.notification_led_color)).build();
    }

    public static Notification createSimpleNotification(Context context, String title, String text, PendingIntent intent) {
        return new Notification.Builder(context, context.getString(R.string.notification_channel_id)).setContentTitle(title).setContentText(text).setSmallIcon(R.drawable.app_logo).setAutoCancel(true).setContentIntent(intent).setDefaults(3).setCategory(NotificationCompat.CATEGORY_MESSAGE).setVisibility(Notification.VISIBILITY_PRIVATE).setPriority(Notification.PRIORITY_HIGH).setWhen(System.currentTimeMillis()).setShowWhen(true).setColorized(true).setColor(context.getColor(R.color.notification_led_color)).build();
    }

    public static void startService(Context context, Intent intent) {
        context.startForegroundService(intent);
    }

    public static void setFragmentTransactionReorderingAllowed(FragmentTransaction transaction, boolean allowed) {
        transaction.setReorderingAllowed(allowed);
    }

    @SuppressLint("NewApi")
    public static void enterPipMode(Activity activity) {
        boolean supportsPip = activity.getPackageManager().hasSystemFeature("android.software.picture_in_picture");
//        Log.i("[Call] Is picture in picture supported: " + supportsPip);
        if (supportsPip) {
            activity.enterPictureInPictureMode();
        }
    }

    public static void vibrate(Vibrator vibrator) {
        long[] timings = {0, 1000, 1000};
        int[] amplitudes = {0, -1, 0};
        VibrationEffect effect = VibrationEffect.createWaveform(timings, amplitudes, 1);
        AudioAttributes audioAttrs = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build();
        vibrator.vibrate(effect, audioAttrs);
    }
}
