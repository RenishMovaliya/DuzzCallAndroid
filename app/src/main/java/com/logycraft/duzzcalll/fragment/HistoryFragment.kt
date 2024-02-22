package com.logycraft.duzzcalll.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.duzzcall.duzzcall.databinding.FragmentHistoryBinding
import com.logycraft.duzzcalll.Activity.NewContactActivity
import com.logycraft.duzzcalll.Adapter.All_History_Adapter
import com.logycraft.duzzcalll.Adapter.All_History_Adapter2
import com.logycraft.duzzcalll.LinphoneManager
import com.logycraft.duzzcalll.Model.ContactModel
import com.logycraft.duzzcalll.Util.ProgressHelper
import com.logycraft.duzzcalll.Util.ProgressHelper.dismissProgressDialog
import com.logycraft.duzzcalll.helper.CallBackListener
import io.reactivex.annotations.Nullable
import org.linphone.core.Call
import org.linphone.core.CallLog
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HistoryFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var core: Array<CallLog>
    lateinit var historyadapter: All_History_Adapter;
    lateinit var historyadapter2: All_History_Adapter2;
    private var callBackListener: CallBackListener? = null
    private var type: String? = "All"

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
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is CallBackListener) callBackListener = activity as CallBackListener?
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        core = LinphoneManager.getCore().callLogs

        val localReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (type.equals("All")) {
                    recyclreviewCall()
                } else {
                    binding.relativeMissed.performClick()
                }
            }
        }

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                localReceiver,
                IntentFilter("com.duzzcall.duzzcall.NOTIFICATION_ACTION")
            )

        binding.relativeAll.setOnClickListener(View.OnClickListener {
            type = "All";
            binding.relativeSelectedBtn.animate().x(0f).duration = 100
            recyclreviewCall()

        })
        binding.btnAdd.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, NewContactActivity::class.java)
            startActivity(intent)
        })

        binding.relativeMissed.setOnClickListener(View.OnClickListener {
            type = "Miss";
            val size: Int = binding.relativeAll.getWidth()
            binding.relativeSelectedBtn.animate().x(size.toFloat()).duration = 100
            missedRecyclerview();

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
                if (type.equals("All")) {
                    historyadapter.filter(s.toString(), type)

                } else {
                    historyadapter2.filter(s.toString(), type)

                }
            }
        })

    }

    private fun missedRecyclerview() {
        core = LinphoneManager.getCore().callLogs
        dismissProgressDialog()

        var missedcalllog: Array<CallLog> = emptyArray()
        if (core != null) {
            for (calllog in core) {
                if (calllog.getDir() == Call.Dir.Incoming) {

                    if (calllog.getStatus() == Call.Status.Missed) {
                        missedcalllog += calllog;
                    }
                }
            }

            historyadapter2 = activity?.let {
                All_History_Adapter2(it,
                    missedcalllog,
                    "miss",
                    object : All_History_Adapter2.OnItemClickListener {
                        override fun onClick(
                            number: String, name: String, remoteAddress: String?
                        ) {
                            remoteAddress?.let { it1 ->
                                callBackListener?.onCallBack(
                                    number, name, it1
                                )
                            };
                        }
                    })
            }!!
            binding.recyclerview.adapter = historyadapter2

        }
    }

    private fun recyclreviewCall() {
        core = LinphoneManager.getCore().callLogs
        dismissProgressDialog()
        if (core != null) {

            activity?.let {
                historyadapter = All_History_Adapter(it, core,
                    "All",
                    object : All_History_Adapter.OnItemClickListener {
                        override fun onClick(number: String, name: String, remoteAddress: String?) {
                            remoteAddress?.let { it1 ->
                                callBackListener?.onCallBack(number, name, it1)
                            }
                        }
                    })
                binding.recyclerview.adapter = historyadapter
                binding.recyclerview.setLayoutManager(LinearLayoutManager(it))

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

    override fun onResume() {
        super.onResume()
        ProgressHelper.showProgrssDialogs(activity)

        if (type.equals("All")) {
            recyclreviewCall()

        } else {
            missedRecyclerview();
        }
    }
}