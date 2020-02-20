package com.example.eventer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.eventer.models.eventsRepository

class EditEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        val event = eventsRepository.getEventById(intent.getIntExtra("id", 0))

        val titleEditText = findViewById<EditText>(R.id.title_edit_text)
        val contentEditText = findViewById<EditText>(R.id.content_edit_text)
        
        titleEditText.setText(event?.title)
        contentEditText.setText(event?.content)
        
        val saveButton = findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener {
            
            val title = this.findViewById<EditText>(
                R.id.title_edit_text
            ).editableText.toString()

            val content = this.findViewById<EditText>(
                R.id.content_edit_text
            ).editableText.toString()

            eventsRepository.updateEventById(event!!.id, title, content)
            this.finish()
        }
    }
}
