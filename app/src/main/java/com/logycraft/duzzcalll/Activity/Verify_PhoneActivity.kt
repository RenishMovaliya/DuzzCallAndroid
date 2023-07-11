package com.logycraft.duzzcalll.Activity

import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonElement
import com.logycraft.duzzcalll.R
import com.logycraft.duzzcalll.Util.BaseActivity
import com.logycraft.duzzcalll.Util.Preference
import com.logycraft.duzzcalll.Util.ProgressHelper
import com.logycraft.duzzcalll.Util.Utils
import com.logycraft.duzzcalll.Util.Utils.Companion.FROM
import com.logycraft.duzzcalll.Util.Utils.Companion.LOGIN
import com.logycraft.duzzcalll.Util.Utils.Companion.MOBILE
import com.logycraft.duzzcalll.Util.Utils.Companion.REGISTER
import com.logycraft.duzzcalll.data.SendOTP
import com.logycraft.duzzcalll.databinding.ActivityPortfolioBinding
import com.logycraft.duzzcalll.databinding.ActivityVerifyPhoneBinding
import com.logycraft.duzzcalll.viewmodel.HomeViewModel

import okhttp3.ResponseBody
import org.json.JSONObject


class Verify_PhoneActivity : BaseActivity() {
    private lateinit var binding: ActivityVerifyPhoneBinding
    lateinit var btn_next: TextView
    lateinit var view_bottom: LinearLayout
    lateinit var entered_otp: String
    lateinit var otpTextView: OtpTextView
    private lateinit var viewModel: HomeViewModel
    private lateinit var countDownTimer: CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_verify_phone)
        binding = ActivityVerifyPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        btn_next = findViewById(R.id.btn_next)
        view_bottom = findViewById(R.id.view_bottom)
        btn_next?.isEnabled = false
        btn_next.alpha = 0.5f

        if (!intent.getStringExtra(FROM).equals(REGISTER)) {
            view_bottom.visibility = View.GONE

        }
        otpTextView = findViewById(R.id.otp_view)
        otpTextView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                // fired when user types something in the Otpbox
                btn_next?.isEnabled = false
                btn_next.alpha = 0.5f
            }

            override fun onOTPComplete(otp: String) {
                // fired when user has entered the OTP fully.
                entered_otp = otp;
                btn_next?.isEnabled = true
                btn_next.alpha = 1f
//                Toast.makeText(this@Verify_PhoneActivity, "The OTP is $otp", Toast.LENGTH_SHORT).show()
            }
        }

        binding.txtNumber.setText("Verify " + Preference.getNumber(this@Verify_PhoneActivity))
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

        val countdownDuration = 60000L // Duration in milliseconds (60 seconds)
        val countdownInterval = 1000L // Interval in milliseconds (1 second)

        countDownTimer = object : CountDownTimer(countdownDuration, countdownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val formattedTime = formatTime(seconds)
                binding.txtCountDown.text = "Resend SMS in " + formattedTime + " secs";

            }

            override fun onFinish() {
                binding.txtCountDown.text = "Resend OTP"
                binding.btnResendOtp.visibility = View.VISIBLE
                btn_next.visibility = View.GONE
            }
        }

        countDownTimer.start()


        binding.btnResendOtp.setOnClickListener {
            ProgressHelper.showProgressDialog()
            SendOTP()
        }

    }


    private fun SendOTP() {

        Preference.getNumber(this@Verify_PhoneActivity)?.let { viewModel.sentOtp(it) }
        viewModel.sentOtpLiveData?.observe(this@Verify_PhoneActivity, Observer {

            if (it.isSuccess == true && it.Responcecode == 200) {
                ProgressHelper.dismissProgressDialog()
                var sendOtp: SendOTP? = it.data
                Toast.makeText(this@Verify_PhoneActivity, "" + sendOtp?.tfaCode, Toast.LENGTH_LONG)
                    .show()

            } else if (it.error != null) {
                ProgressHelper.dismissProgressDialog()
                var errorResponce: ResponseBody = it.error
                val jsonObj = JSONObject(errorResponce!!.charStream().readText())
                showError(jsonObj.getString("errors"))

            } else {
                ProgressHelper.dismissProgressDialog()
                showError("Something Went Wrong!")

            }
            countDownTimer.start()
            btn_next.visibility = View.VISIBLE
            binding.btnResendOtp.visibility = View.GONE
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }

    private fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    private fun VerifyOTP() {
        Preference.getNumber(this@Verify_PhoneActivity)
            ?.let { viewModel.verifyOtp(it, entered_otp, this@Verify_PhoneActivity) }
//        intent.getStringExtra(MOBILE)?.let { viewModel.verifyOtp(it, entered_otp,this@Verify_PhoneActivity) }
        viewModel.verifyOtpLiveData?.observe(this@Verify_PhoneActivity, Observer {

            if (it.isSuccess == true && it.Responcecode == 200) {
                ProgressHelper.dismissProgressDialog()
                var sendOtp: JsonElement? = it.data
//                showError("" + sendOtp.toString())
                Preference.setFirstUser(this@Verify_PhoneActivity, true)
                val intent = Intent(
                    this@Verify_PhoneActivity, DashboardActivity::class.java
                )
                intent.putExtra("PASS", "NEW_PASS")
                intent.putExtra("MOBILE", intent.getStringExtra("MOBILE"))
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            } else if (it.error != null) {
                ProgressHelper.dismissProgressDialog()
                var errorResponce: ResponseBody = it.error
                val jsonObj = JSONObject(errorResponce!!.charStream().readText())
                showError(jsonObj.getString("errors"))
            } else {
                ProgressHelper.dismissProgressDialog()
                showError("Something Went Wrong!")
            }


        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}