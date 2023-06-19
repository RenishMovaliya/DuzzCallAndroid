package com.logycraft.duzzcalll.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.restapiidemo.home.data.HomeRepository
import com.google.gson.JsonElement
import com.logycraft.duzzcalll.data.GenericDataModel
import com.logycraft.duzzcalll.data.SendOTP

class HomeViewModel(application: Application): AndroidViewModel(application){

    private var homeRepository:HomeRepository?=null
    var sentOtpLiveData:LiveData<GenericDataModel<SendOTP>>?=null
    var verifyOtpLiveData:LiveData<GenericDataModel<JsonElement>>?=null
    var deletePostLiveData:LiveData<Boolean>?=null

    init {
        homeRepository = HomeRepository()
        sentOtpLiveData = MutableLiveData()
        deletePostLiveData = MutableLiveData()
    }



    fun sentOtp(postModel: String){
        sentOtpLiveData = homeRepository?.sentOtp(postModel)
    }

    fun verifyOtp(phone: String,otp: String){
        verifyOtpLiveData = homeRepository?.verifyOtp(phone,otp)
    }



}