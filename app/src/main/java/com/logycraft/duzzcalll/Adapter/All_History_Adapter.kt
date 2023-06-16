package com.logycraft.duzzcalll.Adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.logycraft.duzzcalll.R
import com.logycraft.duzzcalll.Terms_And_ConditionActivity

class All_History_Adapter(var activity: Activity,var from: Int) :
    RecyclerView.Adapter<All_History_Adapter.ViewHolder>() {
    var onItemClick: ((String) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.all_history_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (from==0){
            if (position == 2 || position == 4 || position == 8) {
                holder.ic_call_action.setImageDrawable(activity.getDrawable(R.drawable.ic_incoming_call))
            } else if (position == 3 || position == 6) {
                holder.ic_call_action.setImageDrawable(activity.getDrawable(R.drawable.ic_missed_call))
            } else {
                holder.ic_call_action.setImageDrawable(activity.getDrawable(R.drawable.ic_outgoing_call))
            }
        }else{
            holder.ic_call_action.setImageDrawable(activity.getDrawable(R.drawable.ic_missed_call))
        }
//        holder.itemView.setOnClickListener(View.OnClickListener {
//            val intent = Intent(activity, Terms_And_ConditionActivity::class.java)
//            activity.startActivity(intent)
//        })
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(" ")
        }

    }

    override fun getItemCount(): Int {
        return 10
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var txt_contact_name: TextView
//        var txt_contact_number: TextView
//        var contct_image: CircleImageView
        var ic_call_action: ImageView

        init {
//            tv_flag = itemView.findViewById(R.id.tv_flag)
//            tv_country = itemView.findViewById(R.id.tv_country)
            ic_call_action = itemView.findViewById(R.id.ic_call_action)
        }
    }
}