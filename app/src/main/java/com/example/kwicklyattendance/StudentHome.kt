package com.example.kwicklyattendance

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kwicklyattendance.databinding.ActivityAddStudentBinding.inflate
import com.example.kwicklyattendance.databinding.ActivityAdminHomeBinding.inflate
import com.example.kwicklyattendance.databinding.ActivityAttendanceDetailsBinding.inflate
import com.example.kwicklyattendance.databinding.ActivityStudentHomeBinding
import com.example.kwicklyattendance.databinding.LoginActivityBinding.inflate
import java.time.LocalDateTime
import java.util.*

class StudentHome : AppCompatActivity() {
    private lateinit var binding: ActivityStudentHomeBinding
    val firstName = "Diego"
    val lastName = "Cobos"
    val email = "diego1385777@gmail.com"
    val passWord = "password"
    val admin = false
    var date = getCurrentDate()
    var studentDynamoDB = Dynamo_D_B_Connector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_student_home)
        binding = ActivityStudentHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val actionBar = supportActionBar
        actionBar!!.title = "${firstName} ${lastName} Home"
        binding.studentDateLabel.text = date

        doesDuplicatesExist(email, date)

        binding.btnCheckIn.setOnClickListener{
            if(studentDynamoDB.isGoodOrNah(email, date).equals(false)){
                Toast.makeText(applicationContext,"You have already checked in today. Please logout",Toast.LENGTH_SHORT).show()
                binding.btnCheckIn.setEnabled(false)
            }
            else{
                var newAttendanceRecord = AttendanceRecord(UUID.randomUUID().toString(), email, date)
                studentDynamoDB.createAttendanceRecord(newAttendanceRecord)
                Toast.makeText(applicationContext,"${firstName} has checked in today",Toast.LENGTH_SHORT).show()
            }

        }
        binding.btnFinishCheckIn.setOnClickListener{
            finish()
        }


    }
    fun getCurrentDate() :String{
        var cldr = Calendar.getInstance()
        val year = cldr.get(Calendar.YEAR)
        val month = cldr.get(Calendar.MONTH)
        val day = cldr.get(Calendar.DAY_OF_MONTH)
        val currDay = "${month+1}/${day}/${year}"
        return currDay
    }
    fun doesDuplicatesExist(email: String, date: String){
        if(studentDynamoDB.isGoodOrNah(email, date) == false){
            binding.btnCheckIn.setEnabled(false)
        }
        else{
            binding.btnCheckIn.setEnabled(true)
        }
    }
}