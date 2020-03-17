package com.example.eventer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.eventer.models.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ViewEventActivity : AppCompatActivity() {

    private val TAG = "ViewEventActivity"

    //UI elements
    private var titleText: TextView? = null
    private var descriptionText: TextView? = null
    private var createdByText: TextView? = null
    private var editEventButton: Button? = null

    //Firebase references
    private var firestore: FirebaseFirestore? = null
    private var eventsCollection: CollectionReference? = null
    private var eventDocument: DocumentReference? = null
    private var auth: FirebaseAuth? = null

    //Global variables
    private var event: Event? = null
    private var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_event)

        initialise()
    }

    private fun initialise() {
        id = intent.extras!!.get("id") as String?

        titleText = findViewById(R.id.title_text)
        descriptionText = findViewById(R.id.description_text)
        createdByText = findViewById(R.id.created_by_text)
        editEventButton = findViewById(R.id.edit_event_button)

        firestore = FirebaseFirestore.getInstance()
        eventsCollection = firestore!!.collection("events")
        eventDocument = eventsCollection!!.document(id!!)
        auth = FirebaseAuth.getInstance()

        //event = intent.extras?.get("event") as Event

        getEventFromFirestore()

        editEventButton!!.setOnClickListener {
            startActivity(Intent(this, EditEventActivity::class.java
            ).putExtra("event", event))
        }

    }

    private fun getEventFromFirestore() {
        eventDocument!!.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e(TAG, "eventDocument.get():success")
                event = task.result!!.toObject(Event::class.java)

                titleText!!.text = event!!.title
                descriptionText!!.text = event!!.description
                createdByText!!.text = event!!.created_by
            }
            else {
                Log.e(TAG, "eventDocument.get():failure", task.exception)
                Toast.makeText(
                    this,
                    "Couldn't fetch event from server",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        this.recreate()
    }
}