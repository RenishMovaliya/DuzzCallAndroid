package com.logycraft.duzzcalll

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.TextView

class TimerReceiver(private val textView: TextView) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val time = intent?.getStringExtra("time")
        textView.text = time
    }
}