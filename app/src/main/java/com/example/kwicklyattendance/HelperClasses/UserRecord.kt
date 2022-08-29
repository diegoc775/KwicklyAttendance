package com.example.kwicklyattendance.HelperClasses
//this is how the dynamo DB stores both student and admin data
data class UserRecord(
    val UserEmail : String,
    val Admin : Boolean,
    val FirstName : String,
    val LastName : String,
    val Password : String

){}
