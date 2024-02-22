package com.logycraft.duzzcalll.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.duzzcall.duzzcall.R
import com.duzzcall.duzzcall.databinding.FragmentSettingBinding
import com.example.restapiidemo.home.data.UserModel
import com.logycraft.duzzcalll.Activity.HelpActivity
import com.logycraft.duzzcalll.Activity.LoginScreen
import com.logycraft.duzzcalll.LinphoneManager
import com.logycraft.duzzcalll.Util.Preference


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SettingFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentSettingBinding

    var userModel = UserModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.llLogout.setOnClickListener {
            val dialog = activity?.let { it1 ->
                Dialog(
                    it1, R.style.Theme_DuzzCalll
                )
            }
            dialog?.setContentView(com.duzzcall.duzzcall.R.layout.logout_dialog)
            dialog?.setCancelable(false)
            dialog?.window!!.setBackgroundDrawable(ColorDrawable(android.R.color.transparent))
//            dialog?.window!!.statusBarColor = activity?.let { it1 -> ContextCompat.getColor(it1, R.color.appcolour) }!!

            dialog?.window?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    it.decorView.systemUiVisibility = View.STATUS_BAR_VISIBLE
                }
            }
            val cv_logout = dialog?.findViewById<CardView>(com.duzzcall.duzzcall.R.id.cv_logout)
            val cv_cancel = dialog?.findViewById<CardView>(com.duzzcall.duzzcall.R.id.cv_cancel)
            cv_logout?.setOnClickListener {
                activity?.let { it1 -> Preference.setFirstUser(it1, false) }
                val linphoneCore = LinphoneManager.getCore()

                linphoneCore?.let {
                    it.terminateAllCalls()
                    it.clearProxyConfig()
                    it.clearAllAuthInfo()
                    it.clearCallLogs()
                    it.setDefaultProxyConfig(null)
                }

                val intent = Intent(
                    activity, LoginScreen::class.java
                )
                startActivity(intent)
                activity?.finish()

                dialog?.dismiss()
            }
            cv_cancel?.setOnClickListener { dialog.dismiss() }
            dialog?.show()
        }

        binding.linearHelp.setOnClickListener {
            val intent = Intent(activity, HelpActivity::class.java)
            startActivity(intent)
        }

        binding.linAccountsetting.setOnClickListener {

            val newFragment: Fragment = ProfileFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(com.duzzcall.duzzcall.R.id.fragment_container, newFragment)
            transaction?.addToBackStack(null)
            transaction?.commit()


        }

        if (Preference.getUserData(activity) != null) {
            userModel = Preference.getUserData(activity)!!
        }

        if (userModel.first_name.equals(" ")) {
            binding.tvProfileName.setText("John Doi")
            activity?.let {
                val firstLetter =binding.tvProfileName.text.toString()
                val textss = firstLetter?.get(0).toString()
                val bitmap = Preference.textToBitmap(textss, Color.parseColor("#2F80ED"))
                binding.profileImage.setImageBitmap(bitmap)
//                MaterialTextDrawable.with(it)
//                    .text(binding.tvProfileName.text.toString()?.substring(0, 2) ?: "DC")
//                    .into(binding.profileImage)
            }


        } else {
            binding.tvProfileName.setText(userModel.first_name + " " + userModel.last_name)
            val first = userModel.first_name.toString()
            val last = userModel.last_name.toString()
            val firstLetter = first[0]
            val second = last[0]
            val textss = firstLetter+""+second
            val bitmap = Preference.textToBitmap(textss, Color.parseColor("#2F80ED"))
            binding.profileImage.setImageBitmap(bitmap)
//            activity?.let {
//                MaterialTextDrawable.with(it)
//                    .colorMode(MaterialTextDrawable.MaterialColorMode.DARK)
//                    .text(userModel.first_name?.substring(0, 2) ?: "DC")
//                    .into(binding.profileImage)
//            }

        }


    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) = SettingFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }

    }
}