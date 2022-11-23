package com.example.kwicklyattendance.ActivityClasses

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.kwicklyattendance.HelperClasses.Dynamo_D_B_Connector
import com.example.kwicklyattendance.R
import com.example.kwicklyattendance.databinding.LoginActivityBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
//By:Diego Cobos
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginActivityBinding
    var enteredEmail = ""
    var enteredPassword = ""
    var adminOrNah = false
    var studentDynamoDB = Dynamo_D_B_Connector()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.login_activity)
        binding = LoginActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        //Email EDITTEXT Listener
        binding.enterEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                enteredEmail = s.toString()
            }
        })
        //Password EDITTEXT Listener
        binding.enterPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                enteredPassword = s.toString()
            }
        })
        //RADIO BUTTON CHECK CHANGED LISTENER
        setOnCheckedChangeListener()



        binding.btnLogin.setOnClickListener{
            val checkedOrNah = binding.radioGrp.checkedRadioButtonId.toString()
            if(enteredEmail == "" || enteredPassword == "" || checkedOrNah == "-1"){
                Toast.makeText(this, "Please fill out the fields", Toast.LENGTH_SHORT).show()
            }
            else{
                if (checkForInternet(this)) {
                    /////////////////////////////////This is just for testing purposes//////////////////////
//                    intent = Intent(applicationContext, StudentHome::class.java)
//                    startActivity(intent)
                    ///////////////////////////////////////////////////////////////////////////////////////
                    var goodRecord = arrayListOf<String>()
                    enteredEmail = binding.enterEmail.text.toString()
                    this.lifecycleScope.launch(Dispatchers.IO) {
                        goodRecord = studentDynamoDB.checkDBForStudent(enteredEmail)
                        GlobalScope.launch(Dispatchers.Main) {
                            if(goodRecord.size == 1){
                                Toast.makeText(applicationContext,"Invalid User ID in DB.\nPlease try again ", Toast.LENGTH_SHORT).show()
                            }
                            else{
                                val firstName = goodRecord.get(0)
                                val lastName = goodRecord.get(1)
                                val password = goodRecord.get(2)
                                val tempAdmin = goodRecord.get(3)
                                if(adminOrNah.toString().equals(tempAdmin) && enteredPassword.equals(password) && adminOrNah.equals(true)){
                                    val intent = Intent(applicationContext, AdminHome::class.java)
                                    intent.putExtra("FirstName", firstName)
                                    intent.putExtra("LastName", lastName)
                                    intent.putExtra("Email", enteredEmail)
                                    binding.enterEmail.text.clear()
                                    binding.enterPassword.text.clear()
                                    binding.radioGrp.clearCheck()
                                    startActivity(intent)
                                }
                                else if(adminOrNah.toString().equals(tempAdmin) && enteredPassword.equals(password) && adminOrNah.equals(false)){
                                    val intent = Intent(applicationContext, StudentHome::class.java)
                                    intent.putExtra("FirstName", firstName)
                                    intent.putExtra("LastName", lastName)
                                    intent.putExtra("Email", enteredEmail)
                                    binding.enterEmail.text.clear()
                                    binding.enterPassword.text.clear()
                                    binding.radioGrp.clearCheck()
                                    startActivity(intent)
                                }
                                else{
                                    Toast.makeText(applicationContext,"Invalid Credentials \n Please try again", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Internet Connection Unavaliable. Please try again", Toast.LENGTH_SHORT).show()
                }
            }


        }
    }


    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        }
        else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
    private fun setOnCheckedChangeListener() {
        binding.radioGrp.setOnCheckedChangeListener { group, checkedId ->

            if(R.id.rBtnAdmin == checkedId){
                adminOrNah = true
            }
            else{
                adminOrNah = false
            }
        }
    }

}