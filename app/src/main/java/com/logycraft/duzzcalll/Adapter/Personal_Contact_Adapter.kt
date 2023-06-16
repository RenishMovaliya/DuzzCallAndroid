package com.logycraft.duzzcalll.Adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.logycraft.duzzcalll.R
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONException

class Personal_Contact_Adapter(var activity: Activity) :
    RecyclerView.Adapter<Personal_Contact_Adapter.ViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.personal_contact_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    
    }

    override fun getItemCount(): Int {
        return 10
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var txt_contact_name: TextView
//        var txt_contact_number: TextView
//        var contct_image: CircleImageView

        init {
//            tv_flag = itemView.findViewById(R.id.tv_flag)
//            tv_country = itemView.findViewById(R.id.tv_country)
        }
    }
}