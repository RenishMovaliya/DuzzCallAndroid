package com.logycraft.duzzcalll.Activity

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.adwardstark.mtextdrawable.MaterialTextDrawable
import com.duzzcall.duzzcall.R
import com.duzzcall.duzzcall.databinding.ActivityCallBinding
import com.logycraft.duzzcalll.LinphoneManager
import com.logycraft.duzzcalll.LinphonePreferences
import com.logycraft.duzzcalll.Util.CallActivityInterface
import com.logycraft.duzzcalll.Util.LinphoneUtils
import com.logycraft.duzzcalll.extention.addCharacter
import com.logycraft.duzzcalll.extention.disableKeyboard
import com.logycraft.duzzcalll.extention.getKeyEvent
import com.logycraft.duzzcalll.helper.ToneGeneratorHelper
import com.logycraft.duzzcalll.service.LinphoneService
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
    private val longPressTimeout = ViewConfiguration.getLongPressTimeout().toLong()
    private val longPressHandler = Handler(Looper.getMainLooper())
    private val pressedKeys = mutableSetOf<Char>()
    private var toneGeneratorHelper: ToneGeneratorHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallBinding.inflate(layoutInflater)
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }

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
//                    startForeground()
                val call = core.currentCall
                if (call != null) {
                    org.linphone.core.tools.Log.w("[outgoingCall] Call Nulled !")
                    binding.textViewUserName.setText(call.remoteAddress.username)
                    MaterialTextDrawable.with(this@CallActivity)
                        .text(call.remoteAddress.username?.substring(0,2) ?: "DC")
                        .into(binding.imageViewProfile)
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
                    Log.e("ddd",""+call.remoteAddress.asStringUriOnly())
                }

                Call.State.Connected -> {
                    android.util.Log.d("outgoingCall", "Connectedsss")
                    binding.textViewRinging.setText("Connected")
                    binding.textViewUserName.text = call.remoteAddress.username
                    MaterialTextDrawable.with(this@CallActivity)
                        .text(call.remoteAddress.username?.substring(0,2) ?: "DC")
                        .into(binding.imageViewProfile)
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
        MaterialTextDrawable.with(this@CallActivity)
            .text(displayName.substring(0,2) ?: "DC")
            .into(binding.imageViewProfile)
    }

    private fun updateCurrentCallTimer() {
        val call = mCore.currentCall ?: return
        binding.activeCallTimer.setBase(SystemClock.elapsedRealtime() - call.duration * 1000)
        binding.activeCallTimer.start()
    }
}