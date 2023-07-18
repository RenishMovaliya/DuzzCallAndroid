package com.logycraft.duzzcalll.Activity

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.duzzcall.duzzcall.databinding.ActivityCallBinding
import com.logycraft.duzzcalll.LinphoneManager
import com.logycraft.duzzcalll.LinphonePreferences
import com.logycraft.duzzcalll.Util.CallActivityInterface
import com.logycraft.duzzcalll.Util.LinphoneUtils
import com.logycraft.duzzcalll.service.LinphoneService
import org.linphone.core.*
import org.linphone.core.tools.Log
import java.util.*

class CallActivity : AppCompatActivity(), CallActivityInterface {
    lateinit var mCore: Core
    private lateinit var binding: ActivityCallBinding
    private var mListener: CoreListener? = null
    private var mIsSpeakerEnabled = false
    private var seconds = 0
    private var running = false
    private var wasRunning = false
    private var mIsMicMuted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addCoreListener();
        binding.imageViewSpeakerphone.setOnClickListener(View.OnClickListener {
            val z2: Boolean = !mIsSpeakerEnabled
            mIsSpeakerEnabled = z2
            binding.imageViewSpeakerphone.setSelected(z2)
//            if (LinphoneManager.g.isReady()) {
            val core = LinphoneManager.getCore()
            val currentAudioDevice = core.currentCall?.outputAudioDevice
            val speakerEnabled = currentAudioDevice?.type == AudioDevice.Type.Speaker
            for (audioDevice in core.audioDevices) {
                if (speakerEnabled && audioDevice.type == AudioDevice.Type.Earpiece) {
                    core.currentCall?.outputAudioDevice = audioDevice
                    break
                } else if (!speakerEnabled && audioDevice.type == AudioDevice.Type.Speaker) {
                    core.currentCall?.outputAudioDevice = audioDevice
                    break
                }
            }
//            }
        })
        binding.imageViewAudioOff.setOnClickListener(View.OnClickListener {
//            if (CoreManager.isReady()) {
            val core = LinphoneManager.getCore()
            val z: Boolean = !mIsMicMuted
            mIsMicMuted = z
            binding.imageViewAudioOff.setSelected(z)
            core.enableMic(!mIsMicMuted)
//            }
        })

        val core = LinphoneManager.getCore()
        mCore = core
        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds")
            running = savedInstanceState.getBoolean("running")
            wasRunning = savedInstanceState.getBoolean("wasRunning")
        }

        running = true
        val handler = Handler()
        handler.post(object : Runnable {
            override fun run() {
//                val hours = seconds / 3600
                val minutes = seconds % 3600 / 60
                val secs = seconds % 60
                val time = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",

                    minutes,
                    secs
                )
                binding.txtTime.setText(time)
                if (running) {
                    seconds++
                }
                handler.postDelayed(this, 1000)
            }
        })

    }

    fun acceptCallUpdate() {
//        val countDownTimer: CountDownTimer = mCallUpdateCountDownTimer
//        if (countDownTimer != null) {
//            countDownTimer.cancel()
//        }
        LinphoneManager.getCallManager().acceptCallUpdate(false)


    }

    override fun refreshInCallActions() {
        updateButtons()
    }


    private fun addCoreListener() {
        org.linphone.core.tools.Log.i("[outgoingCall] Trying to add the Service's CoreListener to the Core...")
//        if (CoreManager.isReady()) {
        val core = LinphoneManager.getCore()
        binding.imageViewDecline.setOnClickListener(View.OnClickListener {
            hangUp()
        })
        if (core != null) {
            org.linphone.core.tools.Log.i("[outgoingCall] Core Manager found, adding our listener")
            core.addListener(coreListener)

            org.linphone.core.tools.Log.i("[outgoingCall] CoreListener succesfully added to the Core")
            if (core.callsNb > 0) {
                org.linphone.core.tools.Log.w("[outgoingCall] Core listener added while at least one call active !")
//                    startForeground()
                val call = core.currentCall
                if (call != null) {
                    org.linphone.core.tools.Log.w("[outgoingCall] Call Nulled !")
                    binding.textViewUserName.setText(call.remoteAddress.username)
                    binding.textViewUserSipaddress.setText("Outgoing Call")
                    // Starting Android 10 foreground service is a requirement to be able to vibrate if app is in background
                    if (call.dir == Call.Dir.Incoming && call.state == Call.State.IncomingReceived && core.isVibrationOnIncomingCallEnabled) {
//                            vibrate(call.remoteAddress)
                        binding.imageViewAccept.setOnClickListener(View.OnClickListener {
                            call.accept()
                        })
                        binding.imageViewEnd.setOnClickListener(View.OnClickListener {
                            call.terminate()
                        })
                    } else if (call.state == Call.State.Connected) {

                    }
                } else {
                    org.linphone.core.tools.Log.w("[outgoingCall] Couldn't find current call...")
                }
            }
        } else {
            org.linphone.core.tools.Log.e("[outgoingCall] CoreManager instance found but Core is null!")
        }
//        } else {
//            org.linphone.core.tools.Log.w("[outgoingCall] CoreManager isn't ready yet...")
//        }
    }

    private val coreListener = object : CoreListenerStub() {
        override fun onAccountRegistrationStateChanged(
            core: Core,
            account: Account,
            state: RegistrationState?,
            message: String
        ) {
            android.util.Log.d("outgoingCall", "Registration Failed")

        }

        override fun onAudioDeviceChanged(core: Core, audioDevice: AudioDevice) {
        }

        override fun onAudioDevicesListUpdated(core: Core) {
        }

        override fun onCallStateChanged(
            core: Core,
            call: Call,
            state: Call.State?,
            message: String
        ) {
//            binding.textViewUserSipaddress.text = message

            // When a call is received
            when (state) {
                Call.State.IncomingReceived -> {

                    binding.textViewUserSipaddress.setText(call.remoteAddress.asStringUriOnly())
                }

                Call.State.Connected -> {
                    android.util.Log.d("outgoingCall", "Connectedsss")
                    binding.textViewRinging.setText("Connected")
                    binding.textViewUserName.text = call.remoteAddress.username
                    binding.activeCallTimer.visibility = View.VISIBLE
//                    val timer = binding.activeCallTimer
//                    timer.base =
//                        SystemClock.elapsedRealtime() - (1000 * call.duration) // Linphone timestamps are in seconds
                    binding.activeCallTimer.start()


                }

                Call.State.Released -> {
                    android.util.Log.d("outgoingCall", "Released")
                    finish();
                }

                Call.State.OutgoingRinging -> {
                    android.util.Log.d("outgoingCall", "outgoig ringing")
                    binding.textViewRinging.setText("Connecting..")
//                    finish();
                }

                Call.State.OutgoingEarlyMedia -> {
                    android.util.Log.d("outgoingCall", "outgoig ringing")
                    binding.textViewRinging.setText("Ringing")

                }

                else -> {
                    android.util.Log.d("outgoingCall", "outgoig " + state)
                }
            }
        }
    }

    private fun updateButtons() {


    }

    private fun hangUp() {
//        if (CoreManager.isReady()) {
        val core = LinphoneManager.getCore()
        if (core.callsNb == 0) return

        // If the call state isn't paused, we can get it using core.currentCall
        val call = if (core.currentCall != null) core.currentCall else core.calls[0]
        call ?: return

        // Terminating a call is quite simple
        call.terminate()
//        }
    }

    override fun resetCallControlsHidingTimer() {
//        LinphoneUtils.removeFromUIThreadDispatcher(mHideControlsRunnable)
//        LinphoneUtils.dispatchOnUIThreadAfter(mHideControlsRunnable, 4000L)
    }


    override fun onStart() {
        super.onStart()
//        checkAndRequestCallPermissions()
        val core: Core? = LinphoneManager.getCore()
        mCore = core!!
        if (core != null) {
//            core.setNativeVideoWindowId(this.mRemoteVideo)
//            mCore.nativePreviewWindowId = this.mLocalPreview
            mCore.addListener(mListener)
        }
    }

    override fun onResume() {
        super.onResume()
//        mAudioManager = LinphoneManager.getAudioManager()
        updateButtons()

//        ContactsManager.getInstance().addContactsListener(this)
        LinphoneManager.getCallManager().setCallInterface(this)
        if (mCore.callsNb === 0) {
            Log.w("[Call Activity] Resuming but no call found...")
            finish()
        }
        if (LinphoneService.isReady()) {
            LinphoneService.instance().destroyOverlay()
        }
    }

    override fun onPause() {
//        ContactsManager.getInstance().removeContactsListener(this)
        LinphoneManager.getCallManager().setCallInterface(null)
        val core = LinphoneManager.getCore()
        if (LinphonePreferences.instance()
                .isOverlayEnabled() && core != null && core.currentCall != null
        ) {
            val call = core.currentCall
            if (call!!.state === Call.State.StreamsRunning && LinphoneService.isReady()) {
                LinphoneService.instance().createOverlay()
            }
        }
        super.onPause()
    }

    override fun onDestroy() {


        super.onDestroy()
    }

    fun setCurrentCallContactInformation() {
        updateCurrentCallTimer()
        val call: Call = mCore.getCurrentCall() ?: return
//        val contact: LinphoneContact =
//            ContactsManager.getInstance().findContactFromAddress(call.getRemoteAddress())
//        if (contact != null) {
//            ContactAvatar.displayAvatar(contact, this.mContactAvatar as View?, true)
//            mContactName.setText(contact.getFullName())
//            return
//        }
        val displayName: String = LinphoneUtils.getAddressDisplayName(call.getRemoteAddress())
//        ContactAvatar.displayAvatar(displayName, mContactAvatar as View?, true)
        binding.textViewUserName.setText(displayName)
    }

    private fun updateCurrentCallTimer() {
        val call = mCore.currentCall ?: return
        binding.activeCallTimer.setBase(SystemClock.elapsedRealtime() - call.duration * 1000)
        binding.activeCallTimer.start()
    }
}