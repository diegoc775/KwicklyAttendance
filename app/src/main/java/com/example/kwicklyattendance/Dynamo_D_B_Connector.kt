package com.example.kwicklyattendance

import android.app.Activity
import androidx.lifecycle.lifecycleScope
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.internal.StaticCredentialsProvider
import com.amazonaws.mobileconnectors.dynamodbv2.document.ScanFilter
import com.amazonaws.mobileconnectors.dynamodbv2.document.ScanOperationConfig
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive
import com.amazonaws.regions.Region
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Dynamo_D_B_Connector {



    //"AKIAXW24I7IMEGYSPBMO" "2vkYUJPCR7TXLWrq4sCG5b+TSWO3WqnDTbvpWc4s"
    //access/secretKey is how to login to the AWS account
    private val credentialsProvider =
        StaticCredentialsProvider(
            BasicAWSCredentials(
                "AKIAXW24I7IMJ2AG25EB",
                "sX1nS/4BAvKGXff/L2CWGyD3u5Md/I+htIKFRDPW\n")
        )

    //Use the credentials to log into the server where the account is located
    private val dbClient =
        AmazonDynamoDBClient(credentialsProvider).apply {
            setRegion(Region.getRegion("us-east-2"))
        }

    private lateinit var table: Table

    private suspend fun getTable(): Table {
        if (::table.isInitialized)
            return table
        return suspendCoroutine { continuation ->
            table = Table.loadTable(dbClient, "Attendance_Records")
            continuation.resume(table)
        }
    }

    //pull all attendance records from Dynamo client
     fun getAllAttendances() {
        execute {
            val attendances = arrayListOf<AttendanceRecord>()
            getTable().scan(
                ScanOperationConfig()
            ).allResults.forEach {
                attendances.add(Gson().fromJson(Document.toJson(it), AttendanceRecord::class.java))
            }
            attendances.forEach {

                println(" Got Attendance id ${it.id}")
                println(" Got Attendance email ${it.email}")
                println(" Got Attendance dateAttended ${it.dateAttended}")
            }
        }
    }





    //Used to collect data for arraylist of the recyclerview in student detail activity
     fun putSpecificAttendancesIntoArrList(email: String, otherArrLst: ArrayList<String>, activity : AttendanceDetailsInterface) {
        execute {
            val attendances = arrayListOf<AttendanceRecord>()
            val attributeValues = mutableListOf<AttributeValue>()
            attributeValues.add(AttributeValue(email))

            val scanFilter = ScanFilter("email", ComparisonOperator.EQ, attributeValues)
            getTable().scan( scanFilter
            ).allResults.forEach {
                attendances.add(Gson().fromJson(Document.toJson(it), AttendanceRecord::class.java))
            }
            attendances.forEach {
//                println(" Got Attendance id ${it.id}")
//                println(" Got Attendance email ${it.email}")
//                println(" Got Attendance dateAttended ${it.dateAttended}")
                otherArrLst.add(it.dateAttended)
            }
            activity.populate(otherArrLst)
        }
    }

    //Initialize and insert item with data of ${attendance} into DB
    public fun createAttendanceRecord(attendance: AttendanceRecord) {
        execute {
            getTable().putItem(Document.fromJson(Gson().toJson(attendance)))
            println("Record with id: ${attendance.id} successfully created")
        }
    }



    //delete item with ID: ${id}
    private fun deleteAttendance(id: String) {
        execute {
            getTable().deleteItem(Primitive(id))
        }
    }

    // Function that RETURNS VALUE FROM COROUTINE
    //you have to use lifecycle.scope and make another coroutine when you call this
    suspend fun notDoneYet(studentEmail: String, today: String): Boolean {
        return withContext(Dispatchers.IO) {
            // Blocking network request code
            var check = true
            val attendances = arrayListOf<AttendanceRecord>()
            val attributeValues = mutableListOf<AttributeValue>()
            attributeValues.add(AttributeValue(studentEmail))

            val scanFilter = ScanFilter("email", ComparisonOperator.EQ, attributeValues)
            getTable().scan( scanFilter
            ).allResults.forEach {
                attendances.add(Gson().fromJson(Document.toJson(it), AttendanceRecord::class.java))
            }
            attendances.forEach {

                if(it.dateAttended.equals(today)){
                    check = false


                }
            }

            return@withContext check
        }

    }

    //starts coroutine scope
    private fun execute(executionBlock: suspend CoroutineScope.() -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                executionBlock.invoke(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}


