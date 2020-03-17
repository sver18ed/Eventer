package com.example.eventer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.eventer.models.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class EditEventActivity : AppCompatActivity() {

    private val TAG = "EditEventActivity"

    //UI elements
    private var titleEditText: EditText? = null
    private var contentEditText: EditText? = null
    private var saveButton: Button? = null

    //Firebase references
    private var firestore: FirebaseFirestore? = null
    private var eventsCollection: CollectionReference? = null
    private var auth: FirebaseAuth? = null

    //Global variables
    private var event: Event? = null
    private var title: String? = null
    private var content: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        initialise()
    }

    private fun initialise() {
        titleEditText = findViewById(R.id.title_edit_text)
        contentEditText = findViewById(R.id.description_edit_text)
        saveButton = findViewById(R.id.save_button)

        firestore = FirebaseFirestore.getInstance()
        eventsCollection = firestore!!.collection("events")
        auth = FirebaseAuth.getInstance()

        event = intent.extras?.get("event") as Event
        titleEditText!!.setText(event?.title)
        contentEditText!!.setText(event?.description)

        saveButton!!.setOnClickListener {
            updateEvent()
        }
    }

    private fun updateEvent() {
        title = titleEditText!!.editableText.toString()
        content = contentEditText!!.editableText.toString()

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
            eventsCollection!!.document(event!!.id).update(mapOf(
                "title" to title,
                "content" to content
            )).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "update:success")
                    Toast.makeText(
                        this,
                        "Event successfully updated!",
                        Toast.LENGTH_SHORT
                    ).show()
                    this.finish()
                }
                else {
                    Log.d(TAG, "update:failure", task.exception)
                    Toast.makeText(
                        this,
                        "Event failed to update...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                this,
                "You must enter all fields...",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
