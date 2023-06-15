package com.logycraft.duzzcalll

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Verify_PhoneActivity : AppCompatActivity() {

    lateinit var btn_next: TextView
    lateinit var view_bottom : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone)

        btn_next = findViewById(R.id.btn_next)
        view_bottom = findViewById(R.id.view_bottom)

        if (intent.getStringExtra("ADMIN").equals("LOGIN") ||intent.getStringExtra("FORGOT_PASS").equals("PASS") ){
            view_bottom.visibility=View.GONE
        }
        btn_next.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {

                if (intent.getStringExtra("ADMIN").equals("LOGIN")) {
                    val intent = Intent(this@Verify_PhoneActivity, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent)
                    finish();

                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                } else if (intent.getStringExtra("FORGOT_PASS").equals("PASS")) {
                    val intent = Intent(this@Verify_PhoneActivity, PortfolioActivity::class.java)
                    intent.putExtra("PASS", "NEW_PASS")
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                } else {
                    val intent = Intent(this@Verify_PhoneActivity, PortfolioActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }

            }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}