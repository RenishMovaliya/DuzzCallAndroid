package com.logycraft.duzzcalll

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class LoginScreen : AppCompatActivity() {
    lateinit var btnregister : TextView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)
          btnregister = findViewById(R.id.btnregister)
        btnregister.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(this@LoginScreen, SignupActivity::class.java)
                startActivity(intent)            }

        })
    }
}