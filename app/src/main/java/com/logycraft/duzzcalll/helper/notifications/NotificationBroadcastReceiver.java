package com.logycraft.duzzcalll.helper.notifications;

import android.app.Activity;
import android.app.Notification;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.logycraft.duzzcalll.LinphoneManager;
import com.logycraft.duzzcalll.LinphoneContext;
import com.logycraft.duzzcalll.fragment.HistoryFragment;
import com.logycraft.duzzcalll.helper.compatibility.Compatibility;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.ChatMessage;
import org.linphone.core.ChatRoom;
import org.linphone.core.Core;
import org.linphone.core.tools.Log;

//import com.duzzelcall.linphone.core.Address;
//import com.duzzelcall.linphone.core.Call;
//import com.duzzelcall.linphone.core.ChatMessage;
//import com.duzzelcall.linphone.core.ChatRoom;
//import com.duzzelcall.linphone.core.Core;
//import com.duzzelcall.linphone.core.tools.Log;
//import com.duzzelcall.managinig.LinphoneContext;
//import com.duzzelcall.managinig.LinphoneManager;
//import com.duzzelcall.managinig.R;
//import com.duzzelcall.managinig.compatibility.Compatibility;

/* loaded from: classes2.dex */
public class NotificationBroadcastReceiver extends BroadcastReceiver {
    public static final String NOTIFICATION_DISMISSED_ACTION = "com.duzzcall.duzzcall.NOTIFICATION_DISMISSED";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {

        int notifId = intent.getIntExtra(Compatibility.INTENT_NOTIF_ID, 0);
        String localyIdentity = intent.getStringExtra(Compatibility.INTENT_LOCAL_IDENTITY);

        if (intent != null && intent.getAction() != null &&
                intent.getAction().equals(NOTIFICATION_DISMISSED_ACTION)) {
            // Notify the fragment about notification dismissal
//            Intent dismissIntent = new Intent(NOTIFICATION_DISMISSED_ACTION);
//            context.sendBroadcast(dismissIntent);
//            Toast.makeText(context, "lllllllllll", Toast.LENGTH_SHORT).show();

        }




        if (!LinphoneContext.isReady()) {
            Log.e("[Notification Broadcast Receiver] Context not ready, aborting...");
        } else if (intent.getAction().equals(Compatibility.INTENT_REPLY_NOTIF_ACTION) || intent.getAction().equals(Compatibility.INTENT_MARK_AS_READ_ACTION)) {
            String remoteSipAddr = LinphoneContext.instance().getNotificationManager().getSipUriForNotificationId(notifId);
            Core core = LinphoneManager.getCore();
            if (core == null) {
                Log.e("[Notification Broadcast Receiver] Couldn't get Core instance");
                onError(context, notifId);
                return;
            }
            Address remoteAddr = core.interpretUrl(remoteSipAddr);
            if (remoteAddr == null) {
                Log.e("[Notification Broadcast Receiver] Couldn't interpret remote address " + remoteSipAddr);
                onError(context, notifId);
                return;
            }
            Address localAddr = core.interpretUrl(localyIdentity);
            if (localAddr == null) {
                Log.e("[Notification Broadcast Receiver] Couldn't interpret local address " + localyIdentity);
                onError(context, notifId);
                return;
            }
            ChatRoom room = core.getChatRoom(remoteAddr, localAddr);
            if (room == null) {
                Log.e("[Notification Broadcast Receiver] Couldn't find chat room for remote address " + remoteSipAddr + " and local address " + localyIdentity);
                onError(context, notifId);
                return;
            }
            room.markAsRead();
            if (intent.getAction().equals(Compatibility.INTENT_REPLY_NOTIF_ACTION)) {
                String reply = getMessageText(intent).toString();
                if (reply == null) {
                    Log.e("[Notification Broadcast Receiver] Couldn't get reply text");
                    onError(context, notifId);
                    return;
                }
                ChatMessage msg = room.createMessage(reply);
                msg.setUserData(Integer.valueOf(notifId));
                msg.addListener(LinphoneContext.instance().getNotificationManager().getMessageListener());
                msg.send();
                Log.i("[Notification Broadcast Receiver] Reply sent for notif id " + notifId);
                return;
            }
            LinphoneContext.instance().getNotificationManager().dismissNotification(notifId);
        } else if (intent.getAction().equals(Compatibility.INTENT_ANSWER_CALL_NOTIF_ACTION) || intent.getAction().equals(Compatibility.INTENT_HANGUP_CALL_NOTIF_ACTION)) {
            String remoteAddr2 = LinphoneContext.instance().getNotificationManager().getSipUriForCallNotificationId(notifId);
            Core core2 = LinphoneManager.getCore();
            if (core2 == null) {
                Log.e("[Notification Broadcast Receiver] Couldn't get Core instance");
                return;
            }
            Call call = core2.findCallFromUri(remoteAddr2);
            if (call == null) {
                Log.e("[Notification Broadcast Receiver] Couldn't find call from remote address " + remoteAddr2);
            } else if (intent.getAction().equals(Compatibility.INTENT_ANSWER_CALL_NOTIF_ACTION)) {
                LinphoneManager.getCallManager().acceptCall(call);
            } else {
                call.terminate();
            }
        }
    }


    private void onError(Context context, int notifId) {
        Notification replyError = Compatibility.createRepliedNotification(context, "error");
        LinphoneContext.instance().getNotificationManager().sendNotification(notifId, replyError);
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(Compatibility.KEY_TEXT_REPLY);
        }
        return null;
    }
}
