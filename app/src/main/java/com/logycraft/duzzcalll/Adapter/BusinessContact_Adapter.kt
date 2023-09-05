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
import com.adwardstark.mtextdrawable.MaterialTextDrawable
import com.duzzcall.duzzcall.R
import com.logycraft.duzzcalll.data.BusinessResponce
import de.hdodenhof.circleimageview.CircleImageView
import java.nio.charset.StandardCharsets
import java.util.Base64

//import java.util.Base64
//import android.util.Base64


class BusinessContact_Adapter(var activity: Activity?,var businessresponce: List<BusinessResponce>, var isfavourite: Boolean ) :
    RecyclerView.Adapter<BusinessContact_Adapter.ViewHolder>() {
//    var businessresponce: List<BusinessResponce>? = null;
//    lateinit var activity :Context ;
//     var isfavourite  : Boolean = false;

//    constructor(activitys: FragmentActivity, businessresponces: List<BusinessResponce>?, b: Boolean) : this() {
//        businessresponce = businessresponces
//        activity = activitys
//        isfavourite = b
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.personal_contact_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.contact_image.setImageResource(R.drawable.srilankan_airline)
//        holder.txt_contact_name.setText("Sri Lankan Airlines")
//        if (isfavourite){
//            holder.img_star.setImageDrawable(activity?.getDrawable(R.drawable.ic_star_filled))
//        }
//        val data: ByteArray = Base64.decode(businessresponce.get(position).logo, Base64.DEFAULT)
//        val text = kotlin.String(data, StandardCharsets.UTF_8)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val decodedString: String = String(Base64.getDecoder().decode(businessresponce.get(position).logo))
//            Log.d("ImageUrl",decodedString)
//        }
        MaterialTextDrawable.with(activity!!)
            .text(businessresponce.get(position).name.substring(0,2) ?: "DC")
            .into(holder.contact_image)
        holder.txt_contact_name.setText( businessresponce.get(position).name)


    }

    override fun getItemCount(): Int {
        if (isfavourite){
            return 2
        }
        return businessresponce.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var txt_contact_name: TextView
        lateinit var txt_contact_number: TextView
        lateinit var img_star: ImageView
        lateinit var contact_image: CircleImageView

        init {
            txt_contact_name = itemView.findViewById(R.id.txt_contact_name)
            img_star = itemView.findViewById(R.id.img_star)
            txt_contact_number = itemView.findViewById(R.id.txt_contact_number)
            contact_image = itemView.findViewById(R.id.contact_image)
        }
    }
}