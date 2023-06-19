package com.logycraft.duzzcalll.Activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.logycraft.duzzcalll.R
import com.logycraft.duzzcalll.fragment.*

class DashboardActivity : AppCompatActivity() {
    lateinit var bottomNav : BottomNavigationView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
//        loadFragment(HomeFragment())

        bottomNav = findViewById(R.id.bottomNav) as BottomNavigationView
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.favourite -> {
                    loadFragment(FavouriteFragment())
                    true
                }
                R.id.history -> {
                    loadFragment(HistoryFragment())
                    true
                }
                R.id.contact -> {
                    loadFragment(ContactFragment())
                    true
                }
                R.id.dialpad -> {
                    loadFragment(DialFragment())
                    true
                }
                R.id.setting -> {
                    loadFragment(SettingFragment())
                    true
                }
                else -> {
                    true
                }
            }
        }
//        loadFragment(DialFragment())
        bottomNav.setSelectedItemId(R.id.dialpad);

    }
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }
}