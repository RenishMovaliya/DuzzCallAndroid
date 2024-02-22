package com.logycraft.duzzcalll.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.duzzcall.duzzcall.R
import com.duzzcall.duzzcall.databinding.FragmentProfileBinding
import com.example.restapiidemo.home.data.UserModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.logycraft.duzzcalll.Util.Preference
import com.logycraft.duzzcalll.Util.ProgressHelper
import com.logycraft.duzzcalll.Util.ValidationUtils
import com.logycraft.duzzcalll.viewmodel.HomeViewModel
import okhttp3.ResponseBody
import org.json.JSONObject
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private var param1: String? = null
    private var param2: String? = null
    var userModel = UserModel()
    var imguri = ""
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding.rlProfile.visibility = View.GONE
        binding.imgBack.setOnClickListener {
            if (binding.rlProfile.visibility == View.VISIBLE) {
                binding.llAccountDetails.visibility = View.VISIBLE
                binding.rlProfile.visibility = View.GONE
            } else {
                activity?.supportFragmentManager?.popBackStack()
            }
        }
        updatedata()
        binding.svUpdateProfile.visibility = View.GONE
        binding.rlMainProfile.visibility = View.VISIBLE

        binding.imgEdit.setOnClickListener {
            binding.svUpdateProfile.visibility = View.VISIBLE

        }

        binding.profileImage.setOnClickListener {
            binding.llAccountDetails.visibility = View.GONE
            binding.rlProfile.visibility = View.VISIBLE
        }


        binding.btnUpdate.setOnClickListener {

            if (isValidate() && isUsernameValid(binding.etName.text.toString())) {

                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.appcontainer.getWindowToken(), 0)
                updateProfiledata()
            }
        }
//        val filter = InputFilter { source, _, _, _, _, _ ->
//            val pattern = "[a-zA-Z]*".toRegex()
//            if (source != null && !pattern.matches(source)) {
//                // If the entered text contains non-alphabetic characters, reject it
//                ""
//            } else {
//                null // Accept the input
//            }
//        }
//
//        binding.etName.filters = arrayOf(filter)

    }


    private fun isValidate(): Boolean {

        val email = binding.etEmail.text.toString().trim()

        when {
            TextUtils.isEmpty(email) -> {
                showError(getString(R.string.enter_email_address))
                return false
            }
            !ValidationUtils.isValidEmail(email) -> {
                showError(getString(R.string.invalid_email_address))
                return false
            }

            else -> {
                return true
            }
        }
    }

    fun isUsernameValid(username: String): Boolean {
        val regex = Regex("^[A-Za-z0-9]+\\s[A-Za-z0-9]+\$")

        if (regex.matches(username)) {
            return regex.matches(username)

        } else {
            Toast.makeText(activity, "Invalid Name (Firstname Lastname)", Toast.LENGTH_LONG).show()
        }
        return regex.matches(username)
    }

    fun showError(message: String?) {
        Toast.makeText(activity, "$message", Toast.LENGTH_LONG).show()
    }


    fun updatedata() {

        userModel = Preference.getUserData(activity)!!
        if (userModel != null) {
            binding.tvName.setText(userModel.first_name + " " + userModel.last_name)
            binding.tvPhone.setText(userModel.phone)
            binding.tvEmail.setText(userModel.email)

            val first = userModel.first_name.toString()
            val last = userModel.last_name.toString()
            val firstLetter = first[0]
            val second = last[0]
            val textss = firstLetter + "" + second
            val bitmap = Preference.textToBitmap(textss, Color.parseColor("#2F80ED"))
            binding.profileImage.setImageBitmap(bitmap)
            binding.imgProfileImgfull.setImageBitmap(bitmap)
            binding.updateProfileImage.setImageBitmap(bitmap)


//            activity?.let {
//                MaterialTextDrawable.with(it)
//                    .text(userModel.first_name?.substring(0, 2) ?: "DC")
//                    .into(binding.profileImage)
//            }
//            activity?.let {
//                MaterialTextDrawable.with(it)
//                    .text(userModel.first_name?.substring(0, 2) ?: "DC")
//                    .into(binding.imgProfileImgfull)
//            }
//            activity?.let {
//                MaterialTextDrawable.with(it)
//                    .text(userModel.first_name?.substring(0, 2) ?: "DC")
//                    .into(binding.updateProfileImage)
//            }


            binding.etName.setText(userModel.first_name + " " + userModel.last_name)
            binding.etEmail.setText(userModel.email)
            binding.tvExtention.setText(userModel.extension)
        }
    }


    private fun updateProfiledata() {

        val str = binding.etName.text.toString()
        val separated: List<String> = str.split(" ")

        var element: JsonElement? = null
        val `object` = JSONObject()
        `object`.put("first_name", separated[0])
        `object`.put("last_name", separated[1])
        `object`.put("email", binding.etEmail.text.toString())
        element = Gson().fromJson(`object`.toString(), JsonElement::class.java)

        activity?.let { viewModel.updateuser(element, it) }
        activity?.let {
            viewModel.userupdateData?.observe(it, androidx.lifecycle.Observer {

                if (it.isSuccess == true && it.Responcecode == 200) {
                    ProgressHelper.dismissProgressDialog()
                    var usedata: JsonElement? = it.data


                    val str = binding.etName.text.toString()
                    val separated: List<String> = str.split(" ")
                    userModel.first_name = separated[0]
                    userModel.last_name = separated[1]
                    userModel.email = binding.etEmail.text.toString()
                    userModel.profileimg = imguri
                    Preference.setUserData(activity, userModel)

                    updatedata()
                    binding.rlMainProfile.visibility = View.VISIBLE
                    binding.svUpdateProfile.visibility = View.GONE

                } else if (it.error != null) {
                    ProgressHelper.dismissProgressDialog()
                    var errorResponce: ResponseBody = it.error
                    val jsonObj = JSONObject(errorResponce!!.charStream().readText())
                    //                showError("Something went Wrong!")

                    val keys = jsonObj.keys()
                    while (keys.hasNext()) {
                        val key2 = keys.next()
                        val value = jsonObj.optJSONObject(key2)
                        val keys: Iterator<*> = value.keys()

                        while (keys.hasNext()) {
                            val key2 = keys.next()
                            val obj = JSONObject(java.lang.String.valueOf(value))
                            Log.e("Erorrr", "====" + obj.getString(key2.toString()))
                            val responseWithoutBrackets =
                                obj.getString(key2.toString()).removeSurrounding("[\"", "\"]")

                            Toast.makeText(
                                activity, "" + responseWithoutBrackets, Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    ProgressHelper.dismissProgressDialog()
                    showError("Something Went Wrong!")
                }
            })
        }
    }

}