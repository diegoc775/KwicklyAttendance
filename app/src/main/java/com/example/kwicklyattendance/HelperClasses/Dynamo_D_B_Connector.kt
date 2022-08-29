package com.example.kwicklyattendance.HelperClasses

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
import com.example.kwicklyattendance.Interfaces.AttendanceDetailsInterface
import com.example.kwicklyattendance.Interfaces.studentsRVInterface
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Dynamo_D_B_Connector {
    //The file with the majority of the interaction code between AWS and the application


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

    //get the table for the attendance data in student details activity
    private suspend fun getAttendanceTable(): Table {
        if (::table.isInitialized)
            return table
        return suspendCoroutine { continuation ->
            table = Table.loadTable(dbClient, "Attendance_Records")
            continuation.resume(table)
        }
    }

    //get the table for the student data in adminhome
    private suspend fun getUserTable(): Table {
        if (::table.isInitialized)
            return table
        return suspendCoroutine { continuation ->
            table = Table.loadTable(dbClient, "Users")
            continuation.resume(table)
        }
    }

    //pull all attendance records from Dynamo client
     fun getAllAttendances() {
        execute {
            val attendances = arrayListOf<AttendanceRecord>()
            getAttendanceTable().scan(
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


    //get all student records from dynamo db client
    fun getAllStudents(activity : studentsRVInterface){
        execute {
            var students = arrayListOf<UserRecord>()
            val users = arrayListOf<UserRecord>()
            getUserTable().scan(
                ScanOperationConfig()
            ).allResults.forEach {
                users.add(Gson().fromJson(Document.toJson(it), UserRecord::class.java))
            }
            users.forEach {
                if(it.Admin.equals(false)){
                    students.add(it)

                }
//                println(" Got Attendance id ${it.id}")
//                println(" Got Attendance email ${it.email}")
//                println(" Got Attendance dateAttended ${it.dateAttended}")
            }
            activity.populate(students)
        }
    }

    //Used to collect data for arraylist of the recyclerview in student detail activity
     fun putSpecificAttendancesIntoArrList(email: String, otherArrLst: ArrayList<String>, activity : AttendanceDetailsInterface) {
        execute {
            val attendances = arrayListOf<AttendanceRecord>()
            val attributeValues = mutableListOf<AttributeValue>()
            attributeValues.add(AttributeValue(email))

            val scanFilter = ScanFilter("email", ComparisonOperator.EQ, attributeValues)
            getAttendanceTable().scan( scanFilter
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

    //get student profile of what youre trying to delete
    suspend fun checkDBForStudent(email: String):ArrayList<String>  {
        return withContext(Dispatchers.IO){
            var toReturn = arrayListOf<String>()
            val numsOfStudent = arrayListOf<UserRecord>()
            val attributeValues = mutableListOf<AttributeValue>()
            attributeValues.add(AttributeValue(email))

            val scanFilter = ScanFilter("UserEmail", ComparisonOperator.EQ, attributeValues)
            getUserTable().scan( scanFilter
            ).allResults.forEach {
                numsOfStudent.add(Gson().fromJson(Document.toJson(it), UserRecord::class.java))
            }
            if(numsOfStudent.size > 0){
                numsOfStudent.forEach {
                    toReturn.add(it.FirstName)
                    toReturn.add(it.LastName)
                    toReturn.add(it.Password)
                    toReturn.add(it.Admin.toString())
                }
            }
            else{
                toReturn.add("Database does not contain this email")

            }


            return@withContext toReturn

        }
    }

    //Initialize and insert item with data of ${attendance} into DB
    public fun createAttendanceRecord(attendance: AttendanceRecord) {
        execute {
            getAttendanceTable().putItem(Document.fromJson(Gson().toJson(attendance)))
            println("Record with id: ${attendance.id} successfully created")
        }
    }
    //Initialize and insert item with data of inputted user data into DB
    public fun createUserRecord(user: UserRecord) {
        execute {
            getUserTable().putItem(Document.fromJson(Gson().toJson(user)))
            println("Record name: ${user.FirstName} successfully created")
        }
    }



    //delete item with ID: ${id}
    fun deleteStudent(id: String) {
        execute {
            getUserTable().deleteItem(Primitive(id))
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
            getAttendanceTable().scan( scanFilter
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


