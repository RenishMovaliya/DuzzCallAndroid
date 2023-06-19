package com.logycraft.duzzcalll.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.logycraft.duzzcalll.R
import de.hdodenhof.circleimageview.CircleImageView

class Personal_Contact_Adapter(var activity: Activity?,var isfavourite: Boolean) :
    RecyclerView.Adapter<Personal_Contact_Adapter.ViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.personal_contact_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (isfavourite){
            holder.img_star.setImageDrawable(activity?.getDrawable(R.drawable.ic_star_filled))
        }
    }

    override fun getItemCount(): Int {
        if (isfavourite){
            return 4
        }
        return 15
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var txt_contact_name: TextView
        lateinit var txt_contact_number: TextView
        lateinit var img_star: ImageView
        lateinit var contact_image: CircleImageView

        init {
            txt_contact_name = itemView.findViewById(R.id.txt_contact_name)
            txt_contact_number = itemView.findViewById(R.id.txt_contact_number)
            contact_image = itemView.findViewById(R.id.contact_image)
            img_star = itemView.findViewById(R.id.img_star)        }
    }
}