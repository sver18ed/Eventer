package com.example.eventer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.eventer.models.eventsRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ViewEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_event)

        val titleText = findViewById<TextView>(R.id.title_text)
        val contentText = findViewById<TextView>(R.id.content_text)


        val db = FirebaseFirestore.getInstance()

        val event = db.collection("events")
            .document(intent.getStringExtra("id"))
        event.get().addOnSuccessListener {
            titleText.text = it.getString("title")
            contentText.text = it.getString("content")
        }

//        val event = eventsRepository.getEventById(intent.getIntExtra("id", 0))

//        titleText.text = event?.title
//        contentText.text = event?.content

        val editButton = findViewById<Button>(R.id.edit_button)
        editButton.setOnClickListener {
            val intent = Intent(this, EditEventActivity::class.java)
            intent.putExtra("id", event!!.id)
            startActivity(intent)
        }
    }

    override fun onRestart() {
        super.onRestart()
        this.recreate()
    }
}
