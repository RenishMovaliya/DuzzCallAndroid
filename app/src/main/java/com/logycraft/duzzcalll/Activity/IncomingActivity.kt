package com.logycraft.duzzcalll.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.logycraft.duzzcalll.R
import com.logycraft.duzzcalll.databinding.ActivityEditPhoneBinding
import com.logycraft.duzzcalll.databinding.ActivityIncomingCallBinding

import org.linphone.core.Account
import org.linphone.core.AudioDevice
import org.linphone.core.Call
import org.linphone.core.Core
import org.linphone.core.CoreListenerStub
import org.linphone.core.Factory
import org.linphone.core.RegistrationState
import org.linphone.core.tools.service.CoreManager

class IncomingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIncomingCallBinding
    private var mIsSpeakerEnabled = false
    private var mIsMicMuted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_incoming_call)
        binding = ActivityIncomingCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val factory = Factory.instance()
        factory.setDebugMode(true, "Hello Linphone")
//         core = CoreManager.instance().core
        addCoreListener();
//        if (core != null) {
//            org.linphone.core.tools.Log.i("[Incoming Call] Core Manager found, removing our listener")
//            core.removeListener(mListener)
//        }
//        core.addListener(coreListener)
//        core.start()
        if (intent.extras!=null){
            if (intent.extras?.get("action")!=null){
                if (intent.extras?.get("action")!!.equals("Acceptclick")){
                    binding.imageViewAccept.performClick()
                }else if(intent.extras?.get("action")!!.equals("Declineclick")) {
                    binding.imageViewDecline.performClick()
                }
            }

        }




    }

    private val coreListener = object: CoreListenerStub() {
        override fun onAccountRegistrationStateChanged(core: Core, account: Account, state: RegistrationState?, message: String) {
            Log.d("IncomingCall","Registration Failed")
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
                    Log.d("IncomingCall","IncomingReceived")
//                    findViewById<Button>(org.linphone.core.R.id.hang_up).isEnabled = true
//                    findViewById<Button>(org.linphone.core.R.id.answer).isEnabled = true
                    binding.textViewUserName.setText(call.remoteAddress.asStringUriOnly())
                    binding.textViewUserSipaddress.setText("Incoming Call")
                }
                Call.State.Connected -> {
                    Log.d("IncomingCall","Connectedsss")
                    binding.imageViewAccept.visibility= View.GONE
                    binding.imageViewEnd.visibility= View.GONE
                    binding.imageViewDecline.visibility= View.VISIBLE
                    binding.optionview.visibility= View.VISIBLE
                    binding.imageViewDecline.setOnClickListener(View.OnClickListener {
                        call.terminate()
                    })
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

                    val timer = binding.activeCallTimer
                    timer.base =
                        SystemClock.elapsedRealtime() - (1000 * call.duration) // Linphone timestamps are in seconds
                    timer.start()
                    binding.textViewUserName.text = call.remoteAddress.displayName
                    binding.textViewUserSipaddress.text= "Connected"

//                    findViewById<Button>(org.linphone.core.R.id.mute_mic).isEnabled = true
//                    findViewById<Button>(org.linphone.core.R.id.toggle_speaker).isEnabled = true
                }
                Call.State.Released -> {
                    Log.d("IncomingCall","Released")
                    binding.textViewUserSipaddress.text= "Released"
                    finish();
//                    findViewById<Button>(org.linphone.core.R.id.hang_up).isEnabled = false
//                    findViewById<Button>(org.linphone.core.R.id.answer).isEnabled = false
//                    findViewById<Button>(org.linphone.core.R.id.mute_mic).isEnabled = false
//                    findViewById<Button>(org.linphone.core.R.id.toggle_speaker).isEnabled = false
//                    findViewById<EditText>(org.linphone.core.R.id.remote_address).text.clear()
                }

                else -> {

                }
            }
        }
    }
    private fun addCoreListener() {
        org.linphone.core.tools.Log.i("[Incoming Call] Trying to add the Service's CoreListener to the Core...")
        if (CoreManager.isReady()) {
            val core = CoreManager.instance().core
            if (core != null) {
                org.linphone.core.tools.Log.i("[Incoming Call] Core Manager found, adding our listener")
                core.addListener(coreListener)

                org.linphone.core.tools.Log.i("[Incoming Call] CoreListener succesfully added to the Core")
                if (core.callsNb > 0) {
                    org.linphone.core.tools.Log.w("[Incoming Call] Core listener added while at least one call active !")
//                    startForeground()
                    val call = core.currentCall
                    if (call != null) {
                        org.linphone.core.tools.Log.w("[Incoming Call] Call Nulled !")
//                        binding.textViewUserSipaddress.setText(call.remoteAddress.displayName)
                        binding.textViewUserName.setText(call.remoteAddress.displayName)
                        binding.textViewUserSipaddress.setText("Incoming Call")
                        // Starting Android 10 foreground service is a requirement to be able to vibrate if app is in background
                        if (call.dir == Call.Dir.Incoming && call.state == Call.State.IncomingReceived && core.isVibrationOnIncomingCallEnabled) {
//                            vibrate(call.remoteAddress)
                            binding.imageViewAccept.setOnClickListener(View.OnClickListener {
                                call.accept()
                            })
                            binding.imageViewEnd.setOnClickListener(View.OnClickListener {
                                call.terminate()
                            })
                        }else if (call.state == Call.State.Connected){

                        }
                    } else {
                        org.linphone.core.tools.Log.w("[Incoming Call] Couldn't find current call...")
                    }
                }
            } else {
                org.linphone.core.tools.Log.e("[Incoming Call] CoreManager instance found but Core is null!")
            }
        } else {
            org.linphone.core.tools.Log.w("[Incoming Call] CoreManager isn't ready yet...")
        }
    }
}