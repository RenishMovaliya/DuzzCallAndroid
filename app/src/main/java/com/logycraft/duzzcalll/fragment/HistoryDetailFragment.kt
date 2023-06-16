package com.logycraft.duzzcalll.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.logycraft.duzzcalll.Adapter.All_History_Adapter
import com.logycraft.duzzcalll.Adapter.HistoryDetails_Adapter
import com.logycraft.duzzcalll.R
import com.logycraft.duzzcalll.Terms_And_ConditionActivity
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_history.recyclerview
import kotlinx.android.synthetic.main.fragment_history_details.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HistoryDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_history_details, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val adapter = activity?.let { HistoryDetails_Adapter(it) }
        recyclerview.adapter = adapter

        onBackClick.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                activity?.supportFragmentManager!!.popBackStack()
            }

        })

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