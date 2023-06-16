package com.logycraft.duzzcalll.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.logycraft.duzzcalll.Adapter.All_History_Adapter
import com.logycraft.duzzcalll.R
import com.logycraft.duzzcalll.Terms_And_ConditionActivity
import kotlinx.android.synthetic.main.fragment_history.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HistoryFragment : Fragment() {

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
        val view: View = inflater.inflate(com.logycraft.duzzcalll.R.layout.fragment_history, container, false)




        return view;
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        relative_all.setOnClickListener(View.OnClickListener {

            relative_selected_btn.animate().x(0f).duration = 100
//                        val fragment = HomeFragment()
//                        val fragmentManager: FragmentManager = getSupportFragmentManager()
//                        val fragmentTransaction = fragmentManager.beginTransaction()
//                        fragmentTransaction.replace(R.id.app_container, fragment).commit()
            val adapter = activity?.let { All_History_Adapter(it,0) }
            adapter?.onItemClick = { string ->
                callDetailscreen(string)
            }
            recyclerview.adapter = adapter


        })
        val adapter = activity?.let { All_History_Adapter(it,0) }
        adapter?.onItemClick = { string ->
            callDetailscreen(string)
        }
        recyclerview.adapter = adapter
        relative_missed.setOnClickListener(View.OnClickListener {

            val size: Int = relative_all.getWidth()
            relative_selected_btn.animate().x(size.toFloat()).duration = 100
//                        val fragment = HomeFragment()
//                        val fragmentManager: FragmentManager = getSupportFragmentManager()
//                        val fragmentTransaction = fragmentManager.beginTransaction()
//                        fragmentTransaction.replace(R.id.app_container, fragment).commit()
            val adapter = activity?.let { All_History_Adapter(it,1) }
            adapter?.onItemClick = { string ->
                callDetailscreen(string)
            }
            recyclerview.adapter = adapter


        })


    }

    private fun callDetailscreen(string: String) {
        val transaction   = activity?.supportFragmentManager!!.beginTransaction()
        transaction.replace(com.logycraft.duzzcalll.R.id.container, HistoryDetailFragment())
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