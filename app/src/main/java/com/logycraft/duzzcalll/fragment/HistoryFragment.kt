package com.logycraft.duzzcalll.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.duzzcall.duzzcall.R
import com.logycraft.duzzcalll.Adapter.All_History_Adapter
import com.duzzcall.duzzcall.databinding.FragmentHistoryBinding
import com.logycraft.duzzcalll.Activity.NewContactActivity
import com.logycraft.duzzcalll.LinphoneManager
import org.linphone.core.Call
import org.linphone.core.CallLog
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HistoryFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var calllog: Array<CallLog>
    lateinit var core: Array<CallLog>


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
        core = LinphoneManager.getCore().callLogs
        calllog = LinphoneManager.getCore().callLogs

        if (core != null) {
            val adapter = activity?.let { All_History_Adapter(it, core, "All") }
            adapter?.onItemClick = { string ->
                callDetailscreen(string)
            }
            binding.recyclerview.adapter = adapter

        }


        binding.relativeAll.setOnClickListener(View.OnClickListener {

            binding.relativeSelectedBtn.animate().x(0f).duration = 100
//                        val fragment = HomeFragment()
//                        val fragmentManager: FragmentManager = getSupportFragmentManager()
//                        val fragmentTransaction = fragmentManager.beginTransaction()
//                        fragmentTransaction.replace(R.id.app_container, fragment).commit()
            if (core != null) {
                val adapter = activity?.let { All_History_Adapter(it, core, "All") }
                adapter?.onItemClick = { string ->
                    callDetailscreen(string)
                }
                binding.recyclerview.adapter = adapter
            }


        })
        binding.btnAdd.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, NewContactActivity::class.java)
            startActivity(intent)
        })

        binding.relativeMissed.setOnClickListener(View.OnClickListener {

            val size: Int = binding.relativeAll.getWidth()
            binding.relativeSelectedBtn.animate().x(size.toFloat()).duration = 100
//                        val fragment = HomeFragment()
//                        val fragmentManager: FragmentManager = getSupportFragmentManager()
//                        val fragmentTransaction = fragmentManager.beginTransaction()
//                        fragmentTransaction.replace(R.id.app_container, fragment).commit()
            var missedcalllog: Array<CallLog> = emptyArray()
            if (core != null) {
                for (calllog in core) {
                    if (calllog.getDir() == Call.Dir.Incoming) {

                        if (calllog.getStatus() == Call.Status.Missed) {
                            missedcalllog += calllog;
                        }
                    }
                }

                val adapter = activity?.let { All_History_Adapter(it, missedcalllog, "Miss") }
                adapter?.onItemClick = { string ->
                    callDetailscreen(string)
                }
                binding.recyclerview.adapter = adapter
            }


        })


        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                //refreshList()
//                if (s.isNotEmpty()) {
//                    ivSearchClear.visibility = View.VISIBLE
//                } else {
//                    ivSearchClear.visibility = View.GONE
//                }
                filterData()
                if (core != null) {
                    val adapter = activity?.let { All_History_Adapter(it, core, "All") }
                    adapter?.onItemClick = { string ->
                        callDetailscreen(string)
                    }
                    binding.recyclerview.adapter = adapter
                }
//                recyclerview(mendates)
            }
        })


    }

    private fun filterData() {

        core = emptyArray()
//        if (etSearch.text.toString().isEmpty()) {
//            mendates.addAll(mendatesAll)
//        } else {
        for (item in calllog) {
            if (item.remoteAddress.displayName.toString().toLowerCase(Locale.getDefault()).contains(
                    binding.etSearch.text.toString().toLowerCase(Locale.getDefault())
                ) || item.fromAddress.displayName.toString().toLowerCase(Locale.getDefault())
                    .contains(binding.etSearch.text.toString().toLowerCase(Locale.getDefault()))
            ) {
                core += item
            }
        }
        if (core.isEmpty()) {
            binding.recyclerview.visibility = View.GONE
        } else {
            binding.recyclerview.visibility = View.VISIBLE
        }


//        }
    }

    private fun callDetailscreen(string: String) {
        val transaction = activity?.supportFragmentManager!!.beginTransaction()
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