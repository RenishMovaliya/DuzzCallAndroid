package com.logycraft.duzzcalll.Activity

import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.adwardstark.mtextdrawable.MaterialTextDrawable
import com.duzzcall.duzzcall.R
import com.duzzcall.duzzcall.databinding.ActivityOutgoingCallBinding
import com.logycraft.duzzcalll.LinphoneManager
import com.logycraft.duzzcalll.Util.Preference
import org.linphone.core.Account
import org.linphone.core.AudioDevice
import org.linphone.core.Call
import org.linphone.core.Core
import org.linphone.core.CoreListenerStub
import org.linphone.core.Factory
import org.linphone.core.RegistrationState

//import org.linphone.core.tools.service.CoreManager

class OutgoingActivity : AppCompatActivity() {
    private var mIsSpeakerEnabled = false
    private var mIsMicMuted = false
    private lateinit var binding: ActivityOutgoingCallBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOutgoingCallBinding.inflate(layoutInflater)
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }

        setContentView(binding.root)
        val factory = Factory.instance()
        factory.setDebugMode(true, "Hello Linphone")
        binding.imageViewDecline.visibility = View.VISIBLE
        binding.imageViewAccept.visibility = View.GONE
        binding.txtAnswer.visibility = View.GONE
        binding.txtDecline.visibility = View.GONE
        binding.imageViewEnd.visibility = View.GONE
        binding.imageViewDialpad.isEnabled=false
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

    }

    private val coreListener = object : CoreListenerStub() {
        override fun onAccountRegistrationStateChanged(
            core: Core,
            account: Account,
            state: RegistrationState?,
            message: String
        ) {
            Log.d("outgoingCall", "Registration Failed")

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

//                    binding.textViewUserSipaddress.setText(call.remoteAddress.asStringUriOnly())
                    binding.textViewUserSipaddress.setText(Preference.getCountry(this@OutgoingActivity))
                    Log.d("outgoingCall", "===="+call.remoteAddress.asStringUriOnly())
                }

                Call.State.Connected -> {
                    Log.d("outgoingCall", "Connectedsss")
                    binding.textViewRinging.setText("Connected")
                    binding.activeCallTimer.visibility = View.VISIBLE
                    binding.textViewUserName.text = call.remoteAddress.displayName
                    val timer = binding.activeCallTimer
                    timer.base =
                        SystemClock.elapsedRealtime() - (1000 * call.duration) // Linphone timestamps are in seconds
                    timer.start()
                    finish()
                    MaterialTextDrawable.with(this@OutgoingActivity)
                        .text(call.remoteAddress.username?.substring(0,2) ?: "DC")
                        .into(binding.imageViewProfile)
                }

                Call.State.Released -> {
                    Log.d("outgoingCall", "Released")
                    finish();
                }
                Call.State.End -> {
                    Log.d("outgoingCall", "End")
                    finish();
                }
                Call.State.Error -> {
                    Log.d("outgoingCall", "Error")
                    finish();
                }

                Call.State.OutgoingRinging -> {
                    Log.d("outgoingCall", "outgoig ringing")
                    binding.textViewRinging.setText("Connecting..")
                    MaterialTextDrawable.with(this@OutgoingActivity)
                        .text(call.remoteAddress.username?.substring(0,2) ?: "DC")
                        .into(binding.imageViewProfile)
//                    finish();
                }

                Call.State.OutgoingEarlyMedia -> {
                    Log.d("outgoingCall", "outgoig ringing")
                    binding.textViewRinging.setText("Ringing")
                    MaterialTextDrawable.with(this@OutgoingActivity)
                        .text(call.remoteAddress.username?.substring(0,2) ?: "DC")
                        .into(binding.imageViewProfile)

                }

//                else if (call.state == Call.State.Released ||call.state == Call.State.End||call.state == Call.State.Error) {
//                    finish()
//                }

                else -> {
                    Log.d("outgoingCall", "outgoig " + state)
                }
            }
        }
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
                    binding.textViewUserName.setText(call.remoteAddress.displayName)
//                    binding.textViewUserSipaddress.setText("Outgoing Call")
                    binding.textViewUserSipaddress.setText(Preference.getCountry(this@OutgoingActivity))
                    MaterialTextDrawable.with(this@OutgoingActivity)
                        .text(call.remoteAddress.username?.substring(0,2) ?: "DC")
                        .into(binding.imageViewProfile)
                    // Starting Android 10 foreground service is a requirement to be able to vibrate if app is in background
                    if (call.dir == Call.Dir.Incoming && call.state == Call.State.IncomingReceived && core.isVibrationOnIncomingCallEnabled) {
//                            vibrate(call.remoteAddress)
                        binding.imageViewAccept.setOnClickListener(View.OnClickListener {
                            call.accept()
                        })
                        binding.imageViewEnd.setOnClickListener(View.OnClickListener {
                            call.terminate()
                        })
                    } else if (call.state == Call.State.Released ||call.state == Call.State.End||call.state == Call.State.Error) {
                        finish()
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
}