package com.logycraft.duzzcalll.data

import com.google.gson.annotations.SerializedName

class BusinessResponce {
    @SerializedName("business_name")
    var businessName: String? = null

    @SerializedName("business_logo")
    var businessLogo: String? = null

    @SerializedName("line_name")
    var lineName: String? = null

    @SerializedName("line_extension")
    var lineExtension: String? = null

}