package com.logycraft.duzzcalll.helper.compatibility;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import org.linphone.mediastream.Version;
import com.logycraft.duzzcalll.helper.notifications.Notifiable;



public class Compatibility {
    public static final String CHAT_NOTIFICATIONS_GROUP = "CHAT_NOTIF_GROUP";
    public static final String INTENT_ANSWER_CALL_NOTIF_ACTION = "org.linphone.ANSWER_CALL_ACTION";
    public static final String INTENT_HANGUP_CALL_NOTIF_ACTION = "org.linphone.HANGUP_CALL_ACTION";
    public static final String INTENT_LOCAL_IDENTITY = "LOCAL_IDENTITY";
    public static final String INTENT_MARK_AS_READ_ACTION = "org.linphone.MARK_AS_READ_ACTION";
    public static final String INTENT_NOTIF_ID = "NOTIFICATION_ID";
    public static final String INTENT_REPLY_NOTIF_ACTION = "org.linphone.REPLY_ACTION";
    public static final String KEY_TEXT_REPLY = "key_text_reply";

    public static String getDeviceName(Context context) {
        if (Version.sdkAboveOrEqual(25)) {
            return ApiTwentySixPlus.getDeviceName(context);
        }
        String name = null;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                name = adapter.getName();
                return name;
            }
            name = adapter.getName();
        }
        if (name == null) {
            name = Settings.Secure.getString(context.getContentResolver(), "bluetooth_name");
        }
        if (name == null) {
            String name2 = Build.MANUFACTURER + " " + Build.MODEL;
            return name2;
        }
        return name;
    }

    public static void createNotificationChannels(Context context) {
        if (Version.sdkAboveOrEqual(26)) {
            ApiTwentySixPlus.createServiceChannel(context);
            ApiTwentySixPlus.createMessageChannel(context);
        }
    }

    public static Notification createSimpleNotification(Context context, String title, String text, PendingIntent intent) {
        if (Version.sdkAboveOrEqual(26)) {
            return ApiTwentySixPlus.createSimpleNotification(context, title, text, intent);
        }
        return  ApiTwentySixPlus.createSimpleNotification(context, title, text, intent);
//        return ApiTwentyOnePlus.createSimpleNotification(context, title, text, intent);
    }

    public static Notification createMessageNotification(Context context, Notifiable notif, String msgSender, String msg, Bitmap contactIcon, PendingIntent intent) {
//        if (Version.sdkAboveOrEqual(28)) {
//            return ApiTwentyEightPlus.createMessageNotification(context, notif, contactIcon, intent);
//        }
//        if (Version.sdkAboveOrEqual(26)) {
            return ApiTwentySixPlus.createMessageNotification(context, notif, contactIcon, intent);
//        }
//        if (Version.sdkAboveOrEqual(24)) {
//            return ApiTwentyFourPlus.createMessageNotification(context, notif, contactIcon, intent);
//        }
//        return ApiTwentyOnePlus.createMessageNotification(context, notif.getMessages().size(), msgSender, msg, contactIcon, intent);
    }

    public static Notification createRepliedNotification(Context context, String reply) {
        if (Version.sdkAboveOrEqual(26)) {
            return ApiTwentySixPlus.createRepliedNotification(context, reply);
        }
//        if (Version.sdkAboveOrEqual(24)) {
//            return ApiTwentyFourPlus.createRepliedNotification(context, reply);
//        }
        return null;
    }

    public static Notification createMissedCallNotification(Context context, String title, String text, PendingIntent intent, int count) {
        if (Version.sdkAboveOrEqual(26)) {
            return ApiTwentySixPlus.createMissedCallNotification(context, title, text, intent, count);
        }
//        return ApiTwentyOnePlus.createMissedCallNotification(context, title, text, intent, count);
        return ApiTwentySixPlus.createMissedCallNotification(context, title, text, intent, count);
    }

    public static Notification createInCallNotification(Context context, int callId, String msg, int iconID, Bitmap contactIcon, String contactName, PendingIntent intent) {
        if (Version.sdkAboveOrEqual(26)) {
            return ApiTwentySixPlus.createInCallNotification(context, callId, msg, iconID, contactIcon, contactName, intent);
        }
        if (Version.sdkAboveOrEqual(24)) {
//            return ApiTwentyFourPlus.createInCallNotification(context, callId, msg, iconID, contactIcon, contactName, intent);
        }
//        return ApiTwentyOnePlus.createInCallNotification(context, msg, iconID, contactIcon, contactName, intent);
        return ApiTwentySixPlus.createInCallNotification(context, callId, msg, iconID, contactIcon, contactName, intent);
    }

    public static Notification createIncomingCallNotification(Context context, int callId, Bitmap contactIcon, String contactName, String sipUri, PendingIntent intent) {
        if (Version.sdkAboveOrEqual(26)) {
            return ApiTwentySixPlus.createIncomingCallNotification(context, callId, contactIcon, contactName, sipUri, intent);
        }
        if (Version.sdkAboveOrEqual(24)) {
//            return ApiTwentyFourPlus.createIncomingCallNotification(context, callId, contactIcon, contactName, sipUri, intent);
        }
        return ApiTwentySixPlus.createIncomingCallNotification(context, callId, contactIcon, contactName, sipUri, intent);

//        return ApiTwentyOnePlus.createIncomingCallNotification(context, contactIcon, contactName, sipUri, intent);
    }

    public static Notification createNotification(Context context, String title, String message, int icon, int iconLevel, Bitmap largeIcon, PendingIntent intent, int priority, boolean ongoing) {
        if (Version.sdkAboveOrEqual(26)) {
            return ApiTwentySixPlus.createNotification(context, title, message, icon, iconLevel, largeIcon, intent, priority, ongoing);
        }
//        return ApiTwentyOnePlus.createNotification(context, title, message, icon, iconLevel, largeIcon, intent, priority, ongoing);
        return ApiTwentySixPlus.createNotification(context, title, message, icon, iconLevel, largeIcon, intent, priority, ongoing);
    }

    public static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    public static void startService(Context context, Intent intent) {
        if (Version.sdkAboveOrEqual(26)) {
            ApiTwentySixPlus.startService(context, intent);
        } else {
            context.startService(intent);
        }
    }

    public static void setFragmentTransactionReorderingAllowed(FragmentTransaction transaction, boolean allowed) {
        if (Version.sdkAboveOrEqual(26)) {
            ApiTwentySixPlus.setFragmentTransactionReorderingAllowed(transaction, allowed);
        }
    }

    public static void closeContentProviderClient(ContentProviderClient client) {
//        if (Version.sdkAboveOrEqual(24)) {
//            ApiTwentyFourPlus.closeContentProviderClient(client);
//        } else {
//            ApiTwentyOnePlus.closeContentProviderClient(client);
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static boolean isAppUserRestricted(Context context) {
        if (Version.sdkAboveOrEqual(28)) {
            return ApiTwentyEightPlus.isAppUserRestricted(context);
        }
        return false;
    }

    public static boolean isAppIdleMode(Context context) {
        if (Version.sdkAboveOrEqual(23)) {
//            return ApiTwentyThreePlus.isAppIdleMode(context);
        }
        return false;
    }

    public static int getAppStandbyBucket(Context context) {
        if (Version.sdkAboveOrEqual(28)) {
//            return ApiTwentyEightPlus.getAppStandbyBucket(context);
        }
        return 0;
    }

    public static String getAppStandbyBucketNameFromValue(int bucket) {
        if (Version.sdkAboveOrEqual(28)) {
            return ApiTwentyEightPlus.getAppStandbyBucketNameFromValue(bucket);
        }
        return null;
    }

    public static void setShowWhenLocked(Activity activity, boolean enable) {
        if (Version.sdkStrictlyBelow(27)) {
//            ApiTwentyOnePlus.setShowWhenLocked(activity, enable);
        }
    }

    public static void setTurnScreenOn(Activity activity, boolean enable) {
        if (Version.sdkStrictlyBelow(27)) {
//            ApiTwentyOnePlus.setTurnScreenOn(activity, enable);
        }
    }

    public static boolean isDoNotDisturbSettingsAccessGranted(Context context) {
        if (Version.sdkAboveOrEqual(23)) {
//            return ApiTwentyThreePlus.isDoNotDisturbSettingsAccessGranted(context);
        }
        return true;
    }

//    public static boolean isDoNotDisturbPolicyAllowingRinging(Context context, Address remoteAddress) {
//        if (Version.sdkAboveOrEqual(23)) {
//            return ApiTwentyThreePlus.isDoNotDisturbPolicyAllowingRinging(context, remoteAddress);
//        }
//        return true;
//    }

//    public static void createChatShortcuts(Context context) {
//        if (Version.sdkAboveOrEqual(25)) {
//            ApiTwentyFivePlus.createChatShortcuts(context);
//        }
//    }

    public static void removeChatShortcuts(Context context) {
//        if (Version.sdkAboveOrEqual(25)) {
//            ApiTwentyFivePlus.removeChatShortcuts(context);
//        }
    }

    public static void enterPipMode(Activity activity) {
        if (Version.sdkAboveOrEqual(26)) {
            ApiTwentySixPlus.enterPipMode(activity);
        }
    }

    @SuppressLint("NewApi")

    public static Notification.Action getReplyMessageAction(Context context, Notifiable notif) {
        if (Version.sdkAboveOrEqual(29)) {
            return ApiTwentyNinePlus.getReplyMessageAction(context, notif);
        }
        if (Version.sdkAboveOrEqual(28)) {
            return ApiTwentyEightPlus.getReplyMessageAction(context, notif);
        }
        if (Version.sdkAboveOrEqual(24)) {
//            return ApiTwentyFourPlus.getReplyMessageAction(context, notif);
        }
        return null;
    }
    @SuppressLint("NewApi")
    public static Notification.Action getMarkMessageAsReadAction(Context context, Notifiable notif) {
        if (Version.sdkAboveOrEqual(29)) {
            return ApiTwentyNinePlus.getMarkMessageAsReadAction(context, notif);
        }
        if (Version.sdkAboveOrEqual(28)) {
            return ApiTwentyEightPlus.getMarkMessageAsReadAction(context, notif);
        }
        if (Version.sdkAboveOrEqual(24)) {
//            return ApiTwentyFourPlus.getMarkMessageAsReadAction(context, notif);
        }
        return null;
    }

    public static Notification.Action getCallAnswerAction(Context context, int callId) {
        if (Version.sdkAboveOrEqual(29)) {
            return ApiTwentyNinePlus.getCallAnswerAction(context, callId);
        }
        if (Version.sdkAboveOrEqual(24)) {
//            return ApiTwentyFourPlus.getCallAnswerAction(context, callId);
        }
        return null;
    }

    public static Notification.Action getCallDeclineAction(Context context, int callId) {
        if (Version.sdkAboveOrEqual(29)) {
            return ApiTwentyNinePlus.getCallDeclineAction(context, callId);
        }
        if (Version.sdkAboveOrEqual(24)) {
//            return ApiTwentyFourPlus.getCallDeclineAction(context, callId);
        }
        return null;
    }

    public static StatusBarNotification[] getActiveNotifications(NotificationManager manager) {
        if (Version.sdkAboveOrEqual(23)) {
//            return ApiTwentyThreePlus.getActiveNotifications(manager);
        }
        return new StatusBarNotification[0];
    }

    public static void vibrate(Vibrator vibrator) {
        if (Version.sdkAboveOrEqual(26)) {
            ApiTwentySixPlus.vibrate(vibrator);
        } else {
//            ApiTwentyOnePlus.vibrate(vibrator);
        }
    }
}
