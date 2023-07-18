package com.logycraft.duzzcalll.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.logycraft.duzzcalll.Activity.LoginScreen
import com.duzzcall.duzzcall.R
import com.logycraft.duzzcalll.Util.Preference
import com.duzzcall.duzzcall.databinding.FragmentContactBinding
import com.duzzcall.duzzcall.databinding.FragmentDialBinding
import com.duzzcall.duzzcall.databinding.FragmentSettingBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SettingFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.llLogout.setOnClickListener {
            val dialog =
                activity?.let { it1 -> Dialog(it1, android.R.style.Theme_Light_NoTitleBar_Fullscreen) }
            dialog?.setContentView(R.layout.logout_dialog)
            dialog?.setCancelable(false)
            dialog?.window!!.setBackgroundDrawable(ColorDrawable(android.R.color.transparent))

            val cv_logout = dialog?.findViewById<CardView>(R.id.cv_logout)
            val cv_cancel = dialog?.findViewById<CardView>(R.id.cv_cancel)
            cv_logout?.setOnClickListener {
                activity?.let { it1 -> Preference.setFirstUser(it1, false) }
                val intent = Intent(
                    activity, LoginScreen::class.java
                )
                startActivity(intent)
                dialog?.dismiss()
            }
            cv_cancel?.setOnClickListener { dialog.dismiss() }
            dialog?.show()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }
}