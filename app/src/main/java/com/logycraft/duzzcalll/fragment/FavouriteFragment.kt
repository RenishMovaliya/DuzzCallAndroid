package com.logycraft.duzzcalll.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.logycraft.duzzcalll.Adapter.Business_Contact_Adapter
import com.logycraft.duzzcalll.Adapter.Personal_Contact_Adapter
import com.logycraft.duzzcalll.Model.ContactModel
import com.duzzcall.duzzcall.R
import com.duzzcall.duzzcall.databinding.FragmentContactBinding
import com.duzzcall.duzzcall.databinding.FragmentSettingBinding
import com.logycraft.duzzcalll.Activity.NewContactActivity
import com.logycraft.duzzcalll.Activity.Verify_PhoneActivity

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FavouriteFragment : Fragment() {
    private lateinit var binding: FragmentContactBinding
    private var param1: String? = null
    private var param2: String? = null

    private val contactList: MutableList<ContactModel> = mutableListOf()

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
        binding = FragmentContactBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val adapter = Personal_Contact_Adapter(activity, true, contactList)
         binding.recyclerview.setLayoutManager(LinearLayoutManager(activity))
         binding.recyclerview.setAdapter(adapter)

        binding.titleTV.setText(getString(R.string.favourited))
        binding.llSearch.visibility=View.GONE
        binding.tabbar.visibility=View.GONE

        binding.relativePersonal.setOnClickListener(View.OnClickListener {

            binding.relativeSelectedBtn.animate().x(0f).duration = 100
            binding.btnAdd.visibility = View.VISIBLE
            binding.btnCountrySelect.visibility = View.GONE

            val adapter = Personal_Contact_Adapter(activity, true, contactList)
            binding.recyclerview.setLayoutManager(LinearLayoutManager(activity))
            binding.recyclerview.setAdapter(adapter)


        })
        binding.btnAdd.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, NewContactActivity::class.java)
            startActivity(intent)
        })
        binding.relativeBusiness.setOnClickListener(View.OnClickListener {

            binding.btnAdd.visibility = View.GONE
            binding.btnCountrySelect.visibility = View.VISIBLE
            val size: Int = binding.relativePersonal.getWidth()

            binding.relativeSelectedBtn.animate().x(size.toFloat()).duration = 100
            val adapter = Business_Contact_Adapter(activity, false)
            binding.recyclerview.setLayoutManager(LinearLayoutManager(activity))
            binding.recyclerview.setAdapter(adapter)
            binding.btnCountrySelect.setOnClickListener(View.OnClickListener {
                binding.countryCPP.launchCountrySelectionDialog()

            })
            binding.flagimg.setImageResource(binding.countryCPP.selectedCountryFlagResourceId)
            binding.countryCPP.setOnCountryChangeListener {
                binding.flagimg.setImageResource(binding.countryCPP.selectedCountryFlagResourceId)
            }

        })

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