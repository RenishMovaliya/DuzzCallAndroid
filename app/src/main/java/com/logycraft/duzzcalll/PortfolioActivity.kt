package com.logycraft.duzzcalll

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.logycraft.duzzcalll.Util.BaseActivity
import com.logycraft.duzzcalll.Util.ValidationUtils
import kotlinx.android.synthetic.main.activity_login_screen.*
import kotlinx.android.synthetic.main.activity_portfolio.*
import kotlinx.android.synthetic.main.activity_portfolio.et_password

class PortfolioActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portfolio)



        if (intent.getStringExtra("PASS").equals("NEW_PASS")) {
            linear_portfolio.visibility = View.GONE
            view_bottom.visibility = View.GONE
        } else {
            linear_portfolio.visibility = View.VISIBLE
        }


        btn_next.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (intent.getStringExtra("PASS").equals("NEW_PASS")) {
                    if (isPassValidate()) {
                        val intent = Intent(this@PortfolioActivity, DashboardActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        finish();
                    }
                } else {

                    if (isValidate()) {
                        val intent = Intent(this@PortfolioActivity, DashboardActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        finish();
                    }
                }

            }

        })


    }

    private fun isPassValidate(): Boolean {
        val password = et_password.text.toString()
        when {
            TextUtils.isEmpty(password) -> {
                showError(getString(R.string.enter_password))
                return false
            }
            !ValidationUtils.isValidPassword(password) -> {
                showDialogOk(this, "", getString(R.string.password_validation_msg), "OK")
                return false
            }
            else -> {
                return true
            }
        }
    }

    private fun isValidate(): Boolean {

        val firstname = et_first_name.text.toString()
        val lastname = et_last_name.text.toString()
        val password = et_password.text.toString()
        val email = et_email.text.toString().trim()

        when {
            TextUtils.isEmpty(firstname) -> {
                showError(getString(R.string.enter_firstname))
                return false
            }

            TextUtils.isEmpty(lastname) -> {
                showError(getString(R.string.enter_lastname))
                return false
            }

            TextUtils.isEmpty(email) -> {
                showError(getString(R.string.enter_email_address))
                return false
            }
            !ValidationUtils.isValidEmail(email) -> {
                showError(getString(R.string.invalid_email_address))
                return false
            }

            TextUtils.isEmpty(password) -> {
                showError(getString(R.string.enter_password))
                return false
            }
            !ValidationUtils.isValidPassword(password) -> {
                showDialogOk(this, "", getString(R.string.password_validation_msg), "OK")
                return false
            }

            else -> {
                return true
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}