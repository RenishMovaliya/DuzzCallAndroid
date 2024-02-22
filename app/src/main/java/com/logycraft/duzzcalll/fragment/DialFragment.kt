package com.logycraft.duzzcalll.fragment

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.duzzcall.duzzcall.R
import com.duzzcall.duzzcall.databinding.FragmentDialBinding
import com.logycraft.duzzcalll.LinphoneManager
import com.logycraft.duzzcalll.Model.ContactModel
import com.logycraft.duzzcalll.Util.ProgressHelper
import com.logycraft.duzzcalll.data.BusinessResponce
import com.logycraft.duzzcalll.extention.addCharacter
import com.logycraft.duzzcalll.extention.disableKeyboard
import com.logycraft.duzzcalll.extention.getKeyEvent
import com.logycraft.duzzcalll.helper.CallBackListener
import com.logycraft.duzzcalll.helper.ToneGeneratorHelper
import com.logycraft.duzzcalll.viewmodel.HomeViewModel
import okhttp3.ResponseBody
import org.json.JSONObject
import org.linphone.core.CallLog


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
    var businessresponce: ArrayList<BusinessResponce> = ArrayList()
    private lateinit var viewModel: HomeViewModel
    private var dialname = "";
    private var dialImage = "";
    lateinit var core: Array<CallLog>
    private val contactList: MutableList<ContactModel> = mutableListOf()
    private val READ_CONTACTS_PERMISSION_CODE = 123


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
        binding = FragmentDialBinding.inflate(inflater, container, false);
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        return binding.getRoot();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hasRussianLocale = Locale.getDefault().language == "ru"



        if (activity?.let {
                ContextCompat.checkSelfPermission(
                    it, android.Manifest.permission.POST_NOTIFICATIONS
                )
            } != PackageManager.PERMISSION_GRANTED) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101
                )
            }
        }

        if (activity?.let {
                ContextCompat.checkSelfPermission(
                    it, android.Manifest.permission.RECORD_AUDIO
                )
            } != PackageManager.PERMISSION_GRANTED) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it, arrayOf(android.Manifest.permission.RECORD_AUDIO,android.Manifest.permission.READ_CONTACTS), 123
                )
            }
        }


        getbusinessList()
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
            if (activity?.let {
                    ContextCompat.checkSelfPermission(
                        it, android.Manifest.permission.RECORD_AUDIO
                    )
                } != PackageManager.PERMISSION_GRANTED) {
                activity?.let {
                    ActivityCompat.requestPermissions(
                        it, arrayOf(android.Manifest.permission.RECORD_AUDIO), 123
                    )
                }
            }
            if (activity?.let {
                    ContextCompat.checkSelfPermission(
                        it, android.Manifest.permission.READ_CONTACTS
                    )
                } != PackageManager.PERMISSION_GRANTED) {
                activity?.let {
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(android.Manifest.permission.READ_CONTACTS),
                        READ_CONTACTS_PERMISSION_CODE
                    )
                }
            }

                if (!binding.dialpadInput.text.toString().isEmpty()) {
                    core = LinphoneManager.getCore().callLogs

                    for (item1 in businessresponce) {
                        if (item1.lineExtension.equals(binding.dialpadInput.text.toString())) {
                            dialname = item1.businessName.toString()+" "+item1.lineName
                            dialImage = item1.businessLogo.toString()
                            break
                        } else {
                            dialname = ""
                            dialImage = " "
                        }
                    }

                    if (dialname.equals("")){
                        for (item1 in core) {
                            if (item1.remoteAddress.username.equals(binding.dialpadInput.text.toString())) {
                                if (item1.remoteAddress.displayName.equals("Direct Call")){
                                    dialname = item1.remoteAddress.displayName.toString()
                                    dialImage = item1.remoteAddress.methodParam.toString()

                                }else{
                                    dialname = item1.remoteAddress.displayName.toString()
                                    dialImage = item1.remoteAddress.methodParam.toString()
                                    break
                                }

                            } else {
                                dialname = ""
                                dialImage = " "
                            }
                        }
                    }

                    if (dialname.equals("")) {
                        if (activity?.let {
                                ContextCompat.checkSelfPermission(
                                    it, android.Manifest.permission.READ_CONTACTS
                                )
                            } == PackageManager.PERMISSION_GRANTED) {

                            fetchContactList(binding.dialpadInput.text.toString())
                        }else{
                            dialname = "Direct Call"
                            dialImage = " "

                        }
                    }
                    callBackListener?.onCallBack(
                        binding.dialpadInput.text.toString(),
                        dialname,
                        dialImage
                    );
                }



        }
//        binding.dialpadInput.setText("0094773499994")
//        dialpad_input.onTextChangeListener { dialpadValueChanged(it) }
        binding.dialpadInput.requestFocus()
        binding.dialpadInput.disableKeyboard()


//        fetchContactList(binding.dialpadInput.text.toString())

        binding.dialpadCallButton.setImageDrawable(resources.getDrawable(R.drawable.ic_phone_vector))
    }


    override fun onResume() {
        super.onResume()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.appcontainer.getWindowToken(), 0)
    }
    private fun getbusinessList() {

        activity?.let { viewModel.getBusiness(it) }

        activity?.let {
            viewModel.getbusinessLiveData?.observe(it, androidx.lifecycle.Observer {

                if (it.isSuccess == true && it.Responcecode == 200) {
                    ProgressHelper.dismissProgressDialog()

                    it.data?.let { it1 -> businessresponce.addAll(it1) }


                    //                val `objecsst` = JSONObject(usedata.toString())
                    ////                showError("" + sendOtp.toString())
                    //                val intent = Intent(
                    //                    this@Verify_PhoneActivity, Terms_And_ConditionActivity::class.java
                    //                )
                    //                Preference.saveAccessToken(this@Verify_PhoneActivity,objecsst.getString("access_token"))
                    //                intent.putExtra("PASS", "NEW_PASS")
                    //                intent.putExtra("MOBILE", intent.getStringExtra("MOBILE"))
                    //                startActivity(intent)
                    //                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    //                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                } else if (it.error != null) {
                    ProgressHelper.dismissProgressDialog()
                    var errorResponce: ResponseBody = it.error
                    val jsonObj = JSONObject(errorResponce!!.charStream().readText())
//                    showMessage(jsonObj.getString("errors"))
                } else {
                    ProgressHelper.dismissProgressDialog()
//                    showMessage("Something Went Wrong!")
                }


            })
        }

    }

    private fun fetchContactList(toString: String) {
        val cursor = activity?.getContentResolver()?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        var currentGroup = ""
        cursor?.let {
            while (it.moveToNext()) {
                val name =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val imageUriString: String? =
                    cursor.getString(it.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))
                val imageUri: Uri? = imageUriString?.let { Uri.parse(it) }

                if (!name.isNullOrEmpty() && !number.isNullOrEmpty()) {

                    val groupName = name.toString().toUpperCase()
                    val historycontact: String = number.substring(Math.max(number.length - 9, 0))
                    val diall: String = toString.substring(Math.max(toString.length - 9, 0))

                    if (historycontact.equals(diall)) {
                        dialname = name
                        dialImage = " "
                        break
                    } else {
                        dialname = "Direct Call"
                        dialImage = " "
                    }
                    for (contact2 in core) {
                        val phonecontact: String = contact2.remoteAddress.username?.substring(
                            Math.max(
                                contact2.remoteAddress.username!!.length - 9, 0
                            )
                        )!!

                        if (groupName != currentGroup && historycontact.equals(phonecontact)) {
                            currentGroup = groupName
                            contactList.add(ContactModel(groupName, number, imageUri))
//                            Toast.makeText(activity, "aa", Toast.LENGTH_SHORT)
//                                .show()
                            break
                        }

                    }
                }
            }
            it.close()
        }
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

    fun EditText.onTextChangeListener(onTextChangedAction: (newText: String) -> Unit) =
        addTextChangedListener(object :
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