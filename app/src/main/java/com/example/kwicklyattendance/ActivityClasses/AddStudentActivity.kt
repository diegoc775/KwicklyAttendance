package com.example.kwicklyattendance.ActivityClasses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.kwicklyattendance.HelperClasses.Dynamo_D_B_Connector
import com.example.kwicklyattendance.HelperClasses.UserRecord
import com.example.kwicklyattendance.databinding.ActivityAddStudentBinding

class AddStudentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStudentBinding
    var firstNameToAdd = ""
    var lastNameToAdd = ""
    var emailToAdd = ""
    var passwordToAdd = ""
    var adminOrNah = false
    var studentDynamoDB = Dynamo_D_B_Connector()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_add_student)
        binding = ActivityAddStudentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.addStudentFirstName.bringToFront()
        binding.addStudentLastName.bringToFront()
        binding.addStudentEmail.bringToFront()
        binding.addStudentPassword.bringToFront()
        binding.btnAddToClass.setOnClickListener{
            if(firstNameToAdd.isEmpty() || lastNameToAdd.isEmpty() || emailToAdd.isEmpty() || passwordToAdd.isEmpty()){
                Toast.makeText(applicationContext,"Please fill in all fields",Toast.LENGTH_SHORT).show()
            }
            else{
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Review Student")
                builder.setMessage("Name: ${firstNameToAdd} ${lastNameToAdd} \n" +
                        "Email: ${emailToAdd} \nPassword: ${passwordToAdd}")

                builder.setPositiveButton("Finish") { dialog, which ->
                    var newUserRecord = UserRecord(emailToAdd, adminOrNah, firstNameToAdd, lastNameToAdd, passwordToAdd)
                    studentDynamoDB.createUserRecord(newUserRecord)
                    Toast.makeText(applicationContext,"${firstNameToAdd} ${lastNameToAdd} has been added", Toast.LENGTH_SHORT).show()
                    binding.addStudentFirstName.text.clear()
                    binding.addStudentLastName.text.clear()
                    binding.addStudentEmail.text.clear()
                    binding.addStudentPassword.text.clear()
                    firstNameToAdd = ""
                    lastNameToAdd = ""
                    emailToAdd = ""
                    passwordToAdd = ""

                }

                builder.setNegativeButton("Edit") { dialog, which ->

                }


                builder.show()
            }
        }

        binding.btnExit.setOnClickListener {
            finish()
        }


///////////////////////////////////////EditTexts on txtchngd lstnrs/////////////////////////////////
        binding.addStudentFirstName.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                firstNameToAdd = s.toString()
            }
        })
        binding.addStudentLastName.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                lastNameToAdd = s.toString()
            }
        })
        binding.addStudentEmail.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                emailToAdd = s.toString()
            }
        })
        binding.addStudentPassword.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                passwordToAdd = s.toString()
            }
        })
///////////////////////////////////////////////////////////////////////////////////////////////////

    }
}