package com.logycraft.duzzcalll.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.duzzcall.duzzcall.BuildConfig
import com.duzzcall.duzzcall.R
import com.duzzcall.duzzcall.databinding.FragmentProfileBinding
import com.example.restapiidemo.home.data.UserModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.logycraft.duzzcalll.Util.Preference


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private var param1: String? = null
    private var param2: String? = null
    private val CAMERA_REQUEST_CODE = 101
    private val PICK_FROM_GALLERY = 1
    var userModel = UserModel()
    var imguri = ""

    private val PICK_IMAGE_REQUEST = 102

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
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rlProfile.visibility = View.GONE
        binding.imgBack.setOnClickListener {
            if (binding.rlProfile.visibility == View.VISIBLE) {
                binding.llAccountDetails.visibility = View.VISIBLE
                binding.rlProfile.visibility = View.GONE
            } else {
                activity?.supportFragmentManager?.popBackStack()
            }
        }
        updatedata()
        binding.rlUpdateProfile.visibility = View.GONE
        binding.rlMainProfile.visibility = View.VISIBLE

        binding.imgEdit.setOnClickListener {

            binding.rlMainProfile.visibility = View.GONE
            binding.rlUpdateProfile.visibility = View.VISIBLE

        }

        binding.profileImage.setOnClickListener {
            binding.llAccountDetails.visibility = View.GONE
            binding.rlProfile.visibility = View.VISIBLE
        }


//        Update Profilee======================
        binding.imgCamera.setOnClickListener {
            val dialogView: View = layoutInflater.inflate(R.layout.profile_img_dialog, null)
            val dialog = activity?.let { it1 -> BottomSheetDialog(it1) }
            dialog?.setContentView(dialogView)

            val img_item = dialog?.findViewById<ImageView>(R.id.img_delete)
            val ll_camera = dialog?.findViewById<LinearLayout>(R.id.ll_camera)
            val ll_gallery = dialog?.findViewById<LinearLayout>(R.id.ll_gallery)
            img_item?.setOnClickListener {
                dialog.dismiss()
            }
            ll_gallery?.setOnClickListener {
                checkForPermission()
                dialog.dismiss()
            }

            ll_camera?.setOnClickListener {
                if (activity?.let {
                        ContextCompat.checkSelfPermission(
                            it, android.Manifest.permission.CAMERA
                        )
                    } != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request it
                    activity?.let {
                        ActivityCompat.requestPermissions(
                            it,
                            arrayOf(android.Manifest.permission.CAMERA),
                            CAMERA_REQUEST_CODE
                        )
                    }
                } else {
                    openCamera()

                }
                dialog.dismiss()
            }

            dialog?.show()

        }

        binding.btnUpdate.setOnClickListener {
            val str = binding.etName.text.toString()
            val separated: List<String> = str.split(" ")
            userModel.first_name = separated[0]
            userModel.last_name = separated[1]
            userModel.email = binding.etEmail.text.toString()
            userModel.profileimg = imguri
            Preference.setUserData(activity, userModel)

            updatedata()
            binding.rlMainProfile.visibility = View.VISIBLE
            binding.rlUpdateProfile.visibility = View.GONE
        }


        binding.updateImgCamera.setOnClickListener {
            val dialogView: View = layoutInflater.inflate(R.layout.profile_img_dialog, null)
            val dialog = activity?.let { it1 -> BottomSheetDialog(it1) }
            dialog?.setContentView(dialogView)

            val img_item = dialog?.findViewById<ImageView>(R.id.img_delete)
            val ll_camera = dialog?.findViewById<LinearLayout>(R.id.ll_camera)
            val ll_gallery = dialog?.findViewById<LinearLayout>(R.id.ll_gallery)


            ll_camera?.setOnClickListener {
                if (activity?.let {
                        ContextCompat.checkSelfPermission(
                            it, android.Manifest.permission.CAMERA
                        )
                    } != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request it
                    activity?.let {
                        ActivityCompat.requestPermissions(
                            it,
                            arrayOf(android.Manifest.permission.CAMERA),
                            CAMERA_REQUEST_CODE
                        )
                    }
                } else {
                    openCamera()

                }
                dialog.dismiss()
            }

            ll_gallery?.setOnClickListener {
                checkForPermission()
                dialog.dismiss()

            }


            img_item?.setOnClickListener {
                dialog.dismiss()
            }
            dialog?.show()
        }

    }

    private fun openGallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    private fun openCamera() {
        val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    // Handle the captured image from the camera
                    val photo: Bitmap = data?.getExtras()?.get("data") as Bitmap

                    binding.updateProfileImage.setImageBitmap(photo)
                    val tempUri = activity?.let { getImageUri(it, photo) }
                    imguri = tempUri.toString()
                    binding.profileImage.setImageURI(tempUri)
                    userModel.profileimg = imguri
                    Preference.setUserData(activity, userModel)
                    // Now you can use imageUri to display or process the captured image
                }
                PICK_IMAGE_REQUEST -> {
                    // Handle the picked image from the gallery
                    val selectedImageUri: Uri? = data?.data
                    imguri = selectedImageUri.toString()
                    binding.profileImage.setImageURI(selectedImageUri)
                    binding.updateProfileImage.setImageURI(selectedImageUri)
                    userModel.profileimg = imguri
                    Preference.setUserData(activity, userModel)

                }
            }
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap?): Uri? {
        val OutImage = Bitmap.createScaledBitmap(inImage!!, 1000, 1000, true)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            OutImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    fun updatedata() {

        userModel = Preference.getUserData(activity)!!
        if (userModel != null) {
            binding.tvName.setText(userModel.first_name + " " + userModel.last_name)
            binding.tvPhone.setText(userModel.phone)
            binding.tvEmail.setText(userModel.email)
//            if (userModel.profileimg.equals("")){
//                binding.profileImage.setImageResource(com.duzzcall.duzzcall.R.drawable.ic_profile_image)
//            }else{
//                binding.profileImage.setImageURI(Uri.parse(userModel.profileimg))
//            }
            binding.etName.setText(userModel.first_name + " " + userModel.last_name)
            binding.etEmail.setText(userModel.email)
        }
    }

    private fun checkForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Dexter.withActivity(activity)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            openGallery()
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied) {
                            // permission is denied parmenantly, navigate user to app settings
                            Toast.makeText(
                                activity,
                                "Please grant camera and external storage permission.",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                })
                .onSameThread()
                .check()
        } else {
            Dexter.withActivity(activity)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            openGallery()
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied) {
                            // permission is denied parmenantly, navigate user to app settings
                            Toast.makeText(
                                activity,
                                "Please grant camera and external storage permission.",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                })
                .onSameThread()
                .check()
        }
    }

    fun onBackPressed() {
        if (binding.rlUpdateProfile.visibility == View.VISIBLE) {
            binding.rlUpdateProfile.visibility = View.GONE
            binding.rlMainProfile.visibility = View.VISIBLE
        }
    }


}