package com.logycraft.duzzcalll.data

import okhttp3.ResponseBody

data class GenericDataModel<T>(
    val isSuccess: Boolean?,
    val data: T?,
    val error: ResponseBody?,
    val Responcecode: Int?)
