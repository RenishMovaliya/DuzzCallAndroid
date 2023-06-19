package com.app.cancellingretrofitrequests.kotlin.networking

import com.google.gson.JsonElement
import com.logycraft.duzzcalll.data.SendOTP
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface ApiService {
    companion object {
        val BASE_URL: String = "https://dzapi.et.lk/api/extensions/"
    }

    @FormUrlEncoded
    @POST("token/sms")
     fun getResult(
            @Field("phone") phone: String?): Response<SendOTP?>

    @FormUrlEncoded
    @POST("new")
    fun createUser(
        @Field("first_name")  firstname:String,
        @Field("last_name")  last_name:String,
        @Field("phone")  phone:String,
        @Field("password")  password:String,
        @Field("email")  email:String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("token/sms")
    fun sendOtp(
        @Field("phone")  phone:String): Call<ResponseBody>
}