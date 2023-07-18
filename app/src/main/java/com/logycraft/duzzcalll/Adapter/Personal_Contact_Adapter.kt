package com.logycraft.duzzcalll.Adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.logycraft.duzzcalll.Model.ContactModel
import com.duzzcall.duzzcall.R
import com.simplemobiletools.commons.adapters.MyRecyclerViewAdapter
import com.simplemobiletools.commons.helpers.SimpleContactsHelper
import de.hdodenhof.circleimageview.CircleImageView

class Personal_Contact_Adapter(
    var context: Context?, var isfavourite: Boolean, var contactList: MutableList<ContactModel>
) : RecyclerView.Adapter<Personal_Contact_Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.personal_contact_list, parent, false)
        return ViewHolder(view)
    }

    fun setContacts(contacts: List<ContactModel>) {
        contactList.clear()
        contactList.addAll(contacts)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (isfavourite) {
            holder.img_star.setImageDrawable(context?.getDrawable(R.drawable.ic_star_filled))
        } else {
            val currentContact = contactList[position]
            holder.txt_contact_name.setText(currentContact.name)
            holder.txt_contact_number.setText(currentContact.number)
//        holder.contact_image.setImageURI(currentContact.imageUri)
            context?.let {
                SimpleContactsHelper(it).loadContactImage(
                    currentContact.imageUri.toString(), holder.contact_image, currentContact.name
                )
            }
        }
    }


    override fun getItemCount(): Int {
        if (isfavourite) {
            return 4
        }
        return contactList.size
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
            img_star = itemView.findViewById(R.id.img_star)
        }
    }
}