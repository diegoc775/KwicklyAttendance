package com.example.kwicklyattendance.ActivityClasses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.kwicklyattendance.HelperClasses.Dynamo_D_B_Connector
import com.example.kwicklyattendance.R
import com.example.kwicklyattendance.databinding.ActivityRemoveStudentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RemoveStudentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRemoveStudentBinding
    var emailToRemove = ""
    var studentDynamoDB = Dynamo_D_B_Connector()
    var firstName = ""
    var lastName = ""
    var email = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remove_student)

        binding = ActivityRemoveStudentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.enterEmailToRemove.bringToFront()

        binding.enterEmailToRemove.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(binding.enterEmailToRemove.textColors.equals(R.color.red)){
                    binding.enterEmailToRemove.setTextColor(getResources().getColor(R.color.black))
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                emailToRemove = s.toString()
            }
        })
        var goodRecord = arrayListOf<String>()

        binding.btnFinishRemove.setOnClickListener {
            if(emailToRemove.isEmpty()){
                Toast.makeText(applicationContext,"Please enter student's email", Toast.LENGTH_SHORT).show()
            }
            else{
                email = binding.enterEmailToRemove.text.toString()
                this.lifecycleScope.launch(Dispatchers.IO) {
                    goodRecord = studentDynamoDB.checkDBForStudent(emailToRemove)
                    GlobalScope.launch(Dispatchers.Main) {
                        if(goodRecord.size == 1){
                            binding.enterEmailToRemove.setTextColor(getResources().getColor(R.color.red))
                            Toast.makeText(applicationContext,"Email ID does not exist within database.\nPlease try again ", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            firstName = goodRecord.get(0)
                            lastName = goodRecord.get(1)

                            showAlertDialog()

                        }
                    }
                }
            }

        }
        binding.btnExitRemoveActivity.setOnClickListener {
            finish()
        }
    }
    fun showAlertDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Please Confirm Removal:")
        builder.setMessage(
            "First Name: ${firstName}\n" +
                    "Last Name: ${lastName}\n" + "Email: ${email}"
        )
        builder.setPositiveButton("Confirm") { dialog, which ->
            deleteUser()
        }
        builder.setNegativeButton("Back") { dialog, which ->
        }
        builder.show()
    }

    fun deleteUser(){
        studentDynamoDB.deleteStudent(email)
        binding.enterEmailToRemove.text.clear()
        Toast.makeText(applicationContext,"${firstName} ${lastName} has been removed", Toast.LENGTH_SHORT).show()
        firstName = ""
        lastName = ""
        email = ""
    }


}