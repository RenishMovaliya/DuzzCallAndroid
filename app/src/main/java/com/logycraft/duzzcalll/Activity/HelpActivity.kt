package com.logycraft.duzzcalll.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.duzzcall.duzzcall.databinding.ActivityHelpBinding


class HelpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHelpBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.btnPrivacyPolicy.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://duzzcall.com/privacy-policy/")
            startActivity(i)
        }
    }
}