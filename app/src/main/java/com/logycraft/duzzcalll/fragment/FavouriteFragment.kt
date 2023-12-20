package com.logycraft.duzzcalll.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.logycraft.duzzcalll.Adapter.Business_Contact_Adapter
import com.logycraft.duzzcalll.Adapter.Personal_Contact_Adapter
import com.logycraft.duzzcalll.Model.ContactModel
import com.duzzcall.duzzcall.R
import com.duzzcall.duzzcall.databinding.FragmentContactBinding

import com.logycraft.duzzcalll.Activity.NewContactActivity
import com.logycraft.duzzcalll.Util.Preference
import com.logycraft.duzzcalll.data.BusinessResponce
import com.logycraft.duzzcalll.helper.CallBackListener

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FavouriteFragment : Fragment() {
    private lateinit var binding: FragmentContactBinding
    private var param1: String? = null
    private var param2: String? = null
    var favoritesContactlist: ArrayList<BusinessResponce>? = ArrayList()
    private var callBackListener: CallBackListener? = null


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

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //getActivity() is fully created in onActivityCreated and instanceOf differentiate it between different Activities
        if (activity is CallBackListener) callBackListener = activity as CallBackListener?
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (activity?.let { Preference.getFirstUser(it) } != null) {
            favoritesContactlist = activity?.let { Preference.getFavoritesContact(it) }

        }

        val adapter = Personal_Contact_Adapter(activity,
            true,
            favoritesContactlist,
            object : Personal_Contact_Adapter.OnItemClickListener {
                override fun onClick(call: BusinessResponce) {
                    if (!call.lineExtension.toString().isEmpty()) {
                        callBackListener?.onCallBack(
                            call.lineExtension.toString(), call.businessName.toString(), call.businessLogo.toString()
                        );
                    }
                }

            })
        binding.recyclerview.setLayoutManager(LinearLayoutManager(activity))
        binding.recyclerview.setAdapter(adapter)

        binding.titleTV.setText(getString(R.string.favourited))
        binding.llSearch.visibility = View.GONE
        binding.viewFavorite.visibility = View.VISIBLE
        binding.tabbar.visibility = View.GONE

        binding.relativePersonal.setOnClickListener(View.OnClickListener {

            binding.relativeSelectedBtn.animate().x(0f).duration = 100
            binding.btnAdd.visibility = View.VISIBLE
            binding.btnCountrySelect.visibility = View.GONE

//            val adapter = Personal_Contact_Adapter(activity, true, favoritesContactlist,)
//            binding.recyclerview.setLayoutManager(LinearLayoutManager(activity))
//            binding.recyclerview.setAdapter(adapter)


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