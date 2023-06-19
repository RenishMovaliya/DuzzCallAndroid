package com.logycraft.duzzcalll.Util

import android.app.Activity
import android.content.Context
import com.logycraft.duzzcalll.Activity.Edit_PhoneActivity

object Preference {


    fun saveToken(activity: Context, value: String?) {
        val sharedpreferences = activity.getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()
        editor.putString("Value", value)
        editor.apply()
    }

    fun getToken(activity: Context): String? {
        val sharedpreferences =
            activity.getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE)
        return sharedpreferences.getString("Value", "")
    }

    fun saveNumber(activity: Context,value: String?) {
        val sharedpreferences = activity.getSharedPreferences("Number", Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()
        editor.putString("Phone", value)
        editor.apply()
    }

    fun getNumber(activity: Context): String? {
        val sharedpreferences =
            activity.getSharedPreferences("Number", Context.MODE_PRIVATE)
        return sharedpreferences.getString("Phone", "")
    }
}