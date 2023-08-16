package com.logycraft.duzzcalll.helper.compatibility;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.duzzcall.duzzcall.R;
import com.logycraft.duzzcalll.helper.notifications.Notifiable;
import com.logycraft.duzzcalll.helper.notifications.NotificationBroadcastReceiver;


/* loaded from: classes2.dex */
public class ApiTwentyNinePlus {

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static Notification.Action getReplyMessageAction(Context context, Notifiable notif) {
        String replyLabel = context.getResources().getString(R.string.notification_reply_label);
        RemoteInput remoteInput = new RemoteInput.Builder(Compatibility.KEY_TEXT_REPLY).setLabel(replyLabel).build();
        Intent replyIntent = new Intent(context, NotificationBroadcastReceiver.class);
        replyIntent.setAction(Compatibility.INTENT_REPLY_NOTIF_ACTION);
        replyIntent.putExtra(Compatibility.INTENT_NOTIF_ID, notif.getNotificationId());
        replyIntent.putExtra(Compatibility.INTENT_LOCAL_IDENTITY, notif.getLocalIdentity());
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context, notif.getNotificationId(), replyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new Notification.Action.Builder((int) R.drawable.chat_send_over, context.getString(R.string.notification_reply_label), replyPendingIntent).addRemoteInput(remoteInput).setAllowGeneratedReplies(true).setSemanticAction(Notification.Action.SEMANTIC_ACTION_REPLY).build();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static Notification.Action getMarkMessageAsReadAction(Context context, Notifiable notif) {
        Intent markAsReadIntent = new Intent(context, NotificationBroadcastReceiver.class);
        markAsReadIntent.setAction(Compatibility.INTENT_MARK_AS_READ_ACTION);
        markAsReadIntent.putExtra(Compatibility.INTENT_NOTIF_ID, notif.getNotificationId());
        markAsReadIntent.putExtra(Compatibility.INTENT_LOCAL_IDENTITY, notif.getLocalIdentity());
        PendingIntent markAsReadPendingIntent = PendingIntent.getBroadcast(context, notif.getNotificationId(), markAsReadIntent, 201326592);
        return new Notification.Action.Builder((int) R.drawable.chat_send_over, context.getString(R.string.notification_mark_as_read_label), markAsReadPendingIntent).setSemanticAction(Notification.Action.SEMANTIC_ACTION_MARK_AS_READ).build();
    }

    public static NotificationCompat.Action getCallAnswerAction(Context context, int callId) {
        Intent answerIntent = new Intent(context, NotificationBroadcastReceiver.class);
        answerIntent.setAction(Compatibility.INTENT_ANSWER_CALL_NOTIF_ACTION);
        answerIntent.putExtra(Compatibility.INTENT_NOTIF_ID, callId);
        PendingIntent answerPendingIntent = PendingIntent.getBroadcast(context, callId, answerIntent, 201326592);
        return new NotificationCompat.Action.Builder((int) R.drawable.rounded_button__accept, context.getString(R.string.notification_call_answer_label), answerPendingIntent).build();
    }

    public static NotificationCompat.Action getCallDeclineAction(Context context, int callId) {
        Intent hangupIntent = new Intent(context, NotificationBroadcastReceiver.class);
        hangupIntent.setAction(Compatibility.INTENT_HANGUP_CALL_NOTIF_ACTION);
        hangupIntent.putExtra(Compatibility.INTENT_NOTIF_ID, callId);
        PendingIntent hangupPendingIntent = PendingIntent.getBroadcast(context, callId, hangupIntent, 201326592);
        return new NotificationCompat.Action.Builder((int)  R.drawable.rounded_button_decline, context.getString(R.string.notification_call_hangup_label), hangupPendingIntent).build();
    }
}
