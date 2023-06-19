package com.logycraft.duzzcalll.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.logycraft.duzzcalll.R
import com.logycraft.duzzcalll.Util.BaseActivity
import com.logycraft.duzzcalll.Util.ProgressHelper
import com.logycraft.duzzcalll.Util.Utils
import com.logycraft.duzzcalll.Util.Utils.Companion.FROM
import com.logycraft.duzzcalll.Util.Utils.Companion.LOGIN
import com.logycraft.duzzcalll.Util.Utils.Companion.MOBILE
import com.logycraft.duzzcalll.Util.Utils.Companion.REGISTER
import com.logycraft.duzzcalll.data.SendOTP
import com.logycraft.duzzcalll.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_edit_phone.*
import okhttp3.ResponseBody
import org.json.JSONObject

class Verify_PhoneActivity : BaseActivity() {

    lateinit var btn_next: TextView
    lateinit var view_bottom : LinearLayout
    private lateinit var viewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        btn_next = findViewById(R.id.btn_next)
        view_bottom = findViewById(R.id.view_bottom)

        if (!intent.getStringExtra(FROM).equals(REGISTER)  ){
            view_bottom.visibility=View.GONE

        }
        val otpTextView
        otpTextView.
        btn_next.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {

                if (intent.getStringExtra(FROM).equals(LOGIN)) {
                    val intent = Intent(this@Verify_PhoneActivity, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent)
                    finish();

                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                } else {

//                    val intent = Intent(this@Verify_PhoneActivity, PortfolioActivity::class.java)
//                    intent.putExtra("PASS", "NEW_PASS")
//                    intent.putExtra("MOBILE", intent.getStringExtra("MOBILE"))
//                    startActivity(intent)
//                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                    ProgressHelper.showProgrssDialogs(this@Verify_PhoneActivity, "");
                    VerifyOTP();
                }

            }

        })
    }

    private fun VerifyOTP() {
        TODO("Not yet implemented")
//        viewModel.verifyOtp(intent.getStringExtra(MOBILE),)
//        viewModel.sentOtpLiveData?.observe(this@Verify_PhoneActivity, Observer {
//
//            if (it.isSuccess == true &&it.Responcecode==200){
//                ProgressHelper.dismissProgressDialog()
//                var sendOtp: SendOTP? = it.data
//                val intent =   Intent(this@Verify_PhoneActivity, Verify_PhoneActivity::class.java)
//                intent.putExtra(Utils.MOBILE,Tvcountrycode.text.toString()+ mobileNumber)
//                intent.putExtra(FROM, intent.getStringExtra(FROM))
//                intent.putExtra(Utils.OTP, sendOtp?.tfaCode.toString())
//                Utils.VERIFY_TOKEN = sendOtp?.verificationToken.toString()
//                startActivity(intent)
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
//            }else if (it.error!=null){
//                ProgressHelper.dismissProgressDialog()
//                var errorResponce: ResponseBody = it.error
//                val jsonObj = JSONObject(errorResponce!!.charStream().readText())
//                showError(jsonObj.getString("errors"))
//            }else{
//                ProgressHelper.dismissProgressDialog()
//                showError("Something Went Wrong!")
//            }




//        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}