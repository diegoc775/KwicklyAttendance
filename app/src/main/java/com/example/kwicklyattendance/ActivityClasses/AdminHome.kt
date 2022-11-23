package com.example.kwicklyattendance.ActivityClasses

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kwicklyattendance.*
import com.example.kwicklyattendance.HelperClasses.Dynamo_D_B_Connector
import com.example.kwicklyattendance.HelperClasses.UserRecord
import com.example.kwicklyattendance.HelperClasses.studentsCustomAdapter
import com.example.kwicklyattendance.HelperClasses.studentsItemsVM
import com.example.kwicklyattendance.Interfaces.studentsClickInterface
import com.example.kwicklyattendance.Interfaces.studentsRVInterface
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
//By:Diego Cobos
class AdminHome : AppCompatActivity(), studentsRVInterface, studentsClickInterface {
    var firstName = ""
    var lastName = ""
    var email = ""
    val admin = true
    var studentDynamoDB = Dynamo_D_B_Connector()
    var studentItmsData = ArrayList<studentsItemsVM>()
    var studentRecyclerView : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)
        val btnAddStudent = findViewById<Button>(R.id.btnAddStudent)
        val btnRemoveStudent = findViewById<Button>(R.id.btnRemoveStudent)
        val btnLogOut = findViewById<Button>(R.id.btnLogOut)
        studentRecyclerView = findViewById<RecyclerView>(R.id.studentRecyclerView)

        val actionBar = supportActionBar
        val bundle: Bundle? = intent.extras
        val tempFirst :String?= bundle?.getString("FirstName")
        val tempLast :String?= bundle?.getString("LastName")
        val tempEmail :String?= bundle?.getString("Email")
        firstName = tempFirst.toString()
        lastName = tempLast.toString()
        email = tempEmail.toString()

        actionBar!!.title = "${firstName} ${lastName} Home"
        //Toast.makeText(applicationContext,"Welcome ${firstName}!", Toast.LENGTH_SHORT).show()



        studentRecyclerView?.layoutManager = LinearLayoutManager(this)
        val studentAdapter = studentsCustomAdapter(studentItmsData, this)
        studentRecyclerView?.adapter = studentAdapter
        studentDynamoDB.getAllStudents( this)
        studentRecyclerView?.adapter?.notifyDataSetChanged()


        btnAddStudent.setOnClickListener{
            intent = Intent(applicationContext, AddStudentActivity::class.java)
            startActivity(intent)
        }
        btnRemoveStudent.setOnClickListener{
            intent = Intent(applicationContext, RemoveStudentActivity::class.java)
            startActivity(intent)
        }
        btnLogOut.setOnClickListener {
            finish()
        }
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/admin")


    }
    //////////////////////////////////This is what allows the recyclerview to be updated on the scroll up/////////////////////
    override fun onResume(){
        super.onResume()
        println("This is the onresume being called")
        studentDynamoDB.getAllStudents( this)



    }
    /////////////////////////////////Please implement a view model to allow us to remove this and have it immediate respond on
    /////////////////////////////////////////////////// a data set change///////////////////////////////////////////////



    public override fun populate(records : ArrayList<UserRecord>){
        studentItmsData.clear()
        for(i in 0..records.size-1){
            studentItmsData.add(studentsItemsVM(records.get(i).FirstName, records.get(i).LastName, records.get(i).UserEmail))
        }
        studentRecyclerView?.adapter?.notifyDataSetChanged()
    }

    override fun onItemClick(student: studentsItemsVM) {
        val intent = Intent(applicationContext, AttendanceDetailsActivity::class.java)
        intent.putExtra("FirstName", student.studentFirstName)
        intent.putExtra("LastName", student.studentLastName)
        intent.putExtra("Email", student.studentEmail)
        startActivity(intent)
    }
}