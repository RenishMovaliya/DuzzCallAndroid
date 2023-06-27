package com.logycraft.duzzcalll.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.logycraft.duzzcalll.R
import com.logycraft.duzzcalll.Util.Preference

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            if (Preference.getFirstUser(this@SplashActivity)){
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, LoginScreen::class.java)
                startActivity(intent)
                finish()
            }

        }, 2000)
    }
}