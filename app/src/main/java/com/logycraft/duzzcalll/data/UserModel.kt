package com.example.restapiidemo.home.data

data class UserModel(
    var first_name:String?="",
    var last_name:String?="",
    var phone:String?="",
    var password:String?="",
    var email:String?="",
    var profileimg:String?="",
    var extension:String?=""
)