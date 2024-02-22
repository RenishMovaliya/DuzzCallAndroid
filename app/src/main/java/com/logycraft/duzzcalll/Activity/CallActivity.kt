package com.logycraft.duzzcalll.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Rect
import android.os.*
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.duzzcall.duzzcall.R
import com.duzzcall.duzzcall.databinding.ActivityCallBinding
import com.logycraft.duzzcalll.LinphoneManager
import com.logycraft.duzzcalll.LinphonePreferences
import com.logycraft.duzzcalll.TimerReceiver
import com.logycraft.duzzcalll.TimerService
import com.logycraft.duzzcalll.Util.CallActivityInterface
import com.logycraft.duzzcalll.Util.Preference
import com.logycraft.duzzcalll.Util.ProgressHelper
import com.logycraft.duzzcalll.data.BusinessResponce
import com.logycraft.duzzcalll.extention.addCharacter
import com.logycraft.duzzcalll.extention.disableKeyboard
import com.logycraft.duzzcalll.extention.getKeyEvent
import com.logycraft.duzzcalll.helper.ToneGeneratorHelper
import com.logycraft.duzzcalll.service.LinphoneService
import com.logycraft.duzzcalll.viewmodel.HomeViewModel
import okhttp3.ResponseBody
import org.json.JSONObject
import org.linphone.core.*
import org.linphone.core.tools.Log
import java.util.*
import kotlin.math.roundToInt

class CallActivity : AppCompatActivity(), CallActivityInterface {
    lateinit var mCore: Core
    private lateinit var binding: ActivityCallBinding
    private var mListener: CoreListener? = null
    private var mIsSpeakerEnabled = false
    private var seconds = 0
    private var running = false
    private var wasRunning = false
    private var mIsMicMuted = false
    private var icCallrunning = true
    private val longPressTimeout = ViewConfiguration.getLongPressTimeout().toLong()
    var businessresponce: ArrayList<BusinessResponce> = ArrayList()
    private lateinit var viewModel: HomeViewModel
    private val longPressHandler = Handler(Looper.getMainLooper())
    private val pressedKeys = mutableSetOf<Char>()
    private var toneGeneratorHelper: ToneGeneratorHelper? = null
    private var timerReceiver: TimerReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallBinding.inflate(layoutInflater)
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        timerReceiver = TimerReceiver(binding.txtTime)
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
        binding.imageViewDialpad.setOnClickListener(View.OnClickListener {
            var z = true
            val isNumpadVisible = binding.dialpadHolder.getVisibility() == View.VISIBLE
            binding.dialpadHolder.setVisibility(if (isNumpadVisible) View.GONE else View.VISIBLE)
            binding.tvHide.setVisibility(if (isNumpadVisible) View.GONE else View.VISIBLE)
//            binding.mainview.setVisibility(if (!isNumpadVisible) View.GONE else View.VISIBLE)
            if (isNumpadVisible) {
                z = false
            }

            binding.imageViewDialpad.setSelected(z)
        })
        binding.tvHide.setOnClickListener(View.OnClickListener {
            var z = true
            val isNumpadVisible = binding.dialpadHolder.getVisibility() == View.VISIBLE
            binding.dialpadHolder.setVisibility(if (isNumpadVisible) View.GONE else View.VISIBLE)
            binding.tvHide.setVisibility(if (isNumpadVisible) View.GONE else View.VISIBLE)
//            binding.mainview.setVisibility(if (!isNumpadVisible) View.GONE else View.VISIBLE)
            if (isNumpadVisible) {
                z = false
            }

            binding.imageViewDialpad.setSelected(z)
        })

        val core = LinphoneManager.getCore()
        mCore = core
        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds")
            running = savedInstanceState.getBoolean("running")
            wasRunning = savedInstanceState.getBoolean("wasRunning")
        }

        if (!Preference.getFirstHandler(this@CallActivity)) {
            Preference.setFirstHandler(this@CallActivity, true)
            Log.e("rrrrrrrrrrr","hhhhh")
//            running = true
//            val handler = Handler()
//            handler.post(object : Runnable {
//                override fun run() {
////                val hours = seconds / 3600
//                    val minutes = seconds % 3600 / 60
//                    val secs = seconds % 60
//                    val time = String.format(
//                        Locale.getDefault(),
//                        "%02d:%02d",
//
//                        minutes,
//                        secs
//                    )
//                    binding.txtTime.setText(time)
//                    if (running) {
//                        seconds++
//                    }
//                    handler.postDelayed(this, 1000)
//                }
//            })
            val serviceIntent = Intent(this, TimerService::class.java)
            startService(serviceIntent)
        }

        setupCharClick(binding.dialpad1Holder, '1')
        setupCharClick(binding.dialpad2Holder, '2')
        setupCharClick(binding.dialpad3Holder, '3')
        setupCharClick(binding.dialpad4Holder, '4')
        setupCharClick(binding.dialpad5Holder, '5')
        setupCharClick(binding.dialpad6Holder, '6')
        setupCharClick(binding.dialpad7Holder, '7')
        setupCharClick(binding.dialpad8Holder, '8')
        setupCharClick(binding.dialpad9Holder, '9')
        setupCharClick(binding.dialpad0Holder, '0')
        setupCharClick(binding.dialpadPlusHolder, '+', longClickable = false)
        setupCharClick(binding.dialpadAsteriskHolder, '*', longClickable = false)
        setupCharClick(binding.dialpadHashtagHolder, '#', longClickable = false)

        binding.dialpadClearChar.setOnClickListener { clearChar(it) }
        binding.dialpadClearChar.setOnLongClickListener { clearInput(); true }
//        binding.dialpadCallButton.setOnClickListener {
////            outgoingCall();
//
//            if (! binding.dialpadInput.text.toString().isEmpty()){
//                callBackListener?.onCallBack(binding.dialpadInput.text.toString());
//            }
//
//        }
//        binding.dialpadInput.setText("0094773499994")
//        dialpad_input.onTextChangeListener { dialpadValueChanged(it) }
        binding.dialpadInput.requestFocus()
        binding.dialpadInput.disableKeyboard()


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

    private fun dialpadPressed(char: Char, view: View?) {
        binding.dialpadInput.addCharacter(char)
        maybePerformDialpadHapticFeedback(view)
    }

    private fun clearChar(view: View) {
        binding.dialpadInput.dispatchKeyEvent(binding.dialpadInput.getKeyEvent(KeyEvent.KEYCODE_DEL))
        maybePerformDialpadHapticFeedback(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupCharClick(view: View, char: Char, longClickable: Boolean = true) {
        view.isClickable = true
        view.isLongClickable = true
        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dialpadPressed(char, view)
                    startDialpadTone(char)
                    if (longClickable) {
                        longPressHandler.removeCallbacksAndMessages(null)
                        longPressHandler.postDelayed({
                            performLongClick(view, char)
                        }, longPressTimeout)
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    stopDialpadTone(char)
                    if (longClickable) {
                        longPressHandler.removeCallbacksAndMessages(null)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val viewContainsTouchEvent = if (event.rawX.isNaN() || event.rawY.isNaN()) {
                        false
                    } else {
                        view.boundingBox.contains(event.rawX.roundToInt(), event.rawY.roundToInt())
                    }

                    if (!viewContainsTouchEvent) {
                        stopDialpadTone(char)
                        if (longClickable) {
                            longPressHandler.removeCallbacksAndMessages(null)
                        }
                    }
                }
            }
            false
        }
    }

    private fun startDialpadTone(char: Char) {
//        if (config.dialpadBeeps) {
        pressedKeys.add(char)
        toneGeneratorHelper?.startTone(char)
//        }
    }

    val View.boundingBox
        get() = Rect().also { getGlobalVisibleRect(it) }

    private fun stopDialpadTone(char: Char) {
//        if (config.dialpadBeeps) {
        if (!pressedKeys.remove(char)) return
        if (pressedKeys.isEmpty()) {
            toneGeneratorHelper?.stopTone()
        } else {
            startDialpadTone(pressedKeys.last())
        }
//        }
    }

    private fun clearInput() {
        binding.dialpadInput.setText("")
    }

    private fun maybePerformDialpadHapticFeedback(view: View?) {

    }

    private fun performLongClick(view: View, char: Char) {
        if (char == '0') {
            clearChar(view)
            dialpadPressed('+', view)
        } else {
//            val result = speedDial(char.digitToInt())
//            if (result) {
//                stopDialpadTone(char)
//                clearChar(view)
//            }
        }
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
                val call = core.currentCall
                if (call != null) {
                    org.linphone.core.tools.Log.w("[outgoingCall] Call Nulled !")
                    icCallrunning=true
                    getbusinessList(call)
                    if (!getFirstTwoCharacters(call.remoteAddress.displayName.toString()).equals("00")) {
                        binding.textViewUserName.setText(call.remoteAddress.displayName)
                    } else {
                        binding.textViewUserName.setText("Direct Call")

                    }
                    binding.textViewNumber.setText(call.remoteAddress.username)
                    binding.textViewUserSipaddress.setText(Preference.getCountry(this@CallActivity))

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

    fun getFirstTwoCharacters(input: String): String {
        if (input.length >= 2) {
            return input.substring(0, 2)
        } else {
            return input
        }
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
                    Log.e("nameeeeee", "" + call.remoteAddress.asStringUriOnly())
                }

                Call.State.Connected -> {
                    android.util.Log.d("outgoingCall", "Connectedsss")
                    binding.textViewRinging.setText("Connected")
//                    binding.textViewUserName.text = call.remoteAddress.username
                    icCallrunning=true
                    getbusinessList(call)

                    if (!getFirstTwoCharacters(call.remoteAddress.displayName.toString()).equals("00")) {
                        binding.textViewUserName.setText(call.remoteAddress.displayName)
                    } else {
                        binding.textViewUserName.setText("Direct Call")

                    }
                    binding.textViewNumber.setText(call.remoteAddress.username)
//                    if (call.remoteAddress.methodParam.equals(" ") || call.remoteAddress.methodParam == null) {
////                        MaterialTextDrawable.with(this@CallActivity)
////                            .text(call.remoteAddress.username?.substring(0, 2) ?: "DC")
////                            .into(binding.imageViewProfile)
//
////                        val firstLetter =call.remoteAddress.username
////                        val textss = firstLetter?.get(0).toString()
//                        Log.e("nameeeeee","oooooo"+binding.textViewUserName.text.toString())
//
//                        val split: Array<String> = binding.textViewUserName.text.toString().split(" ").toTypedArray()
//                        var firstLetter = ""
//                        var second = ""
//                        var firstword = "D"
//                        var secondword = "C"
//
//                        if (split != null && split.size >= 2) {
//                            android.util.Log.e("nameeeeeeeet","namee")
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
//                            android.util.Log.e("nameeeeeeeet","errorr")
//                            firstword = split[0]
//                            firstLetter = firstword[0].toString()
//                            second="C"
//                        }
//                        val textss = ""+firstLetter+second
//                        val bitmap = Preference.textToBitmap(textss, Color.parseColor("#2F80ED"))
//                        binding.imageViewProfileDirectCall.visibility=View.VISIBLE
//                        binding.card.visibility=View.GONE
//                        binding.imageViewProfileDirectCall.setImageBitmap(bitmap)
//                    } else {
//                        binding.imageViewProfileDirectCall.visibility=View.GONE
//                        binding.card.visibility=View.VISIBLE
//                        Glide.with(this@CallActivity).load(call.remoteAddress.methodParam)
//                            .into(binding.imageViewProfile)
//                    }

//                    val timer = binding.activeCallTimer
//                    timer.base =
//                        SystemClock.elapsedRealtime() - (1000 * call.duration) // Linphone timestamps are in seconds
                    binding.activeCallTimer.start()

                }

                Call.State.Released -> {
                    android.util.Log.d("outgoingCall", "Released")
                    Log.e("errrrrrrrr", "destoryy relese")
                    icCallrunning=false
                    Preference.setFirstHandler(this@CallActivity, false)


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

    private fun getbusinessList(call: Call) {
        viewModel.getBusiness(this@CallActivity)

        viewModel.getbusinessLiveData?.observe(this@CallActivity, androidx.lifecycle.Observer {

            if (it.isSuccess == true && it.Responcecode == 200) {
                ProgressHelper.dismissProgressDialog()

                it.data?.let { it1 -> businessresponce.addAll(it1) }
                var paraam = "";

                if (businessresponce != null) {
                    for (item1 in businessresponce) {
                        if (item1.lineExtension.equals(call.remoteAddress.username)) {

                            android.util.Log.e("errorrellll", "" + item1.businessName.toString())
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
                        Glide.with(this@CallActivity).load(paraam)
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
        val filter = IntentFilter("TIMER_UPDATE")
        registerReceiver(timerReceiver, filter)
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
        unregisterReceiver(timerReceiver)

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
        if (!icCallrunning){
            val serviceIntent = Intent(this, TimerService::class.java)
            stopService(serviceIntent)
            Preference.setFirstHandler(this@CallActivity, false)

        }


    }

    private fun updateCurrentCallTimer() {
        val call = mCore.currentCall ?: return
        binding.activeCallTimer.setBase(SystemClock.elapsedRealtime() - call.duration * 1000)
        binding.activeCallTimer.start()
    }
}