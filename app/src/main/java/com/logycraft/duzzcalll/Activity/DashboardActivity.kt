package com.logycraft.duzzcalll.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.duzzcall.duzzcall.R
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.logycraft.duzzcalll.LinphoneContext
import com.logycraft.duzzcalll.LinphoneManager
import com.logycraft.duzzcalll.LinphonePreferences
import com.logycraft.duzzcalll.Util.Preference
import com.logycraft.duzzcalll.fragment.*
import com.logycraft.duzzcalll.helper.CallBackListener
import com.logycraft.duzzcalll.helper.showGrantedToast
import com.logycraft.duzzcalll.helper.showPermanentlyDeniedDialog
import com.logycraft.duzzcalll.helper.showRationaleDialog
import com.logycraft.duzzcalll.service.LinphoneService
import org.linphone.core.*

//import com.logycraft.duzzcalll.core.*

class DashboardActivity : AppCompatActivity(), CallBackListener,  PermissionRequest.Listener{
    lateinit var bottomNav: BottomNavigationView
     lateinit var core: Core

    private val request by lazy {
        permissionsBuilder(Manifest.permission.RECORD_AUDIO).build()
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
//        loadFragment(HomeFragment())

        bottomNav = findViewById(R.id.bottomNav) as BottomNavigationView
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.favourite -> {
                    loadFragment(FavouriteFragment())
                    true
                }

                R.id.history -> {
                    loadFragment(HistoryFragment())
                    true
                }

                R.id.contact -> {
                    loadFragment(ContactFragment())
                    true
                }

                R.id.dialpad -> {
                    loadFragment(DialFragment())
                    true
                }

                R.id.setting -> {
                    loadFragment(SettingFragment())
                    true
                }

                else -> {
                    true
                }
            }
        }
//        loadFragment(DialFragment())
        bottomNav.setSelectedItemId(R.id.dialpad);
        val factory = Factory.instance()
        factory.setDebugMode(true, "Hello Linphone")
        core = LinphoneManager.getCore()
//        core = factory.createCore(null, null, this)

//        findViewById<Button>(org.linphone.core.R.id.connect).setOnClickListener {
//            login()
//            it.isEnabled = false
//        }

        // For video to work, we need two TextureViews:
        // one for the remote video and one for the local preview
//        core.nativeVideoWindowId = findViewById(org.linphone.core.R.id.remote_video_surface)
        // The local preview is a org.linphone.mediastream.video.capture.CaptureTextureView
        // which inherits from TextureView and contains code to keep the ratio of the capture video
//        core.nativePreviewWindowId = findViewById(org.linphone.core.R.id.local_preview_video_surface)

        // Here we enable the video capture & display at Core level
        // It doesn't mean calls will be made with video automatically,
        // But it allows to use it later
//        core.setVideoCaptureEnabled(true)
//        core.setVideoDisplayEnabled(true)
//        core.enableVideoCapture(true)
//        core.enableVideoDisplay(true)
        // When enabling the video, the remote will either automatically answer the update request
        // or it will ask it's user depending on it's policy.
        // Here we have configured the policy to always automatically accept video requests
        core.videoActivationPolicy.automaticallyAccept = true
        // If you don't want to automatically accept,
        // you'll have to use a code similar to the one in toggleVideo to answer a received request
        loginLinphone()
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

//    private var accountCreator: AccountCreator = TODO()
//    private val accountCreator: AccountCreator


    private var accountToCheck: Account? = null
    val waitForServerAnswer = MutableLiveData<Boolean>()
    private fun loginLinphone() {
        var usedata = Preference.getLoginData(this@DashboardActivity)
        waitForServerAnswer.value = true
        val username = usedata?.extension?.extension
        val password = usedata?.extension?.password
        val domain = "dzcl.et.lk"
        val transportType = TransportType.Udp
        val  displayName = usedata?.extension?.firstName+" "+usedata?.extension?.lastName
        val authInfo = Factory.instance()
            .createAuthInfo(username.toString(), null, password, null, null, domain, null)
        authInfo.domain
        val params = core.createAccountParams()
        val identity = Factory.instance().createAddress("sip:$username@$domain")
        params.identityAddress = identity

        val address = Factory.instance().createAddress("sip:$domain")
        address?.transport = transportType
        params.serverAddress = address
//        params.isRegisterEnabled = true
        params.registerEnabled=true
        val account = core.createAccount(params)

        core.addAuthInfo(authInfo)
        core.addAccount(account)

        // Asks the CaptureTextureView to resize to match the captured video's size ratio
        core.config.setBool("video", "auto_resize_preview_to_keep_ratio", true)

        core.defaultAccount = account
        core.addListener(coreListener)
        core.start()


        // We will need the RECORD_AUDIO permission for video call
        if (packageManager.checkPermission(
                Manifest.permission.RECORD_AUDIO,
                packageName
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            request.send()
//            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 0)
            return
        }
        LinphonePreferences.instance().setServiceNotificationVisibility(true)
//        LinphoneService().onCreate();
        LinphoneContext.instance().getNotificationManager().startForeground()
        request.addListener(this)
    }

        private val coreListener = object : CoreListenerStub() {
        override fun onAccountRegistrationStateChanged(
            core: Core,
            account: Account,
            state: RegistrationState?,
            message: String
        ) {
//            findViewById<TextView>(org.linphone.core.R.id.registration_status).text = message
            if (state == RegistrationState.Failed) {
//                findViewById<Button>(org.linphone.core.R.id.connect).isEnabled = true
                Log.i("SipTest", "REG Failed")
            } else if (state == RegistrationState.Ok) {
                Log.i("SipTest", "REG Success")
                //Start incoming call service
            }
        }

        override fun onCallStateChanged(
            core: Core,
            call: Call,
            state: Call.State?,
            message: String
        )
        {
            // This function will be called each time a call state changes,
            // which includes new incoming/outgoing calls
//            findViewById<TextView>(org.linphone.core.R.id.call_status).text = message
            Log.i("SipTest", "Call message" + message)
            when (state) {
                Call.State.OutgoingInit -> {
                    // First state an outgoing call will go through
                }
                Call.State.OutgoingProgress -> {
                    // Right after outgoing init
                }
                Call.State.OutgoingRinging -> {
                    // This state will be reached upon reception of the 180 RINGING
                }
                Call.State.Connected -> {
                    // When the 200 OK has been received
                }
                Call.State.StreamsRunning -> {
                    // This state indicates the call is active.
                    // You may reach this state multiple times, for example after a pause/resume
                    // or after the ICE negotiation completes
                    // Wait for the call to be connected before allowing a call update
//                    findViewById<Button>(org.linphone.core.R.id.pause).isEnabled = true
//                    findViewById<Button>(org.linphone.core.R.id.pause).text = "Pause"
//                    findViewById<Button>(org.linphone.core.R.id.toggle_video).isEnabled = true
//
//                    // Only enable toggle camera button if there is more than 1 camera and the video is enabled
//                    // We check if core.videoDevicesList.size > 2 because of the fake camera with static image created by our SDK (see below)
//                    findViewById<Button>(org.linphone.core.R.id.toggle_camera).isEnabled =
//                        core.videoDevicesList.size > 2 && call.currentParams.videoEnabled()
                }
                Call.State.Paused -> {
                    // When you put a call in pause, it will became Paused
//                    findViewById<Button>(org.linphone.core.R.id.pause).text = "Resume"
//                    findViewById<Button>(org.linphone.core.R.id.toggle_video).isEnabled = false
                }
                Call.State.PausedByRemote -> {
                    // When the remote end of the call pauses it, it will be PausedByRemote
                }
                Call.State.Updating -> {
                    // When we request a call update, for example when toggling video
                }
                Call.State.UpdatedByRemote -> {
                    // When the remote requests a call update
                }
                Call.State.Released -> {
                    // Call state will be released shortly after the End state
//                    findViewById<EditText>(org.linphone.core.R.id.remote_address).isEnabled = true
//                    findViewById<Button>(org.linphone.core.R.id.call).isEnabled = true
//                    findViewById<Button>(org.linphone.core.R.id.pause).isEnabled = false
//                    findViewById<Button>(org.linphone.core.R.id.pause).text = "Pause"
//                    findViewById<Button>(org.linphone.core.R.id.toggle_video).isEnabled = false
//                    findViewById<Button>(org.linphone.core.R.id.hang_up).isEnabled = false
//                    findViewById<Button>(org.linphone.core.R.id.toggle_camera).isEnabled = false
                }
                Call.State.Error -> {

                }
                Call.State.IncomingReceived -> {
                    Log.i("SipTest", "Call message" + "IncomingReceived")
//                    findViewById<Button>(org.linphone.core.R.id.hang_up).isEnabled = true
//                    findViewById<Button>(org.linphone.core.R.id.answer).isEnabled = true
//                    findViewById<EditText>(org.linphone.core.R.id.remote_address).setText(call.remoteAddress.asStringUriOnly())
                }
                Call.State.Connected -> {
                    Log.i("SipTest", "Call message" + "Connected")
//                    findViewById<Button>(org.linphone.core.R.id.mute_mic).isEnabled = true
//                    findViewById<Button>(org.linphone.core.R.id.toggle_speaker).isEnabled = true
                }
                Call.State.Released -> {
                    Log.i("SipTest", "Call message" + "Released")
//                    findViewById<Button>(org.linphone.core.R.id.hang_up).isEnabled = false
//                    findViewById<Button>(org.linphone.core.R.id.answer).isEnabled = false
//                    findViewById<Button>(org.linphone.core.R.id.mute_mic).isEnabled = false
//                    findViewById<Button>(org.linphone.core.R.id.toggle_speaker).isEnabled = false
//                    findViewById<EditText>(org.linphone.core.R.id.remote_address).text.clear()
                }
                else -> {
                    Log.i("SipTest", "Call message" + "messagefffff")
                }
            }
        }
    }
//    private val coreListener = object : CoreListenerStub() {
//        override fun onAccountRegistrationStateChanged(
//            core: Core,
//            account: Account,
//            state: RegistrationState?,
//            message: String
//        ) {
//            if (account == accountToCheck) {
//                org.linphone.core.tools.Log.i("[Assistant] [Generic Login] Registration state is $state: $message")
//                if (state == RegistrationState.Ok) {
//                    waitForServerAnswer.value = false
//                    leaveAssistantEvent.value = Event(true)
//                    core.removeListener(this)
//                } else if (state == RegistrationState.Failed) {
//                    waitForServerAnswer.value = false
//                    invalidCredentialsEvent.value = Event(true)
//                    core.removeListener(this)
//                }
//            }
//        }
//    }

    private fun outgoingCall(remoteid:String) {
        // As for everything we need to get the SIP URI of the remote and convert it to an Address
        val remoteSipUri = "sip:"+remoteid+"@dzcl.et.lk"
        val remoteAddress = Factory.instance().createAddress(remoteSipUri)
        remoteAddress?.displayName= "duzzcalls"
        remoteAddress
            ?: return // If address parsing fails, we can't continue with outgoing call process

        // We also need a CallParams object
        // Create call params expects a Call object for incoming calls, but for outgoing we must use null safely
        val params = core.createCallParams(null)
        params ?: return // Same for params

        // We can now configure it
        // Here we ask for no encryption but we could ask for ZRTP/SRTP/DTLS
        params.mediaEncryption = MediaEncryption.None
        // If we wanted to start the call with video directly
        //params.enableVideo(true)

        // Finally we start the call
//        core.inviteAddressWithParams(remoteAddress, params)
//        coreContext.startCall(remoteAddress)
        core.inviteAddressWithParams(remoteAddress, params)
//        val intentAction = Intent(this, OutgoingActivity::class.java)
//        startActivity(intentAction)
        // Call process can be followed in onCallStateChanged callback from core listener
    }

    override fun onCallBack(remoteid:String) {
        outgoingCall(remoteid);

    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        when {
            result.anyPermanentlyDenied() -> showPermanentlyDeniedDialog(result)
            result.anyShouldShowRationale() -> showRationaleDialog(result, request)
            result.allGranted() -> showGrantedToast(result)
        }
    }


}