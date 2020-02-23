package com.example.eventer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore

class CreateEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        val titleEditText = findViewById<EditText>(R.id.title_edit_text)
        val contentEditText = findViewById<EditText>(R.id.content_edit_text)

        val db = FirebaseFirestore.getInstance()

        val saveButton = findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener {

            val title = titleEditText.editableText.toString()
            val content = contentEditText.editableText.toString()

            val event = hashMapOf(
                "title" to title,
                "content" to content
            )

            db.collection("events").add(event)

            val intent = Intent(this, ViewEventActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }
}
