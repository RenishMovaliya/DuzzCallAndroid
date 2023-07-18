package com.logycraft.duzzcalll.Util;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.widget.Toast;

import com.duzzcall.duzzcall.R;
import com.logycraft.duzzcalll.LinphoneContext;
import com.logycraft.duzzcalll.LinphoneManager;
import com.logycraft.duzzcalll.LinphonePreferences;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.CallParams;
import org.linphone.core.Core;
import org.linphone.core.MediaEncryption;
import org.linphone.core.ProxyConfig;
import org.linphone.core.tools.Log;
import org.linphone.mediastream.Version;

//import com.duzzelcall.linphone.core.Address;
//import com.duzzelcall.linphone.core.Call;
//import com.duzzelcall.linphone.core.CallParams;
//import com.duzzelcall.linphone.core.Core;
//import com.duzzelcall.linphone.core.MediaEncryption;
//import com.duzzelcall.linphone.core.ProxyConfig;
//import com.duzzelcall.linphone.core.tools.Log;
//import com.duzzelcall.linphone.mediastream.Version;
//import com.duzzelcall.managinig.LinphoneContext;
//import com.duzzelcall.managinig.LinphoneManager;
//import com.duzzelcall.managinig.R;
//import com.duzzelcall.managinig.contacts.ContactsManager;
//import com.duzzelcall.managinig.contacts.LinphoneContact;
//import com.duzzelcall.managinig.dialer.views.AddressType;
//import com.duzzelcall.managinig.settings.LinphonePreferences;
//import com.duzzelcall.managinig.utils.FileUtils;

/* loaded from: classes2.dex */
public class CallManager {
//    private BandwidthManager mBandwidthManager = new BandwidthManager();
    private CallActivityInterface mCallInterface;
    private Context mContext;

    public CallManager(Context context) {
        this.mContext = context;
    }

    public void destroy() {
//        this.mBandwidthManager.destroy();
    }

    public void terminateCurrentCallOrConferenceOrAll() {
        Core core = LinphoneManager.getCore();
        Call call = core.getCurrentCall();
        if (call != null) {
            call.terminate();
        } else if (core.isInConference()) {
            core.terminateConference();
        } else {
            core.terminateAllCalls();
        }
    }

    public void addVideo() {
        Call call = LinphoneManager.getCore().getCurrentCall();
        if (call.getState() != Call.State.End && call.getState() != Call.State.Released && !call.getCurrentParams().videoEnabled()) {
            enableCamera(call, true);
            reinviteWithVideo();
        }
    }

    public void removeVideo() {
        Core core = LinphoneManager.getCore();
        Call call = core.getCurrentCall();
        CallParams params = core.createCallParams(call);
        params.enableVideo(false);
        call.update(params);
    }

    public void switchCamera() {
        Core core = LinphoneManager.getCore();
        if (core == null) {
            return;
        }
        String currentDevice = core.getVideoDevice();
        Log.i("[Call Manager] Current camera device is " + currentDevice);
        String[] devices = core.getVideoDevicesList();
        int length = devices.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            String d = devices[i];
            if (d.equals(currentDevice) || d.equals("StaticImage: Static picture")) {
                i++;
            } else {
                Log.i("[Call Manager] New camera device will be " + d);
                core.setVideoDevice(d);
                break;
            }
        }
        Call call = core.getCurrentCall();
        if (call == null) {
            Log.i("[Call Manager] Switching camera while not in call");
        } else {
            call.update(null);
        }
    }

    public boolean acceptCall(Call call) {
        if (call == null) {
            return false;
        }
        Core core = LinphoneManager.getCore();
        CallParams params = core.createCallParams(call);
        boolean isLowBandwidthConnection = !LinphoneUtils.isHighBandwidthConnection(LinphoneContext.instance().getApplicationContext());
        if (params == null) {
            Log.e("[Call Manager] Could not create call params for call");
            return false;
        }
        params.enableLowBandwidth(isLowBandwidthConnection);
        params.setRecordFile(FileUtils.getCallRecordingFilename(this.mContext, call.getRemoteAddress()));
        call.acceptWithParams(params);
        return true;
    }

    public void acceptCallUpdate(boolean accept) {
        Core core = LinphoneManager.getCore();
        Call call = core.getCurrentCall();
        if (call == null) {
            return;
        }
        CallParams params = core.createCallParams(call);
        if (accept) {
            params.enableVideo(true);
            core.enableVideoCapture(true);
            core.enableVideoDisplay(true);
        }
        call.acceptUpdate(params);
    }

    public void inviteAddress(Address address, boolean forceZRTP) {
        boolean isLowBandwidthConnection = !LinphoneUtils.isHighBandwidthConnection(LinphoneContext.instance().getApplicationContext());
        inviteAddress(address, false, isLowBandwidthConnection, forceZRTP);
    }

    public void inviteAddress(Address address, boolean videoEnabled, boolean lowBandwidth) {
        inviteAddress(address, videoEnabled, lowBandwidth, false);
    }

    public void newOutgoingCall(AddressType address) {
        String to = address.getText().toString();
        newOutgoingCall(to, address.getDisplayedName());
    }

    public void newOutgoingCall(String to, String displayName) {
//        LinphoneContact contact;
        String alias;
        if (to == null) {
            return;
        }
//        if ((!to.startsWith("sip:") || !to.contains("@")) && (contact = ContactsManager.getInstance().findContactFromPhoneNumber(to)) != null && (alias = contact.getContactFromPresenceModelForUriOrTel(to)) != null) {
//            to = alias;
//        }
        LinphonePreferences preferences = LinphonePreferences.instance();
        Core core = LinphoneManager.getCore();
        Address address = core.interpretUrl(to);
        boolean z = false;
        if (address == null) {
            Log.e("[Call Manager] Couldn't convert to String to Address : " + to);
            return;
        }
        ProxyConfig lpc = core.getDefaultProxyConfig();
        if (this.mContext.getResources().getBoolean(R.bool.forbid_self_call) && lpc != null && address.weakEqual(lpc.getIdentityAddress())) {
            return;
        }
        address.setDisplayName(displayName);
        boolean isLowBandwidthConnection = !LinphoneUtils.isHighBandwidthConnection(LinphoneContext.instance().getApplicationContext());
        if (core.isNetworkReachable()) {
            if (Version.isVideoCapable()) {
                boolean prefVideoEnable = preferences.isVideoEnabled();
                boolean prefInitiateWithVideo = preferences.shouldInitiateVideoCall();
                if (prefVideoEnable && prefInitiateWithVideo) {
                    z = true;
                }
                inviteAddress(address, z, isLowBandwidthConnection);
                return;
            }
            inviteAddress(address, false, isLowBandwidthConnection);
            return;
        }
        Context context = this.mContext;
        Toast.makeText(context, context.getString(R.string.error_network_unreachable), Toast.LENGTH_LONG).show();
        Log.e("[Call Manager] Error: " + this.mContext.getString(R.string.error_network_unreachable));
    }

    public void playDtmf(ContentResolver r, char dtmf) {
        try {
            if (Settings.System.getInt(r, "dtmf_tone") == 0) {
                return;
            }
        } catch (Settings.SettingNotFoundException e) {
            Log.e("[Call Manager] playDtmf exception: " + e);
        }
        LinphoneManager.getCore().playDtmf(dtmf, -1);
    }

    public boolean shouldShowAcceptCallUpdateDialog(Call call) {
        if (call == null) {
            return true;
        }
        boolean remoteVideo = call.getRemoteParams().videoEnabled();
        boolean localVideo = call.getCurrentParams().videoEnabled();
        boolean autoAcceptCameraPolicy = LinphonePreferences.instance().shouldAutomaticallyAcceptVideoRequests();
        return remoteVideo && !localVideo && !autoAcceptCameraPolicy && !call.getCore().isInConference();
    }

    public void setCallInterface(CallActivityInterface callInterface) {
        this.mCallInterface = callInterface;
    }

    public void resetCallControlsHidingTimer() {
        CallActivityInterface callActivityInterface = this.mCallInterface;
        if (callActivityInterface != null) {
            callActivityInterface.resetCallControlsHidingTimer();
        }
    }

    public void refreshInCallActions() {
        CallActivityInterface callActivityInterface = this.mCallInterface;
        if (callActivityInterface != null) {
            callActivityInterface.refreshInCallActions();
        }
    }

    public void removeCallFromConference(Call call) {
        if (call == null || call.getConference() == null) {
            return;
        }
        call.getConference().removeParticipant(call.getRemoteAddress());
        if (call.getCore().getConferenceSize() <= 1) {
            call.getCore().leaveConference();
        }
    }

    public void pauseConference() {
        Core core = LinphoneManager.getCore();
        if (core == null) {
            return;
        }
        if (core.isInConference()) {
            Log.i("[Call Manager] Pausing conference");
            core.leaveConference();
            return;
        }
        Log.w("[Call Manager] Core isn't in a conference, can't pause it");
    }

    public void resumeConference() {
        Core core = LinphoneManager.getCore();
        if (core == null) {
            return;
        }
        if (!core.isInConference()) {
            Log.i("[Call Manager] Resuming conference");
            core.enterConference();
            return;
        }
        Log.w("[Call Manager] Core is already in a conference, can't resume it");
    }

    private void inviteAddress(Address address, boolean videoEnabled, boolean lowBandwidth, boolean forceZRTP) {
        Core core = LinphoneManager.getCore();
        CallParams params = core.createCallParams(null);
//        this.mBandwidthManager.updateWithProfileSettings(params);
        if (videoEnabled && params.videoEnabled()) {
            params.enableVideo(true);
        } else {
            params.enableVideo(false);
        }
        if (lowBandwidth) {
            params.enableLowBandwidth(true);
            Log.d("[Call Manager] Low bandwidth enabled in call params");
        }
        if (forceZRTP) {
            params.setMediaEncryption(MediaEncryption.ZRTP);
        }
        String recordFile = FileUtils.getCallRecordingFilename(LinphoneContext.instance().getApplicationContext(), address);
        params.setRecordFile(recordFile);
        core.inviteAddressWithParams(address, params);
    }

    private boolean reinviteWithVideo() {
        Core core = LinphoneManager.getCore();
        Call call = core.getCurrentCall();
        if (call == null) {
            Log.e("[Call Manager] Trying to add video while not in call");
            return false;
        } else if (call.getRemoteParams().lowBandwidthEnabled()) {
            Log.e("[Call Manager] Remote has low bandwidth, won't be able to do video");
            return false;
        } else {
            CallParams params = core.createCallParams(call);
            if (params.videoEnabled()) {
                return false;
            }
//            this.mBandwidthManager.updateWithProfileSettings(params);
            if (!params.videoEnabled()) {
                return false;
            }
            call.update(params);
            return true;
        }
    }

    private void enableCamera(Call call, boolean enable) {
        if (call != null) {
            call.enableCamera(enable);
            if (this.mContext.getResources().getBoolean(R.bool.enable_call_notification)) {
                LinphoneContext.instance().getNotificationManager().displayCallNotification(LinphoneManager.getCore().getCurrentCall());
            }
        }
    }
}
