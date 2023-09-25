package com.logycraft.duzzcalll.fragment

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import com.logycraft.duzzcalll.Util.ValidationUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private var param1: String? = null
    private var param2: String? = null
    private val CAMERA_REQUEST_CODE = 101
    var userModel = UserModel()
    var imguri = ""

    private val REQUEST_TAKE_PHOTO = 101
    private val REQUEST_PICK_PHOTO = 102
    private var photoURI: Uri? = null

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


        val paths = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "DuzzCall/ProfilePhoto/duzz_profile_img.jpg"
        )
        if (paths.exists()) {
            val imageUri = Uri.parse("file://$paths")
            binding.profileImage.setImageURI(imageUri)
            binding.updateProfileImage.setImageURI(imageUri)
        } else {
            binding.profileImage.setImageResource(R.drawable.ic_profile_image)
            binding.updateProfileImage.setImageResource(R.drawable.ic_profile_image)
        }


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
//                checkForPermission()
                openGallery()
                dialog.dismiss()
            }

            ll_camera?.setOnClickListener {

                if (activity?.let {
                        ContextCompat.checkSelfPermission(
                            it, android.Manifest.permission.CAMERA
                        )
                    } != PackageManager.PERMISSION_GRANTED) {
                    activity?.let {
                        ActivityCompat.requestPermissions(
                            it, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE
                        )
                    }
                } else {
                    openCameraAndSavePhoto()
                }
                dialog.dismiss()
            }

            dialog?.show()

        }

        binding.btnUpdate.setOnClickListener {

            if (isValidate() && isUsernameValid(binding.etName.text.toString())) {
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

        }


        binding.updateImgCamera.setOnClickListener {
            binding.imgCamera.performClick();
        }

    }


    private fun getImageFileUri(): Uri? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                createUriAfterQ()
            } else {
                createUriForPreQ()
            }
        } catch (e: Exception) {
            Log.e("sdsdsd", "Error occurred while creating the Uri: ${e.localizedMessage}")
            null
        }
    }

    @Throws(IOException::class)
    private fun createUriForPreQ(): Uri? {
        val customDirectory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "DuzzCall/ProfilePhoto"
        )

        if (!customDirectory.exists()) {
            customDirectory.mkdirs()
        }

        clearDirectory(
            customDirectory
        )
//        val storageDir = File(
//            "DuzzCall/ProfilePhoto/"
//        )

        val mFile = File.createTempFile(
            "duzz_profile_img",
            ".jpg",
            customDirectory
        )
        return FileProvider.getUriForFile(
            requireContext(), getString(R.string.file_provider_authorities), mFile
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createUriAfterQ(): Uri? {
        val customDirectory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "DuzzCall/ProfilePhoto"
        )

        if (!customDirectory.exists()) {
            customDirectory.mkdirs()
        }

        clearDirectory(
            customDirectory
        )
        val fileName = "duzz_profile_img.jpg"
        val storageDir = File(
            "DuzzCall/ProfilePhoto/"
        )
        val resolver = requireContext().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + File.separator + storageDir
            )
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        return uri
    }

    fun clearDirectory(directory: File) {
        if (directory.isDirectory) {

            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isDirectory) {
                        clearDirectory(file)
                    } else {
                        file.delete()
                    }
                }
            }
        }
        directory.delete()
    }

    private fun openCameraAndSavePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                photoURI = getImageFileUri()
                photoURI?.let {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, it)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }


    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_PICK_PHOTO)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_TAKE_PHOTO -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Log.e("aaaa", "User confirm taken photo")
                        // Show uri in image view
                        binding.profileImage.setImageURI(photoURI)
                        binding.updateProfileImage.setImageURI(photoURI)
                    }
                    Activity.RESULT_CANCELED -> {
                        Log.e("aaaa", "User denied taken photo")
                    }
                }
            }
            REQUEST_PICK_PHOTO -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Log.e("aaaa", "User confirm pick photo")

                        val contentURI = data?.data
                        contentURI?.let {
                            binding.profileImage.setImageURI(it)
                            binding.updateProfileImage.setImageURI(it)

                            val fileName = "duzz_profile_img.jpg"

                            val storageDir = File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                "DuzzCall/ProfilePhoto"
                            )
                            val selectimgpath = getRealPathFromURI(contentURI)

                            val destFile = File(storageDir, fileName)
                            selectimgpath?.let { it1 -> copyFile(it1, destFile.path) }

                        }
                    }
                    Activity.RESULT_CANCELED -> {
                        Log.e("aaaa", "User denied pick photo")

                    }
                }
            }
        }

    }

    private fun isValidate(): Boolean {

        val email = binding.etEmail.text.toString().trim()

        when {
            TextUtils.isEmpty(email) -> {
                showError(getString(R.string.enter_email_address))
                return false
            }
            !ValidationUtils.isValidEmail(email) -> {
                showError(getString(R.string.invalid_email_address))
                return false
            }

            else -> {
                return true
            }
        }
    }

    fun isUsernameValid(username: String): Boolean {
        val regex = Regex("^[A-Za-z]+\\s[A-Za-z]+$")

        if (regex.matches(username)) {
            return regex.matches(username)

        } else {
            Toast.makeText(activity, "Invalid Name", Toast.LENGTH_LONG).show()
        }
        return regex.matches(username)
    }

    fun showError(message: String?) {
        Toast.makeText(activity, "$message", Toast.LENGTH_LONG).show()
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
            Dexter.withActivity(activity).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        openGallery()
                    }

                    if (report.isAnyPermissionPermanentlyDenied) {
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
                    permissions: List<PermissionRequest>, token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).onSameThread().check()
        } else {
            Dexter.withActivity(activity).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        openGallery()
                    }

                    if (report.isAnyPermissionPermanentlyDenied) {
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
                    permissions: List<PermissionRequest>, token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).onSameThread().check()
        }
    }


    private fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity?.contentResolver?.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            return it.getString(columnIndex)
        }
        return null
    }

    @Throws(IOException::class)
    private fun copyFile(sourcePath: String, destPath: String) {
        val sourceFile = File(sourcePath)
        val destFile = File(destPath)
        val sourceStream = sourceFile.inputStream()
        val destStream = FileOutputStream(destFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (sourceStream.read(buffer).also { length = it } > 0) {
            destStream.write(buffer, 0, length)
        }
        sourceStream.close()
        destStream.close()
    }

}