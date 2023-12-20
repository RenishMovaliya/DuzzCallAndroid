package com.logycraft.duzzcalll.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.logycraft.duzzcalll.Model.ContactModel
import com.duzzcall.duzzcall.R
import com.duzzcall.duzzcall.databinding.FragmentContactBinding
import com.logycraft.duzzcalll.Adapter.BusinessContact_Adapter
import com.logycraft.duzzcalll.Util.ProgressHelper
import com.logycraft.duzzcalll.data.BusinessResponce
import com.logycraft.duzzcalll.helper.CallBackListener
import com.logycraft.duzzcalll.viewmodel.HomeViewModel
import okhttp3.ResponseBody
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ContactFragment : Fragment() {
    private val READ_CONTACTS_PERMISSION_CODE = 1
    private lateinit var binding: FragmentContactBinding
    private lateinit var recyclerViewContacts: RecyclerView
    private val contactList: MutableList<ContactModel> = mutableListOf()

    //    lateinit var contactdapter: Personal_Contact_Adapter;
    private var listSupplier: ArrayList<ContactModel> = ArrayList()
    var businessresponce: ArrayList<BusinessResponce> = ArrayList()
    var businesdata: MutableList<BusinessResponce> = mutableListOf()

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: HomeViewModel
    private var callBackListener: CallBackListener? = null

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
        // Inflate the layout for this fragment
//        binding = FragmentContactBinding.inflate(layoutInflater)
//        val view: View = inflater.inflate(binding, container, false)

        binding = FragmentContactBinding.inflate(inflater, container, false);
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        return binding.getRoot();

//        return view;
    }

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //getActivity() is fully created in onActivityCreated and instanceOf differentiate it between different Activities
        if (activity is CallBackListener) callBackListener = activity as CallBackListener?
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

//        contactdapter = Personal_Contact_Adapter(activity, false, contactList)
//        binding.recyclerview.setLayoutManager(LinearLayoutManager(activity))
//        binding.recyclerview.setAdapter(contactdapter)
        binding.titleTV.setText(getString(R.string.contacts))



        binding.relativePersonal.setOnClickListener(View.OnClickListener {

            binding.relativeSelectedBtn.animate().x(0f).duration = 100
            binding.btnAdd.visibility = View.VISIBLE
            binding.btnCountrySelect.visibility = View.GONE

//            val adapter = Personal_Contact_Adapter(activity, false, contactList)
//            binding.recyclerview.setLayoutManager(LinearLayoutManager(activity))
//            binding.recyclerview.setAdapter(adapter)

        })
        binding.relativeBusiness.setOnClickListener(View.OnClickListener {

            binding.btnAdd.visibility = View.GONE
            binding.btnCountrySelect.visibility = View.VISIBLE
            val size: Int = binding.relativePersonal.getWidth()

            binding.relativeSelectedBtn.animate().x(size.toFloat()).duration = 100
            getbusinessList()

            binding.btnCountrySelect.setOnClickListener(View.OnClickListener {
                binding.countryCPP.launchCountrySelectionDialog()

            })
            binding.flagimg.setImageResource(binding.countryCPP.selectedCountryFlagResourceId)
            binding.countryCPP.setOnCountryChangeListener {
                binding.flagimg.setImageResource(binding.countryCPP.selectedCountryFlagResourceId)
            }

        })

        binding.relativeBusiness.performClick()
//        if (activity?.let {
//                ContextCompat.checkSelfPermission(
//                    it, android.Manifest.permission.READ_CONTACTS
//                )
//            } != PackageManager.PERMISSION_GRANTED) {
//            // Permission is not granted, request it
//            activity?.let {
//                ActivityCompat.requestPermissions(
//                    it,
//                    arrayOf(android.Manifest.permission.READ_CONTACTS),
//                    READ_CONTACTS_PERMISSION_CODE
//                )
//            }
//        } else {
//            // Permission already granted, fetch the contact list
////            fetchContactList()
//        }


        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {

                filterData()
                recyclerview(businessresponce)
            }
        })


    }

    private fun getbusinessList() {


        activity?.let { viewModel.getBusiness(it) }

        activity?.let {
            viewModel.getbusinessLiveData?.observe(it, androidx.lifecycle.Observer {

                if (it.isSuccess == true && it.Responcecode == 200) {
                    ProgressHelper.dismissProgressDialog()

                    it.data?.let { it1 -> businessresponce.addAll(it1) }
                    it.data?.let { it1 -> businesdata.addAll(it1) }

                    recyclerview(businessresponce)


                    //                val `objecsst` = JSONObject(usedata.toString())
                    ////                showError("" + sendOtp.toString())
                    //                val intent = Intent(
                    //                    this@Verify_PhoneActivity, Terms_And_ConditionActivity::class.java
                    //                )
                    //                Preference.saveAccessToken(this@Verify_PhoneActivity,objecsst.getString("access_token"))
                    //                intent.putExtra("PASS", "NEW_PASS")
                    //                intent.putExtra("MOBILE", intent.getStringExtra("MOBILE"))
                    //                startActivity(intent)
                    //                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    //                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                } else if (it.error != null) {
                    ProgressHelper.dismissProgressDialog()
                    var errorResponce: ResponseBody = it.error
                    val jsonObj = JSONObject(errorResponce!!.charStream().readText())
                    showMessage(jsonObj.getString("errors"))
                } else {
                    ProgressHelper.dismissProgressDialog()
                    showMessage("Something Went Wrong!")
                }


            })
        }

    }

    private fun recyclerview(businessresponce: ArrayList<BusinessResponce>) {

        val adapter = businessresponce?.let { it1 ->
            BusinessContact_Adapter(activity,
                it1,
                false,
                object : BusinessContact_Adapter.OnItemClickListener {
                    override fun onClick(business: BusinessResponce) {
                        if (!business.lineExtension.toString().isEmpty()) {
                            callBackListener?.onCallBack(
                                business.lineExtension.toString(),
                                business.businessName.toString(),
                                business.businessLogo.toString()
                            );
                        }
                    }
                })
        }
        binding.recyclerview.setLayoutManager(LinearLayoutManager(activity))
        binding.recyclerview.setAdapter(adapter)

    }

    fun showMessage(message: String?) {
        Toast.makeText(activity, "$message", Toast.LENGTH_LONG).show()
    }


    private fun setDataInList() {

//        contactdapter = Personal_Contact_Adapter(activity, false, contactList)
//        recyclerview.setAdapter(contactdapter)
        val uniqueContacts = contactList.distinctBy { it.name }
//        contactdapter.setContacts(uniqueContacts)
    }


    private fun filterData() {
        businessresponce.clear()
//        if (etSearch.text.toString().isEmpty()) {
//            mendates.addAll(mendatesAll)
//        } else {
        for (item in businesdata) {
            if (item.businessName.toString().toLowerCase(Locale.getDefault()).contains(
                    binding.etSearch.text.toString().toLowerCase(Locale.getDefault())
                ) || item.lineExtension.toString().toLowerCase(Locale.getDefault()).contains(
                    binding.etSearch.text.toString().toLowerCase(Locale.getDefault())
                )
            ) {
                businessresponce.add(item)
            }
        }
        if (businessresponce.isEmpty()) {
            binding.recyclerview.visibility = View.GONE
        } else {
            binding.recyclerview.visibility = View.VISIBLE
        }
//        }
    }


//    private fun fetchContactList() {
//        val cursor = activity?.getContentResolver()?.query(
//            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//            null,
//            null,
//            null,
//            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
//        )
//        var currentGroup = ""
////        cursor?.let {
////            while (it.moveToNext()) {
////                val name =
////                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
////                val number =
////                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
////                val imageUriString: String? =
////                    cursor.getString(it.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))
////                val imageUri: Uri? = imageUriString?.let { Uri.parse(it) }
////
////                if (!name.isNullOrEmpty() && !number.isNullOrEmpty()) {
////                    val contact = ContactModel(name, number, imageUri)
////                    val groupName = name[0].toString().toUpperCase()
////
////                    if (groupName != currentGroup) {
////                        currentGroup = groupName
////                        contactList.add(ContactModel(groupName, "", imageUri))
////                    }
////
////                    contactList.add(contact)
////                    listSupplier.add(contact)
////                }
////            }
////            it.close()
////            contactdapter.notifyDataSetChanged()
//        }
//        val uniqueContacts = contactList.distinctBy { it.name }
//        contactdapter.setContacts(uniqueContacts)
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            READ_CONTACTS_PERMISSION_CODE -> {
                // If the request is cancelled, the grantResults array is empty
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, fetch the contact list
//                    fetchContactList()
                } else {
                    // Permission denied
                    println("Read contacts permission denied")
                }
                return
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) = ContactFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }
}