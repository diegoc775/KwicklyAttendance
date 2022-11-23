package com.example.kwicklyattendance.ActivityClasses

import com.example.kwicklyattendance.HelperClasses.CustomAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kwicklyattendance.HelperClasses.Dynamo_D_B_Connector
import com.example.kwicklyattendance.Interfaces.AttendanceDetailsInterface
import com.example.kwicklyattendance.HelperClasses.ItemsViewModel
import com.example.kwicklyattendance.R
import com.example.kwicklyattendance.databinding.ActivityAttendanceDetailsBinding
//By:Diego Cobos
class AttendanceDetailsActivity : AppCompatActivity(), AttendanceDetailsInterface {
    private lateinit var binding: ActivityAttendanceDetailsBinding
    var studentFirstName = ""
    var studentLastName = ""
    var email = ""
    //val passWord = "password"
    val admin = false
    var studentDynamoDB = Dynamo_D_B_Connector()
    var itmsData = ArrayList<ItemsViewModel>()
    var recyclerview : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_details)
        val actionBar = supportActionBar
        actionBar!!.title = "Student Attendance Details"
        val bundle: Bundle? = intent.extras
        val tempFirst :String?= bundle?.getString("FirstName")
        val tempLast :String?= bundle?.getString("LastName")
        val tempEmail :String?= bundle?.getString("Email")
        studentFirstName = tempFirst.toString()
        studentLastName = tempLast.toString()
        email = tempEmail.toString()
        var attendanceLabel = findViewById<TextView>(R.id.attendanceLabel)
        var btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
        attendanceLabel.text = "${studentFirstName}'s Records"
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
            itmsData.add(ItemsViewModel(R.drawable.attendance_icon_no_background, daysAttended.get(i)))
        }
        recyclerview?.adapter?.notifyDataSetChanged()
    }
}