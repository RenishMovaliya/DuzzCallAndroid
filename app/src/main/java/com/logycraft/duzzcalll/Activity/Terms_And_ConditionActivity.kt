package com.logycraft.duzzcalll.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.duzzcall.duzzcall.R
import com.logycraft.duzzcalll.Util.Utils
import com.logycraft.duzzcalll.Util.Utils.Companion.FROM
import com.logycraft.duzzcalll.Util.Utils.Companion.REGISTER

class Terms_And_ConditionActivity : AppCompatActivity() {

    lateinit var btn_agree_and_continue : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_condition)

        btn_agree_and_continue = findViewById(R.id.btn_agree_and_continue)
        btn_agree_and_continue.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(this@Terms_And_ConditionActivity, PortfolioActivity::class.java)
                intent.putExtra(FROM,REGISTER)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }

        })

    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}