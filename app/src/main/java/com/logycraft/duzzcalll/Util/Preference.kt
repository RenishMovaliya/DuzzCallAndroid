package com.logycraft.duzzcalll.Util

import android.content.Context
import android.content.SharedPreferences
import android.graphics.*
import android.preference.PreferenceManager
import com.example.restapiidemo.home.data.UserModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.logycraft.duzzcalll.data.BusinessResponce
import com.logycraft.duzzcalll.data.LoginData
import java.lang.reflect.Type


object Preference {

    var edit: SharedPreferences.Editor? = null
    const val MyPREFERENCES = "DuzzFirst"
    var Register = "Register"
    fun textToBitmap(text: String, backgroundColor: Int): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = 600f
        paint.color = Color.WHITE // Text color
        paint.textAlign = Paint.Align.LEFT

        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)

        val width = textBounds.width() + 600 // Add some padding
        val height = textBounds.height()+600

        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)

        // Draw background color
        val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        backgroundPaint.color = backgroundColor
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        // Calculate the position to center the text
        val x = (width - textBounds.width()) / 2f - textBounds.left
        val y = (height - textBounds.height()) / 2f - textBounds.top

        // Draw text on the canvas
        canvas.drawText(text, x, y, paint)

        return image
    }
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


    fun saveCountry(activity: Context, value: String?) {
        val sharedpreferences = activity.getSharedPreferences("Country", Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()
        editor.putString("CountryValue", value)
        editor.apply()
    }

    fun getCountry(activity: Context): String? {
        val sharedpreferences = activity.getSharedPreferences("Country", Context.MODE_PRIVATE)
        return sharedpreferences.getString("CountryValue", "")
    }

    fun saveAccessToken(activity: Context, value: String?) {
        val sharedpreferences = activity.getSharedPreferences("MyPREFERENCES2", Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()
        editor.putString("Value2", value)
        editor.apply()
    }

    fun getAccessToken(activity: Context): String? {
        val sharedpreferences = activity.getSharedPreferences("MyPREFERENCES2", Context.MODE_PRIVATE)
        return sharedpreferences.getString("Value2", "")
    }

  fun saveExtension(activity: Context, value: String?) {
        val sharedpreferences = activity.getSharedPreferences("Extenction", Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()
        editor.putString("Value3", value)
        editor.apply()
    }

    fun getExtension(activity: Context): String? {
        val sharedpreferences = activity.getSharedPreferences("Extenction", Context.MODE_PRIVATE)
        return sharedpreferences.getString("Value3", "")
    }
    fun saveExtension_password(activity: Context, value: String?) {
        val sharedpreferences = activity.getSharedPreferences("extension_password", Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()
        editor.putString("Value4", value)
        editor.apply()
    }

    fun getExtension_password(activity: Context): String? {
        val sharedpreferences = activity.getSharedPreferences("extension_password", Context.MODE_PRIVATE)
        return sharedpreferences.getString("Value4", "")
    }



    fun saveNumber(activity: Context, value: String?) {
        val sharedpreferences = activity.getSharedPreferences("Number", Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()
        editor.putString("Phone", value)
        editor.apply()
    }
    fun saveExtensionNumber(activity: Context, value: String?) {
        val sharedpreferences = activity.getSharedPreferences("EXNumber", Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()
        editor.putString("EXPhone", value)
        editor.apply()
    }
    fun getExtensionNumber(activity: Context): String? {
        val sharedpreferences = activity.getSharedPreferences("EXNumber", Context.MODE_PRIVATE)
        return sharedpreferences.getString("EXPhone", "")
    }
    fun getOTP(activity: Context): String? {
        val sharedpreferences = activity.getSharedPreferences("OTP", Context.MODE_PRIVATE)
        return sharedpreferences.getString("V_OTP", "")
    }

    fun saveOTP(activity: Context, value: String?) {
        val sharedpreferences = activity.getSharedPreferences("OTP", Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()
        editor.putString("V_OTP", value)
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

    fun setUserData(context: Context?, str: UserModel?) {
        val gson = Gson()
        val json = gson.toJson(str)
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        edit = preferences.edit()
        edit?.putString("MyUser", json)
        edit?.apply()
    }

    fun getUserData(context: Context?): UserModel? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        val json = preferences.getString("MyUser", "")
        return gson.fromJson(json, UserModel::class.java)
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

    fun getFirstHandler(c1: Context): Boolean {
        val sharedpreferences =
            c1.getSharedPreferences("MyPREFERENCES22", Context.MODE_PRIVATE)
        return sharedpreferences.getBoolean("Register22", false)
    }

    fun setFirstHandler(c1: Context, value: Boolean) {
        val sharedpreferences = c1.getSharedPreferences("MyPREFERENCES22", Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()
        editor.putBoolean("Register22", value)
        editor.apply()
    }

    fun setFavoritesContact(c1: Context,str: ArrayList<BusinessResponce>?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c1)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(str)
        editor.putString("Favorites", json)
        editor.apply()
    }

    fun getFavoritesContact(c1: Context):ArrayList<BusinessResponce>? {
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(c1)
        val gson = Gson()
        val json = sharedPreferences.getString("Favorites", null)
        val type: Type = object : TypeToken<ArrayList<BusinessResponce>?>() {}.type
        return gson.fromJson<ArrayList<BusinessResponce>>(json, type)
    }

}