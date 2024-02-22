package com.logycraft.duzzcalll.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.duzzcall.duzzcall.R
import com.duzzcall.duzzcall.databinding.ActivityNewContactBinding
import com.duzzcall.duzzcall.databinding.ActivityPortfolioBinding

class NewContactActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewContactBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnAddToContact.setOnClickListener {
            finish()
        }
    }
}