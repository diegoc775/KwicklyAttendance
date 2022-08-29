package com.example.kwicklyattendance.HelperClasses
//how attendance records are stored within DynamoDB
data class AttendanceRecord(
    val id: String,
    val email: String,
    val dateAttended: String

){}
