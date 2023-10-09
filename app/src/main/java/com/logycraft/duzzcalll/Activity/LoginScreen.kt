package com.logycraft.duzzcalll.Activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.duzzcall.duzzcall.R
import com.duzzcall.duzzcall.databinding.ActivityLoginScreenBinding
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.hbb20.CountryCodePicker
import com.logycraft.duzzcalll.Adapter.Country_List_Adapter
import com.logycraft.duzzcalll.LinphoneManager
import com.logycraft.duzzcalll.Model.ContactModel
import com.logycraft.duzzcalll.Model.Country
import com.logycraft.duzzcalll.Util.BaseActivity
import com.logycraft.duzzcalll.Util.Preference
import com.logycraft.duzzcalll.Util.ProgressHelper
import com.logycraft.duzzcalll.Util.Utils
import com.logycraft.duzzcalll.Util.Utils.Companion.FORGOT
import com.logycraft.duzzcalll.Util.Utils.Companion.FROM
import com.logycraft.duzzcalll.data.LoginData
import com.logycraft.duzzcalll.service.LinphoneService
import com.logycraft.duzzcalll.service.ServiceWaitThread
import com.logycraft.duzzcalll.service.ServiceWaitThreadListener
import com.logycraft.duzzcalll.viewmodel.HomeViewModel
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.util.*

class LoginScreen : BaseActivity(), ServiceWaitThreadListener {
    private lateinit var viewModel: HomeViewModel
    private lateinit var loggg: LoginData
    private lateinit var binding: ActivityLoginScreenBinding

    //    var itemsList: ArrayList<Country>? = null
    private var itemsList: java.util.ArrayList<Country> = java.util.ArrayList()
    private var country_list: java.util.ArrayList<Country> = java.util.ArrayList()
    var textsearch = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login_screen)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        binding.btnregister.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(this@LoginScreen, Terms_And_ConditionActivity::class.java)
////                val intent = Intent(this@LoginScreen, DashboardActivity::class.java)
                startActivity(intent)

            }
        })
//        Xiaomi
//        OnePlus
        val deviceMan = Build.MANUFACTURER
        Log.d("DeviceCompany", deviceMan);
//        if(deviceMan.equals("OnePlus")){
////            binding.etMobileNumber.setText("+919909799097")
////            binding.etPassword.setText("12345678")
//            binding.etMobileNumber.setText("773785342")
//            binding.Tvcountrycode.setText("+94")
//        }
//        else{
//            binding.etMobileNumber.setText("+919909699096")
//            binding.etPassword.setText("1234567890")
//        }
//        binding.etMobileNumber.setText("+919909799097")
//////        binding.etMobileNumber.setText("+94773785342")
//        binding.etPassword.setText("12345678")


        binding.tvCountryname.setOnClickListener {
//            countryCodePicker.launchCountrySelectionDialog()

            val dialog = Dialog(
                this@LoginScreen, android.R.style.Theme_Black_NoTitleBar_Fullscreen
            )
            dialog.setContentView(R.layout.country_pick_dialog)
            dialog.setCancelable(true)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(android.R.color.transparent))

            val recyclerview = dialog.findViewById<RecyclerView>(R.id.recyclerview)
            val et_search = dialog.findViewById<EditText>(R.id.et_search)

            val inputStream = resources.openRawResource(R.raw.country_list)
            val writer: Writer = StringWriter()
            val buffer = CharArray(1024)
            try {
                val bufferedReader: Reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
                var n: Int
                while (bufferedReader.read(buffer).also { n = it } != -1) {
                    writer.write(buffer, 0, n)
                }
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            val jsonString = writer.toString()
            var jsonArray: JSONArray? = null
            try {
                jsonArray = JSONArray(jsonString)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            itemsList!!.addAll(parseJson(jsonArray))
            country_list!!.addAll(parseJson(jsonArray))

            val countryListAdapter = Country_List_Adapter(
                this@LoginScreen,
                itemsList,
                object : Country_List_Adapter.OnItemClickListener {
                    override fun onItemClick(country_name: String?, country_code: String?) {
                        binding.Tvcountrycode.setText(country_code)
                        binding.tvCountryname.setText(country_name)
                        dialog.dismiss()
                    }
                })
            recyclerview.setAdapter(countryListAdapter)

//            val countryListAdapter = Country_List_Adapter(this@LoginScreen, jsonArray,Country_List_Adapter.OnItemClickListener)
            recyclerview.layoutManager = LinearLayoutManager(this@LoginScreen)

            et_search.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(editable: Editable) {}

                override fun beforeTextChanged(
                    charSequence: CharSequence, i2: Int, i3: Int, i4: Int
                ) {
                }

                override fun onTextChanged(charSequence: CharSequence, i2: Int, i3: Int, i4: Int) {
                    this@LoginScreen.textsearch = charSequence.toString().trim { it <= ' ' }


//                    countryListAdapter.filter(
//                        this@LoginScreen.textsearch,
//                    )
//                    filterData()

                    country_list.clear()

                    for (item in itemsList) {
                        if (item.name.toString().toLowerCase(Locale.getDefault()).contains(
                                et_search.text.toString().toLowerCase(Locale.getDefault())
                            ) || item.dial_code.toString().toLowerCase(Locale.getDefault())
                                .contains(
                                    et_search.text.toString().toLowerCase(Locale.getDefault())
                                )
                        ) {
                            country_list.add(item)
                        }
                    }

                    val countryListAdapter = Country_List_Adapter(
                        this@LoginScreen,
                        country_list,
                        object : Country_List_Adapter.OnItemClickListener {
                            override fun onItemClick(country_name: String?, country_code: String?) {
                                binding.Tvcountrycode.setText(country_code)
                                binding.tvCountryname.setText(country_name)
                                dialog.dismiss()
                            }
                        })
                    recyclerview.setAdapter(countryListAdapter)

                }
            })

            dialog.show()

        }


        binding.btnLogin.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (isValidate()) {

                    Login(
                        binding.Tvcountrycode.text.toString(),
                        binding.etMobileNumber.text.toString()
                    )

                }
            }
        })

        binding.btnForgotPass.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(this@LoginScreen, Edit_PhoneActivity::class.java)
                intent.putExtra(FROM, FORGOT)
                startActivity(intent)
            }
        })

        val customDirectory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "DuzzCall/ProfilePhoto"
        )

        if (!customDirectory.exists()) {
            customDirectory.mkdirs()
        }

        clearDirectory(
            customDirectory
        )

    }

    fun clearDirectory(directory: File) {
        if (directory.isDirectory) {
            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isDirectory) {
                        clearDirectory(file)
                    } else {
                        file.delete()
                    }
                }
            }
        }
        directory.delete()
    }

    var mobileNumber = ""
    var countrycode = ""
    private fun isValidate(): Boolean {
        val password = binding.etPassword.text.toString()
        mobileNumber = binding.etMobileNumber.text.toString()
        countrycode = binding.Tvcountrycode.text.toString()

        when {
            TextUtils.isEmpty(mobileNumber) -> {
                showError(getString(R.string.enter_mobile_number))
                return false
            }

            TextUtils.isEmpty(countrycode) -> {
                showError(getString(R.string.select_country))
                return false
            }

//            TextUtils.isEmpty(password) -> {
//                showError(getString(R.string.enter_password))
//                return false
//            }
//            !ValidationUtils.isValidPassword(password) -> {
//                showDialogOk(this, "", getString(R.string.password_validation_msg), "OK")
//                showError(getString(R.string.password_validation_msg))
//                return false
//            }

            else -> {
                return true
            }
        }


    }

    fun parseJson(jsonArray: JSONArray?): List<Country> {
        val itemList = mutableListOf<Country>()

        for (i in 0 until jsonArray!!.length()) {
            val jsonItem = jsonArray.getJSONObject(i)
            val name = jsonItem.getString("name")
            val flag = jsonItem.getString("flag")
            val code = jsonItem.getString("code")
            val dial_code = jsonItem.getString("dial_code")

            val item = Country(name, flag, code, dial_code)
            itemList.add(item)
        }

        return itemList
    }

    override fun onStart() {
        super.onStart()
        if (LinphoneService.isReady()) {
            onServiceReady()
//            LinphoneManager.getInstance().changeStatusToOnline()
            return
        }
        try {
            startService(Intent().setClass(this, LinphoneService::class.java))
            ServiceWaitThread(this).start()
        } catch (ise: IllegalStateException) {
            Log.e("Linphone", "Exception raised while starting service: $ise")
        }
    }

    private fun Login(c_code: kotlin.String, phone: kotlin.String) {
        var element: JsonElement? = null
        val `object` = JSONObject()
        `object`.put("country_code", c_code)
        `object`.put("phone", phone)
        element = Gson().fromJson(`object`.toString(), JsonElement::class.java)

        viewModel.loginUser(element)
        viewModel.loginuserLiveData?.observe(this@LoginScreen, Observer {

            if (it.isSuccess == true && it.Responcecode == 200) {
                ProgressHelper.dismissProgressDialog()
                var usedata: JsonElement? = it.data
                val `objecsst` = JSONObject(usedata.toString())


//                Preference.setLoginData(this@LoginScreen,usedata)
                Preference.saveCountry(this@LoginScreen,binding.tvCountryname.text.toString())
                Preference.saveToken(
                    this@LoginScreen, "Bearer " + `objecsst`.getString("verification_token")
                )
//                Toast.makeText(
//                    this@LoginScreen, "" + `objecsst`.getString("tfa_code"), Toast.LENGTH_LONG
//                ).show()

//                if (`objecsst`.getString("is_new").equals("false")) {
//                    val intent = Intent(this@LoginScreen, DashboardActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent)
//                } else {
                val intent = Intent(this@LoginScreen, Verify_PhoneActivity::class.java)
                Preference.saveNumber(this@LoginScreen, binding.etMobileNumber.text.toString())

                intent.putExtra(
                    Utils.COUNTRY_CODE, binding.Tvcountrycode.text.toString()
                )
                Log.d(
                    "OTP_is_HERE",
                    binding.etMobileNumber.text.toString() + "   " + `objecsst`.getString("tfa_code") + `objecsst`.getString(
                        "is_new"
                    )
                )
                intent.putExtra(Utils.OTP, `objecsst`.getString("tfa_code"))
//                Toast.makeText(
//                            this@LoginScreen, ""+`objecsst`.getString("tfa_code"), Toast.LENGTH_LONG
//                        ).show()
                intent.putExtra("isNew", `objecsst`.getString("is_new"))
                startActivity(intent)
//                }
//                val intent = Intent(this@LoginScreen, DashboardActivity::class.java)

//                intent.putExtra(FROM, LOGIN)
//                intent.putExtra("MOBILE", mobileNumber)

//                startActivity(intent)

//                showError("" + usedata?.extension?.accessToken)


            } else if (it.error != null) {
                ProgressHelper.dismissProgressDialog()
                var errorResponce: ResponseBody = it.error
                val jsonObj = JSONObject(errorResponce!!.charStream().readText())


//                {"message":"Unable to send verification code to your phone number. Please try again."}
                showError(jsonObj.getString("message"))
//
//                var errorResponce: ResponseBody = it.error
//                val jsonObj = JSONObject(errorResponce!!.charStream().readText())
//
//                val keys = jsonObj.keys()
//                while (keys.hasNext()) {
//                    val key2 = keys.next()
//                    val value = jsonObj.optJSONObject(key2)
//                    val keys: Iterator<*> = value.keys()
//
//                    while (keys.hasNext()) {
//                        val key2 = keys.next()
//                        val obj = JSONObject(java.lang.String.valueOf(value))
//                        Log.e("Erorrr", "====" + obj.getString(key2.toString()))
//                        val responseWithoutBrackets =
//                            obj.getString(key2.toString()).removeSurrounding("[\"", "\"]")
//
//                        Toast.makeText(
//                            this@LoginScreen, "" + responseWithoutBrackets, Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }


            } else {
                ProgressHelper.dismissProgressDialog()
                showError("Something Went Wrong!")
            }

        })
    }

    override fun onServiceReady() {
        LinphoneManager.getInstance().changeStatusToOnline()
    }
}