package com.logycraft.duzzcalll.fragment

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import androidx.annotation.Nullable
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.logycraft.duzzcalll.R
import com.logycraft.duzzcalll.databinding.FragmentContactBinding
import com.logycraft.duzzcalll.databinding.FragmentDialBinding
import com.logycraft.duzzcalll.extention.addCharacter
import com.logycraft.duzzcalll.extention.disableKeyboard
import com.logycraft.duzzcalll.extention.getKeyEvent
import com.logycraft.duzzcalll.helper.CallBackListener
import com.logycraft.duzzcalll.helper.ToneGeneratorHelper


import java.util.*
import kotlin.math.roundToInt


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



class DialFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var hasRussianLocale = false
    private lateinit var binding: FragmentDialBinding
    private var toneGeneratorHelper: ToneGeneratorHelper? = null
    private val longPressTimeout = ViewConfiguration.getLongPressTimeout().toLong()
    private val longPressHandler = Handler(Looper.getMainLooper())
    private val pressedKeys = mutableSetOf<Char>()
    private var callBackListener: CallBackListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }
    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //getActivity() is fully created in onActivityCreated and instanceOf differentiate it between different Activities
        if (activity is CallBackListener) callBackListener = activity as CallBackListener?
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val view = inflater.inflate(R.layout.fragment_dial,    container, false)
//        // Inflate the layout for this fragment
//
//
//
//        return view
        binding = FragmentDialBinding.inflate(inflater,container,false);
        return binding.getRoot();
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

//        arrayOf(
//            dialpad_0_holder,
//            dialpad_1_holder,
//            dialpad_2_holder,
//            dialpad_3_holder,
//            dialpad_4_holder,
//            dialpad_5_holder,
//            dialpad_6_holder,
//            dialpad_7_holder,
//            dialpad_8_holder,
//            dialpad_9_holder,
//            bindingdialpad_plus_holder,
//            dialpad_asterisk_holder,
//            dialpad_hashtag_holder
//        ).forEach {
//            it.background = ResourcesCompat.getDrawable(resources, R.drawable.pill_background,
//                activity?.theme
//            )
////            it.background?.alpha = 30
//        }

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
        binding.dialpadCallButton.setOnClickListener {
//            outgoingCall();

            if (! binding.dialpadInput.text.toString().isEmpty()){
                callBackListener?.onCallBack(binding.dialpadInput.text.toString());
            }

        }
        binding.dialpadInput.setText("0094773499994")
//        dialpad_input.onTextChangeListener { dialpadValueChanged(it) }
        binding.dialpadInput.requestFocus()
        binding.dialpadInput.disableKeyboard()




        binding.dialpadCallButton.setImageDrawable(resources.getDrawable(R.drawable.ic_phone_vector))
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
        binding.dialpadInput.addCharacter(char)
        maybePerformDialpadHapticFeedback(view)
    }

    private fun clearChar(view: View) {
        binding.dialpadInput.dispatchKeyEvent(binding.dialpadInput.getKeyEvent(KeyEvent.KEYCODE_DEL))
        maybePerformDialpadHapticFeedback(view)
    }

    private fun clearInput() {
        binding.dialpadInput.setText("")
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