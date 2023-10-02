package com.logycraft.duzzcalll.fragment

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.duzzcall.duzzcall.databinding.FragmentSettingBinding
import com.example.restapiidemo.home.data.UserModel
import com.logycraft.duzzcalll.Activity.LoginScreen
import com.logycraft.duzzcalll.LinphoneManager
import com.logycraft.duzzcalll.Util.Preference
import java.io.File

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

        val customDirectory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "DuzzCall/ProfilePhoto"
        )

        if (!customDirectory.exists()) {
            customDirectory.mkdirs()
        }
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_IMAGES
            ),
            101
        )

        binding.llLogout.setOnClickListener {
            val dialog = activity?.let { it1 ->
                Dialog(
                    it1, android.R.style.Theme_Light_NoTitleBar_Fullscreen
                )
            }
            dialog?.setContentView(com.duzzcall.duzzcall.R.layout.logout_dialog)
            dialog?.setCancelable(false)
            dialog?.window!!.setBackgroundDrawable(ColorDrawable(android.R.color.transparent))

            val cv_logout = dialog?.findViewById<CardView>(com.duzzcall.duzzcall.R.id.cv_logout)
            val cv_cancel = dialog?.findViewById<CardView>(com.duzzcall.duzzcall.R.id.cv_cancel)
            cv_logout?.setOnClickListener {
                activity?.let { it1 -> Preference.setFirstUser(it1, false) }
                val linphoneCore = LinphoneManager.getCore()

                linphoneCore.setDefaultProxyConfig(null);
                linphoneCore.clearAllAuthInfo();
                linphoneCore.clearProxyConfig();
                linphoneCore.clearCallLogs()
                val intent = Intent(
                    activity, LoginScreen::class.java
                )
                startActivity(intent)

                dialog?.dismiss()
            }
            cv_cancel?.setOnClickListener { dialog.dismiss() }
            dialog?.show()
        }

        val paths = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "DuzzCall/ProfilePhoto/duzz_profile_img.jpg"
        )

        if (paths.exists()) {
            val imageUri = Uri.parse("file://$paths")
            binding.profileImage.setImageURI(imageUri)
        } else {
            binding.profileImage.setImageResource(com.duzzcall.duzzcall.R.drawable.ic_profile_image)
        }

        val readImagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            android.Manifest.permission.READ_MEDIA_IMAGES
        else
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE

        binding.linAccountsetting.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                android.Manifest.permission.READ_MEDIA_IMAGES
                activity?.let { it1 ->
                    ActivityCompat.requestPermissions(
                        it1,
                        arrayOf(
                            android.Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        1
                    )
                }
            } else {
                activity?.let { it1 ->
                    ActivityCompat.requestPermissions(
                        it1,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        101
                    )
                }
            }
            if (activity?.let { it1 ->
                    ContextCompat.checkSelfPermission(
                        it1,
                        readImagePermission
                    )
                } == PackageManager.PERMISSION_GRANTED) {
                val newFragment: Fragment = ProfileFragment()
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.add(com.duzzcall.duzzcall.R.id.fragment_container, newFragment)
                transaction?.addToBackStack(null)
                transaction?.commit()

            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    android.Manifest.permission.READ_MEDIA_IMAGES
                    activity?.let { it1 ->
                        ActivityCompat.requestPermissions(
                            it1,
                            arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                            101
                        )
                    }
                } else {
                    activity?.let { it1 ->
                        ActivityCompat.requestPermissions(
                            it1,
                            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            101
                        )
                    }
                }
            }
        }

        if (Preference.getUserData(activity) != null) {
            userModel = Preference.getUserData(activity)!!
        }

        if (userModel.first_name.equals(" ")) {
            binding.tvProfileName.setText("John Doi")
        } else {
            binding.tvProfileName.setText(userModel.first_name + " " + userModel.last_name)
        }

//        if (userModel.profileimg==null && userModel.profileimg.equals("") && userModel.profileimg?.isEmpty()!!){
//            binding.profileImage.setImageResource(com.duzzcall.duzzcall.R.drawable.ic_profile_image)
//        }else{
//            binding.profileImage.setImageURI(Uri.parse(userModel.profileimg))
//
//        }


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