package com.example.kwicklyattendance

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kwicklyattendance.databinding.ActivityStudentHomeBinding
import com.example.kwicklyattendance.databinding.LoginActivityBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.login_activity)
        binding = LoginActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        binding.btnLogin.setOnClickListener{
            /////////////////////////////////This is just for testing purposes//////////////////////
            intent = Intent(applicationContext, StudentHome::class.java)
            startActivity(intent)
            ///////////////////////////////////////////////////////////////////////////////////////
        }
    }
}