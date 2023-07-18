package com.logycraft.duzzcalll;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.core.content.ContextCompat;

//import com.android.volley.BuildConfig;
//import com.duzzelcall.linphone.core.AccountCreator;
//import com.duzzelcall.linphone.core.AccountCreatorListenerStub;
//import com.duzzelcall.linphone.core.Call;
//import com.duzzelcall.linphone.core.Core;
//import com.duzzelcall.linphone.core.CoreListener;
//import com.duzzelcall.linphone.core.CoreListenerStub;
//import com.duzzelcall.linphone.core.Factory;
//import com.duzzelcall.linphone.core.FriendList;
//import com.duzzelcall.linphone.core.PresenceActivity;
//import com.duzzelcall.linphone.core.PresenceBasicStatus;
//import com.duzzelcall.linphone.core.PresenceModel;
//import com.duzzelcall.linphone.core.ProxyConfig;
//import com.duzzelcall.linphone.core.Reason;
//import com.duzzelcall.linphone.core.Tunnel;
//import com.duzzelcall.linphone.core.TunnelConfig;
//import com.duzzelcall.linphone.core.tools.Log;
//import com.duzzelcall.managinig.assistant.PhoneAccountLinkingAssistantActivity;
//import com.duzzelcall.managinig.call.AndroidAudioManager;
//import com.duzzelcall.managinig.call.CallManager;
//import com.duzzelcall.managinig.contacts.ContactsManager;
//import com.duzzelcall.managinig.settings.LinphonePreferences;
//import com.duzzelcall.managinig.utils.MediaScanner;
//import com.duzzelcall.managinig.utils.PushNotificationUtils;

import com.duzzcall.duzzcall.BuildConfig;
import com.duzzcall.duzzcall.R;
import com.logycraft.duzzcalll.Util.CallManager;
import com.logycraft.duzzcalll.Util.LinphoneUtils;

import org.linphone.core.AccountCreator;
import org.linphone.core.AccountCreatorListenerStub;
import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.CoreListener;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.Factory;
import org.linphone.core.FriendList;
import org.linphone.core.PresenceActivity;
import org.linphone.core.PresenceBasicStatus;
import org.linphone.core.PresenceModel;
import org.linphone.core.ProxyConfig;
import org.linphone.core.Reason;
import org.linphone.core.Tunnel;
import org.linphone.core.TunnelConfig;
import org.linphone.core.tools.Log;
import org.linphone.core.tools.PushNotificationUtils;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class LinphoneManager implements SensorEventListener {
    private AccountCreator mAccountCreator;
    private AccountCreatorListenerStub mAccountCreatorListener;
//    private AndroidAudioManager mAudioManager;
    private final String mBasePath;
    private boolean mCallGsmON;
    private final String mCallLogDatabaseFile;
    private CallManager mCallManager;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private Core mCore;
    private CoreListenerStub mCoreListener;
    private final String mFriendsDatabaseFile;
    private boolean mHasLastCallSasBeenRejected;
    private Runnable mIterateRunnable;
//    private final MediaScanner mMediaScanner;
    private final PowerManager mPowerManager;
    private final Sensor mProximity;
    private boolean mProximitySensingEnabled;
    private PowerManager.WakeLock mProximityWakelock;
    private final String mRingSoundFile;
    private final SensorManager mSensorManager;
    private TelephonyManager mTelephonyManager;
    private Timer mTimer;
    private final String mUserCertsPath;
    private boolean mExited = false;
    private final LinphonePreferences mPrefs = LinphonePreferences.instance();
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() { // from class: com.duzzelcall.managinig.LinphoneManager.1
        @Override // android.telephony.PhoneStateListener
        public void onCallStateChanged(int state, String phoneNumber) {
            if (state == 0) {
                Log.i("[Manager] Phone state is idle");
                LinphoneManager.this.setCallGsmON(false);
            } else if (state == 1) {
                Log.i("[Manager] Phone state is ringing");
                LinphoneManager.this.setCallGsmON(true);
            } else if (state == 2) {
                Log.i("[Manager] Phone state is off hook");
                LinphoneManager.this.setCallGsmON(true);
            }
        }
    };

    public LinphoneManager(Context c) {
        this.mContext = c;
        String absolutePath = c.getFilesDir().getAbsolutePath();
        this.mBasePath = absolutePath;
        this.mCallLogDatabaseFile = absolutePath + "/linphone-log-history.db";
        this.mFriendsDatabaseFile = absolutePath + "/linphone-friends.db";
        this.mRingSoundFile = absolutePath + "/share/sounds/linphone/rings/notes_of_the_optimistic.mkv";
        String str = absolutePath + "/user-certs";
        this.mUserCertsPath = str;
        this.mPowerManager = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
        this.mConnectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        SensorManager sensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
        this.mSensorManager = sensorManager;
        this.mProximity = sensorManager.getDefaultSensor(8);
        this.mTelephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        Log.i("[Manager] Registering phone state listener");
        if (Build.VERSION.SDK_INT >= 31) {
            if (ContextCompat.checkSelfPermission(c, "android.permission.READ_PHONE_STATE") == 0) {
                this.mTelephonyManager.listen(this.mPhoneStateListener, 32);
            }
        } else {
            this.mTelephonyManager.listen(this.mPhoneStateListener, 32);
        }
        this.mHasLastCallSasBeenRejected = false;
        this.mCallManager = new CallManager(c);
        File f = new File(str);
        if (!f.exists() && !f.mkdir()) {
            Log.e("[Manager] " + str + " can't be created.");
        }
//        this.mMediaScanner = new MediaScanner(c);
        this.mCoreListener = new CoreListenerStub() { // from class: com.duzzelcall.managinig.LinphoneManager.2
            @Override
            // com.duzzelcall.linphone.core.CoreListenerStub, com.duzzelcall.linphone.core.CoreListener
            public void onCallStateChanged(Core core, final Call call, Call.State state, String message) {
                Log.i("[Manager] Call state is [", state, "]");
                if (state == Call.State.IncomingReceived && !call.equals(core.getCurrentCall()) && call.getReplacedCall() != null) {
                    return;
                }
                if ((state == Call.State.IncomingReceived || state == Call.State.IncomingEarlyMedia) && LinphoneManager.this.getCallGsmON()) {
                    if (LinphoneManager.this.mCore != null) {
                        call.decline(Reason.Busy);
                    }
                } else if (state == Call.State.IncomingReceived && LinphonePreferences.instance().isAutoAnswerEnabled() && !LinphoneManager.this.getCallGsmON()) {
                    LinphoneUtils.dispatchOnUIThreadAfter(new Runnable() { // from class: com.duzzelcall.managinig.LinphoneManager.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (LinphoneManager.this.mCore != null && LinphoneManager.this.mCore.getCallsNb() > 0) {
                                LinphoneManager.this.mCallManager.acceptCall(call);
//                                LinphoneManager.this.mAudioManager.routeAudioToEarPiece();
                            }
                        }
                    }, LinphoneManager.this.mPrefs.getAutoAnswerTime());
                } else if (state == Call.State.End || state == Call.State.Error) {
                    if (LinphoneManager.this.mCore.getCallsNb() == 0) {
                        LinphoneManager.this.enableProximitySensing(false);
                    }
                } else if (state == Call.State.UpdatedByRemote) {
                    boolean remoteVideo = call.getRemoteParams().videoEnabled();
                    boolean localVideo = call.getCurrentParams().videoEnabled();
                    boolean autoAcceptCameraPolicy = LinphonePreferences.instance().shouldAutomaticallyAcceptVideoRequests();
                    if (remoteVideo && !localVideo && !autoAcceptCameraPolicy && LinphoneManager.this.mCore.getConference() == null) {
                        call.deferUpdate();
                    }
                }
            }

            @Override
            // com.duzzelcall.linphone.core.CoreListenerStub, com.duzzelcall.linphone.core.CoreListener
            public void onFriendListCreated(Core core, FriendList list) {
                if (LinphoneContext.isReady()) {
//                    list.addListener(ContactsManager.getInstance());
                }
            }

            @Override
            // com.duzzelcall.linphone.core.CoreListenerStub, com.duzzelcall.linphone.core.CoreListener
            public void onFriendListRemoved(Core core, FriendList list) {
//                list.removeListener(ContactsManager.getInstance());
            }
        };
        this.mAccountCreatorListener = new AccountCreatorListenerStub() { // from class: com.duzzelcall.managinig.LinphoneManager.3
            @Override
            // com.duzzelcall.linphone.core.AccountCreatorListenerStub, com.duzzelcall.linphone.core.AccountCreatorListener
            public void onIsAccountExist(AccountCreator accountCreator, AccountCreator.Status status, String resp) {
                if (status.equals(AccountCreator.Status.AccountExist)) {
                    accountCreator.isAccountLinked();
                }
            }

            @Override
            // com.duzzelcall.linphone.core.AccountCreatorListenerStub, com.duzzelcall.linphone.core.AccountCreatorListener
            public void onLinkAccount(AccountCreator accountCreator, AccountCreator.Status status, String resp) {
                if (status.equals(AccountCreator.Status.AccountNotLinked)) {
                    LinphoneManager.this.askLinkWithPhoneNumber();
                }
            }

            @Override
            // com.duzzelcall.linphone.core.AccountCreatorListenerStub, com.duzzelcall.linphone.core.AccountCreatorListener
            public void onIsAccountLinked(AccountCreator accountCreator, AccountCreator.Status status, String resp) {
                if (status.equals(AccountCreator.Status.AccountNotLinked)) {
                    LinphoneManager.this.askLinkWithPhoneNumber();
                }
            }
        };
    }

    public static synchronized LinphoneManager getInstance() {
        LinphoneManager manager;
        synchronized (LinphoneManager.class) {
            manager = LinphoneContext.instance().getLinphoneManager();
            if (manager == null) {
                throw new RuntimeException("[Manager] Linphone Manager should be created before accessed");
            }
            if (manager.mExited) {
                throw new RuntimeException("[Manager] Linphone Manager was already destroyed. Better use getCore and check returned value");
            }
        }
        return manager;
    }

//    public static synchronized AndroidAudioManager getAudioManager() {
//        AndroidAudioManager androidAudioManager;
//        synchronized (LinphoneManager.class) {
//            androidAudioManager = getInstance().mAudioManager;
//        }
//        return androidAudioManager;
//    }

    public static synchronized CallManager getCallManager() {
        CallManager callManager;
        synchronized (LinphoneManager.class) {
            callManager = getInstance().mCallManager;
        }
        return callManager;
    }

    public static synchronized Core getCore() {
        synchronized (LinphoneManager.class) {
            if (!LinphoneContext.isReady()) {
                return null;
            }
            if (getInstance().mExited) {
                return null;
            }
            return getInstance().mCore;
        }
    }

//    public MediaScanner getMediaScanner() {
//        return this.mMediaScanner;
//    }

    public synchronized void destroy() {
        destroyManager();
        this.mExited = true;
    }

    public void restartCore() {
        Log.w("[Manager] Restarting Core");
        this.mCore.stop();
        this.mCore.start();
    }

    private void destroyCore() {
        Log.w("[Manager] Destroying Core");
        if (LinphonePreferences.instance() != null && LinphonePreferences.instance().isPushNotificationEnabled()) {
            Log.w("[Manager] Setting network reachability to False to prevent unregister and allow incoming push notifications");
            this.mCore.setNetworkReachable(false);
        }
        this.mCore.stop();
        this.mCore.removeListener(this.mCoreListener);
    }

    private synchronized void destroyManager() {
        Log.w("[Manager] Destroying Manager");
        changeStatusToOffline();
        if (this.mTelephonyManager != null) {
            Log.i("[Manager] Unregistering phone state listener");
            this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
        }
        CallManager callManager = this.mCallManager;
        if (callManager != null) {
            callManager.destroy();
        }
//        MediaScanner mediaScanner = this.mMediaScanner;
//        if (mediaScanner != null) {
//            mediaScanner.destroy();
//        }
//        AndroidAudioManager androidAudioManager = this.mAudioManager;
//        if (androidAudioManager != null) {
//            androidAudioManager.destroy();
//        }
        Timer timer = this.mTimer;
        if (timer != null) {
            timer.cancel();
        }
        if (this.mCore != null) {
            destroyCore();
            this.mCore = null;
        }
    }

    public synchronized void startLibLinphone(boolean isPush, CoreListener listener) {
        try {
            Core createCore = Factory.instance().createCore(this.mPrefs.getLinphoneDefaultConfig(), this.mPrefs.getLinphoneFactoryConfig(), this.mContext);
            this.mCore = createCore;
            createCore.addListener(listener);
            this.mCore.addListener(this.mCoreListener);
            if (isPush) {
                Log.w("[Manager] We are here because of a received push notification, enter background mode before starting the Core");
                this.mCore.enterBackground();
            }
            this.mCore.start();
            this.mIterateRunnable = new Runnable() { // from class: com.duzzelcall.managinig.LinphoneManager.4
                @Override // java.lang.Runnable
                public void run() {
                    if (LinphoneManager.this.mCore != null) {
                        LinphoneManager.this.mCore.iterate();
                    }
                }
            };
            TimerTask lTask = new TimerTask() { // from class: com.duzzelcall.managinig.LinphoneManager.5
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    LinphoneUtils.dispatchOnUIThread(LinphoneManager.this.mIterateRunnable);
                }
            };
            Timer timer = new Timer("Linphone scheduler");
            this.mTimer = timer;
            timer.schedule(lTask, 0L, 20L);
            configureCore();
        } catch (Exception e) {
            Log.e(e, "[Manager] Cannot start linphone");
        }
    }

    private synchronized void configureCore() {
        String url;
        int i = 1;
        Log.i("[Manager] Configuring Core");
//        this.mAudioManager = new AndroidAudioManager(this.mContext);
        this.mCore.setZrtpSecretsFile(this.mBasePath + "/zrtp_secrets");
        String deviceName = this.mPrefs.getDeviceName(this.mContext);
        String appName = this.mContext.getResources().getString(R.string.user_agent);
        String userAgent = appName + "/" + BuildConfig.VERSION_NAME + " (" + deviceName + ") LinphoneSDK";
        this.mCore.setUserAgent(userAgent, getString(R.string.linphone_sdk_version) + " (" + getString(R.string.linphone_sdk_branch) + ")");
        this.mCore.setCallLogsDatabasePath(this.mCallLogDatabaseFile);
        this.mCore.setFriendsDatabasePath(this.mFriendsDatabaseFile);
        this.mCore.setUserCertificatesPath(this.mUserCertsPath);
        enableDeviceRingtone(this.mPrefs.isDeviceRingtoneEnabled());
        int availableCores = Runtime.getRuntime().availableProcessors();
        Log.w("[Manager] MediaStreamer : " + availableCores + " cores detected and configured");
        this.mCore.migrateLogsFromRcToDb();
        String uri = getString(R.string.default_conference_factory_uri);
        ProxyConfig[] proxyConfigList = this.mCore.getProxyConfigList();
        int length = proxyConfigList.length;
        int i2 = 0;
        while (i2 < length) {
            ProxyConfig lpc = proxyConfigList[i2];
            if (lpc.getIdentityAddress().getDomain().equals(getString(R.string.default_domain))) {
                if (lpc.getConferenceFactoryUri() == null) {
                    lpc.edit();
                    Object[] objArr = new Object[i];
                    objArr[0] = "[Manager] Setting conference factory on proxy config " + lpc.getIdentityAddress().asString() + " to default value: " + uri;
                    Log.i(objArr);
                    lpc.setConferenceFactoryUri(uri);
                    lpc.done();
                }
                if (this.mCore.limeX3DhAvailable() && ((url = this.mCore.getLimeX3DhServerUrl()) == null || url.isEmpty())) {
                    String url2 = getString(R.string.default_lime_x3dh_server_url);
                    Object[] objArr2 = new Object[i];
                    objArr2[0] = "[Manager] Setting LIME X3Dh server url to default value: " + url2;
                    Log.i(objArr2);
                    this.mCore.setLimeX3DhServerUrl(url2);
                }
            }
            i2++;
            i = 1;
        }
//        if (this.mContext.getResources().getBoolean(R.bool.enable_push_id)) {
            PushNotificationUtils.init(this.mContext);
//        }
        this.mProximityWakelock = this.mPowerManager.newWakeLock(32, this.mContext.getPackageName() + ";manager_proximity_sensor");
        resetCameraFromPreferences();
        AccountCreator createAccountCreator = this.mCore.createAccountCreator(LinphonePreferences.instance().getXmlrpcUrl());
        this.mAccountCreator = createAccountCreator;
//        createAccountCreator.setListener(this.mAccountCreatorListener);
        createAccountCreator.addListener(mAccountCreatorListener);
        this.mCallGsmON = false;
        Log.i("[Manager] Core configured");
    }

    public void resetCameraFromPreferences() {
        String[] videoDevicesList;
        Core core = getCore();
        if (core == null) {
            return;
        }
        boolean useFrontCam = LinphonePreferences.instance().useFrontCam();
        String firstDevice = null;
        for (String camera : core.getVideoDevicesList()) {
            if (firstDevice == null) {
                firstDevice = camera;
            }
            if (useFrontCam && camera.contains("Front")) {
                Log.i("[Manager] Found front facing camera: " + camera);
                core.setVideoDevice(camera);
                return;
            }
        }
        Log.i("[Manager] Using first camera available: " + firstDevice);
        core.setVideoDevice(firstDevice);
    }

    public AccountCreator getAccountCreator() {
        if (this.mAccountCreator == null) {
            Log.w("[Manager] Account creator shouldn't be null !");
            AccountCreator createAccountCreator = this.mCore.createAccountCreator(LinphonePreferences.instance().getXmlrpcUrl());
            this.mAccountCreator = createAccountCreator;
            createAccountCreator.addListener(this.mAccountCreatorListener);
//            createAccountCreator.setListener(this.mAccountCreatorListener);
        }
        return this.mAccountCreator;
    }

    public void isAccountWithAlias() {
        if (this.mCore.getDefaultProxyConfig() != null) {
            long now = new Timestamp(new Date().getTime()).getTime();
            AccountCreator accountCreator = getAccountCreator();
            if (LinphonePreferences.instance().getLinkPopupTime() == null || Long.parseLong(LinphonePreferences.instance().getLinkPopupTime()) < now) {
                accountCreator.reset();
                accountCreator.setUsername(LinphonePreferences.instance().getAccountUsername(LinphonePreferences.instance().getDefaultAccountIndex()));
                accountCreator.isAccountExist();
                return;
            }
            return;
        }
        LinphonePreferences.instance().setLinkPopupTime(null);
    }

    public void askLinkWithPhoneNumber() {
        ProxyConfig proxyConfig;
        if (!LinphonePreferences.instance().isLinkPopupEnabled()) {
            return;
        }
        long now = new Timestamp(new Date().getTime()).getTime();
        if ((LinphonePreferences.instance().getLinkPopupTime() != null && Long.parseLong(LinphonePreferences.instance().getLinkPopupTime()) >= now) || (proxyConfig = this.mCore.getDefaultProxyConfig()) == null || !proxyConfig.getDomain().equals(getString(R.string.default_domain))) {
            return;
        }
        long future = new Timestamp(86400000).getTime();
        long newDate = now + future;
        LinphonePreferences.instance().setLinkPopupTime(String.valueOf(newDate));
        final Dialog dialog = LinphoneUtils.getDialog(this.mContext, String.format(getString(R.string.link_account_popup), proxyConfig.getIdentityAddress().asStringUriOnly()));
//        Button delete = (Button) dialog.findViewById(R.id.dialog_delete_button);
//        delete.setVisibility(View.GONE);
//        Button ok = (Button) dialog.findViewById(R.id.dialog_ok_button);
//        ok.setText(getString(R.string.link));
//        ok.setVisibility(View.VISIBLE);
//        Button cancel = (Button) dialog.findViewById(R.id.dialog_cancel_button);
//        cancel.setText(getString(R.string.maybe_later));
//        dialog.findViewById(R.id.dialog_do_not_ask_again_layout).setVisibility(View.VISIBLE);
//        final CheckBox doNotAskAgain = (CheckBox) dialog.findViewById(R.id.doNotAskAgain);
//        dialog.findViewById(R.id.doNotAskAgainLabel).setOnClickListener(new View.OnClickListener() { // from class: com.duzzelcall.managinig.LinphoneManager.6
//            @Override // android.view.View.OnClickListener
//            public void onClick(View v) {
//                CheckBox checkBox = doNotAskAgain;
//                checkBox.setChecked(!checkBox.isChecked());
//            }
//        });
//        ok.setOnClickListener(new View.OnClickListener() { // from class: com.duzzelcall.managinig.LinphoneManager.7
//            @Override // android.view.View.OnClickListener
//            public void onClick(View view) {
//                Intent assistant = new Intent();
//                assistant.setClass(LinphoneManager.this.mContext, PhoneAccountLinkingAssistantActivity.class);
//                LinphoneManager.this.mContext.startActivity(assistant);
//                dialog.dismiss();
//            }
//        });
//        cancel.setOnClickListener(new View.OnClickListener() { // from class: com.duzzelcall.managinig.LinphoneManager.8
//            @Override // android.view.View.OnClickListener
//            public void onClick(View view) {
//                if (doNotAskAgain.isChecked()) {
//                    LinphonePreferences.instance().enableLinkPopup(false);
//                }
//                dialog.dismiss();
//            }
//        });
        dialog.show();
    }

    private boolean isPresenceModelActivitySet() {
        Core core = this.mCore;
        return (core == null || core.getPresenceModel() == null || this.mCore.getPresenceModel().getActivity() == null) ? false : true;
    }

    public void changeStatusToOnline() {
        Core core = this.mCore;
        if (core == null) {
            return;
        }
        PresenceModel model = core.createPresenceModel();
        model.setBasicStatus(PresenceBasicStatus.Open);
        this.mCore.setPresenceModel(model);
    }

    public void changeStatusToOnThePhone() {
        if (this.mCore == null) {
            return;
        }
        if (isPresenceModelActivitySet() && this.mCore.getPresenceModel().getActivity().getType() != PresenceActivity.Type.OnThePhone) {
            this.mCore.getPresenceModel().getActivity().setType(PresenceActivity.Type.OnThePhone);
        } else if (!isPresenceModelActivitySet()) {
            PresenceModel model = this.mCore.createPresenceModelWithActivity(PresenceActivity.Type.OnThePhone, null);
            this.mCore.setPresenceModel(model);
        }
    }

    private void changeStatusToOffline() {
        Core core = this.mCore;
        if (core != null) {
            PresenceModel model = core.getPresenceModel();
            model.setBasicStatus(PresenceBasicStatus.Closed);
            this.mCore.setPresenceModel(model);
        }
    }

    public void initTunnelFromConf() {
        if (!this.mCore.tunnelAvailable()) {
            return;
        }
        NetworkInfo info = this.mConnectivityManager.getActiveNetworkInfo();
        Tunnel tunnel = this.mCore.getTunnel();
        tunnel.cleanServers();
        TunnelConfig config = this.mPrefs.getTunnelConfig();
        if (config.getHost() != null) {
            tunnel.addServer(config);
            manageTunnelServer(info);
        }
    }

    private boolean isTunnelNeeded(NetworkInfo info) {
        if (info == null) {
            Log.i("[Manager] No connectivity: tunnel should be disabled");
            return false;
        }
        String pref = this.mPrefs.getTunnelMode();
        if (getString(R.string.tunnel_mode_entry_value_always).equals(pref)) {
            return true;
        }
        if (info.getType() == 1 || !getString(R.string.tunnel_mode_entry_value_3G_only).equals(pref)) {
            return false;
        }
        Log.i("[Manager] Need tunnel: 'no wifi' connection");
        return true;
    }

    private void manageTunnelServer(NetworkInfo info) {
        Core core = this.mCore;
        if (core != null && core.tunnelAvailable()) {
            Tunnel tunnel = this.mCore.getTunnel();
            Log.i("[Manager] Managing tunnel");
            if (isTunnelNeeded(info)) {
                Log.i("[Manager] Tunnel need to be activated");
                tunnel.setMode(Tunnel.Mode.Enable);
                return;
            }
            Log.i("[Manager] Tunnel should not be used");
            String pref = this.mPrefs.getTunnelMode();
            tunnel.setMode(Tunnel.Mode.Disable);
            if (getString(R.string.tunnel_mode_entry_value_auto).equals(pref)) {
                tunnel.setMode(Tunnel.Mode.Auto);
            }
        }
    }

    public void enableProximitySensing(boolean enable) {
        if (enable) {
            if (!this.mProximitySensingEnabled) {
                this.mSensorManager.registerListener(this, this.mProximity, 3);
                this.mProximitySensingEnabled = true;
            }
        } else if (this.mProximitySensingEnabled) {
            this.mSensorManager.unregisterListener(this);
            this.mProximitySensingEnabled = false;
            if (this.mProximityWakelock.isHeld()) {
                this.mProximityWakelock.release();
            }
        }
    }

    private Boolean isProximitySensorNearby(SensorEvent event) {
        float threshold = 4.001f;
        boolean z = false;
        float distanceInCm = event.values[0];
        float maxDistance = event.sensor.getMaximumRange();
        Log.d("[Manager] Proximity sensor report [" + distanceInCm + "] , for max range [" + maxDistance + "]");
        if (maxDistance <= 4.001f) {
            threshold = maxDistance;
        }
        if (distanceInCm < threshold) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent event) {
        if (event.timestamp == 0) {
            return;
        }
        if (isProximitySensorNearby(event).booleanValue()) {
            if (!this.mProximityWakelock.isHeld()) {
                this.mProximityWakelock.acquire();
            }
        } else if (this.mProximityWakelock.isHeld()) {
            this.mProximityWakelock.release();
        }
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void enableDeviceRingtone(boolean use) {
        if (use) {
            this.mCore.setRing(null);
        } else {
            this.mCore.setRing(this.mRingSoundFile);
        }
    }

    public boolean getCallGsmON() {
        return this.mCallGsmON;
    }

    public void setCallGsmON(boolean on) {
        Core core;
        this.mCallGsmON = on;
        if (on && (core = this.mCore) != null) {
            core.pauseAllCalls();
        }
    }

    private String getString(int key) {
        return this.mContext.getString(key);
    }

    public boolean hasLastCallSasBeenRejected() {
        return this.mHasLastCallSasBeenRejected;
    }

    public void lastCallSasRejected(boolean rejected) {
        this.mHasLastCallSasBeenRejected = rejected;
    }
}
