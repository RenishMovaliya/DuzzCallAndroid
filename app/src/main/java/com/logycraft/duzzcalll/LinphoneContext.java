package com.logycraft.duzzcalll;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.ContactsContract;


import com.duzzcall.duzzcall.R;
import com.logycraft.duzzcalll.Activity.CallActivity;
import com.logycraft.duzzcalll.Activity.IncomingActivity;
import com.logycraft.duzzcalll.Activity.OutgoingActivity;
import com.logycraft.duzzcalll.Util.LinphoneUtils;
import com.logycraft.duzzcalll.helper.compatibility.Compatibility;
import com.logycraft.duzzcalll.helper.notifications.NotificationsManager;
import com.logycraft.duzzcalll.service.LinphoneService;

import org.linphone.core.Call;
import org.linphone.core.ConfiguringState;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.Factory;
import org.linphone.core.GlobalState;
import org.linphone.core.LogLevel;
import org.linphone.core.LoggingService;
import org.linphone.core.LoggingServiceListener;
import org.linphone.core.tools.Log;
import org.linphone.core.tools.PushNotificationUtils;
import org.linphone.core.tools.compatibility.DeviceUtils;
import org.linphone.mediastream.Version;

import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes2.dex */
public class LinphoneContext extends Application {
    private static LinphoneContext sInstance = null;
//    private ContactsManager mContactsManager;
    private Context mContext;
    private LinphoneManager mLinphoneManager;
    private NotificationsManager mNotificationManager;
    private final LoggingServiceListener mJavaLoggingService = new LoggingServiceListener() { // from class: com.duzzelcall.managinig.LinphoneContext.1
        @Override // com.duzzelcall.linphone.core.LoggingServiceListener
        public void onLogMessageWritten(LoggingService logService, String domain, LogLevel lev, String message) {
            int i = AnonymousClass3.$SwitchMap$org$linphone$core$LogLevel[lev.ordinal()];
            if (i == 1) {
                Log.d(domain, message);
            } else if (i == 2) {
                Log.i(domain, message);
            } else if (i == 3) {
                Log.w(domain, message);
            } else if (i == 4) {
                Log.e(domain, message);
            } else {
                Log.e(domain, message);
            }
        }
    };
    private final ArrayList<CoreStartedListener> mCoreStartedListeners = new ArrayList<>();
    private CoreListenerStub mListener = new CoreListenerStub() { // from class: com.duzzelcall.managinig.LinphoneContext.2
        @Override
        // com.duzzelcall.linphone.core.CoreListenerStub, com.duzzelcall.linphone.core.CoreListener
        public void onGlobalStateChanged(Core core, GlobalState state, String message) {
            Log.i("[Context] Global state is [", state, "]");
            if (state == GlobalState.On) {
                Iterator it = LinphoneContext.this.mCoreStartedListeners.iterator();
                while (it.hasNext()) {
                    CoreStartedListener listener = (CoreStartedListener) it.next();
                    listener.onCoreStarted();
                }
            }
        }

        @Override
        // com.duzzelcall.linphone.core.CoreListenerStub, com.duzzelcall.linphone.core.CoreListener
        public void onConfiguringStatus(Core core, ConfiguringState status, String message) {
            Log.i("[Context] Configuring state is [", status, "]");
            if (status == ConfiguringState.Successful) {
                LinphonePreferences.instance().setPushNotificationEnabled(LinphonePreferences.instance().isPushNotificationEnabled());
            }
        }

        @Override
        // com.duzzelcall.linphone.core.CoreListenerStub, com.duzzelcall.linphone.core.CoreListener
        public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
            Log.i("[Context] Call state is [", state, "]");
            if (LinphoneContext.this.mContext.getResources().getBoolean(R.bool.enable_call_notification)) {
                LinphoneContext.this.mNotificationManager.displayCallNotification(call);
            }
            if (state == Call.State.IncomingReceived || state == Call.State.IncomingEarlyMedia) {
                if (Version.sdkStrictlyBelow(24) && !LinphoneContext.this.mLinphoneManager.getCallGsmON()) {
                    LinphoneContext.this.onIncomingReceived();
                }
                if (!LinphoneService.isReady()) {
                    Log.i("[Context] Service not running, starting it");
                    Intent intent = new Intent("android.intent.action.MAIN");
                    intent.setClass(LinphoneContext.this.mContext, LinphoneService.class);
                    LinphoneContext.this.mContext.startService(intent);
                }
            } else if (state == Call.State.OutgoingInit) {
                LinphoneContext.this.onOutgoingStarted();
            } else if (state == Call.State.Connected) {
                LinphoneContext.this.onCallStarted();
            } else if (state == Call.State.End || state == Call.State.Released || state == Call.State.Error) {
                if (LinphoneService.isReady()) {
                    LinphoneService.instance().destroyOverlay();
                }
                if (state == Call.State.Released && call.getCallLog().getStatus() == Call.Status.Missed) {
                    LinphoneContext.this.mNotificationManager.displayMissedCallNotification(call);
                }
            }
        }
    };

    /* loaded from: classes2.dex */
    public interface CoreStartedListener {
        void onCoreStarted();
    }

    /* renamed from: com.duzzelcall.managinig.LinphoneContext$3 */
    /* loaded from: classes2.dex */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$org$linphone$core$LogLevel;

        static {
            int[] iArr = new int[LogLevel.values().length];
            $SwitchMap$org$linphone$core$LogLevel = iArr;
            try {
                iArr[LogLevel.Debug.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$linphone$core$LogLevel[LogLevel.Message.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$linphone$core$LogLevel[LogLevel.Warning.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$linphone$core$LogLevel[LogLevel.Error.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$linphone$core$LogLevel[LogLevel.Fatal.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public static boolean isReady() {
        return sInstance != null;
    }

    public static LinphoneContext instance() {
        LinphoneContext linphoneContext = sInstance;
        if (linphoneContext == null) {
            throw new RuntimeException("[Context] Linphone Context not available!");
        }
        return linphoneContext;
    }

    public LinphoneContext(Context context) {
        this.mContext = context;
        LinphonePreferences.instance().setContext(context);
        Factory.instance().setLogCollectionPath(context.getFilesDir().getAbsolutePath());
        boolean isDebugEnabled = LinphonePreferences.instance().isDebugEnabled();
        LinphoneUtils.configureLoggingService(false, context.getString(R.string.app_name));
        dumpDeviceInformation();
        dumpLinphoneInformation();
        sInstance = this;
        Log.i("[Context] Ready");
        this.mLinphoneManager = new LinphoneManager(context);
        this.mNotificationManager = new NotificationsManager(context);
        if (DeviceUtils.isAppUserRestricted(this.mContext)) {
            Log.w("[Context] Device has been restricted by user (Android 9+), push notifications won't work !");
        }
        int bucket = DeviceUtils.getAppStandbyBucket(this.mContext);
        if (bucket > 0) {
            Log.w("[Context] Device is in bucket " + Compatibility.getAppStandbyBucketNameFromValue(bucket));
        }
        if (!PushNotificationUtils.isAvailable(this.mContext)) {
            Log.w("[Context] Push notifications won't work !");
        }
    }

    public void start(boolean isPush) {
        Log.i("[Context] Starting, push status is ", Boolean.valueOf(isPush));
        this.mLinphoneManager.startLibLinphone(isPush, mListener);
        this.mNotificationManager.onCoreReady();
//        this.mContactsManager = new ContactsManager(this.mContext);
//        if (!Version.sdkAboveOrEqual(26) || this.mContactsManager.hasReadContactsAccess()) {
//            this.mContext.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, this.mContactsManager);
//        }
//        if (this.mContactsManager.hasReadContactsAccess()) {
//            this.mContactsManager.enableContactsAccess();
//        }
//        this.mContactsManager.initializeContactManager();
    }

    public void destroy() {
        Log.i("[Context] Destroying");
        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.removeListener(this.mListener);
        }
        NotificationsManager notificationsManager = this.mNotificationManager;
        if (notificationsManager != null) {
            notificationsManager.destroy();
        }
//        ContactsManager contactsManager = this.mContactsManager;
//        if (contactsManager != null) {
//            contactsManager.destroy();
//        }
        LinphoneManager linphoneManager = this.mLinphoneManager;
        if (linphoneManager != null) {
            linphoneManager.destroy();
        }
//        sInstance = null;
        if (LinphonePreferences.instance().useJavaLogger()) {
            Factory.instance().getLoggingService().removeListener(this.mJavaLoggingService);
        }
        LinphonePreferences.instance().destroy();
    }

    public void updateContext(Context context) {
        this.mContext = context;
    }

    public Context getApplicationContext() {
        return this.mContext;
    }

    public LoggingServiceListener getJavaLoggingService() {
        return this.mJavaLoggingService;
    }

    public NotificationsManager getNotificationManager() {
        return this.mNotificationManager;
    }

    public LinphoneManager getLinphoneManager() {
        return this.mLinphoneManager;
    }

//    public ContactsManager getContactsManager() {
//        return this.mContactsManager;
//    }

    public void addCoreStartedListener(CoreStartedListener listener) {
        this.mCoreStartedListeners.add(listener);
    }

    public void removeCoreStartedListener(CoreStartedListener listener) {
        this.mCoreStartedListeners.remove(listener);
    }

    private void dumpDeviceInformation() {
        Log.i("==== Phone information dump ====");
        Log.i("DISPLAY NAME=" + Compatibility.getDeviceName(this.mContext));
        Log.i("DEVICE=" + Build.DEVICE);
        Log.i("MODEL=" + Build.MODEL);
        Log.i("MANUFACTURER=" + Build.MANUFACTURER);
        Log.i("ANDROID SDK=" + Build.VERSION.SDK_INT);
        StringBuilder sb = new StringBuilder();
        sb.append("ABIs=");
        for (String abi : Version.getCpuAbis()) {
            sb.append(abi);
            sb.append(", ");
        }
        Log.i(sb.substring(0, sb.length() - 2));
    }

    private void dumpLinphoneInformation() {
        Log.i("==== Linphone information dump ====");
        Log.i("VERSION NAME=1.1.1");
        Log.i("VERSION CODE=1");
        Log.i("PACKAGE=com.duzzelcall.managinig.debug");
        Log.i("BUILD TYPE=debug");
//        Log.i("SDK VERSION=" + this.mContext.getString(R.string.linphone_sdk_version));
//        Log.i("SDK BRANCH=" + this.mContext.getString(R.string.linphone_sdk_branch));
    }

    public void onIncomingReceived() {
        Intent intent = new Intent(this.mContext, IncomingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.mContext.startActivity(intent);
    }

    public void onOutgoingStarted() {
        Intent intent = new Intent(this.mContext, OutgoingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.mContext.startActivity(intent);
    }

    public void onCallStarted() {
        Intent intent = new Intent(this.mContext, CallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.mContext.startActivity(intent);
    }
}
