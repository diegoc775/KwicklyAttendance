package com.example.kwicklyattendance

import CustomAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kwicklyattendance.databinding.ActivityAttendanceDetailsBinding
import com.example.kwicklyattendance.databinding.ActivityStudentHomeBinding

class AttendanceDetailsActivity : AppCompatActivity(), AttendanceDetailsInterface {
    private lateinit var binding: ActivityAttendanceDetailsBinding
    var studentFirstName = "Diego"
    var studentLastName = "Cobos"
    val email = "diego1385777@gmail.com"
    val passWord = "password"
    val admin = false
    var studentDynamoDB = Dynamo_D_B_Connector()
    var itmsData = ArrayList<ItemsViewModel>()
    var recyclerview : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_details)
        recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        val daysAttended = arrayListOf<String>()
        recyclerview?.layoutManager = LinearLayoutManager(this)
        val adapter = CustomAdapter(itmsData)
        recyclerview?.adapter = adapter
        studentDynamoDB.putSpecificAttendancesIntoArrList(email, daysAttended, this)
    }

    public override fun populate(daysAttended : ArrayList<String>){
        itmsData.clear()
        for(i in 0..daysAttended.size-1){
            itmsData.add(ItemsViewModel(R.drawable.ic_launcher_foreground, daysAttended.get(i)))
        }
        recyclerview?.adapter?.notifyDataSetChanged()
    }
}