package com.logycraft.duzzcalll

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

class PortfolioActivity : AppCompatActivity() {
    lateinit var btn_next: TextView
    lateinit var linear_portfolio: LinearLayout
    lateinit var view_bottom : LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portfolio)

        btn_next = findViewById(R.id.btn_next)
        linear_portfolio = findViewById(R.id.linear_portfolio)
        view_bottom = findViewById(R.id.view_bottom)


        if (intent.getStringExtra("PASS").equals("NEW_PASS")) {
            linear_portfolio.visibility = View.GONE
            view_bottom.visibility=View.GONE
        } else {
            linear_portfolio.visibility = View.VISIBLE
        }


        btn_next.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(this@PortfolioActivity, DashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish();
            }

        })


    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}