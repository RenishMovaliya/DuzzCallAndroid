package com.logycraft.duzzcalll.Activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.duzzcall.duzzcall.R
import com.duzzcall.duzzcall.databinding.ActivityOutgoingCallBinding
import com.logycraft.duzzcalll.LinphoneManager
import com.logycraft.duzzcalll.Util.Preference
import com.logycraft.duzzcalll.Util.ProgressHelper
import com.logycraft.duzzcalll.data.BusinessResponce
import com.logycraft.duzzcalll.viewmodel.HomeViewModel
import okhttp3.ResponseBody
import org.json.JSONObject
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
    var businessresponce: ArrayList<BusinessResponce> = ArrayList()
    private lateinit var viewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOutgoingCallBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

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
        binding.imageViewDialpad.isEnabled = false
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
                    Log.d("outgoingCall", "====" + call.remoteAddress.asStringUriOnly())
                }

                Call.State.Connected -> {
                    Log.d("outgoingCall", "Connectedsss")
                    binding.textViewRinging.setText("Connected")
                    binding.activeCallTimer.visibility = View.VISIBLE

                    if (!getFirstTwoCharacters(call.remoteAddress.displayName.toString()).equals("00")) {
                        binding.textViewUserName.setText(call.remoteAddress.displayName)
                    } else {
                        binding.textViewUserName.setText("Direct Call")

                    }
                    binding.textViewNumber.setText(call.remoteAddress.username)
                    getbusinessList(call)
//                    if (call.remoteAddress.methodParam.equals(" ") ||call.remoteAddress.methodParam=="null") {
//
//                        Log.e("nameeeeee", "out0" + binding.textViewUserName.text.toString())
//
//                        val split: Array<String> =
//                            binding.textViewUserName.text.toString().split(" ").toTypedArray()
//                        val firstword = split[0]
//                        val secondword = split[1]
//                        val firstLetter = firstword[0]
//                        val second = secondword[0]
//                        val textss = "" + firstLetter + second
//                        val bitmap = Preference.textToBitmap(textss, Color.parseColor("#2F80ED"))
//                        binding.imageViewProfileDirectCall.visibility = View.VISIBLE
//                        binding.card.visibility = View.GONE
//                        binding.imageViewProfileDirectCall.setImageBitmap(bitmap)
////                        MaterialTextDrawable.with(this@OutgoingActivity)
////                            .text(call.remoteAddress.username?.substring(0,2) ?: "DC")
////                            .into(binding.imageViewProfile)
//                    } else {
//                        binding.imageViewProfileDirectCall.visibility = View.GONE
//                        binding.card.visibility = View.VISIBLE
//                        Glide.with(this@OutgoingActivity).load(call.remoteAddress.methodParam)
//                            .into(binding.imageViewProfile)
//                    }
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
                    getbusinessList(call)

//                    if (call.remoteAddress.methodParam.equals(" ") ||call.remoteAddress.methodParam=="null" ) {
////                        val firstLetter =call.remoteAddress.username
////                        val textss = firstLetter?.get(0).toString()
//                        org.linphone.core.tools.Log.e(
//                            "nameeeeee",
//                            "out1" + binding.textViewUserName.text.toString()
//                        )
//
//                        val split: Array<String> =
//                            binding.textViewUserName.text.toString().split(" ").toTypedArray()
//                        val firstword = split[0]
//                        val secondword = split[1]
//                        val firstLetter = firstword[0]
//                        val second = secondword[0]
//                        val textss = "" + firstLetter + second
//                        val bitmap = Preference.textToBitmap(textss, Color.parseColor("#2F80ED"))
//                        binding.imageViewProfileDirectCall.visibility = View.VISIBLE
//                        binding.card.visibility = View.GONE
//                        binding.imageViewProfileDirectCall.setImageBitmap(bitmap)
////                        MaterialTextDrawable.with(this@OutgoingActivity)
////                            .text(call.remoteAddress.username?.substring(0,2) ?: "DC")
////                            .into(binding.imageViewProfile)
//                    } else {
//                        binding.imageViewProfileDirectCall.visibility = View.GONE
//                        binding.card.visibility = View.VISIBLE
//                        Glide.with(this@OutgoingActivity).load(call.remoteAddress.methodParam)
//                            .into(binding.imageViewProfile)
//                    }
//                    finish();
                }

                Call.State.OutgoingEarlyMedia -> {
                    Log.d("outgoingCall", "outgoig ringing")
                    binding.textViewRinging.setText("Ringing")
                    getbusinessList(call)

//                    if (call.remoteAddress.methodParam.equals(" ") ||call.remoteAddress.methodParam=="null") {
//                        org.linphone.core.tools.Log.e(
//                            "nameeeeee",
//                            "out2" + binding.textViewUserName.text.toString()
//                        )
//
//                        val split: Array<String> =
//                            binding.textViewUserName.text.toString().split(" ").toTypedArray()
//                        val firstword = split[0]
//                        val secondword = split[1]
//                        val firstLetter = firstword[0]
//                        val second = secondword[0]
//                        val textss = "" + firstLetter + second
//                        binding.imageViewProfileDirectCall.visibility = View.VISIBLE
//                        binding.card.visibility = View.GONE
//                        val bitmap = Preference.textToBitmap(textss, Color.parseColor("#2F80ED"))
//                        binding.imageViewProfileDirectCall.setImageBitmap(bitmap)
//
////                        MaterialTextDrawable.with(this@OutgoingActivity)
////                            .text(call.remoteAddress.username?.substring(0,2) ?: "DC")
////                            .into(binding.imageViewProfile)
//                    } else {
//                        binding.imageViewProfileDirectCall.visibility = View.GONE
//                        binding.card.visibility = View.VISIBLE
//                        Glide.with(this@OutgoingActivity).load(call.remoteAddress.methodParam)
//                            .into(binding.imageViewProfile)
//                    }

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

    private fun getbusinessList(call: Call) {
        viewModel.getBusiness(this@OutgoingActivity)

        viewModel.getbusinessLiveData?.observe(this@OutgoingActivity, androidx.lifecycle.Observer {

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
                            binding.textViewUserName.text.toString().split(" ").toTypedArray()
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
                        Glide.with(this@OutgoingActivity).load(paraam)
                            .into(binding.imageViewProfile)
                    }

                } else {
                    val split: Array<String> =
                        binding.textViewUserName.text.toString().split(" ").toTypedArray()
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
                    if (!getFirstTwoCharacters(call.remoteAddress.displayName.toString()).equals("00")) {
                        binding.textViewUserName.setText(call.remoteAddress.displayName)
                    } else {
                        binding.textViewUserName.setText("Direct Call")

                    }
                    binding.textViewNumber.setText(call.remoteAddress.username)//                    binding.textViewUserSipaddress.setText("Outgoing Call")
                    binding.textViewUserSipaddress.setText(Preference.getCountry(this@OutgoingActivity))

                    getbusinessList(call)
//                    if (call.remoteAddress.methodParam.equals(" ") ||call.remoteAddress.methodParam=="null") {
////                        val textss = firstLetter?.get(0).toString()
//
//                        org.linphone.core.tools.Log.e(
//                            "nameeeeee",
//                            "out3" + binding.textViewUserName.text.toString()
//                        )
//
//                        val split: Array<String> =
//                            binding.textViewUserName.text.toString().split(" ").toTypedArray()
//
//                        var firstLetter = ""
//                        var second = ""
//                        var firstword = "D"
//                        var secondword = "C"
//
//                        if (split != null && split.size >= 2) {
//                            Log.e("nameeeeeeeet","namee")
//                            if (split[0] != null && split[0].isNotEmpty()) {
//                                firstword = split[0]
//                                firstLetter = firstword[0].toString()
//                            }
//
//                            if (split[1] != null && split[1].isNotEmpty()) {
//                                secondword = split[1]
//                                second = secondword[0].toString()
//                            }
//                        }else{
//                            Log.e("nameeeeeeeet","errorr")
//                            firstword = split[0]
//                            firstLetter = firstword[0].toString()
//                            second="C"
//                        }
//
//                        val textss = "" + firstLetter + second
//                        val bitmap = Preference.textToBitmap(textss, Color.parseColor("#2F80ED"))
//                        binding.imageViewProfileDirectCall.visibility = View.VISIBLE
//                        binding.card.visibility = View.GONE
//                        binding.imageViewProfileDirectCall.setImageBitmap(bitmap)
////                        MaterialTextDrawable.with(this@OutgoingActivity)
////                            .text(call.remoteAddress.username?.substring(0,2) ?: "DC")
////                            .into(binding.imageViewProfile)
//                    } else {
//                        org.linphone.core.tools.Log.e(
//                            "nameeeeee",
//                            "else_out3" + call.remoteAddress.methodParam
//                        )
//
//                        binding.imageViewProfileDirectCall.visibility = View.GONE
//                        binding.card.visibility = View.VISIBLE
//                        Glide.with(this@OutgoingActivity).load(call.remoteAddress.methodParam)
//                            .into(binding.imageViewProfile)
//                    }
                    // Starting Android 10 foreground service is a requirement to be able to vibrate if app is in background
                    if (call.dir == Call.Dir.Incoming && call.state == Call.State.IncomingReceived && core.isVibrationOnIncomingCallEnabled) {
//                            vibrate(call.remoteAddress)
                        binding.imageViewAccept.setOnClickListener(View.OnClickListener {
                            call.accept()
                        })
                        binding.imageViewEnd.setOnClickListener(View.OnClickListener {
                            call.terminate()
                        })
                    } else if (call.state == Call.State.Released || call.state == Call.State.End || call.state == Call.State.Error) {
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

    fun getFirstTwoCharacters(input: String): String {
        if (input.length >= 2) {
            return input.substring(0, 2)
        } else {
            return input
        }
    }
}