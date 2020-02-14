package com.example.eventer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class CreateAccount : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val returnButton = this.findViewById<ImageButton>(R.id.return_button_create)
        returnButton.setOnClickListener{
            finish()
        }
    }
}
