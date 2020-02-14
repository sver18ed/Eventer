package com.example.eventer


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val returnButton = this.findViewById<ImageButton>(R.id.return_button)
        returnButton.setOnClickListener{
            finish()
        }
        val createAccountButton = this.findViewById<Button>(R.id.create_account_button)
        createAccountButton.setOnClickListener{
            val intent = Intent(this, CreateAccount::class.java)
            startActivity(intent)
        }
    }
}
