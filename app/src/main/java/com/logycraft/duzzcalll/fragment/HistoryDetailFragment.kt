package com.logycraft.duzzcalll.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.logycraft.duzzcalll.Adapter.HistoryDetails_Adapter
import com.duzzcall.duzzcall.R
import com.duzzcall.duzzcall.databinding.FragmentContactBinding
import com.duzzcall.duzzcall.databinding.FragmentHistoryDetailsBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HistoryDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentHistoryDetailsBinding
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

        binding = FragmentHistoryDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val adapter = activity?.let { HistoryDetails_Adapter(it) }
        binding.recyclerview.adapter = adapter

        binding.onBackClick.setOnClickListener(object : View.OnClickListener {
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