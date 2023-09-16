package com.logycraft.duzzcalll.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.duzzcall.duzzcall.R
import com.logycraft.duzzcalll.LinphoneManager
import com.logycraft.duzzcalll.LinphonePreferences
import com.logycraft.duzzcalll.Util.Preference
import com.logycraft.duzzcalll.service.LinphoneService
import com.logycraft.duzzcalll.service.ServiceWaitThread
import com.logycraft.duzzcalll.service.ServiceWaitThreadListener
import java.io.File
import java.util.*

class SplashActivity : AppCompatActivity(), ServiceWaitThreadListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        LinphonePreferences.instance().setDebugEnabled(true)
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

    override fun onStart() {
        super.onStart()
        if (Preference.getFirstUser(this@SplashActivity)){
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

    }

    override fun onServiceReady() {
        LinphoneManager.getInstance().changeStatusToOnline()
    }

}