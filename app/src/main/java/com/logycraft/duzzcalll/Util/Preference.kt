package com.logycraft.duzzcalll.Util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.logycraft.duzzcalll.data.LoginData

object Preference {

    var edit: SharedPreferences.Editor? = null
    const val MyPREFERENCES = "DuzzFirst"
    var Register = "Register"

    fun saveToken(activity: Context, value: String?) {
        val sharedpreferences = activity.getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()
        editor.putString("Value", value)
        editor.apply()
    }

    fun getToken(activity: Context): String? {
        val sharedpreferences = activity.getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE)
        return sharedpreferences.getString("Value", "")
    }

    fun saveNumber(activity: Context, value: String?) {
        val sharedpreferences = activity.getSharedPreferences("Number", Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()
        editor.putString("Phone", value)
        editor.apply()
    }

    fun getNumber(activity: Context): String? {
        val sharedpreferences = activity.getSharedPreferences("Number", Context.MODE_PRIVATE)
        return sharedpreferences.getString("Phone", "")
    }

    fun setLoginData(context: Context?, str: LoginData?) {
        val gson = Gson()
        val json = gson.toJson(str)
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        edit = preferences.edit()

        edit?.putString("MyObject", json)
        edit?.apply()
    }

    fun getLoginData(context: Context?): LoginData? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        val json = preferences.getString("MyObject", "")
        return gson.fromJson(json, LoginData::class.java)
    }


    fun getFirstUser(c1: Context): Boolean {
        val sharedpreferences =
            c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
        return sharedpreferences.getBoolean(Register, false)
    }

    fun setFirstUser(c1: Context, value: Boolean) {
        val sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()
        editor.putBoolean(Register, value)
        editor.apply()
    }


}