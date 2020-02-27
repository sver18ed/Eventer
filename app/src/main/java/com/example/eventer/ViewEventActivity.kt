package com.example.eventer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.eventer.models.Event

class ViewEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_event)

        val titleText = findViewById<TextView>(R.id.title_text)
        val contentText = findViewById<TextView>(R.id.content_text)

        val event = intent.extras?.get("event") as Event

        titleText.text = event.title
        contentText.text = event.content

        val editButton = findViewById<Button>(R.id.edit_button)
        editButton.setOnClickListener {
            val intent = Intent(this, EditEventActivity::class.java)
            intent.putExtra("event", event)
            startActivity(intent)
        }
    }

    override fun onRestart() {
        super.onRestart()
        this.recreate()
    }
}
