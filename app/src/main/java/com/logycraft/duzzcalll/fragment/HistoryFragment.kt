package com.logycraft.duzzcalll.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.duzzcall.duzzcall.R
import com.logycraft.duzzcalll.Adapter.All_History_Adapter
import com.duzzcall.duzzcall.databinding.FragmentHistoryBinding
import com.duzzcall.duzzcall.databinding.FragmentHistoryDetailsBinding
import com.logycraft.duzzcalll.Activity.NewContactActivity


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HistoryFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentHistoryBinding
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
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.relativeAll.setOnClickListener(View.OnClickListener {

            binding.relativeSelectedBtn.animate().x(0f).duration = 100
//                        val fragment = HomeFragment()
//                        val fragmentManager: FragmentManager = getSupportFragmentManager()
//                        val fragmentTransaction = fragmentManager.beginTransaction()
//                        fragmentTransaction.replace(R.id.app_container, fragment).commit()
            val adapter = activity?.let { All_History_Adapter(it,0) }
            adapter?.onItemClick = { string ->
                callDetailscreen(string)
            }
            binding.recyclerview.adapter = adapter


        })
        binding.btnAdd.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, NewContactActivity::class.java)
            startActivity(intent)
        })
        val adapter = activity?.let { All_History_Adapter(it,0) }
        adapter?.onItemClick = { string ->
            callDetailscreen(string)
        }
        binding.recyclerview.adapter = adapter
        binding.relativeMissed.setOnClickListener(View.OnClickListener {

            val size: Int =  binding.relativeAll.getWidth()
            binding.relativeSelectedBtn.animate().x(size.toFloat()).duration = 100
//                        val fragment = HomeFragment()
//                        val fragmentManager: FragmentManager = getSupportFragmentManager()
//                        val fragmentTransaction = fragmentManager.beginTransaction()
//                        fragmentTransaction.replace(R.id.app_container, fragment).commit()
            val adapter = activity?.let { All_History_Adapter(it,1) }
            adapter?.onItemClick = { string ->
                callDetailscreen(string)
            }
            binding.recyclerview.adapter = adapter


        })


    }

    private fun callDetailscreen(string: String) {
        val transaction   = activity?.supportFragmentManager!!.beginTransaction()
        transaction.replace(R.id.container, HistoryDetailFragment())
        transaction.commit()
        transaction.addToBackStack(null)
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