package com.logycraft.duzzcalll

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class LoginScreen : AppCompatActivity() {
    lateinit var btnregister : TextView
    lateinit var btn_login : TextView
    lateinit var btn_forgot_pass : TextView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)
          btnregister = findViewById(R.id.btnregister)
        btn_login = findViewById(R.id.btn_login)
        btn_forgot_pass = findViewById(R.id.btn_forgot_pass)
        btnregister.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(this@LoginScreen, Terms_And_ConditionActivity::class.java)
                startActivity(intent)            }

        })

        btn_login.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(this@LoginScreen, Verify_PhoneActivity::class.java)
                intent.putExtra("ADMIN","LOGIN")
                startActivity(intent)            }

        })

        btn_forgot_pass.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(this@LoginScreen, Edit_PhoneActivity::class.java)
                intent.putExtra("ADMIN","FORGOT")
                startActivity(intent)
            }

        })
    }
}