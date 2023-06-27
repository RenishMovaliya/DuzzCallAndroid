package com.logycraft.duzzcalll.fragment

import android.Manifest.permission.READ_CONTACTS
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.database.getStringOrNull
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.logycraft.duzzcalll.Adapter.Business_Contact_Adapter
import com.logycraft.duzzcalll.Adapter.Personal_Contact_Adapter
import com.logycraft.duzzcalll.Model.ContactModel
import com.logycraft.duzzcalll.R
import kotlinx.android.synthetic.main.fragment_contact.*
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ContactFragment : Fragment() {
    private val READ_CONTACTS_PERMISSION_CODE = 1

    private lateinit var recyclerViewContacts: RecyclerView
    private val contactList: MutableList<ContactModel> = mutableListOf()
    lateinit var contactdapter: Personal_Contact_Adapter;
    private var listSupplier: ArrayList<ContactModel> = ArrayList()

    private var param1: String? = null
    private var param2: String? = null


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
        val view: View = inflater.inflate(R.layout.fragment_contact, container, false)



        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        contactdapter = Personal_Contact_Adapter(activity, false, contactList)
        recyclerview.setLayoutManager(LinearLayoutManager(activity))
        recyclerview.setAdapter(contactdapter)
        titleTV.setText(getString(R.string.contacts))



        relative_personal.setOnClickListener(View.OnClickListener {

            relative_selected_btn.animate().x(0f).duration = 100
            btnAdd.visibility = View.VISIBLE
            btn_country_select.visibility = View.GONE

            val adapter = Personal_Contact_Adapter(activity, false, contactList)
            recyclerview.setLayoutManager(LinearLayoutManager(activity))
            recyclerview.setAdapter(adapter)

        })
        relative_business.setOnClickListener(View.OnClickListener {

            btnAdd.visibility = View.GONE
            btn_country_select.visibility = View.VISIBLE
            val size: Int = relative_personal.getWidth()

            relative_selected_btn.animate().x(size.toFloat()).duration = 100
            val adapter = Business_Contact_Adapter(activity, false)
            recyclerview.setLayoutManager(LinearLayoutManager(activity))
            recyclerview.setAdapter(adapter)

        })


        if (activity?.let {
                ContextCompat.checkSelfPermission(
                    it, android.Manifest.permission.READ_CONTACTS
                )
            } != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(android.Manifest.permission.READ_CONTACTS),
                    READ_CONTACTS_PERMISSION_CODE
                )
            }
        } else {
            // Permission already granted, fetch the contact list
            fetchContactList()
        }


        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {

                filterData()
                setDataInList()
            }
        })


    }

    private fun setDataInList() {

//        contactdapter = Personal_Contact_Adapter(activity, false, contactList)
//        recyclerview.setAdapter(contactdapter)
        val uniqueContacts = contactList.distinctBy { it.name }
        contactdapter.setContacts(uniqueContacts)
    }

    private fun filterData() {

        contactList.clear()
        if (et_search.text.toString().isEmpty()) {
            contactList.addAll(listSupplier)
        } else {
            for (ClientsDetail in listSupplier) {
                if (ClientsDetail.name.toString().toLowerCase(Locale.getDefault()).contains(
                        et_search.text.toString().toLowerCase(Locale.getDefault())
                    ) || ClientsDetail.number.toString().toLowerCase(Locale.getDefault())
                        .contains(et_search.text.toString().toLowerCase(Locale.getDefault()))
                ) {
                    contactList.add(ClientsDetail)
                }
            }
            val uniqueContacts = contactList.distinctBy { it.name }
            contactdapter.setContacts(uniqueContacts)
        }

    }


    private fun fetchContactList() {
        val cursor = activity?.getContentResolver()?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        var currentGroup = ""
        cursor?.let {
            while (it.moveToNext()) {
                val name =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val imageUriString: String? =
                    cursor.getString(it.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))
                val imageUri: Uri? = imageUriString?.let { Uri.parse(it) }

                if (!name.isNullOrEmpty() && !number.isNullOrEmpty()) {
                    val contact = ContactModel(name, number, imageUri)
                    val groupName = name[0].toString().toUpperCase()

                    if (groupName != currentGroup) {
                        currentGroup = groupName
                        contactList.add(ContactModel(groupName, "", imageUri))
                    }

                    contactList.add(contact)
                    listSupplier.add(contact)
                }
            }
            it.close()
            contactdapter.notifyDataSetChanged()
        }
        val uniqueContacts = contactList.distinctBy { it.name }
        contactdapter.setContacts(uniqueContacts)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            READ_CONTACTS_PERMISSION_CODE -> {
                // If the request is cancelled, the grantResults array is empty
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, fetch the contact list
                    fetchContactList()
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