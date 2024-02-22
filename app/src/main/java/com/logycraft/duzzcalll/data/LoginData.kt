package com.logycraft.duzzcalll.data

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.security.cert.Extension


class LoginData {
    @SerializedName("extension")
    @Expose
    var extension: Extension? = null

    @SerializedName("verified")
    @Expose
    var verified: Boolean? = null

    class Extension {
        @SerializedName("first_name")
        @Expose
        var firstName: String? = null

        @SerializedName("last_name")
        @Expose
        var lastName: String? = null

        @SerializedName("extension")
        @Expose
        var extension: String? = null

        @SerializedName("extension_password")
        @Expose
        var extensionpassword: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("phone")
        @Expose
        var phone: String? = null

        @SerializedName("country_code")
        @Expose
        var country_code: String? = null

        @SerializedName("access_token")
        @Expose
        var accessToken: String? = null

        @SerializedName("refresh_token")
        @Expose
        var refreshToken: String? = null

        @SerializedName("expires_at")
        @Expose
        var expiresAt: String? = null

//        @SerializedName("verification_token")
//        var verificationToken: String? = null
//
//        @SerializedName("tfa_code")
//        var tfaCode: Int? = null
    }
}