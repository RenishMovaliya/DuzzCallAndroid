package com.logycraft.duzzcalll

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import java.util.*


class TimerService : Service() {

    private var running = true
    private var seconds = 0
    private val handler = Handler()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler.post(object : Runnable {
            override fun run() {
                val minutes = seconds % 3600 / 60
                val secs = seconds % 60
                val time = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs)
                val broadcastIntent = Intent("TIMER_UPDATE")
                broadcastIntent.putExtra("time", time)
                sendBroadcast(broadcastIntent)

                if (running) {
                    seconds++
                }
                handler.postDelayed(this, 1000)
            }
        })

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}