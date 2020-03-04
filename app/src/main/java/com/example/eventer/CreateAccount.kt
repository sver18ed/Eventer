package com.example.eventer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.google.firebase.firestore.FirebaseFirestore

class CreateAccount : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val emailEditText = findViewById<EditText>(R.id.email_create)
        val passwordEditText = findViewById<EditText>(R.id.password_create)

        val db = FirebaseFirestore.getInstance()

        val createButton = findViewById<Button>(R.id.ok_create)
        createButton.setOnClickListener {

            val email = emailEditText.editableText.toString()
            val password = passwordEditText.editableText.toString()

            val users = db.collection("users")

            val user = hashMapOf(
                "email" to email,
                "password" to password
            )

            users.document(email).set(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    this.finish()
                }
            }
        }

        val returnButton = this.findViewById<ImageButton>(R.id.return_button_create)
        returnButton.setOnClickListener{
            finish()
        }
    }
}
