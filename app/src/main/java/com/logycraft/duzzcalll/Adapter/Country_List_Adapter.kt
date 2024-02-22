package com.logycraft.duzzcalll.Adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.duzzcall.duzzcall.R
import com.logycraft.duzzcalll.Model.Country
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class Country_List_Adapter(
    var activity: Activity,
    var itemsList: ArrayList<Country>?,
    var listener: OnItemClickListener?
) :
    RecyclerView.Adapter<Country_List_Adapter.ViewHolder>() {
    var onItemClick: ((String) -> Unit)? = null
    var country_list: ArrayList<Country>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.item_country_list, parent, false)
        return ViewHolder(view)
    }

    interface OnItemClickListener {
        fun onItemClick(country_name: String?, country_code: String?)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
//            holder.tv_country_flag.text = jsonObject.getString("flag")
            val currentContact = itemsList!![position]
            holder.tv_country_flag.text = currentContact.flag
            holder.tv_name.text =  currentContact.name
            holder.tv_dial_code.text =  currentContact.dial_code

            holder.itemView.setOnClickListener {
                listener?.onItemClick(
                    currentContact.name
                            ,currentContact.dial_code
                )
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }


    }


    fun filter(str: String) {
        try {
            this.itemsList?.clear()
            if (!str.isEmpty()) {
                val it: MutableIterator<Country?> = this.country_list!!.iterator()
                while (it.hasNext()) {
                    val next: Country? = it.next()
                    if (next != null) {
                        if (next.name != null) {
                            Log.i("ContentValues", "filter BOTH: CALLED")
                            if (next.name.toLowerCase(Locale.getDefault()).contains(
                                    str.lowercase(
                                        Locale.getDefault()
                                    )
                                )
                            ) {
                                this.itemsList?.add(next)
                            }
                        }
                    }
                }
                notifyDataSetChanged()
            }

            notifyDataSetChanged()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun getItemCount(): Int {
        return itemsList!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_country_flag: TextView
        var tv_name: TextView
        var tv_dial_code: TextView

        init {

            tv_country_flag = itemView.findViewById(R.id.tv_country_flag)
            tv_name = itemView.findViewById(R.id.tv_name)
            tv_dial_code = itemView.findViewById(R.id.tv_dial_code)
        }
    }
}