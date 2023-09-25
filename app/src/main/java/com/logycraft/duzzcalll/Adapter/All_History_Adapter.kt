package com.logycraft.duzzcalll.Adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.duzzcall.duzzcall.R
import de.hdodenhof.circleimageview.CircleImageView
import org.linphone.core.Call
import org.linphone.core.CallLog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class All_History_Adapter(
    var activity: Activity, var calllog: Array<CallLog>, var callType: String
) : RecyclerView.Adapter<All_History_Adapter.ViewHolder>() {
    var onItemClick: ((String) -> Unit)? = null
    var misscall: Array<CallLog> = emptyArray()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.all_history_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        if (from==0){
//            if (position == 2 || position == 4 || position == 8) {
//                holder.ic_call_action.setImageDrawable(activity.getDrawable(R.drawable.ic_incoming_call))
//                holder.txt_contact_name.setTextColor(activity.getColor(R.color.regular_txt_colour))
//            } else if (position == 3 || position == 6) {
//                holder.ic_call_action.setImageDrawable(activity.getDrawable(R.drawable.ic_missed_call))
//                holder.txt_contact_name.setTextColor(activity.getColor(R.color.missedcall_color))
//            } else {
//                holder.ic_call_action.setImageDrawable(activity.getDrawable(R.drawable.ic_outgoing_call))
//                holder.txt_contact_name.setTextColor(activity.getColor(R.color.regular_txt_colour))
//            }
//        }else{
//            holder.ic_call_action.setImageDrawable(activity.getDrawable(R.drawable.ic_missed_call))
//            holder.txt_contact_name.setTextColor(activity.getColor(R.color.missedcall_color))
//        }


//        holder.ic_call_action.setImageDrawable(activity.getDrawable(R.drawable.ic_missed_call))
        Log.e("daaaaaaaa", "" + calllog.get(position).getStatus());

        var address = ""
        var number = ""

//        if (callType.equals("All")) {

            if (calllog.get(position).getDir() == Call.Dir.Incoming) {
                address = calllog.get(position).getFromAddress().displayName.toString();
                number = calllog.get(position).getFromAddress().username.toString();
                if (calllog.get(position).getStatus() == Call.Status.Missed) {
                    holder.ic_call_action.setImageResource(R.drawable.ic_missed_call);
                    holder.txt_contact_name.setTextColor(activity.getColor(com.duzzcall.duzzcall.R.color.missedcall_color))
                } else {
                    holder.ic_call_action.setImageResource(R.drawable.ic_incoming_call);
                }
            } else {
                address = calllog.get(position).remoteAddress.displayName.toString();
                number = calllog.get(position).remoteAddress.username.toString();
                holder.ic_call_action.setImageResource(R.drawable.ic_outgoing_call);
            }

            holder.txt_contact_name.text = address
            holder.txt_contact_number.text = number


            val timestamp: Long = calllog.get(position).getStartDate() * 1000
            val logTime: Calendar = Calendar.getInstance()
            logTime.setTimeInMillis(timestamp)
            holder.txt_call_time.setText(timestampToHumanDate(logTime))
//        } else {
//
//            misscall = calllog
//            if (calllog.get(position).getStatus() == Call.Status.Missed) {
//                holder.ic_call_action.setImageResource(R.drawable.ic_missed_call);
//                address = misscall.get(position).getFromAddress().displayName.toString();
//                number = misscall.get(position).getFromAddress().username.toString();
//
//                holder.txt_contact_name.text = address
//                holder.txt_contact_number.text = number
//                holder.txt_contact_name.setTextColor(activity.getColor(R.color.missedcall_color))
//                val timestamp: Long = misscall.get(position).getStartDate() * 1000
//                val logTime: Calendar = Calendar.getInstance()
//                logTime.setTimeInMillis(timestamp)
//                holder.txt_call_time.setText(timestampToHumanDate(logTime))
//            } else {
//                misscall = calllog.toMutableList().apply { removeAt(position) }.toTypedArray()
//
//            }
//        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(" ")
        }
    }

    override fun getItemCount(): Int {
//        if (callType.equals("Miss")) {
//            return misscall.size
//        }
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
            return "Today"
        }
        if (isYesterday(cal)) {
            return "Yesterday"
        }
        val dateFormat = SimpleDateFormat("EEE d MMM")
        return dateFormat.format(cal.time)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_contact_name: TextView
        var txt_contact_number: TextView
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
        }
    }


}