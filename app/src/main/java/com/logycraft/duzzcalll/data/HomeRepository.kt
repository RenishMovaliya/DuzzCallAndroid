package com.example.restapiidemo.home.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.restapiidemo.network.ApiClient
import com.example.restapiidemo.network.ApiInterface
import com.google.gson.JsonElement
import com.logycraft.duzzcalll.Util.Preference
import com.logycraft.duzzcalll.data.GenericDataModel
import com.logycraft.duzzcalll.data.SendOTP
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeRepository {

    private var apiInterface: ApiInterface? = null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    fun fetchAllPosts(): LiveData<List<UserModel>> {
        val data = MutableLiveData<List<UserModel>>()

        apiInterface?.fetchAllPosts()?.enqueue(object : Callback<List<UserModel>> {

            override fun onFailure(call: Call<List<UserModel>>, t: Throwable) {
                data.value = null
            }

            override fun onResponse(
                call: Call<List<UserModel>>, response: Response<List<UserModel>>
            ) {

                val res = response.body()
                if (response.code() == 200 && res != null) {
                    data.value = res
                } else {
                    data.value = null
                }

            }
        })

        return data

    }

    fun createUser(jsonElement: JsonElement, context: Context): LiveData<GenericDataModel<JsonElement>> {
        val data = MutableLiveData<GenericDataModel<JsonElement>>()

        Preference.getToken(context)?.let {
            apiInterface?.createUser(jsonElement, it)?.enqueue(object : Callback<JsonElement> {
                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    data.value = null
                }

                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
    //                val res = response.body()
    //                if (response.isSuccessful){
                    data.value = GenericDataModel(
                        response.isSuccessful, response.body(), response.errorBody(), response.code()
                    )
    //                }else{
    //                    data.value = GenericDataModel(response.isSuccessful,null,response.errorBody(),response.code())
    //                }
                }
            })
        }

        return data

    }

    fun sentOtp(phone: String): LiveData<GenericDataModel<SendOTP>> {
        val data = MutableLiveData<GenericDataModel<SendOTP>>()

        apiInterface?.sendOtp(phone)?.enqueue(object : Callback<SendOTP> {
            override fun onFailure(call: Call<SendOTP>, t: Throwable) {
                data.value = null
            }

            override fun onResponse(call: Call<SendOTP>, response: Response<SendOTP>) {
                val res = response.body()
//                GenericDataModel(response.isSuccessful,response.body(),"Test",false)
//                if (response.errorBody()!=null){
//                    val jObjError = JSONObject(response.errorBody()!!.string())
//                    Log.d("Something Went Wrong!",jObjError.getString("errors"))
//                }else{
//                    if (response.code() == 201 && res!=null){.
                if (response.isSuccessful) {
                    data.value = GenericDataModel(
                        response.isSuccessful,
                        response.body(),
                        response.errorBody(),
                        response.code()
                    )
                } else {
                    data.value = GenericDataModel(
                        response.isSuccessful, null, response.errorBody(), response.code()
                    )
                }

//                    }else{
//                        data.value = null
//                    }
//                }

            }
        })

        return data

    }

    fun verifyOtp(element: JsonElement, context: Context): LiveData<GenericDataModel<JsonElement>> {
        val data = MutableLiveData<GenericDataModel<JsonElement>>()

        Preference.getToken(context)?.let {
            apiInterface?.verifyOtp(element, it)
                ?.enqueue(object : Callback<JsonElement> {
                    override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                        data.value = null
                    }

                    override fun onResponse(
                        call: Call<JsonElement>, response: Response<JsonElement>
                    ) {
                        val res = response.body()
                        if (response.isSuccessful) {
                            data.value = GenericDataModel(
                                response.isSuccessful,
                                response.body(),
                                response.errorBody(),
                                response.code()
                            )
                        } else {
                            data.value = GenericDataModel(
                                response.isSuccessful, null, response.errorBody(), response.code()
                            )
                        }

                    }
                })
        }

        return data

    }


    fun loginuser(element: JsonElement): LiveData<GenericDataModel<JsonElement>> {
        val data = MutableLiveData<GenericDataModel<JsonElement>>()

        apiInterface?.loginUser(element)?.enqueue(object : Callback<JsonElement> {
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                data.value = null
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                val res = response.body()
                if (response.isSuccessful) {
                    data.value = GenericDataModel(
                        response.isSuccessful,
                        response.body(),
                        response.errorBody(),
                        response.code()
                    )
                } else {
                    data.value = GenericDataModel(
                        response.isSuccessful, null, response.errorBody(), response.code()
                    )
                }
            }
        })

        return data

    }

}