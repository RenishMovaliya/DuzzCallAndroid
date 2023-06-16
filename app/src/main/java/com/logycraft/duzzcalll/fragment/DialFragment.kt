package com.logycraft.duzzcalll.fragment

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Telephony
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.*
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.core.content.res.ResourcesCompat
import androidx.loader.content.CursorLoader
import com.logycraft.duzzcalll.R
import com.logycraft.duzzcalll.extention.addCharacter
import com.logycraft.duzzcalll.extention.disableKeyboard
import com.logycraft.duzzcalll.extention.getKeyEvent
import com.logycraft.duzzcalll.helper.ToneGeneratorHelper
import kotlinx.android.synthetic.main.dialpad.*
import kotlinx.android.synthetic.main.fragment_dial.*
import java.util.*
import kotlin.math.roundToInt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

//lateinit var dialpad_1_holder : RelativeLayout
//lateinit var dialpad_2_holder : RelativeLayout
//lateinit var dialpad_3_holder : RelativeLayout
//lateinit var dialpad_4_holder : RelativeLayout
//lateinit var dialpad_5_holder : RelativeLayout
//lateinit var dialpad_6_holder : RelativeLayout
//lateinit var dialpad_7_holder : RelativeLayout
//lateinit var dialpad_8_holder : RelativeLayout
//lateinit var dialpad_9_holder : RelativeLayout
//lateinit var dialpad_0_holder : RelativeLayout
//lateinit var dialpad_plus_holder : RelativeLayout
//lateinit var dialpad_asterisk_holder : RelativeLayout
//lateinit var dialpad_hashtag_holder : RelativeLayout
//lateinit var dialpad_input : EditText

class DialFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var hasRussianLocale = false
    private var toneGeneratorHelper: ToneGeneratorHelper? = null
    private val longPressTimeout = ViewConfiguration.getLongPressTimeout().toLong()
    private val longPressHandler = Handler(Looper.getMainLooper())
    private val pressedKeys = mutableSetOf<Char>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dial,    container, false)
        // Inflate the layout for this fragment



        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hasRussianLocale = Locale.getDefault().language == "ru"
//        dialpad_1_holder = view.findViewById(R.id.dialpad_1_holder)
//        dialpad_2_holder = view.findViewById(R.id.dialpad_2_holder)
//        dialpad_3_holder = view.findViewById(R.id.dialpad_3_holder)
//        dialpad_4_holder = view.findViewById(R.id.dialpad_4_holder)
//        dialpad_5_holder = view.findViewById(R.id.dialpad_5_holder)
//        dialpad_6_holder = view.findViewById(R.id.dialpad_6_holder)
//        dialpad_7_holder = view.findViewById(R.id.dialpad_7_holder)
//        dialpad_8_holder = view.findViewById(R.id.dialpad_8_holder)
//        dialpad_9_holder = view.findViewById(R.id.dialpad_9_holder)
//        dialpad_0_holder = view.findViewById(R.id.dialpad_0_holder)
//        dialpad_plus_holder = view.findViewById(R.id.dialpad_plus_holder)
//        dialpad_hashtag_holder = view.findViewById(R.id.dialpad_hashtag_holder)
//        dialpad_asterisk_holder = view.findViewById(R.id.dialpad_asterisk_holder)
//        dialpad_input = view.findViewById(R.id.dialpad_input)

//        setupMaterialScrollListener(dialpad_list, dialpad_toolbar)
//        updateNavigationBarColor(getProperBackgroundColor())

//        if (checkAppSideloading()) {
//            return
//        }

//        if (config.hideDialpadNumbers) {
//            dialpad_1_holder.isVisible = false
//            dialpad_2_holder.isVisible = false
//            dialpad_3_holder.isVisible = false
//            dialpad_4_holder.isVisible = false
//            dialpad_5_holder.isVisible = false
//            dialpad_6_holder.isVisible = false
//            dialpad_7_holder.isVisible = false
//            dialpad_8_holder.isVisible = false
//            dialpad_9_holder.isVisible = false
//            dialpad_plus_holder.isVisible = true
//            dialpad_0_holder.visibility = View.INVISIBLE
//        }

        arrayOf(
            dialpad_0_holder,
            dialpad_1_holder,
            dialpad_2_holder,
            dialpad_3_holder,
            dialpad_4_holder,
            dialpad_5_holder,
            dialpad_6_holder,
            dialpad_7_holder,
            dialpad_8_holder,
            dialpad_9_holder,
            dialpad_plus_holder,
            dialpad_asterisk_holder,
            dialpad_hashtag_holder
        ).forEach {
            it.background = ResourcesCompat.getDrawable(resources, R.drawable.pill_background,
                activity?.theme
            )
//            it.background?.alpha = 30
        }

//        setupOptionsMenu()
//        speedDialValues = config.getSpeedDialValues()
//        privateCursor = activity.getMyContactsCursor(favoritesOnly = false, withPhoneNumbersOnly = true)

//        toneGeneratorHelper = ToneGeneratorHelper(this, DIALPAD_TONE_LENGTH_MS)

//        if (hasRussianLocale) {
//            initRussianChars()
//            dialpad_2_letters.append("\nАБВГ")
//            dialpad_3_letters.append("\nДЕЁЖЗ")
//            dialpad_4_letters.append("\nИЙКЛ")
//            dialpad_5_letters.append("\nМНОП")
//            dialpad_6_letters.append("\nРСТУ")
//            dialpad_7_letters.append("\nФХЦЧ")
//            dialpad_8_letters.append("\nШЩЪЫ")
//            dialpad_9_letters.append("\nЬЭЮЯ")
//
//            val fontSize = resources.getDimension(R.dimen.small_text_size)
//            arrayOf(
//                dialpad_2_letters, dialpad_3_letters, dialpad_4_letters, dialpad_5_letters, dialpad_6_letters, dialpad_7_letters, dialpad_8_letters,
//                dialpad_9_letters
//            ).forEach {
//                it.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize)
//            }
//        }

        setupCharClick(dialpad_1_holder, '1')
        setupCharClick(dialpad_2_holder, '2')
        setupCharClick(dialpad_3_holder, '3')
        setupCharClick(dialpad_4_holder, '4')
        setupCharClick(dialpad_5_holder, '5')
        setupCharClick(dialpad_6_holder, '6')
        setupCharClick(dialpad_7_holder, '7')
        setupCharClick(dialpad_8_holder, '8')
        setupCharClick(dialpad_9_holder, '9')
        setupCharClick(dialpad_0_holder, '0')
        setupCharClick(dialpad_plus_holder, '+', longClickable = false)
        setupCharClick(dialpad_asterisk_holder, '*', longClickable = false)
        setupCharClick(dialpad_hashtag_holder, '#', longClickable = false)

        dialpad_clear_char.setOnClickListener { clearChar(it) }
        dialpad_clear_char.setOnLongClickListener { clearInput(); true }
//        dialpad_call_button.setOnClickListener { initCall(dialpad_input.value, 0) }
//        dialpad_input.onTextChangeListener { dialpadValueChanged(it) }
        dialpad_input.requestFocus()
        dialpad_input.disableKeyboard()




        dialpad_call_button.setImageDrawable(resources.getDrawable(R.drawable.ic_phone_vector))
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DialFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DialFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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

    private fun dialpadPressed(char: Char, view: View?) {
        dialpad_input.addCharacter(char)
        maybePerformDialpadHapticFeedback(view)
    }

    private fun clearChar(view: View) {
        dialpad_input.dispatchKeyEvent(dialpad_input.getKeyEvent(KeyEvent.KEYCODE_DEL))
        maybePerformDialpadHapticFeedback(view)
    }

    private fun clearInput() {
        dialpad_input.setText("")
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
    @TargetApi(Build.VERSION_CODES.O)
    private fun maybePerformDialpadHapticFeedback(view: View?) {

    }
    fun EditText.onTextChangeListener(onTextChangedAction: (newText: String) -> Unit) = addTextChangedListener(object :
        TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            onTextChangedAction(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })

    private fun startDialpadTone(char: Char) {
//        if (config.dialpadBeeps) {
            pressedKeys.add(char)
            toneGeneratorHelper?.startTone(char)
//        }
    }

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

    data class SpeedDial(val id: Int, var number: String, var displayName: String) {
        fun isValid() = number.trim().isNotEmpty()
    }
    val View.boundingBox
        get() = Rect().also { getGlobalVisibleRect(it) }


}