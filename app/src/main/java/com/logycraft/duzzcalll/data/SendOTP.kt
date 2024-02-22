package com.logycraft.duzzcalll.data

import com.google.gson.annotations.SerializedName




public data class SendOTP (

    @SerializedName("verification_token" ) var verificationToken : String? = null,
    @SerializedName("tfa_code"           ) var tfaCode           : Int?    = null

)