package com.logycraft.duzzcalll.Util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import java.util.prefs.Preferences

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    fun showMessage(message: String?) {
        Toast.makeText(this, "$message", Toast.LENGTH_LONG).show()
    }

    fun showError(message: String?) {
        showMessage(message)
    }
    fun showDialogOk(
        context: Context?,
        title: String?,
        message: String?,
        positiveBtn: String?,
    ) {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
        alertDialogBuilder
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(positiveBtn, { dialog, id -> dialog.dismiss() })
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

}