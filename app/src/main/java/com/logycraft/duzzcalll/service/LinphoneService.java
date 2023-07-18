package com.logycraft.duzzcalll.service;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.WindowManager;

import com.duzzcall.duzzcall.R;
import com.logycraft.duzzcalll.LinphoneContext;
import com.logycraft.duzzcalll.LinphoneManager;
import com.logycraft.duzzcalll.LinphonePreferences;

import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.tools.Log;
import org.linphone.core.tools.service.ActivityMonitor;
import org.linphone.mediastream.Version;

//import com.duzzelcall.linphone.core.Call;
//import com.duzzelcall.linphone.core.Core;
//import com.duzzelcall.linphone.core.tools.Log;
//import com.duzzelcall.linphone.mediastream.Version;
//import com.duzzelcall.managinig.LinphoneContext;
//import com.duzzelcall.managinig.LinphoneManager;
//import com.duzzelcall.managinig.R;
//import com.duzzelcall.managinig.call.views.LinphoneGL2JNIViewOverlay;
//import com.duzzelcall.managinig.call.views.LinphoneOverlay;
//import com.duzzelcall.managinig.call.views.LinphoneTextureViewOverlay;
//import com.duzzelcall.managinig.settings.LinphonePreferences;

/* loaded from: classes2.dex */
public final class LinphoneService extends Service {
    private static LinphoneService sInstance;
    private Application.ActivityLifecycleCallbacks mActivityCallbacks;
//    private LinphoneOverlay mOverlay;
    private WindowManager mWindowManager;
    private boolean misLinphoneContextOwned;

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
       setupActivityMonitor();
        this.misLinphoneContextOwned = false;
        if (!LinphoneContext.isReady()) {
            new LinphoneContext(getApplicationContext());
            this.misLinphoneContextOwned = true;
        }
        new LinphoneContext(getApplicationContext());
        Log.i("[Service] Created");
        this.mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        boolean isPush = false;
        if (intent != null && intent.getBooleanExtra("PushNotification", false)) {
            Log.i("[Service] [Push Notification] LinphoneService started because of a push");
            isPush = true;
        }
        if (sInstance != null) {
            Log.w("[Service] Attempt to start the LinphoneService but it is already running !");
            return START_STICKY;
        }
        sInstance = this;
        if (LinphonePreferences.instance().getServiceNotificationVisibility() || (Version.sdkAboveOrEqual(26) && intent != null && intent.getBooleanExtra("ForceStartForeground", false))) {
            Log.i("[Service] Background service mode enabled, displaying notification");
            LinphoneContext.instance().getNotificationManager().startForeground();
        }
        if (this.misLinphoneContextOwned) {
            LinphoneContext.instance().start(isPush);
        } else {
            LinphoneContext.instance().updateContext(this);
        }
        Log.i("[Service] Started");
        return START_STICKY;
    }

    @Override // android.app.Service
    public void onTaskRemoved(Intent rootIntent) {
        boolean serviceNotif = LinphonePreferences.instance().getServiceNotificationVisibility();
        if (serviceNotif) {
            Log.i("[Service] Service is running in foreground, don't stop it");
        } else if (getResources().getBoolean(R.bool.kill_service_with_task_manager)) {
            Log.i("[Service] Task removed, stop service");
            Core core = LinphoneManager.getCore();
            if (core != null) {
                core.terminateAllCalls();
            }
            if (LinphonePreferences.instance().isPushNotificationEnabled() && core != null) {
                core.setNetworkReachable(false);
            }
            stopSelf();
        }
        super.onTaskRemoved(rootIntent);
    }

    @Override // android.app.Service
    public synchronized void onDestroy() {
        Log.i("[Service] Destroying");
        if (this.mActivityCallbacks != null) {
            getApplication().unregisterActivityLifecycleCallbacks(this.mActivityCallbacks);
            this.mActivityCallbacks = null;
        }
        destroyOverlay();
        LinphoneContext.instance().destroy();
        sInstance = null;
        super.onDestroy();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean isReady() {
        return sInstance != null;
    }

    public static LinphoneService instance() {
        if (isReady()) {
            return sInstance;
        }
        throw new RuntimeException("LinphoneService not instantiated yet");
    }

    public void createOverlay() {
        Log.i("[Service] Creating video overlay");
//        if (this.mOverlay != null) {
//            destroyOverlay();
//        }
        Core core = LinphoneManager.getCore();
        Call call = core.getCurrentCall();
        if (call == null || !call.getCurrentParams().videoEnabled()) {
            return;
        }
//        if ("MSAndroidOpenGLDisplay".equals(core.getVideoDisplayFilter())) {
//            this.mOverlay = new LinphoneGL2JNIViewOverlay(this);
//        } else {
//            this.mOverlay = new LinphoneTextureViewOverlay(this);
//        }
//        WindowManager.LayoutParams params = this.mOverlay.getWindowManagerLayoutParams();
//        params.x = 0;
//        params.y = 0;
//        this.mOverlay.addToWindowManager(this.mWindowManager, params);
    }

    public void destroyOverlay() {
//        Log.i("[Service] Destroying video overlay");
//        LinphoneOverlay linphoneOverlay = this.mOverlay;
//        if (linphoneOverlay != null) {
//            linphoneOverlay.removeFromWindowManager(this.mWindowManager);
//            this.mOverlay.destroy();
//        }
//        this.mOverlay = null;
    }

    private void setupActivityMonitor() {
        if (this.mActivityCallbacks != null) {
            return;
        }
        Application application = getApplication();
        ActivityMonitor activityMonitor = new ActivityMonitor();
        this.mActivityCallbacks = activityMonitor;
        application.registerActivityLifecycleCallbacks(activityMonitor);
    }
}
