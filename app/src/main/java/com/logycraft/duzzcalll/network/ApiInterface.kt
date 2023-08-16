package com.example.restapiidemo.network

import com.example.restapiidemo.home.data.UserModel
import com.google.gson.JsonElement
import com.logycraft.duzzcalll.data.LoginData
import com.logycraft.duzzcalll.data.SendOTP
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @GET("posts")
    fun fetchAllPosts(): Call<List<UserModel>>

//    @FormUrlEncoded
//    @POST("new")
//    fun createUser(
//        @Field("first_name")  firstname:String,
//        @Field("last_name")  last_name:String,
//        @Field("phone")  phone:String,
//        @Field("password")  password:String,
//        @Field("email")  email:String):Call<SendOTP>

    @PATCH("update")
    fun createUser(
        @Body jsonElement: JsonElement?,@Header("Authorization") authHeader:String):Call<JsonElement>


    @FormUrlEncoded
    @POST("token/sms")
    fun sendOtp(
        @Field("phone")  phone:String):Call<SendOTP>

    @POST("token/verify")
    fun verifyOtp(
        @Body jsonElement: JsonElement?,@Header("Authorization") authHeader:String):Call<JsonElement>

    @DELETE("posts/{id}")
    fun deletePost(@Path("id") id:Int):Call<String>


    @POST("login")
    fun loginUser(
        @Body jsonElement: JsonElement?
    ): Call<JsonElement>
}