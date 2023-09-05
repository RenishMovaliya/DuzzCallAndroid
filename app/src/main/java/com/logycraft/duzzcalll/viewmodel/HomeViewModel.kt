package com.logycraft.duzzcalll.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.restapiidemo.home.data.HomeRepository
import com.google.gson.JsonElement
import com.logycraft.duzzcalll.data.BusinessResponce
import com.logycraft.duzzcalll.data.GenericDataModel
import com.logycraft.duzzcalll.data.LoginData
import com.logycraft.duzzcalll.data.SendOTP

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private var homeRepository: HomeRepository? = null
    var sentOtpLiveData: LiveData<GenericDataModel<SendOTP>>? = null
    var verifyOtpLiveData: LiveData<GenericDataModel<LoginData>>? = null
    var userLiveData: LiveData<GenericDataModel<JsonElement>>? = null
    var loginuserLiveData: LiveData<GenericDataModel<JsonElement>>? = null
    var getbusinessLiveData: LiveData<GenericDataModel<List<BusinessResponce>>>? = null
    var deletePostLiveData: LiveData<Boolean>? = null

    init {
        homeRepository = HomeRepository()
        sentOtpLiveData = MutableLiveData()
        deletePostLiveData = MutableLiveData()
    }


    fun sentOtp(postModel: String) {
        sentOtpLiveData = homeRepository?.sentOtp(postModel)
    }

    fun verifyOtp(element: JsonElement, context: Context) {
        verifyOtpLiveData = homeRepository?.verifyOtp(element, context)
    }

    fun createUser(element: JsonElement, context: Context) {
        userLiveData = homeRepository?.createUser(element, context)
    }

    fun loginUser(element: JsonElement) {
        loginuserLiveData = homeRepository?.loginuser(element)
    }

    fun getBusiness(context: Context) {
        getbusinessLiveData = homeRepository?.getBusinessContact(context)
    }


}