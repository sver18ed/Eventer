package com.example.eventer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.eventer.models.Event
import com.google.firebase.firestore.FirebaseFirestore

class EditEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        val titleEditText = findViewById<EditText>(R.id.title_edit_text)
        val contentEditText = findViewById<EditText>(R.id.content_edit_text)

        val db = FirebaseFirestore.getInstance()

        val event = intent.extras?.get("event") as Event
        
        titleEditText.setText(event.title)
        contentEditText.setText(event.content)
        
        val saveButton = findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener {
            
            val title = this.findViewById<EditText>(
                R.id.title_edit_text
            ).editableText.toString()

            val content = this.findViewById<EditText>(
                R.id.content_edit_text
            ).editableText.toString()

            val event = db.collection("events").document(event.id)
            event.update(mapOf(
                    "title" to title,
                    "content" to content
                ))
            this.finish()
        }
    }
}
