package com.logycraft.duzzcalll.Adapter

import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.duzzcall.duzzcall.R
import com.logycraft.duzzcalll.LinphoneManager
import com.logycraft.duzzcalll.Model.ContactModel
import com.logycraft.duzzcalll.Util.ProgressHelper
import de.hdodenhof.circleimageview.CircleImageView
import org.linphone.core.Call
import org.linphone.core.CallLog
import java.text.SimpleDateFormat
import java.util.*

class All_History_Adapter(
    var activity: Activity,
    var calllog: Array<CallLog>,
    var type: String,
    var listener: OnItemClickListener?
) : RecyclerView.Adapter<All_History_Adapter.ViewHolder>() {
    var onItemClick: ((String) -> Unit)? = null
    var missedcalllog: Array<CallLog> = emptyArray()
    lateinit var calltype: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.all_history_list, parent, false)
        return ViewHolder(view)
    }

    interface OnItemClickListener {
        fun onClick(get: String, toString: String, remoteAddress: String?)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        var address = ""
        var number = ""
        calltype = type
        Log.e("callogssssss", "" + calltype)

        if (calllog.get(position).getDir() == Call.Dir.Incoming) {
            address = calllog.get(position).getFromAddress().displayName.toString();
            number = calllog.get(position).getFromAddress().username.toString();
            if (calllog.get(position).getStatus() == Call.Status.Missed) {
                holder.ic_call_action.setImageResource(R.drawable.ic_missed_call);
                holder.txt_contact_name.setTextColor(activity.getColor(com.duzzcall.duzzcall.R.color.missedcall_color))
            } else {
                holder.ic_call_action.setImageResource(R.drawable.ic_incoming_call);
                holder.txt_contact_name.setTextColor(activity.getColor(com.duzzcall.duzzcall.R.color.text_color_gray))

            }
        } else {
            address = calllog.get(position).remoteAddress.displayName.toString();
            number = calllog.get(position).remoteAddress.username.toString();
            holder.ic_call_action.setImageResource(R.drawable.ic_outgoing_call);
            holder.txt_contact_name.setTextColor(activity.getColor(com.duzzcall.duzzcall.R.color.text_color_gray))

        }


        if (!getFirstTwoCharacters(address).equals("00")) {
            Log.e("favourited", "business--" + address)
            holder.txt_contact_name.text = address

        } else if (getFirstTwoCharacters(address).equals("00")) {
            Log.e("favourited", "oo  --" + address)
            holder.txt_contact_name.text = "Direct Call"

        }
        else if (!getFirstTwoCharacters(address).equals("00")) {
            Log.e(
                "favourited",
                "business--" + address + calllog.get(position).remoteAddress.displayName
            )
            holder.txt_contact_name.text = address

        }
        holder.txt_contact_number.text = number


        val timestamp: Long = calllog.get(position).getStartDate() * 1000
        val logTime: Calendar = Calendar.getInstance()
        logTime.setTimeInMillis(timestamp)
        Log.e("dateeeeee", "" + logTime)
        holder.txt_call_time.setText(timestampToHumanDate(logTime))

        if (type.equals("miss")) {
            holder.tv_call_duration.visibility = View.GONE
        } else {
            holder.tv_call_duration.visibility = View.VISIBLE
        }
        holder.tv_call_duration.setText(ConvertSecondToHHMMSSString(calllog.get(position).duration))

        holder.itemView.setOnClickListener {

            Log.e("ggggggggggg", "" + calllog.get(position).remoteAddress)
            var methodparam = " ";
            if (calllog.get(position).remoteAddress.methodParam == null) {
                methodparam = " "
            } else {
                methodparam = calllog.get(position).remoteAddress.methodParam!!
            }

            listener?.onClick(number, holder.txt_contact_name.text.toString(), methodparam)
        }
    }

    fun getFirstTwoCharacters(input: String): String {
        if (input.length >= 2) {
            return input.substring(0, 2)
        } else {
            return input
        }
    }

    fun filter(query: String, type: String?) {

        calllog = if (type.equals("All")) {
            if (query.isEmpty()) {
                LinphoneManager.getCore().callLogs
            } else {
                LinphoneManager.getCore().callLogs.filter {
                    it.remoteAddress.displayName?.contains(query, true) == true ||
                            it.remoteAddress.username?.contains(
                                query,
                                true
                            ) == true || it.remoteAddress.displayName == null
                }.toTypedArray()
            }
        } else {
            if (query.isEmpty()) {
                LinphoneManager.getCore().callLogs.filter {
                    it.getDir() == Call.Dir.Incoming && it.getStatus() == Call.Status.Missed
                }.toTypedArray()
            } else {
                LinphoneManager.getCore().callLogs.filter {
                    it.getDir() == Call.Dir.Incoming && it.getStatus() == Call.Status.Missed &&
                            (it.fromAddress.displayName?.contains(query, true) == true ||
                                    it.fromAddress.username?.contains(query, true) == true)
                }.toTypedArray()
            }
        }

        notifyDataSetChanged()
    }

    private fun ConvertSecondToHHMMSSString(nSecondTime: Int): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.time.LocalTime.MIN.plusSeconds(nSecondTime.toLong()).toString()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    override fun getItemCount(): Int {
        return calllog.size
    }

    private fun isSameDay(cal1: Calendar?, cal2: Calendar?): Boolean {
        return cal1 != null && cal2 != null && cal1[0] === cal2[0] && cal1[1] === cal2[1] && cal1[6] === cal2[6]
    }

    private fun isToday(cal: Calendar): Boolean {
        return isSameDay(cal, Calendar.getInstance())
    }

    private fun isYesterday(cal: Calendar): Boolean {
        val yesterday = Calendar.getInstance()
        yesterday.roll(5, -1)
        return isSameDay(cal, yesterday)
    }

    private fun timestampToHumanDate(cal: Calendar): String? {
        if (isToday(cal)) {
            val dateFormat = SimpleDateFormat("HH:mm")
            return dateFormat.format(cal.time)
//            return "Today"
        }

        val dateFormat = SimpleDateFormat("EEE")
        return dateFormat.format(cal.time)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_contact_name: TextView
        var txt_contact_number: TextView
        var tv_call_duration: TextView
        var txt_call_time: TextView
        var contct_image: CircleImageView
        var ic_call_action: ImageView

        init {
//            tv_flag = itemView.findViewById(R.id.tv_flag)
//            tv_country = itemView.findViewById(R.id.tv_country)
            ic_call_action = itemView.findViewById(com.duzzcall.duzzcall.R.id.ic_call_action)
            contct_image = itemView.findViewById(com.duzzcall.duzzcall.R.id.contct_image)
            txt_contact_number =
                itemView.findViewById(com.duzzcall.duzzcall.R.id.txt_contact_number)
            txt_call_time = itemView.findViewById(com.duzzcall.duzzcall.R.id.txt_call_time)
            txt_contact_name = itemView.findViewById(com.duzzcall.duzzcall.R.id.txt_contact_name)
            tv_call_duration = itemView.findViewById(com.duzzcall.duzzcall.R.id.tv_call_duration)
        }
    }


}