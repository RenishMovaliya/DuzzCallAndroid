package com.logycraft.duzzcalll.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.logycraft.duzzcalll.Adapter.Business_Contact_Adapter
import com.logycraft.duzzcalll.Adapter.Personal_Contact_Adapter
import com.logycraft.duzzcalll.R
import kotlinx.android.synthetic.main.fragment_contact.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ContactFragment : Fragment() {

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

        val adapter = Personal_Contact_Adapter(activity)
        recyclerview.setLayoutManager(LinearLayoutManager(activity))
        recyclerview.setAdapter(adapter)

        relative_personal.setOnClickListener(View.OnClickListener {

            relative_selected_btn.animate().x(0f).duration = 100
            btnAdd.visibility=View.VISIBLE
            btn_country_select.visibility=View.GONE

            val adapter = Personal_Contact_Adapter(activity)
            recyclerview.setLayoutManager(LinearLayoutManager(activity))
            recyclerview.setAdapter(adapter)

        })
        relative_business.setOnClickListener(View.OnClickListener {

            btnAdd.visibility=View.GONE
            btn_country_select.visibility=View.VISIBLE
            val size: Int = relative_personal.getWidth()

            relative_selected_btn.animate().x(size.toFloat()).duration = 100
            val adapter = Business_Contact_Adapter(activity)
            recyclerview.setLayoutManager(LinearLayoutManager(activity))
            recyclerview.setAdapter(adapter)

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