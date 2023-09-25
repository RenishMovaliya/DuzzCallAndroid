package com.logycraft.duzzcalll.Adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.duzzcall.duzzcall.R
import com.logycraft.duzzcalll.Util.Preference
import com.logycraft.duzzcalll.data.BusinessResponce
import de.hdodenhof.circleimageview.CircleImageView

//import java.util.Base64
//import android.util.Base64

class BusinessContact_Adapter(
    var activity: Activity?,
    var businessresponce: ArrayList<BusinessResponce>,
    var isfavourite: Boolean,
    var listener: OnItemClickListener?
) : RecyclerView.Adapter<BusinessContact_Adapter.ViewHolder>() {
    var favoritesContactlist: ArrayList<BusinessResponce>? = ArrayList()
//    var favoritesContact = BusinessResponce()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.personal_contact_list, parent, false)
        return ViewHolder(view)
    }

    interface OnItemClickListener {
        fun onClick(get: BusinessResponce)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.rl_layout.setOnClickListener {
            listener?.onClick(businessresponce.get(position))
        }

        activity?.let {
            Glide.with(it).load(businessresponce.get(position).businessLogo).centerCrop()
                .into(holder.contact_image)
        };
        holder.txt_contact_name.setText(businessresponce.get(position).businessName)
        holder.txt_contact_number.setText(businessresponce.get(position).lineExtension)

        var listdata: MutableList<BusinessResponce>? = mutableListOf()
        listdata = activity?.let {
            Preference.getFavoritesContact(it)
        }

        if (listdata != null && !listdata.isEmpty() && businessresponce != null) {

//            if (listdata.contains(businessresponce.get(position)) ){
//                Log.e("favourited",""+businessresponce.get(position).lineExtension)
//            }
            for (item1 in listdata) {
                if (item1.lineExtension.equals(businessresponce.get(position).lineExtension)) {
                    Log.e("favourited", "bbb" + businessresponce.get(position).lineExtension)
                    holder.img_star_blank.visibility = View.GONE
                    holder.img_star_filled.visibility = View.VISIBLE
                }
            }
//            if (position != (listdata.size - 1)) {
//                for (item1 in listdata) {
//                    if (item1.lineExtension.equals(businessresponce.get(position).lineExtension)) {
//                        holder.img_star_blank.visibility = View.GONE
//                        holder.img_star_filled.visibility = View.VISIBLE
//                        break
//                    }
////                    for (item2 in businessresponce) {
////                        if (item1.lineExtension == item2.lineExtension) {
////                            holder.img_star_blank.visibility = View.GONE
////                            holder.img_star_filled.visibility = View.VISIBLE
////                            break
////                        }
//////                        else {
//////                            holder.img_star_blank.visibility = View.VISIBLE
//////                            holder.img_star_filled.visibility = View.GONE
//////                            break
//////                        }
////                    }
//                }

//            } else {
//                holder.img_star_blank.visibility = View.VISIBLE
//                holder.img_star_filled.visibility = View.GONE
//            }

        } else {
            holder.img_star_blank.visibility = View.VISIBLE
            holder.img_star_filled.visibility = View.GONE
        }

        holder.img_star_blank.setOnClickListener {
            holder.img_star_filled.visibility = View.VISIBLE
            holder.img_star_blank.visibility = View.GONE

            if (activity?.let { it1 -> Preference.getFavoritesContact(it1) } == null) {
//                favoritesContact.businessLogo = businessresponce.get(position).businessLogo;
//                favoritesContact.businessName = businessresponce.get(position).businessName;
//                favoritesContact.lineExtension = businessresponce.get(position).lineExtension;
//                favoritesContact.lineName = businessresponce.get(position).lineName
                favoritesContactlist?.add(businessresponce.get(position))

                activity?.let { it1 ->
                    Preference.setFavoritesContact(it1, favoritesContactlist)
                }

            } else {
//                favoritesContact.businessLogo = businessresponce.get(position).businessLogo;
//                favoritesContact.businessName = businessresponce.get(position).businessName;
//                favoritesContact.lineExtension = businessresponce.get(position).lineExtension;
//                favoritesContact.lineName = businessresponce.get(position).lineName;
                favoritesContactlist = Preference.getFavoritesContact(activity!!)

                favoritesContactlist?.add(businessresponce.get(position))
                activity?.let { it1 ->
                    Preference.setFavoritesContact(it1, favoritesContactlist)
                }
            }
        }

        holder.img_star_filled.setOnClickListener {
            favoritesContactlist = Preference.getFavoritesContact(activity!!)
            favoritesContactlist?.remove(businessresponce.get(position))

            Log.e("Favlouritesize", "" + favoritesContactlist?.size)

//            for (item1 in favoritesContactlist!!) {
//                if (item1.lineExtension.equals(businessresponce.get(position).lineExtension)) {
//                    favoritesContactlist?.remove(item1)
//                    Log.e("Favlouritesize", "current")
//
//                }
//            }



//            var favoriteslist: List<BusinessResponce>? = favoritesContactlist
//            Log.e("Favlouritesize", "" + favoriteslist?.size)
//            if (favoriteslist!!.contains(businessresponce.get(position))) {
//                Log.e("Favlouritesize", "YES " + favoriteslist?.size)
//            }
//            Log.e("Favlouritesize", " after removed " + favoriteslist?.size)
//            if (favoritesContactlist != null) {
//                for (item1 in favoritesContactlist!!) {
//                    if (item1.lineExtension == businessresponce.get(position).lineExtension) {
//                        favoritesContactlist?.remove(item1)
//                    }
//                }
//            }
            Log.e("Favlouritesize", " after removed " + favoritesContactlist?.size)
            activity?.let { it1 -> Preference.setFavoritesContact(it1, favoritesContactlist) }

            holder.img_star_filled.visibility = View.GONE
            holder.img_star_blank.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return businessresponce.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var txt_contact_name: TextView
        lateinit var txt_contact_number: TextView
        lateinit var img_star_filled: ImageView
        lateinit var img_star_blank: ImageView
        lateinit var contact_image: CircleImageView
        lateinit var rl_layout: RelativeLayout

        init {
            txt_contact_name = itemView.findViewById(R.id.txt_contact_name)
            img_star_blank = itemView.findViewById(R.id.img_star_blank)
            img_star_filled = itemView.findViewById(R.id.img_star_filled)
            txt_contact_number = itemView.findViewById(R.id.txt_contact_number)
            contact_image = itemView.findViewById(R.id.contact_image)
            rl_layout = itemView.findViewById(R.id.rl_layout)
        }
    }
}