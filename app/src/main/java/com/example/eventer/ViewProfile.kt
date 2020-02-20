package com.example.eventer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.eventer.models.UserRepository
import com.example.eventer.models.userRepository

class ViewProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)

//
//        val user = userRepository.getUserById(id)
//
//        val nameText = findViewById<TextView>(R.id.actual_name)
//        val emailText = findViewById<TextView>(R.id.actual_email)
//        val infoText = findViewById<TextView>(R.id.actual_info)
//
//        nameText.text = user!!.name
//        emailText.text = user.email
//        infoText.text = user.info

    }

}
