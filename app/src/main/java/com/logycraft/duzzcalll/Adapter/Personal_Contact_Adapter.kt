package com.logycraft.duzzcalll.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.duzzcall.duzzcall.R
import com.logycraft.duzzcalll.Model.ContactModel

import com.logycraft.duzzcalll.Util.Preference
import com.logycraft.duzzcalll.data.BusinessResponce
import de.hdodenhof.circleimageview.CircleImageView

class Personal_Contact_Adapter(
    var context: Context?,
    var isfavourite: Boolean,
    var favoritesContactlist: ArrayList<BusinessResponce>?,
    var listener: OnItemClickListener
) : RecyclerView.Adapter<Personal_Contact_Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.personal_contact_list, parent, false)
        return ViewHolder(view)
    }

    interface OnItemClickListener {
        fun onClick(get: BusinessResponce)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            favoritesContactlist?.get(position)?.let { it1 -> listener.onClick(it1) }
        }

        holder.img_star.setImageResource(R.drawable.ic_star_filled)
        holder.img_star.setOnClickListener {
            favoritesContactlist = context?.let { it1 -> Preference.getFavoritesContact(it1) }
            favoritesContactlist?.remove(favoritesContactlist!!.get(position))
            context?.let { it1 -> Preference.setFavoritesContact(it1, favoritesContactlist) }
            notifyDataSetChanged()
        }

        if (favoritesContactlist != null) {

            holder.txt_contact_name.setText(favoritesContactlist!!.get(position).businessName+" "+ favoritesContactlist!!.get(position).lineName)
            holder.txt_contact_number.setText(favoritesContactlist?.get(position)?.lineExtension)
            context?.let {
                Glide.with(it).load(favoritesContactlist?.get(position)?.businessLogo)
                    .into(holder.contact_image)
            };
        }
    }


    override fun getItemCount(): Int {
        if (favoritesContactlist == null) {
            return 0
        }
        return favoritesContactlist?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var txt_contact_name: TextView
        lateinit var txt_contact_number: TextView
        lateinit var img_star: ImageView
        lateinit var contact_image: ImageView

        init {
            txt_contact_name = itemView.findViewById(R.id.txt_contact_name)
            txt_contact_number = itemView.findViewById(R.id.txt_contact_number)
            contact_image = itemView.findViewById(R.id.contact_image)
            img_star = itemView.findViewById(R.id.img_star_blank)
        }
    }
}