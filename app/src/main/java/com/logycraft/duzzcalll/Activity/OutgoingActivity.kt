package com.logycraft.duzzcalll.Activity

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.logycraft.duzzcalll.databinding.ActivityOutgoingCallBinding
import org.linphone.core.Account
import org.linphone.core.AudioDevice
import org.linphone.core.Call
import org.linphone.core.Core
import org.linphone.core.CoreListenerStub
import org.linphone.core.Factory
import org.linphone.core.RegistrationState
import org.linphone.core.tools.service.CoreManager

class OutgoingActivity : AppCompatActivity() {
    private var mIsSpeakerEnabled = false
    private var mIsMicMuted = false
    private lateinit var binding: ActivityOutgoingCallBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutgoingCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val factory = Factory.instance()
        factory.setDebugMode(true, "Hello Linphone")
        binding.imageViewDecline.visibility = View.VISIBLE
        binding.imageViewAccept.visibility = View.GONE
        binding.imageViewEnd.visibility = View.GONE
        addCoreListener();
        binding.imageViewSpeakerphone.setOnClickListener(View.OnClickListener {
            val z2: Boolean = !mIsSpeakerEnabled
            mIsSpeakerEnabled = z2
            binding.imageViewSpeakerphone.setSelected(z2)
            if (CoreManager.isReady()) {
                val core = CoreManager.instance().core
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
            }
        })
        binding.imageViewAudioOff.setOnClickListener(View.OnClickListener {
            if (CoreManager.isReady()) {
                val core = CoreManager.instance().core
                val z: Boolean = !mIsMicMuted
                mIsMicMuted = z
                binding.imageViewAudioOff.setSelected(z)
                core.enableMic(!mIsMicMuted)
            }
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
//            findViewById<TextView>(org.linphone.core.R.id.registration_status).text = message
//
//            if (state == RegistrationState.Failed) {
//                findViewById<Button>(org.linphone.core.R.id.connect).isEnabled = true
//            } else if (state == RegistrationState.Ok) {
//                findViewById<LinearLayout>(org.linphone.core.R.id.register_layout).visibility = View.GONE
//                findViewById<RelativeLayout>(org.linphone.core.R.id.call_layout).visibility = View.VISIBLE
//            }
        }

        override fun onAudioDeviceChanged(core: Core, audioDevice: AudioDevice) {
            // This callback will be triggered when a successful audio device has been changed
        }

        override fun onAudioDevicesListUpdated(core: Core) {
            // This callback will be triggered when the available devices list has changed,
            // for example after a bluetooth headset has been connected/disconnected.
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
                    Log.d("outgoingCall", "IncomingReceived")
//                    findViewById<Button>(org.linphone.core.R.id.hang_up).isEnabled = true
//                    findViewById<Button>(org.linphone.core.R.id.answer).isEnabled = true
                    binding.textViewUserSipaddress.setText(call.remoteAddress.asStringUriOnly())
                }

                Call.State.Connected -> {
                    Log.d("outgoingCall", "Connectedsss")
//                    binding.imageViewAccept.visibility= View.GONE
//                    binding.imageViewEnd.visibility= View.GONE

//                    binding.imageViewDecline.setOnClickListener(View.OnClickListener {
//                        call.terminate()
//                    })
                    binding.textViewRinging.setText("Connected")
                    binding.textViewUserName.text = call.remoteAddress.username
                    val timer = binding.activeCallTimer
                    timer.base =
                        SystemClock.elapsedRealtime() - (1000 * call.duration) // Linphone timestamps are in seconds
                    timer.start()

//                    findViewById<Button>(org.linphone.core.R.id.mute_mic).isEnabled = true
//                    findViewById<Button>(org.linphone.core.R.id.toggle_speaker).isEnabled = true
                }

                Call.State.Released -> {
                    Log.d("outgoingCall", "Released")
                    finish();
                }

                Call.State.OutgoingRinging -> {
                    Log.d("outgoingCall", "outgoig ringing")
                    binding.textViewRinging.setText("connecting..")
//                    finish();
                }

                Call.State.OutgoingEarlyMedia -> {
                    Log.d("outgoingCall", "outgoig ringing")
                    binding.textViewRinging.setText("Ringing")

                }

                else -> {
                    Log.d("outgoingCall", "outgoig " + state)
                }
            }
        }
    }

    private fun hangUp() {
        if (CoreManager.isReady()) {
            val core = CoreManager.instance().core
            if (core.callsNb == 0) return

            // If the call state isn't paused, we can get it using core.currentCall
            val call = if (core.currentCall != null) core.currentCall else core.calls[0]
            call ?: return

            // Terminating a call is quite simple
            call.terminate()
        }
    }

    private fun addCoreListener() {
        org.linphone.core.tools.Log.i("[outgoingCall] Trying to add the Service's CoreListener to the Core...")
        if (CoreManager.isReady()) {
            val core = CoreManager.instance().core
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
        } else {
            org.linphone.core.tools.Log.w("[outgoingCall] CoreManager isn't ready yet...")
        }
    }
}