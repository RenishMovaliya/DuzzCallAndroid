package com.logycraft.duzzcalll.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.duzzcall.duzzcall.R

class HistoryDetails_Adapter(var activity: Activity) :
    RecyclerView.Adapter<HistoryDetails_Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.history_details_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            if (position == 2 || position == 4 ) {
                holder.ic_call_action.setImageDrawable(activity.getDrawable(R.drawable.ic_incoming_call))
                holder.txt_contact_name.setText(activity.getString(R.string.incoming_call_txt))
//                holder.txt_call_time.visibility==View.VISIBLE
                holder.txt_call_time.setText("2m 56s")
                holder.txt_contact_name.setTextColor(activity.getColor(R.color.regular_txt_colour))
            } else if (position == 0 || position == 5) {
                holder.ic_call_action.setImageDrawable(activity.getDrawable(R.drawable.ic_missed_call))
                holder.txt_contact_name.setText(activity.getString(R.string.missed_call_txt))
                holder.txt_contact_name.setTextColor(activity.getColor(R.color.missedcall_color))
                holder.txt_call_time.setText("")

            } else {
                holder.ic_call_action.setImageDrawable(activity.getDrawable(R.drawable.ic_outgoing_call))
                holder.txt_contact_name.setText(activity.getString(R.string.outgoing_call_txt))
                holder.txt_contact_name.setTextColor(activity.getColor(R.color.regular_txt_colour))
//                holder.txt_call_time.visibility==View.VISIBLE
                holder.txt_call_time.setText("6m 12s")
            }

//        holder.itemView.setOnClickListener(View.OnClickListener {
//            val intent = Intent(activity, Terms_And_ConditionActivity::class.java)
//            activity.startActivity(intent)
//        })


    }

    override fun getItemCount(): Int {
        return 7
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_contact_name: TextView
        var txt_call_time: TextView
//        var contct_image: CircleImageView
        var ic_call_action: ImageView

        init {
            txt_contact_name = itemView.findViewById(R.id.txt_contact_name)
            txt_call_time = itemView.findViewById(R.id.txt_call_time)
//            tv_country = itemView.findViewById(R.id.tv_country)
            ic_call_action = itemView.findViewById(R.id.ic_call_action)
        }
    }
}