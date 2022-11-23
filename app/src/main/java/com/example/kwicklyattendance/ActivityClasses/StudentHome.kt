package com.example.kwicklyattendance.ActivityClasses

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.kwicklyattendance.HelperClasses.AttendanceRecord
import com.example.kwicklyattendance.HelperClasses.Dynamo_D_B_Connector
import com.example.kwicklyattendance.databinding.ActivityStudentHomeBinding
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.util.*
//By:Diego Cobos
class StudentHome : AppCompatActivity() {
    private lateinit var binding: ActivityStudentHomeBinding
    var firstName = ""
    var lastName = ""
    var email = ""
    val admin = false
    var date = getCurrentDate()
    var studentDynamoDB = Dynamo_D_B_Connector()
    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(this.applicationContext)
    }
    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    //big issue resolved on below line: "key=" needed to be added before the server actual server key string
    private val serverKey = "key=AAAAm7qMflk:APA91bGDp7UNb-JtEVPRLg4tSKYoM6SvPUHbSAgrzIN7K1gtP5_U8OYwe0FLuBTg-DQ-T2hfG5o1TjeNzk0cv1d6vwbVwW4ltjEdqOJvYWEUhMs2kLcWm1h2ghRR1A89bCjN7E6LReia"
    private val contentType = "application/json"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_student_home)
        binding = ActivityStudentHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/admin")
        val actionBar = supportActionBar
        val bundle: Bundle? = intent.extras
        val tempFirst :String?= bundle?.getString("FirstName")
        val tempLast :String?= bundle?.getString("LastName")
        val tempEmail :String?= bundle?.getString("Email")
        firstName = tempFirst.toString()
        lastName = tempLast.toString()
        email = tempEmail.toString()

        actionBar!!.title = "${firstName} ${lastName} Home"
        binding.studentDateLabel.text = date
        binding.btnCheckIn.setEnabled(false)
        attendanceRecordCoroutine()


        binding.btnCheckIn.setOnClickListener{
            val topic = "/topics/admin" //topic has to match what the receiver subscribed to
            //issue resolved on above line: "/topics/" needed to be added before the actual topic name
            val notification = JSONObject()
            val notifcationBody = JSONObject()

            try {
                notifcationBody.put("title", "Student Check In")
                notifcationBody.put("message","${firstName} ${lastName} has checked in for today" )   //Enter your notification message
                notification.put("to", topic)
                notification.put("data", notifcationBody)
                Log.e("TAG", "try")
            } catch (e: JSONException) {
                Log.e("TAG", "onCreate: " + e.message)
            }
            sendNotification(notification)



            var newAttendanceRecord = AttendanceRecord(UUID.randomUUID().toString(), email, date)
            studentDynamoDB.createAttendanceRecord(newAttendanceRecord)
            binding.btnCheckIn.setEnabled(false)



        }
        binding.btnFinishCheckIn.setOnClickListener{
            intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
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

    private fun sendNotification(notification: JSONObject) {
        Log.e("TAG", "sendNotification")
        val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener<JSONObject> { response ->
                Log.i("TAG", "onResponse: $response")
                //msg.setText("")
            }
            ,Response.ErrorListener {
                Toast.makeText(this@StudentHome, "OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", Toast.LENGTH_LONG).show()
                Log.i("TAG", "onErrorResponse: Didn't work")
            }) {

            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)
    }


}