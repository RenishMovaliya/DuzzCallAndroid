package com.logycraft.duzzcalll;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;

//import com.android.volley.BuildConfig;
//import com.duzzelcall.linphone.core.Address;
//import com.duzzelcall.linphone.core.AuthInfo;
//import com.duzzelcall.linphone.core.Config;
//import com.duzzelcall.linphone.core.Core;
//import com.duzzelcall.linphone.core.Factory;
//import com.duzzelcall.linphone.core.MediaEncryption;
//import com.duzzelcall.linphone.core.NatPolicy;
//import com.duzzelcall.linphone.core.ProxyConfig;
//import com.duzzelcall.linphone.core.Transports;
//import com.duzzelcall.linphone.core.Tunnel;
//import com.duzzelcall.linphone.core.TunnelConfig;
//import com.duzzelcall.linphone.core.VideoActivationPolicy;
//import com.duzzelcall.linphone.core.VideoDefinition;
//import com.duzzelcall.linphone.core.tools.Log;
//import com.duzzelcall.linphone.mediastream.Version;
//import com.duzzelcall.managinig.R;
//import com.duzzelcall.managinig.compatibility.Compatibility;
//import com.duzzelcall.managinig.utils.LinphoneUtils;

import com.duzzcall.duzzcall.BuildConfig;
import com.duzzcall.duzzcall.R;
import com.logycraft.duzzcalll.Util.LinphoneUtils;
import com.logycraft.duzzcalll.helper.compatibility.Compatibility;

import org.linphone.core.Address;
import org.linphone.core.AuthInfo;
import org.linphone.core.Config;
import org.linphone.core.Core;
import org.linphone.core.Factory;
import org.linphone.core.MediaEncryption;
import org.linphone.core.NatPolicy;
import org.linphone.core.ProxyConfig;
import org.linphone.core.TunnelConfig;
import org.linphone.core.Tunnel;
import org.linphone.core.VideoActivationPolicy;
import org.linphone.core.VideoDefinition;
import org.linphone.core.tools.Log;
import org.linphone.mediastream.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/* loaded from: classes2.dex */
public class LinphonePreferences {
    private static final String DEFAULT_ASSISTANT_RC = "/default_assistant_create.rc";
    private static final String LINPHONE_ASSISTANT_RC = "/linphone_assistant_create.rc";
    private static final int LINPHONE_CORE_RANDOM_PORT = -1;
    private static final String LINPHONE_DEFAULT_RC = "/.linphonerc";
    private static final String LINPHONE_FACTORY_RC = "/linphonerc";
    private static final String LINPHONE_LPCONFIG_XSD = "/lpconfig.xsd";
    private static LinphonePreferences sInstance;
    private String mBasePath;
    private Context mContext;
    private TunnelConfig mTunnelConfig = null;

    private LinphonePreferences() {
    }

    public static synchronized LinphonePreferences instance() {
        LinphonePreferences linphonePreferences;
        synchronized (LinphonePreferences.class) {
            if (sInstance == null) {
                sInstance = new LinphonePreferences();
            }
            linphonePreferences = sInstance;
        }
        return linphonePreferences;
    }

    public void destroy() {
        this.mContext = null;
        sInstance = null;
    }

    public void setContext(Context c) {
        this.mContext = c;
        this.mBasePath = c.getFilesDir().getAbsolutePath();
        try {
            copyAssetsFromPackage();
        } catch (IOException e) {
        }
    }

    private void copyAssetsFromPackage() throws IOException {
        copyIfNotExist(R.raw.linphonerc_default, getLinphoneDefaultConfig());
        copyFromPackage(R.raw.linphonerc_factory, new File(getLinphoneFactoryConfig()).getName());
        copyIfNotExist(R.raw.lpconfig, this.mBasePath + LINPHONE_LPCONFIG_XSD);
        copyFromPackage(R.raw.default_assistant_create, new File(this.mBasePath + DEFAULT_ASSISTANT_RC).getName());
        copyFromPackage(R.raw.linphone_assistant_create, new File(this.mBasePath + LINPHONE_ASSISTANT_RC).getName());
    }

    private void copyIfNotExist(int ressourceId, String target) throws IOException {
        File lFileToCopy = new File(target);
        if (!lFileToCopy.exists()) {
            copyFromPackage(ressourceId, lFileToCopy.getName());
        }
    }

    private void copyFromPackage(int ressourceId, String target) throws IOException {
        FileOutputStream lOutputStream = this.mContext.openFileOutput(target, 0);
        InputStream lInputStream = this.mContext.getResources().openRawResource(ressourceId);
        byte[] buff = new byte[8048];
        while (true) {
            int readByte = lInputStream.read(buff);
            if (readByte != -1) {
                lOutputStream.write(buff, 0, readByte);
            } else {
                lOutputStream.flush();
                lOutputStream.close();
                lInputStream.close();
                return;
            }
        }
    }

    public String getLinphoneDefaultConfig() {
        return this.mBasePath + LINPHONE_DEFAULT_RC;
    }

    public String getLinphoneFactoryConfig() {
        return this.mBasePath + LINPHONE_FACTORY_RC;
    }

    public String getDefaultDynamicConfigFile() {
        return this.mBasePath + DEFAULT_ASSISTANT_RC;
    }

    public String getLinphoneDynamicConfigFile() {
        return this.mBasePath + LINPHONE_ASSISTANT_RC;
    }

    private String getString(int key) {
        if (this.mContext == null && LinphoneContext.isReady()) {
            this.mContext = LinphoneContext.instance().getApplicationContext();
        }
        return this.mContext.getString(key);
    }

    private Core getLc() {
        if (!LinphoneContext.isReady()) {
            return null;
        }
        return LinphoneManager.getCore();
    }

    public Config getConfig() {
        Core core = getLc();
        if (core != null) {
            return core.getConfig();
        }
        if (!LinphoneContext.isReady()) {
            File linphonerc = new File(this.mBasePath + LINPHONE_DEFAULT_RC);
            if (linphonerc.exists()) {
                return Factory.instance().createConfig(linphonerc.getAbsolutePath());
            }
            Context context = this.mContext;
            if (context != null) {
                InputStream inputStream = context.getResources().openRawResource(R.raw.linphonerc_default);
                InputStreamReader inputreader = new InputStreamReader(inputStream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                StringBuilder text = new StringBuilder();
                while (true) {
                    try {
                        String line = buffreader.readLine();
                        if (line == null) {
                            break;
                        }
                        text.append(line);
                        text.append('\n');
                    } catch (IOException ioe) {
                        Log.e(ioe);
                    }
                }
                return Factory.instance().createConfigFromString(text.toString());
            }
            return null;
        }
        return Factory.instance().createConfig(getLinphoneDefaultConfig());
    }

    public boolean isFirstLaunch() {
        if (getConfig() == null) {
            return true;
        }
        return getConfig().getBool("app", "first_launch", true);
    }

    public void firstLaunchSuccessful() {
        getConfig().setBool("app", "first_launch", false);
    }

    public String getRingtone(String defaultRingtone) {
        String ringtone = getConfig().getString("app", "ringtone", defaultRingtone);
        return (ringtone == null || ringtone.isEmpty()) ? defaultRingtone : ringtone;
    }

    private ProxyConfig getProxyConfig(int n) {
        if (getLc() == null) {
            return null;
        }
        ProxyConfig[] prxCfgs = getLc().getProxyConfigList();
        if (n >= 0 && n < prxCfgs.length) {
            return prxCfgs[n];
        }
        return null;
    }

    private AuthInfo getAuthInfo(int n) {
        ProxyConfig prxCfg = getProxyConfig(n);
        if (prxCfg == null) {
            return null;
        }
        Address addr = prxCfg.getIdentityAddress();
        return getLc().findAuthInfo(null, addr.getUsername(), addr.getDomain());
    }

    public String getAccountUsername(int n) {
        AuthInfo authInfo = getAuthInfo(n);
        if (authInfo == null) {
            return null;
        }
        return authInfo.getUsername();
    }

    public String getAccountHa1(int n) {
        AuthInfo authInfo = getAuthInfo(n);
        if (authInfo == null) {
            return null;
        }
        return authInfo.getHa1();
    }

    public String getAccountDomain(int n) {
        ProxyConfig proxyConf = getProxyConfig(n);
        return proxyConf != null ? proxyConf.getDomain() : "";
    }

    public int getDefaultAccountIndex() {
        ProxyConfig defaultPrxCfg;
        if (getLc() == null || (defaultPrxCfg = getLc().getDefaultProxyConfig()) == null) {
            return -1;
        }
        ProxyConfig[] prxCfgs = getLc().getProxyConfigList();
        for (int i = 0; i < prxCfgs.length; i++) {
            if (defaultPrxCfg.getIdentityAddress().equals(prxCfgs[i].getIdentityAddress())) {
                return i;
            }
        }
        return -1;
    }

    public int getAccountCount() {
        if (getLc() == null || getLc().getProxyConfigList() == null) {
            return 0;
        }
        return getLc().getProxyConfigList().length;
    }

    public void setAccountEnabled(int n, boolean enabled) {
        int count;
        if (getLc() == null) {
            return;
        }
        ProxyConfig prxCfg = getProxyConfig(n);
        if (prxCfg == null) {
            LinphoneUtils.displayErrorAlert("Errors", this.mContext);
            Toast.makeText(mContext, "Error Display!", Toast.LENGTH_SHORT).show();
            return;
        }
        prxCfg.edit();
        prxCfg.enableRegister(enabled);
        prxCfg.done();
        if (!enabled && getLc().getDefaultProxyConfig().getIdentityAddress().equals(prxCfg.getIdentityAddress()) && (count = getLc().getProxyConfigList().length) > 1) {
            for (int i = 0; i < count; i++) {
                if (isAccountEnabled(i)) {
                    getLc().setDefaultProxyConfig(getProxyConfig(i));
                    return;
                }
            }
        }
    }

    private boolean isAccountEnabled(int n) {
        return getProxyConfig(n).registerEnabled();
    }

    public void setEchoCancellation(boolean enable) {
        if (getLc() == null) {
            return;
        }
        getLc().enableEchoCancellation(enable);
    }

    public boolean echoCancellationEnabled() {
        if (getLc() == null) {
            return false;
        }
        return getLc().echoCancellationEnabled();
    }

    public int getEchoCalibration() {
        return getConfig().getInt("sound", "ec_delay", -1);
    }

    public float getMicGainDb() {
        return getLc().getMicGainDb();
    }

    public void setMicGainDb(float gain) {
        getLc().setMicGainDb(gain);
    }

    public float getPlaybackGainDb() {
        return getLc().getPlaybackGainDb();
    }

    public void setPlaybackGainDb(float gain) {
        getLc().setPlaybackGainDb(gain);
    }

    public boolean useFrontCam() {
        if (getConfig() == null) {
            return false;
        }
        return getConfig().getBool("app", "front_camera_default", true);
    }

    public void setFrontCamAsDefault(boolean frontcam) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "front_camera_default", frontcam);
    }

    public String getCameraDevice() {
        return getLc().getVideoDevice();
    }

    public void setCameraDevice(String device) {
        getLc().setVideoDevice(device);
    }

    public boolean isVideoEnabled() {
        return getLc() != null && getLc().videoSupported() && getLc().videoEnabled();
    }

    public void enableVideo(boolean enable) {
        if (getLc() == null) {
            return;
        }
        getLc().enableVideoCapture(enable);
        getLc().enableVideoDisplay(enable);
    }

    public boolean shouldInitiateVideoCall() {
        if (getLc() == null) {
            return false;
        }
        return getLc().getVideoActivationPolicy().getAutomaticallyInitiate();
    }

    public void setInitiateVideoCall(boolean initiate) {
        if (getLc() == null) {
            return;
        }
        VideoActivationPolicy vap = getLc().getVideoActivationPolicy();
        vap.setAutomaticallyInitiate(initiate);
        getLc().setVideoActivationPolicy(vap);
    }

    public boolean shouldAutomaticallyAcceptVideoRequests() {
        if (getLc() == null) {
            return false;
        }
        VideoActivationPolicy vap = getLc().getVideoActivationPolicy();
        return vap.getAutomaticallyAccept();
    }

    public void setAutomaticallyAcceptVideoRequests(boolean accept) {
        if (getLc() == null) {
            return;
        }
        VideoActivationPolicy vap = getLc().getVideoActivationPolicy();
        vap.setAutomaticallyAccept(accept);
        getLc().setVideoActivationPolicy(vap);
    }

    public String getVideoPreset() {
        if (getLc() == null) {
            return null;
        }
        String preset = getLc().getVideoPreset();
        return preset == null ? "default" : preset;
    }

    public void setVideoPreset(String preset) {
        if (getLc() == null) {
            return;
        }
        if (preset.equals("default")) {
            preset = null;
        }
        getLc().setVideoPreset(preset);
        if (!getVideoPreset().equals("custom")) {
            getLc().setPreferredFramerate(0.0f);
        }
        setPreferredVideoSize(getPreferredVideoSize());
    }

    public String getPreferredVideoSize() {
        return getConfig().getString("video", "size", "qvga");
    }

    public void setPreferredVideoSize(String preferredVideoSize) {
        if (getLc() == null) {
            return;
        }
        VideoDefinition preferredVideoDefinition = Factory.instance().createVideoDefinitionFromName(preferredVideoSize);
        getLc().setPreferredVideoDefinition(preferredVideoDefinition);
    }

    public int getPreferredVideoFps() {
        if (getLc() == null) {
            return 0;
        }
        return (int) getLc().getPreferredFramerate();
    }

    public void setPreferredVideoFps(int fps) {
        if (getLc() == null) {
            return;
        }
        getLc().setPreferredFramerate(fps);
    }

    public int getBandwidthLimit() {
        if (getLc() == null) {
            return 0;
        }
        return getLc().getDownloadBandwidth();
    }

    public void setBandwidthLimit(int bandwidth) {
        if (getLc() == null) {
            return;
        }
        getLc().setUploadBandwidth(bandwidth);
        getLc().setDownloadBandwidth(bandwidth);
    }

    public boolean isFriendlistsubscriptionEnabled() {
        if (getConfig() == null) {
            return false;
        }
        if (getConfig().getBool("app", "friendlist_subscription_enabled", false)) {
            getConfig().setBool("app", "friendlist_subscription_enabled", false);
            enabledFriendlistSubscription(true);
        }
        return getLc().isFriendListSubscriptionEnabled();
    }

    public void enabledFriendlistSubscription(boolean enabled) {
        if (getLc() == null) {
            return;
        }
        getLc().enableFriendListSubscription(enabled);
    }

    public boolean isPresenceStorageInNativeAndroidContactEnabled() {
        if (getConfig() == null) {
            return false;
        }
        return getConfig().getBool("app", "store_presence_in_native_contact", false);
    }

    public void enabledPresenceStorageInNativeAndroidContact(boolean enabled) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "store_presence_in_native_contact", enabled);
    }

    public boolean isDisplayContactOrganization() {
        if (getConfig() == null) {
            return false;
        }
        return getConfig().getBool("app", "display_contact_organization", false);
    }

    public void enabledDisplayContactOrganization(boolean enabled) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "display_contact_organization", enabled);
    }

    public boolean isMediaEncryptionMandatory() {
        if (getLc() == null) {
            return false;
        }
        return getLc().isMediaEncryptionMandatory();
    }

    public void setMediaEncryptionMandatory(boolean accept) {
        if (getLc() == null) {
            return;
        }
        getLc().setMediaEncryptionMandatory(accept);
    }

    public boolean acceptIncomingEarlyMedia() {
        if (getConfig() == null) {
            return false;
        }
        return getConfig().getBool("sip", "incoming_calls_early_media", false);
    }

    public void setAcceptIncomingEarlyMedia(boolean accept) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("sip", "incoming_calls_early_media", accept);
    }

    public boolean useRfc2833Dtmfs() {
        if (getLc() == null) {
            return false;
        }
        return getLc().getUseRfc2833ForDtmf();
    }

    public void sendDtmfsAsRfc2833(boolean use) {
        if (getLc() == null) {
            return;
        }
        getLc().setUseRfc2833ForDtmf(use);
    }

    public boolean useSipInfoDtmfs() {
        if (getLc() == null) {
            return false;
        }
        return getLc().getUseInfoForDtmf();
    }

    public void sendDTMFsAsSipInfo(boolean use) {
        if (getLc() == null) {
            return;
        }
        getLc().setUseInfoForDtmf(use);
    }

    public int getIncTimeout() {
        if (getLc() == null) {
            return 0;
        }
        return getLc().getIncTimeout();
    }

    public void setIncTimeout(int timeout) {
        if (getLc() == null) {
            return;
        }
        getLc().setIncTimeout(timeout);
    }

    public String getVoiceMailUri() {
        if (getConfig() == null) {
            return null;
        }
        return getConfig().getString("app", "voice_mail", null);
    }

    public void setVoiceMailUri(String uri) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setString("app", "voice_mail", uri);
    }

    public boolean getNativeDialerCall() {
        if (getConfig() == null) {
            return false;
        }
        return getConfig().getBool("app", "native_dialer_call", false);
    }

    public void setNativeDialerCall(boolean use) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "native_dialer_call", use);
    }

    public boolean isWifiOnlyEnabled() {
        if (getLc() == null) {
            return false;
        }
        return getLc().wifiOnlyEnabled();
    }

    public void setWifiOnlyEnabled(Boolean enable) {
        if (getLc() == null) {
            return;
        }
        getLc().enableWifiOnly(enable.booleanValue());
    }

    public void useRandomPort(boolean enabled) {
        useRandomPort(enabled, true);
    }

    private void useRandomPort(boolean enabled, boolean apply) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "random_port", enabled);
        if (apply) {
            if (enabled) {
                setSipPort(-1);
            } else {
                setSipPort(5060);
            }
        }
    }

    public boolean isUsingRandomPort() {
        if (getConfig() == null) {
            return true;
        }
        return getConfig().getBool("app", "random_port", true);
    }

    public String getSipPort() {
        if (getLc() == null) {
            return null;
        }
//        Transports transports = getLc().getTransports();
//        int port = transports.getUdpPort() > 0 ? transports.getUdpPort() : transports.getTcpPort();
        return "3510";
    }

    public void setSipPort(int port) {
        if (getLc() == null) {
            return;
        }
//        Transports transports = getLc().getTransports();
//        transports.setUdpPort(port);
//        transports.setTcpPort(port);
//        transports.setTlsPort(-1);
//        getLc().setTransports(transports);
    }

    private NatPolicy getOrCreateNatPolicy() {
        if (getLc() == null) {
            return null;
        }
        NatPolicy nat = getLc().getNatPolicy();
        if (nat == null) {
            return getLc().createNatPolicy();
        }
        return nat;
    }

    public String getStunServer() {
        NatPolicy nat = getOrCreateNatPolicy();
        if (nat == null) {
            return null;
        }
        return nat.getStunServer();
    }

    public void setStunServer(String stun) {
        NatPolicy nat;
        if (getLc() == null || (nat = getOrCreateNatPolicy()) == null) {
            return;
        }
        nat.setStunServer(stun);
        getLc().setNatPolicy(nat);
    }

    public boolean isIceEnabled() {
        NatPolicy nat = getOrCreateNatPolicy();
        if (nat == null) {
            return false;
        }
        return nat.iceEnabled();
    }

    public void setIceEnabled(boolean enabled) {
        NatPolicy nat;
        if (getLc() == null || (nat = getOrCreateNatPolicy()) == null) {
            return;
        }
        nat.enableIce(enabled);
        if (enabled) {
            nat.enableStun(true);
        }
        getLc().setNatPolicy(nat);
    }

    public boolean isTurnEnabled() {
        NatPolicy nat = getOrCreateNatPolicy();
        if (nat == null) {
            return false;
        }
        return nat.turnEnabled();
    }

    public void setTurnEnabled(boolean enabled) {
        NatPolicy nat;
        if (getLc() == null || (nat = getOrCreateNatPolicy()) == null) {
            return;
        }
        nat.enableTurn(enabled);
        getLc().setNatPolicy(nat);
    }

    public String getTurnUsername() {
        NatPolicy nat = getOrCreateNatPolicy();
        if (nat == null) {
            return null;
        }
        return nat.getStunServerUsername();
    }

    public void setTurnUsername(String username) {
        NatPolicy nat;
        if (getLc() == null || (nat = getOrCreateNatPolicy()) == null) {
            return;
        }
        AuthInfo authInfo = getLc().findAuthInfo(null, nat.getStunServerUsername(), null);
        if (authInfo != null) {
            AuthInfo cloneAuthInfo = authInfo.clone();
            getLc().removeAuthInfo(authInfo);
            cloneAuthInfo.setUsername(username);
            cloneAuthInfo.setUserid(username);
            getLc().addAuthInfo(cloneAuthInfo);
        } else {
            getLc().addAuthInfo(Factory.instance().createAuthInfo(username, username, null, null, null, null));
        }
        nat.setStunServerUsername(username);
        getLc().setNatPolicy(nat);
    }

    public void setTurnPassword(String password) {
        NatPolicy nat;
        if (getLc() == null || (nat = getOrCreateNatPolicy()) == null) {
            return;
        }
        AuthInfo authInfo = getLc().findAuthInfo(null, nat.getStunServerUsername(), null);
        if (authInfo != null) {
            AuthInfo cloneAuthInfo = authInfo.clone();
            getLc().removeAuthInfo(authInfo);
            cloneAuthInfo.setPassword(password);
            getLc().addAuthInfo(cloneAuthInfo);
            return;
        }
        getLc().addAuthInfo(Factory.instance().createAuthInfo(nat.getStunServerUsername(), nat.getStunServerUsername(), password, null, null, null));
    }

    public MediaEncryption getMediaEncryption() {
        if (getLc() == null) {
            return null;
        }
        return getLc().getMediaEncryption();
    }

    public void setMediaEncryption(MediaEncryption menc) {
        if (getLc() == null || menc == null) {
            return;
        }
        getLc().setMediaEncryption(menc);
    }

    public boolean isPushNotificationEnabled() {
        if (getConfig() == null) {
            return true;
        }
        return getConfig().getBool("app", "push_notification", true);
    }

    public void setPushNotificationEnabled(boolean enable) {
        ProxyConfig[] proxyConfigList;
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "push_notification", enable);
        Core core = getLc();
        if (core == null) {
            return;
        }
        String str = null;
        int i = 1;
        if (enable) {
            String regId = getPushNotificationRegistrationID();
            String appId = getString(R.string.gcm_defaultSenderId);
            if (regId != null && core.getProxyConfigList().length > 0) {
                ProxyConfig[] proxyConfigList2 = core.getProxyConfigList();
                int length = proxyConfigList2.length;
                int i2 = 0;
                while (i2 < length) {
                    ProxyConfig lpc = proxyConfigList2[i2];
                    if (lpc != null) {
                        if (!lpc.isPushNotificationAllowed()) {
                            lpc.edit();
                            lpc.setContactUriParameters(str);
                            lpc.done();
                            if (lpc.getIdentityAddress() != null) {
                                Object[] objArr = new Object[i];
                                objArr[0] = "[Push Notification] infos removed from proxy config " + lpc.getIdentityAddress().asStringUriOnly();
                                Log.d(objArr);
                            }
                        } else {
                            String contactInfos = "app-id=" + appId + ";pn-type=" + "firebase" + ";pn-timeout=0;pn-tok=" + regId + ";pn-silent=1";
                            String prevContactParams = lpc.getContactParameters();
                            if (prevContactParams == null || prevContactParams.compareTo(contactInfos) != 0) {
                                lpc.edit();
                                lpc.setContactUriParameters(contactInfos);
                                lpc.done();
                                if (lpc.getIdentityAddress() != null) {
                                    Object[] objArr2 = new Object[i];
                                    objArr2[0] = "[Push Notification] infos added to proxy config " + lpc.getIdentityAddress().asStringUriOnly();
                                    Log.d(objArr2);
                                }
                            }
                        }
                    }
                    i2++;
                    str = null;
                    i = 1;
                }
                Log.i("[Push Notification] Refreshing registers to ensure token is up to date: " + regId);
                core.refreshRegisters();
            }
        } else if (core.getProxyConfigList().length > 0) {
            for (ProxyConfig lpc2 : core.getProxyConfigList()) {
                lpc2.edit();
                lpc2.setContactUriParameters(null);
                lpc2.done();
                if (lpc2.getIdentityAddress() != null) {
                    Log.d("[Push Notification] infos removed from proxy config " + lpc2.getIdentityAddress().asStringUriOnly());
                }
            }
            core.refreshRegisters();
        }
    }

    private String getPushNotificationRegistrationID() {
        if (getConfig() == null) {
            return null;
        }
        return getConfig().getString("app", "push_notification_regid", null);
    }

    public void setPushNotificationRegistrationID(String regId) {
        if (getConfig() == null) {
            return;
        }
        Log.i("[Push Notification] New token received: " + regId);
        getConfig().setString("app", "push_notification_regid", regId != null ? regId : "");
        setPushNotificationEnabled(isPushNotificationEnabled());
    }

    public void useIpv6(Boolean enable) {
        if (getLc() == null) {
            return;
        }
        getLc().enableIpv6(enable.booleanValue());
    }

    public boolean isUsingIpv6() {
        if (getLc() == null) {
            return false;
        }
        return getLc().ipv6Enabled();
    }

    public boolean isDebugEnabled() {
        if (getConfig() == null) {
            return false;
        }
        return getConfig().getBool("app", BuildConfig.BUILD_TYPE, false);
    }

    public void setDebugEnabled(boolean enabled) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", BuildConfig.BUILD_TYPE, enabled);
        LinphoneUtils.configureLoggingService(enabled, this.mContext.getString(R.string.app_name));
    }

    public void setJavaLogger(boolean enabled) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "java_logger", enabled);
        LinphoneUtils.configureLoggingService(isDebugEnabled(), this.mContext.getString(R.string.app_name));
    }

    public boolean useJavaLogger() {
        if (getConfig() == null) {
            return false;
        }
        return getConfig().getBool("app", "java_logger", false);
    }

    public boolean isAutoStartEnabled() {
        if (getConfig() == null) {
            return false;
        }
        return getConfig().getBool("app", "auto_start", false);
    }

    public void setAutoStart(boolean autoStartEnabled) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "auto_start", autoStartEnabled);
    }

    public String getSharingPictureServerUrl() {
        if (getLc() == null) {
            return null;
        }
        return getLc().getFileTransferServer();
    }

    public void setSharingPictureServerUrl(String url) {
        if (getLc() == null) {
            return;
        }
        getLc().setFileTransferServer(url);
    }

    public String getLogCollectionUploadServerUrl() {
        if (getLc() == null) {
            return null;
        }
        return getLc().getLogCollectionUploadServerUrl();
    }

    public void setLogCollectionUploadServerUrl(String url) {
        if (getLc() == null) {
            return;
        }
        getLc().setLogCollectionUploadServerUrl(url);
    }

    public String getRemoteProvisioningUrl() {
        if (getLc() == null) {
            return null;
        }
        return getLc().getProvisioningUri();
    }

    public void setRemoteProvisioningUrl(String url) {
        if (getLc() == null) {
            return;
        }
        if (url != null && url.isEmpty()) {
            url = null;
        }
        getLc().setProvisioningUri(url);
    }

    public String getDefaultDisplayName() {
        if (getLc() == null) {
            return null;
        }
        return getLc().createPrimaryContactParsed().getDisplayName();
    }

    public void setDefaultDisplayName(String displayName) {
        if (getLc() == null) {
            return;
        }
        Address primary = getLc().createPrimaryContactParsed();
        primary.setDisplayName(displayName);
        getLc().setPrimaryContact(primary.asString());
    }

    public String getDefaultUsername() {
        if (getLc() == null) {
            return null;
        }
        return getLc().createPrimaryContactParsed().getUsername();
    }

    public void setDefaultUsername(String username) {
        if (getLc() == null) {
            return;
        }
        Address primary = getLc().createPrimaryContactParsed();
        primary.setUsername(username);
        getLc().setPrimaryContact(primary.asString());
    }

    public TunnelConfig getTunnelConfig() {
        if (getLc() != null && getLc().tunnelAvailable()) {
            Tunnel tunnel = getLc().getTunnel();
            if (this.mTunnelConfig == null) {
                TunnelConfig[] servers = tunnel.getServers();
                if (servers.length > 0) {
                    this.mTunnelConfig = servers[0];
                } else {
                    this.mTunnelConfig = Factory.instance().createTunnelConfig();
                }
            }
            return this.mTunnelConfig;
        }
        return null;
    }

    public String getTunnelHost() {
        TunnelConfig config = getTunnelConfig();
        if (config != null) {
            return config.getHost();
        }
        return null;
    }

    public void setTunnelHost(String host) {
        TunnelConfig config = getTunnelConfig();
        if (config != null) {
            config.setHost(host);
            LinphoneManager.getInstance().initTunnelFromConf();
        }
    }

    public int getTunnelPort() {
        TunnelConfig config = getTunnelConfig();
        if (config != null) {
            return config.getPort();
        }
        return -1;
    }

    public void setTunnelPort(int port) {
        TunnelConfig config = getTunnelConfig();
        if (config != null) {
            config.setPort(port);
            LinphoneManager.getInstance().initTunnelFromConf();
        }
    }

    public String getTunnelHost2() {
        TunnelConfig config = getTunnelConfig();
        if (config != null) {
            return config.getHost2();
        }
        return null;
    }

    public void setTunnelHost2(String host) {
        TunnelConfig config = getTunnelConfig();
        if (config != null) {
            config.setHost2(host);
            LinphoneManager.getInstance().initTunnelFromConf();
        }
    }

    public int getTunnelPort2() {
        TunnelConfig config = getTunnelConfig();
        if (config != null) {
            return config.getPort2();
        }
        return -1;
    }

    public void setTunnelPort2(int port) {
        TunnelConfig config = getTunnelConfig();
        if (config != null) {
            config.setPort2(port);
            LinphoneManager.getInstance().initTunnelFromConf();
        }
    }

    public void enableTunnelDualMode(boolean enable) {
        LinphoneManager.getInstance().initTunnelFromConf();
        getLc().getTunnel().enableDualMode(enable);
    }

    public boolean isTunnelDualModeEnabled() {
        Tunnel tunnel = getLc().getTunnel();
        if (tunnel != null) {
            return tunnel.dualModeEnabled();
        }
        return false;
    }

    public String getTunnelMode() {
        return getConfig().getString("app", "tunnel", null);
    }

    public void setTunnelMode(String mode) {
        getConfig().setString("app", "tunnel", mode);
        LinphoneManager.getInstance().initTunnelFromConf();
    }

    public boolean adaptiveRateControlEnabled() {
        if (getLc() == null) {
            return false;
        }
        return getLc().adaptiveRateControlEnabled();
    }

    public void enableAdaptiveRateControl(boolean enabled) {
        if (getLc() == null) {
            return;
        }
        getLc().enableAdaptiveRateControl(enabled);
    }

    public int getCodecBitrateLimit() {
        if (getConfig() == null) {
            return 36;
        }
        return getConfig().getInt("audio", "codec_bitrate_limit", 36);
    }

    public void setCodecBitrateLimit(int bitrate) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setInt("audio", "codec_bitrate_limit", bitrate);
    }

    public String getXmlrpcUrl() {
        if (getConfig() == null) {
            return null;
        }
        return getConfig().getString("assistant", "xmlrpc_url", null);
    }

    public String getLinkPopupTime() {
        if (getConfig() == null) {
            return null;
        }
        return getConfig().getString("app", "link_popup_time", null);
    }

    public void setLinkPopupTime(String date) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setString("app", "link_popup_time", date);
    }

    public boolean isLinkPopupEnabled() {
        if (getConfig() == null) {
            return true;
        }
        return getConfig().getBool("app", "link_popup_enabled", true);
    }

    public void enableLinkPopup(boolean enable) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "link_popup_enabled", enable);
    }

    public boolean isDNDSettingsPopupEnabled() {
        if (getConfig() == null) {
            return true;
        }
        return getConfig().getBool("app", "dnd_settings_popup_enabled", true);
    }

    public void enableDNDSettingsPopup(boolean enable) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "dnd_settings_popup_enabled", enable);
    }

    public boolean isLimeSecurityPopupEnabled() {
        if (getConfig() == null) {
            return true;
        }
        return getConfig().getBool("app", "lime_security_popup_enabled", true);
    }

    public void enableLimeSecurityPopup(boolean enable) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "lime_security_popup_enabled", enable);
    }

    public String getDebugPopupAddress() {
        if (getConfig() == null) {
            return null;
        }
        return getConfig().getString("app", "debug_popup_magic", null);
    }

    public String getActivityToLaunchOnIncomingReceived() {
        return getConfig() == null ? "org.linphone.call.CallIncomingActivity" : getConfig().getString("app", "incoming_call_activity", "org.linphone.call.CallIncomingActivity");
    }

    public void setActivityToLaunchOnIncomingReceived(String name) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setString("app", "incoming_call_activity", name);
    }

    public boolean getServiceNotificationVisibility() {
        if (getConfig() == null) {
            return false;
        }
        return getConfig().getBool("app", "show_service_notification", false);
    }

    public void setServiceNotificationVisibility(boolean enable) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "show_service_notification", enable);
    }

    public String getCheckReleaseUrl() {
        if (getConfig() == null) {
            return null;
        }
        return getConfig().getString("misc", "version_check_url_root", null);
    }

    public int getLastCheckReleaseTimestamp() {
        if (getConfig() == null) {
            return 0;
        }
        return getConfig().getInt("app", "version_check_url_last_timestamp", 0);
    }

    public void setLastCheckReleaseTimestamp(int timestamp) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setInt("app", "version_check_url_last_timestamp", timestamp);
    }

    public boolean isOverlayEnabled() {
        if ((!Version.sdkAboveOrEqual(26) || !this.mContext.getResources().getBoolean(R.bool.allow_pip_while_video_call)) && getConfig() != null) {
            return getConfig().getBool("app", "display_overlay", false);
        }
        return false;
    }

    public void enableOverlay(boolean enable) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "display_overlay", enable);
    }

    public boolean isDeviceRingtoneEnabled() {
        int readExternalStorage = this.mContext.getPackageManager().checkPermission("android.permission.READ_EXTERNAL_STORAGE", this.mContext.getPackageName());
        return getConfig() == null ? readExternalStorage == 0 : getConfig().getBool("app", "device_ringtone", true) && readExternalStorage == 0;
    }

    public void enableDeviceRingtone(boolean enable) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "device_ringtone", enable);
        LinphoneManager.getInstance().enableDeviceRingtone(enable);
    }

    public boolean isIncomingCallVibrationEnabled() {
        if (getConfig() == null) {
            return true;
        }
        return getConfig().getBool("app", "incoming_call_vibration", true);
    }

    public void enableIncomingCallVibration(boolean enable) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "incoming_call_vibration", enable);
    }

    public boolean isBisFeatureEnabled() {
        if (getConfig() == null) {
            return true;
        }
        return getConfig().getBool("app", "bis_feature", true);
    }

    public boolean isAutoAnswerEnabled() {
        if (getConfig() == null) {
            return false;
        }
        return getConfig().getBool("app", "auto_answer", false);
    }

    public void enableAutoAnswer(boolean enable) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "auto_answer", enable);
    }

    public int getAutoAnswerTime() {
        if (getConfig() == null) {
            return 0;
        }
        return getConfig().getInt("app", "auto_answer_delay", 0);
    }

    public void setAutoAnswerTime(int time) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setInt("app", "auto_answer_delay", time);
    }

    public void disableFriendsStorage() {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("misc", "store_friends", false);
    }

    public boolean useBasicChatRoomFor1To1() {
        if (getConfig() == null) {
            return false;
        }
        return getConfig().getBool("app", "prefer_basic_chat_room", false);
    }

    public int getAutoDownloadFileMaxSize() {
        if (getLc() == null) {
            return -1;
        }
        return getLc().getMaxSizeForAutoDownloadIncomingFiles();
    }

    public void setAutoDownloadFileMaxSize(int size) {
        if (getLc() == null) {
            return;
        }
        getLc().setMaxSizeForAutoDownloadIncomingFiles(size);
    }

    public void setDownloadedImagesVisibleInNativeGallery(boolean visible) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "make_downloaded_images_public_in_gallery", visible);
    }

    public boolean makeDownloadedImagesVisibleInNativeGallery() {
        if (getConfig() == null) {
            return false;
        }
        return getConfig().getBool("app", "make_downloaded_images_public_in_gallery", true);
    }

    public boolean hasPowerSaverDialogBeenPrompted() {
        if (getConfig() == null) {
            return false;
        }
        return getConfig().getBool("app", "android_power_saver_dialog", false);
    }

    public void powerSaverDialogPrompted(boolean b) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "android_power_saver_dialog", b);
    }

    public boolean isDarkModeEnabled() {
        boolean z = false;
        if (getConfig() != null) {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                z = true;
            }
            boolean useNightModeDefault = z;
            Context context = this.mContext;
            if (context != null) {
                int nightMode = context.getResources().getConfiguration().uiMode & 48;
                if (nightMode == 32) {
                    useNightModeDefault = true;
                }
            }
            return getConfig().getBool("app", "dark_mode", useNightModeDefault);
        }
        return false;
    }

    public void enableDarkMode(boolean enable) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "dark_mode", enable);
    }

    public String getDeviceName(Context context) {
        String defaultValue = Compatibility.getDeviceName(context);
        return getConfig() == null ? defaultValue : getConfig().getString("app", "device_name", defaultValue);
    }

    public void setDeviceName(String name) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setString("app", "device_name", name);
    }

    public boolean isEchoCancellationCalibrationDone() {
        if (getConfig() == null) {
            return false;
        }
        return getConfig().getBool("app", "echo_cancellation_calibration_done", false);
    }

    public void setEchoCancellationCalibrationDone(boolean done) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "echo_cancellation_calibration_done", done);
    }

    public boolean isOpenH264CodecDownloadEnabled() {
        if (getConfig() == null) {
            return true;
        }
        return getConfig().getBool("app", "open_h264_download_enabled", true);
    }

    public void setOpenH264CodecDownloadEnabled(boolean enable) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "open_h264_download_enabled", enable);
    }

    public boolean isVideoPreviewEnabled() {
        return getConfig() != null && isVideoEnabled() && getConfig().getBool("app", "video_preview", false);
    }

    public void setVideoPreviewEnabled(boolean enabled) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "video_preview", enabled);
    }

    public boolean shortcutsCreationEnabled() {
        if (getConfig() == null) {
            return false;
        }
        return getConfig().getBool("app", "shortcuts", false);
    }

    public void enableChatRoomsShortcuts(boolean enable) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "shortcuts", enable);
    }

    public boolean hideEmptyChatRooms() {
        if (getConfig() == null) {
            return true;
        }
        return getConfig().getBool("misc", "hide_empty_chat_rooms", true);
    }

    public void setHideEmptyChatRooms(boolean hide) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("misc", "hide_empty_chat_rooms", hide);
    }

    public boolean hideRemovedProxiesChatRooms() {
        if (getConfig() == null) {
            return true;
        }
        return getConfig().getBool("misc", "hide_chat_rooms_from_removed_proxies", true);
    }

    public void setHideRemovedProxiesChatRooms(boolean hide) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("misc", "hide_chat_rooms_from_removed_proxies", hide);
    }

    public void enableEphemeralMessages(boolean enable) {
        if (getConfig() == null) {
            return;
        }
        getConfig().setBool("app", "ephemeral", enable);
    }

    public boolean isEphemeralMessagesEnabled() {
        if (getConfig() == null) {
            return true;
        }
        return getConfig().getBool("app", "ephemeral", false);
    }
}
