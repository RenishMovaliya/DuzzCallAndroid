package com.logycraft.duzzcalll.Activity

import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.duzzcall.duzzcall.R
import com.duzzcall.duzzcall.databinding.ActivityIncomingCallBinding
import com.logycraft.duzzcalll.LinphoneManager
import com.logycraft.duzzcalll.Util.Preference
import com.logycraft.duzzcalll.Util.ProgressHelper
import com.logycraft.duzzcalll.data.BusinessResponce
import com.logycraft.duzzcalll.viewmodel.HomeViewModel
import okhttp3.ResponseBody
import org.json.JSONObject
import org.linphone.core.*


//import org.linphone.core.tools.service.CoreManager

class IncomingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIncomingCallBinding
    private var mIsSpeakerEnabled = false
    private var mIsMicMuted = false
    private lateinit var countDownTimer: CountDownTimer
    var businessresponce: ArrayList<BusinessResponce> = ArrayList()
    private lateinit var viewModel: HomeViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }

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
        if (intent.extras != null) {
            if (intent.extras?.get("action") != null) {
                if (intent.extras?.get("action")!!.equals("Acceptclick")) {
                    binding.imageViewAccept.performClick()
                } else if (intent.extras?.get("action")!!.equals("Declineclick")) {
                    binding.imageViewDecline.performClick()
                }
            }
        }
    }

    private val coreListener = object : CoreListenerStub() {
        override fun onAccountRegistrationStateChanged(
            core: Core, account: Account, state: RegistrationState?, message: String
        ) {
            Log.d("IncomingCall", "Registration Failed")
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
            core: Core, call: Call, state: Call.State?, message: String
        ) {

            when (state) {
                Call.State.IncomingReceived -> {
                    Log.d("IncomingCall", "IncomingReceived")
                    getbusinessList(call)
                    binding.textViewUserName.setText(call.remoteAddress.username)
                    binding.tvDisplayName.setText(call.remoteAddress.displayName)
                    Log.e(
                        "usernameeeeeeee",
                        "" + call.remoteAddress.username + "------" + call.remoteAddress.displayName
                    )

                    binding.textViewUserSipaddress.setText(Preference.getCountry(this@IncomingActivity))

                }
                Call.State.Connected -> {
                    Log.d("IncomingCall", "Connectedsss")
                    binding.imageViewDecline.setOnClickListener(View.OnClickListener {
                        call.terminate()
                    })
                    binding.imageViewSpeakerphone.setOnClickListener(View.OnClickListener {
                        val z2: Boolean = !mIsSpeakerEnabled
                        mIsSpeakerEnabled = z2
                        binding.imageViewSpeakerphone.setSelected(z2)

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

                    })
                    binding.imageViewAudioOff.setOnClickListener(View.OnClickListener {
                        val core = LinphoneManager.getCore()
                        val z: Boolean = !mIsMicMuted
                        mIsMicMuted = z
                        binding.imageViewAudioOff.setSelected(z)
                        core.enableMic(!mIsMicMuted)

                    })
                    binding.activeCallTimer.visibility = View.VISIBLE
                    val timer = binding.activeCallTimer
                    timer.base =
                        SystemClock.elapsedRealtime() - (1000 * call.duration) // Linphone timestamps are in seconds
                    Log.e("durationnnnnn", "" + call.duration)

                    timer.start()
                    binding.textViewUserName.text = call.remoteAddress.displayName
                    binding.textViewUserSipaddress.text = "Connected"

                }
                Call.State.Released -> {
                    Log.d("IncomingCall", "Released")
                    binding.textViewUserSipaddress.text = "Released"
                    finish();

                }

                else -> {

                }
            }
        }
    }

    private fun getbusinessList(call: Call) {
        viewModel.getBusiness(this@IncomingActivity)

        viewModel.getbusinessLiveData?.observe(this@IncomingActivity, androidx.lifecycle.Observer {

            if (it.isSuccess == true && it.Responcecode == 200) {
                ProgressHelper.dismissProgressDialog()

                it.data?.let { it1 -> businessresponce.addAll(it1) }
                var paraam = "";

                if (businessresponce != null) {
                    for (item1 in businessresponce) {
                        if (item1.lineExtension.equals(call.remoteAddress.username)) {

                            Log.e("errorrellll",""+ item1.businessName.toString())
                            paraam = item1.businessLogo.toString();
                            break
                        }
                    }
                    if (paraam == null || paraam.equals("")) {
                        val split: Array<String> =
                            binding.tvDisplayName.text.toString().split(" ").toTypedArray()
                        val firstword = split[0]
                        val secondword = split[1]
                        val firstLetter = firstword[0]
                        val second = secondword[0]
                        val textss = "" + firstLetter + second
                        val bitmap = Preference.textToBitmap(textss, Color.parseColor("#2F80ED"))
                        binding.imageViewProfileDirectCall.visibility = View.VISIBLE
                        binding.card.visibility = View.GONE
                        binding.imageViewProfileDirectCall.setImageBitmap(bitmap)
                    } else {
                        binding.imageViewProfileDirectCall.visibility = View.GONE
                        binding.card.visibility = View.VISIBLE
                        Glide.with(this@IncomingActivity).load(paraam)
                            .into(binding.imageViewProfile)
                    }

                } else {
                    val split: Array<String> =
                        binding.tvDisplayName.text.toString().split(" ").toTypedArray()
                    val firstword = split[0]
                    val secondword = split[1]
                    val firstLetter = firstword[0]
                    val second = secondword[0]
                    val textss = "" + firstLetter + second
                    val bitmap = Preference.textToBitmap(textss, Color.parseColor("#2F80ED"))
                    binding.imageViewProfileDirectCall.visibility = View.VISIBLE
                    binding.card.visibility = View.GONE
                    binding.imageViewProfileDirectCall.setImageBitmap(bitmap)
                }


            } else if (it.error != null) {
                ProgressHelper.dismissProgressDialog()
                var errorResponce: ResponseBody = it.error
                val jsonObj = JSONObject(errorResponce!!.charStream().readText())
                showMessage(jsonObj.getString("errors"))
            } else {
                ProgressHelper.dismissProgressDialog()
                showMessage("Something Went Wrong!")
            }

        })

    }

    fun showMessage(message: String?) {
        Toast.makeText(this, "$message", Toast.LENGTH_LONG).show()
    }


    private fun addCoreListener() {
        org.linphone.core.tools.Log.i("[Incoming Call] Trying to add the Service's CoreListener to the Core...")
//        if (CoreManager.isReady()) {
        val core = LinphoneManager.getCore()
        if (core != null) {
            org.linphone.core.tools.Log.i("[Incoming Call] Core Manager found, adding our listener")
            core.addListener(coreListener)

            org.linphone.core.tools.Log.i("[Incoming Call] CoreListener succesfully added to the Core")
            if (core.callsNb > 0) {
                org.linphone.core.tools.Log.w("[Incoming Call] Core listener added while at least one call active !")
                val call = core.currentCall
                if (call != null) {
                    org.linphone.core.tools.Log.w("[Incoming Call] Call Nulled !")
                    getbusinessList(call)

                    binding.textViewUserName.setText(call.remoteAddress.username)
                    Log.e(
                        "usernameeeeeeee",
                        "" + call.remoteAddress.username + "------" + call.remoteAddress.displayName
                    )
                    binding.tvDisplayName.setText(call.remoteAddress.displayName)

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
                    } else if (call.state == Call.State.Connected) {

                    }
                } else {
                    org.linphone.core.tools.Log.w("[Incoming Call] Couldn't find current call...")
                }
            }
        } else {
            org.linphone.core.tools.Log.e("[Incoming Call] CoreManager instance found but Core is null!")
        }
//        } else {
//            org.linphone.core.tools.Log.w("[Incoming Call] CoreManager isn't ready yet...")
//        }
    }

    fun getFirstTwoCharacters(input: String): String {
        if (input.length >= 2) {
            return input.substring(0, 2)
        } else {
            return input
        }
    }
}