package com.example.eventer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.eventer.models.Event
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

            val events = db.collection("events")
            val id = events.document().id

            val event = hashMapOf(
                "id" to id,
                "title" to title,
                "content" to content
            )

            events.document(id).set(event).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, ViewEventActivity::class.java)
                    intent.putExtra("event", Event(id, title, content))
                    startActivity(intent)
                    this.finish()
                }
            }
        }
    }
}


//          val id = db.collection("events").document().id