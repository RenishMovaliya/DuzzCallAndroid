package com.logycraft.duzzcalll.helper.notifications;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.service.notification.StatusBarNotification;


import com.duzzcall.duzzcall.R;
import com.logycraft.duzzcalll.Activity.CallActivity;
import com.logycraft.duzzcalll.Activity.DashboardActivity;
import com.logycraft.duzzcalll.Activity.IncomingActivity;
import com.logycraft.duzzcalll.Activity.OutgoingActivity;
import com.logycraft.duzzcalll.LinphoneManager;
import com.logycraft.duzzcalll.Util.LinphoneUtils;
import com.logycraft.duzzcalll.helper.compatibility.Compatibility;
import com.logycraft.duzzcalll.service.LinphoneService;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.ChatMessage;
import org.linphone.core.ChatMessageListenerStub;
import org.linphone.core.ChatRoom;
import org.linphone.core.ChatRoomCapabilities;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.Reason;
import org.linphone.core.tools.Log;
import org.linphone.core.tools.compatibility.DeviceUtils;

import java.io.File;
import java.util.HashMap;

/* loaded from: classes2.dex */
public class NotificationsManager {
    private static final int MISSED_CALLS_NOTIF_ID = 2;
    private static final int SERVICE_NOTIF_ID = 1;
    private final Context mContext;
    private int mLastNotificationId;
    private CoreListenerStub mListener;
    private ChatMessageListenerStub mMessageListener;
    private final NotificationManager mNM;
    private final Notification mServiceNotification;
    private final HashMap<String, Notifiable> mChatNotifMap = new HashMap<>();
    private final HashMap<String, Notifiable> mCallNotifMap = new HashMap<>();
    private int mCurrentForegroundServiceNotification = 0;
    private String mCurrentChatRoomAddress = null;

    public NotificationsManager(Context context) {
        PendingIntent pendingIntent;
        this.mContext = context;
        @SuppressLint("WrongConstant") NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        this.mNM = notificationManager;
        if (context.getResources().getBoolean(R.bool.keep_missed_call_notification_upon_restart)) {
            StatusBarNotification[] notifs = Compatibility.getActiveNotifications(notificationManager);
            if (notifs != null && notifs.length > 1) {
                for (StatusBarNotification notif : notifs) {
                    if (notif.getId() != 2) {
                        dismissNotification(notif.getId());
                    }
                }
            }
        } else {
            notificationManager.cancelAll();
        }
        this.mLastNotificationId = 5;
        Compatibility.createNotificationChannels(this.mContext);
        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeResource(this.mContext.getResources(), R.drawable.app_logo_notification);
        } catch (Exception e) {
            Log.e(e);
        }
        Intent notifIntent = new Intent(this.mContext, DashboardActivity.class);
        notifIntent.putExtra("Notification", true);
        addFlagsToIntent(notifIntent);
        if (Build.VERSION.SDK_INT >= 23) {
            pendingIntent = PendingIntent.getActivity(this.mContext, 0, notifIntent, 201326592);
        } else {
            pendingIntent = PendingIntent.getActivity(this.mContext, 0, notifIntent, 134217728);
        }
        Context context2 = this.mContext;
        this.mServiceNotification = Compatibility.createNotification(context2, context2.getString(R.string.service_name), "", R.drawable.app_logo_notification, R.drawable.app_logo_notification, bm, pendingIntent, -2, true);
        this.mListener = new CoreListenerStub() { // from class: com.duzzelcall.managinig.notifications.NotificationsManager.1
            @Override
            // com.duzzelcall.linphone.core.CoreListenerStub, com.duzzelcall.linphone.core.CoreListener
            public void onMessageSent(Core core, ChatRoom room, ChatMessage message) {
                if (room.hasCapability(ChatRoomCapabilities.OneToOne.toInt())) {
//                    Compatibility.createChatShortcuts(NotificationsManager.this.mContext);
                }
            }

            @Override
            // com.duzzelcall.linphone.core.CoreListenerStub, com.duzzelcall.linphone.core.CoreListener
            public void onMessageReceived(Core core, final ChatRoom cr, final ChatMessage message) {
                final String textMessage;
//                if (!message.isOutgoing() && !NotificationsManager.this.mContext.getResources().getBoolean(R.bool.disable_chat) && !NotificationsManager.this.mContext.getResources().getBoolean(R.bool.disable_chat_message_notification)) {
//                    int i = 0;
//                    if (NotificationsManager.this.mCurrentChatRoomAddress != null && NotificationsManager.this.mCurrentChatRoomAddress.equals(cr.getPeerAddress().asStringUriOnly())) {
//                        Log.i("[Notifications Manager] Message received for currently displayed chat room, do not make a notification");
//                    } else if (message.getErrorInfo() != null && message.getErrorInfo().getReason() == Reason.UnsupportedContent) {
//                        Log.w("[Notifications Manager] Message received but content is unsupported, do not notify it");
//                    } else if (!message.hasTextContent() && message.getFileTransferInformation() == null) {
//                        Log.w("[Notifications Manager] Message has no text or file transfer information to display, ignoring it...");
//                    } else {
//                        final Address from = message.getFromAddress();
//                        final LinphoneContact contact = ContactsManager.getInstance().findContactFromAddress(from);
//                        if (!message.hasTextContent()) {
//                            textMessage = NotificationsManager.this.mContext.getString(R.string.content_description_incoming_file);
//                        } else {
//                            textMessage = message.getTextContent();
//                        }
//                        String file = null;
//                        Content[] contents = message.getContents();
//                        int length = contents.length;
//                        while (true) {
//                            if (i >= length) {
//                                break;
//                            }
//                            Content c = contents[i];
//                            if (!c.isFile()) {
//                                i++;
//                            } else {
//                                String file2 = c.getFilePath();
//                                LinphoneManager.getInstance().getMediaScanner().scanFile(new File(file2), new MediaScannerListener() { // from class: com.duzzelcall.managinig.notifications.NotificationsManager.1.1
//                                    @Override // com.duzzelcall.managinig.utils.MediaScannerListener
//                                    public void onMediaScanned(String path, Uri uri) {
//                                        NotificationsManager.this.createNotification(cr, contact, from, textMessage, message.getTime(), uri, FileUtils.getMimeFromFile(path));
//                                    }
//                                });
//                                file = file2;
//                                break;
//                            }
//                        }
//                        if (file == null) {
//                            NotificationsManager.this.createNotification(cr, contact, from, textMessage, message.getTime(), null, null);
//                        }
//                        if (cr.hasCapability(ChatRoomCapabilities.OneToOne.toInt())) {
//                            Compatibility.createChatShortcuts(NotificationsManager.this.mContext);
//                        }
//                    }
//                }
            }
        };
        this.mMessageListener = new ChatMessageListenerStub() { // from class: com.duzzelcall.managinig.notifications.NotificationsManager.2
            @Override
            // com.duzzelcall.linphone.core.ChatMessageListenerStub, com.duzzelcall.linphone.core.ChatMessageListener
            public void onMsgStateChanged(ChatMessage msg, ChatMessage.State state) {
                if (msg.getUserData() == null) {
                    return;
                }
                int notifId = ((Integer) msg.getUserData()).intValue();
                Log.i("[Notifications Manager] Reply message state changed (" + state.name() + ") for notif id " + notifId);
                if (state != ChatMessage.State.InProgress) {
                    msg.removeListener(this);
                }
                if (state == ChatMessage.State.Delivered || state == ChatMessage.State.Displayed) {
                    Notifiable notif2 = (Notifiable) NotificationsManager.this.mChatNotifMap.get(msg.getChatRoom().getPeerAddress().asStringUriOnly());
                    if (notif2 == null) {
                        Log.e("[Notifications Manager] Couldn't find message notification for SIP URI " + msg.getChatRoom().getPeerAddress().asStringUriOnly());
                        NotificationsManager.this.dismissNotification(notifId);
                        return;
                    }
                    if (notif2.getNotificationId() != notifId) {
                        Log.w("[Notifications Manager] Notif ID doesn't match: " + notifId + " != " + notif2.getNotificationId());
                    }
                    NotificationsManager.this.displayReplyMessageNotification(msg, notif2);
                } else if (state == ChatMessage.State.NotDelivered) {
                    Log.e("[Notifications Manager] Couldn't send reply, message is not delivered");
                    NotificationsManager.this.dismissNotification(notifId);
                }
            }
        };
    }

    public void onCoreReady() {
        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.addListener(this.mListener);
        }
    }

    public void destroy() {
        Log.i("[Notifications Manager] Getting destroyed, clearing Service & Call notifications");
        int i = this.mCurrentForegroundServiceNotification;
        if (i > 0) {
            this.mNM.cancel(i);
        }
        for (Notifiable notifiable : this.mCallNotifMap.values()) {
            this.mNM.cancel(notifiable.getNotificationId());
        }
        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.removeListener(this.mListener);
        }
    }

    @SuppressLint("WrongConstant")
    private void addFlagsToIntent(Intent intent) {
        intent.addFlags(196608);
    }

    public void startForeground() {
        if (LinphoneService.isReady()) {
            Log.i("[Notifications Manager] Starting Service as foreground");
            LinphoneService.instance().startForeground(1, this.mServiceNotification);
            this.mCurrentForegroundServiceNotification = 1;
        }
    }

    private void startForeground(Notification notification, int id) {
        if (LinphoneService.isReady()) {
            Log.i("[Notifications Manager] Starting Service as foreground while in call");
            LinphoneService.instance().startForeground(id, notification);
            mCurrentForegroundServiceNotification = id;
        }
    }

    public void stopForeground() {
        if (LinphoneService.isReady()) {
            Log.i("[Notifications Manager] Stopping Service as foreground");
            LinphoneService.instance().stopForeground(true);
            this.mCurrentForegroundServiceNotification = 0;
        }
    }

    public void removeForegroundServiceNotificationIfPossible() {
        if (LinphoneService.isReady() && this.mCurrentForegroundServiceNotification == 1 && !isServiceNotificationDisplayed()) {
            Log.i("[Notifications Manager] Linphone has started after device boot, stopping Service as foreground");
            stopForeground();
        }
    }

    public void setCurrentlyDisplayedChatRoom(String address) {
        this.mCurrentChatRoomAddress = address;
        if (address != null) {
            resetMessageNotifCount(address);
        }
    }

    public void dismissMissedCallNotification() {
        dismissNotification(2);
    }

    public void sendNotification(int id, Notification notif) {
        Log.i("[Notifications Manager] Notifying " + id);
        this.mNM.notify(id, notif);
    }

    public void dismissNotification(int notifId) {
        Log.i("[Notifications Manager] Dismissing " + notifId);
        this.mNM.cancel(notifId);
    }

    public void resetMessageNotifCount(String address) {
        Notifiable notif = this.mChatNotifMap.get(address);
        if (notif != null) {
            notif.resetMessages();
            this.mNM.cancel(notif.getNotificationId());
        }
    }

    public ChatMessageListenerStub getMessageListener() {
        return this.mMessageListener;
    }

    private boolean isServiceNotificationDisplayed() {
//        return LinphonePreferences.instance().getServiceNotificationVisibility();
        return true;
    }

    public String getSipUriForNotificationId(int notificationId) {
        for (String addr : this.mChatNotifMap.keySet()) {
            if (this.mChatNotifMap.get(addr).getNotificationId() == notificationId) {
                return addr;
            }
        }
        return null;
    }

    @SuppressLint("WrongConstant")
    private void displayMessageNotificationFromNotifiable(Notifiable notif, String remoteSipUri, String localSipUri) {
//        PendingIntent pendingIntent;
//        String message;
//        String from;
//        Intent notifIntent = new Intent(this.mContext, DashboardActivity.class);
//        notifIntent.putExtra("RemoteSipUri", remoteSipUri);
//        notifIntent.putExtra("LocalSipUri", localSipUri);
//        addFlagsToIntent(notifIntent);
//        if (Build.VERSION.SDK_INT >= 31) {
//            pendingIntent = PendingIntent.getActivity(this.mContext, notif.getNotificationId(), notifIntent, 201326592);
//        } else {
//            pendingIntent = PendingIntent.getActivity(this.mContext, notif.getNotificationId(), notifIntent, 1073741824);
//        }
//        NotifiableMessage lastNotifiable = notif.getMessages().get(notif.getMessages().size() - 1);
//        String from2 = lastNotifiable.getSender();
//        String message2 = lastNotifiable.getMessage();
//        Bitmap bm = lastNotifiable.getSenderBitmap();
//        if (!notif.isGroup()) {
//            from = from2;
//            message = message2;
//        } else {
//            String message3 = this.mContext.getString(R.string.group_chat_notif).replace("%1", from2).replace("%2", message2);
//            from = notif.getGroupTitle();
//            message = message3;
//        }
//        Notification notification = Compatibility.createMessageNotification(this.mContext, notif, from, message, bm, pendingIntent);
//        sendNotification(notif.getNotificationId(), notification);
    }

    public void displayReplyMessageNotification(ChatMessage msg, Notifiable notif) {
        if (msg == null || notif == null) {
            return;
        }
        Log.i("[Notifications Manager] Updating message notification with reply for notif " + notif.getNotificationId());
        NotifiableMessage notifMessage = new NotifiableMessage(msg.getTextContent(), notif.getMyself(), System.currentTimeMillis(), null, null);
        notif.addMessage(notifMessage);
        ChatRoom cr = msg.getChatRoom();
        displayMessageNotificationFromNotifiable(notif, cr.getPeerAddress().asStringUriOnly(), cr.getLocalAddress().asStringUriOnly());
    }

    public void displayGroupChatMessageNotification(String subject, String conferenceAddress, String fromName, Uri fromPictureUri, String message, Address localIdentity, long timestamp, Uri filePath, String fileMime) {
//        Bitmap bm = ImageUtils.getRoundBitmapFromUri(this.mContext, fromPictureUri);
//        Notifiable notif = this.mChatNotifMap.get(conferenceAddress);
//        NotifiableMessage notifMessage = new NotifiableMessage(message, fromName, timestamp, filePath, fileMime);
//        if (notif == null) {
//            notif = new Notifiable(this.mLastNotificationId);
//            this.mLastNotificationId++;
//            this.mChatNotifMap.put(conferenceAddress, notif);
//        }
//        Log.i("[Notifications Manager] Creating group chat message notifiable " + notif);
//        notifMessage.setSenderBitmap(bm);
//        notif.addMessage(notifMessage);
//        notif.setIsGroup(true);
//        notif.setGroupTitle(subject);
//        notif.setMyself(LinphoneUtils.getAddressDisplayName(localIdentity));
//        notif.setLocalIdentity(localIdentity.asString());
//        displayMessageNotificationFromNotifiable(notif, conferenceAddress, localIdentity.asStringUriOnly());
    }

    public void displayMessageNotification(String fromSipUri, String fromName, Uri fromPictureUri, String message, Address localIdentity, long timestamp, Uri filePath, String fileMime) {
        String fromName2;
        if (fromName != null) {
            fromName2 = fromName;
        } else {
            fromName2 = fromSipUri;
        }
//        Bitmap bm = ImageUtils.getRoundBitmapFromUri(this.mContext, fromPictureUri);
//        Notifiable notif = this.mChatNotifMap.get(fromSipUri);
//        NotifiableMessage notifMessage = new NotifiableMessage(message, fromName2, timestamp, filePath, fileMime);
//        if (notif == null) {
//            notif = new Notifiable(this.mLastNotificationId);
//            this.mLastNotificationId++;
//            this.mChatNotifMap.put(fromSipUri, notif);
//        }
//        Log.i("[Notifications Manager] Creating chat message notifiable " + notif);
//        notifMessage.setSenderBitmap(bm);
//        notif.addMessage(notifMessage);
//        notif.setIsGroup(false);
//        notif.setMyself(LinphoneUtils.getAddressDisplayName(localIdentity));
//        notif.setLocalIdentity(localIdentity.asString());
//        displayMessageNotificationFromNotifiable(notif, fromSipUri, localIdentity.asStringUriOnly());
    }

    public void displayMissedCallNotification(Call call) {
        String body = "missed";
        String body2="Call";
        Intent missedCallNotifIntent = new Intent(this.mContext, DashboardActivity.class);
        addFlagsToIntent(missedCallNotifIntent);
        @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(this.mContext, 2, missedCallNotifIntent, 134217728);
        int missedCallCount = 2;
//        int missedCallCount = LinphoneManager.getCore().getMissedCallsCount();
//        if (missedCallCount > 1) {
//            body = this.mContext.getString(R.string.missed_calls_notif_body).replace("%i", String.valueOf(missedCallCount));
//            Log.i("[Notifications Manager] Creating missed calls notification");
//        }
//        else {
//            Address address = call.getRemoteAddress();
//            LinphoneContact c = ContactsManager.getInstance().findContactFromAddress(address);
//            if (c != null) {
//                body2 = c.getFullName();
//            } else {
//                body2 = address.getDisplayName();
//                if (body2 == null) {
//                    body2 = address.asStringUriOnly();
//                }
//            }
//            Log.i("[Notifications Manager] Creating missed call notification");
//            body = body2;
//        }
        Context context = this.mContext;
        Notification notif = Compatibility.createMissedCallNotification(context, context.getString(R.string.missed_calls_notif_title), body, pendingIntent, missedCallCount);
        sendNotification(2, notif);
    }

    @SuppressLint("WrongConstant")
    public void displayCallNotification(Call call) {
        PendingIntent pendingIntent;
        Notifiable notif;
        int iconId;
        int notificationTextId;
        String name;
        Notification notification;
        int i;
        Notifiable notif2;
        if (call == null) {
            return;
        }
//        Class callNotifIntentClass = IncomingActivity.class;
        Class callNotifIntentClass = CallActivity.class;
        if (call.getState() == Call.State.IncomingReceived || call.getState() == Call.State.IncomingEarlyMedia) {
            callNotifIntentClass = IncomingActivity.class;
        } else if (call.getState() == Call.State.OutgoingInit || call.getState() == Call.State.OutgoingProgress || call.getState() == Call.State.OutgoingRinging || call.getState() == Call.State.OutgoingEarlyMedia) {
            callNotifIntentClass = OutgoingActivity.class;
        }
        Intent callNotifIntent = new Intent(this.mContext, callNotifIntentClass);
        callNotifIntent.addFlags(268435456);
        if (Build.VERSION.SDK_INT >= 23) {
            pendingIntent = PendingIntent.getActivity(this.mContext, 0, callNotifIntent, 201326592);
        } else {
            pendingIntent = PendingIntent.getActivity(this.mContext, 0, callNotifIntent, 134217728);
        }
        Address address = call.getRemoteAddress();
        String addressAsString = address.asStringUriOnly();
        Notifiable notif3 = this.mCallNotifMap.get(addressAsString);
        if (notif3 != null) {
            notif = notif3;
        } else {
            Notifiable notif4 = new Notifiable(this.mLastNotificationId);
            this.mLastNotificationId++;
            this.mCallNotifMap.put(addressAsString, notif4);
            notif = notif4;
        }
        switch (AnonymousClass3.$SwitchMap$org$linphone$core$Call$State[call.getState().ordinal()]) {
            case 1:
            case 2:
                if (this.mCurrentForegroundServiceNotification == notif.getNotificationId()) {
                    Log.i("[Notifications Manager] Call ended, stopping notification used to keep service alive");
                    stopForeground();
                }
                this.mNM.cancel(notif.getNotificationId());
                this.mCallNotifMap.remove(addressAsString);
                return;
            case 3:
            case 4:
            case 5:
//                iconId = R.drawable.topbar_call_notification;
                iconId = R.drawable.app_logo;
                notificationTextId = R.string.incall_notif_paused;
                break;
            case 6:
            case 7:
                iconId = R.drawable.app_logo;
//                iconId = R.drawable.topbar_call_notification;
                notificationTextId = R.string.incall_notif_incoming;
                break;
            case 8:
            case 9:
            case 10:
            case 11:
//                iconId = R.drawable.topbar_call_notification;
                iconId = R.drawable.app_logo;
                notificationTextId = R.string.incall_notif_outgoing;
                break;
            default:
                if (call.getCurrentParams().videoEnabled()) {
//                    iconId = R.drawable.topbar_videocall_notification;
                    iconId = R.drawable.app_logo;
                    notificationTextId = R.string.app_name;
//                    notificationTextId = R.string.incall_notif_video;
                    break;
                } else {
                    iconId = R.drawable.app_logo;
//                    iconId = R.drawable.topbar_call_notification;
                    notificationTextId = R.string.app_name;
//                    notificationTextId = R.string.incall_notif_active;
                    break;
                }
        }
        if (notif.getIconResourceId() == iconId && notif.getTextResourceId() == notificationTextId) {
            return;
        }
        if (notif.getTextResourceId() == R.string.incall_notif_incoming) {
            dismissNotification(notif.getNotificationId());
        }
        notif.setIconResourceId(iconId);
        notif.setTextResourceId(notificationTextId);
        Log.i("[Notifications Manager] Call notification notifiable is " + notif + ", pending intent " + callNotifIntentClass);
//        LinphoneContact contact = ContactsManager.getInstance().findContactFromAddress(address);
//        Uri pictureUri = contact != null ? contact.getThumbnailUri() : null;
//        Bitmap bm = ImageUtils.getRoundBitmapFromUri(this.mContext, pictureUri);
//        if (contact != null) {
//            name = contact.getFullName();
//            name = "contact.getFullName()";
//        } else {
            name = LinphoneUtils.getAddressDisplayName(address);
//            name = "LinphoneUtils.getAddressDisplayName(address)";
////        }
        boolean isIncoming = callNotifIntentClass == IncomingActivity.class;
        if (isIncoming) {
            notification = Compatibility.createIncomingCallNotification(this.mContext, notif.getNotificationId(), null, name, addressAsString, pendingIntent);
            notif2 = notif;
            i = 1;
        } else {
            notif2 = notif;
            i = 1;
            notification = Compatibility.createInCallNotification(this.mContext, notif.getNotificationId(), this.mContext.getString(notificationTextId), iconId, null, name, pendingIntent);
        }
        if (isServiceNotificationDisplayed() || isIncoming) {
            sendNotification(notif2.getNotificationId(), notification);
        } else if (call.getCore().getCallsNb() == 0) {
            Object[] objArr = new Object[i];
            objArr[0] = "[Notifications Manager] Foreground service mode is disabled, stopping call notification used to keep it alive";
            Log.i(objArr);
            stopForeground();
        } else if (this.mCurrentForegroundServiceNotification != 0) {
            sendNotification(notif2.getNotificationId(), notification);
        } else if (DeviceUtils.isAppUserRestricted(this.mContext)) {
            Object[] objArr2 = new Object[i];
            objArr2[0] = "[Notifications Manager] App has been restricted, can't use call notification to keep service alive !";
            Log.w(objArr2);
            sendNotification(notif2.getNotificationId(), notification);
        } else {
            Object[] objArr3 = new Object[i];
            objArr3[0] = "[Notifications Manager] Foreground service mode is disabled, using call notification to keep it alive";
            Log.i(objArr3);
            startForeground(notification, notif2.getNotificationId());
        }
    }

    /* renamed from: com.duzzelcall.managinig.notifications.NotificationsManager$3 */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$org$linphone$core$Call$State;

        static {
            int[] iArr = new int[Call.State.values().length];
            $SwitchMap$org$linphone$core$Call$State = iArr;
            try {
                iArr[Call.State.Released.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$linphone$core$Call$State[Call.State.End.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$linphone$core$Call$State[Call.State.Paused.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$linphone$core$Call$State[Call.State.PausedByRemote.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$linphone$core$Call$State[Call.State.Pausing.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$linphone$core$Call$State[Call.State.IncomingEarlyMedia.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$linphone$core$Call$State[Call.State.IncomingReceived.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$linphone$core$Call$State[Call.State.OutgoingEarlyMedia.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$org$linphone$core$Call$State[Call.State.OutgoingInit.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$org$linphone$core$Call$State[Call.State.OutgoingProgress.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$org$linphone$core$Call$State[Call.State.OutgoingRinging.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
        }
    }

    public String getSipUriForCallNotificationId(int notificationId) {
        for (String addr : this.mCallNotifMap.keySet()) {
            if (this.mCallNotifMap.get(addr).getNotificationId() == notificationId) {
                return addr;
            }
        }
        return null;
    }

//    public void createNotification(ChatRoom cr, LinphoneContact contact, Address from, String textMessage, long time, Uri file, String mime) {
//        if (cr.hasCapability(ChatRoomCapabilities.OneToOne.toInt())) {
//            if (contact != null) {
//                displayMessageNotification(cr.getPeerAddress().asStringUriOnly(), contact.getFullName(), contact.getThumbnailUri(), textMessage, cr.getLocalAddress(), time, file, mime);
//                return;
//            } else {
//                displayMessageNotification(cr.getPeerAddress().asStringUriOnly(), from.getUsername(), null, textMessage, cr.getLocalAddress(), time, file, mime);
//                return;
//            }
//        }
//        String subject = cr.getSubject();
//        if (contact != null) {
//            displayGroupChatMessageNotification(subject, cr.getPeerAddress().asStringUriOnly(), contact.getFullName(), contact.getThumbnailUri(), textMessage, cr.getLocalAddress(), time, file, mime);
//        } else {
//            displayGroupChatMessageNotification(subject, cr.getPeerAddress().asStringUriOnly(), from.getUsername(), null, textMessage, cr.getLocalAddress(), time, file, mime);
//        }
//    }
}
