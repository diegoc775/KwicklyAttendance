package com.example.kwicklyattendance

import android.app.PendingIntent.getActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.kwicklyattendance.databinding.ActivityAddStudentBinding.inflate
import com.example.kwicklyattendance.databinding.ActivityAdminHomeBinding.inflate
import com.example.kwicklyattendance.databinding.ActivityAttendanceDetailsBinding.inflate
import com.example.kwicklyattendance.databinding.ActivityStudentHomeBinding
import com.example.kwicklyattendance.databinding.LoginActivityBinding.inflate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
        binding.btnCheckIn.setEnabled(false)
        attendanceRecordCoroutine()


        binding.btnCheckIn.setOnClickListener{
            var newAttendanceRecord = AttendanceRecord(UUID.randomUUID().toString(), email, date)
            studentDynamoDB.createAttendanceRecord(newAttendanceRecord)
            binding.btnCheckIn.setEnabled(false)
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
    fun attendanceRecordCoroutine(){
        this.lifecycleScope.launch(Dispatchers.IO) {
            var check = studentDynamoDB.notDoneYet(email, date)
            if(check == false){
                GlobalScope.launch(Dispatchers.Main) {
                    Toast.makeText(applicationContext,"You have already checked in today. Please logout",Toast.LENGTH_SHORT).show()
                    //binding.btnCheckIn.setEnabled(false)
                }

            }
            else{


                GlobalScope.launch(Dispatchers.Main) {
                    Toast.makeText(applicationContext,"${firstName} ${lastName}: Please click to check in",Toast.LENGTH_SHORT).show()
                    binding.btnCheckIn.setEnabled(true)
                }

            }
        }
    }
}