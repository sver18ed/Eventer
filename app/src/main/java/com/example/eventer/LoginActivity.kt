package com.example.eventer


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val returnButton = this.findViewById<ImageButton>(R.id.return_button_login)
        returnButton.setOnClickListener{
            finish()
        }
    }
}
