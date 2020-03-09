package com.example.eventer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class CreateEventActivity : AppCompatActivity() {

    private val TAG = "CreateEventActivity"

    //UI elements
    private var titleEditText: EditText? = null
    private var contentEditText: EditText? = null
    private var saveButton: Button? = null

    //Firebase references
    private var firestore: FirebaseFirestore? = null
    private var eventsCollection: CollectionReference? = null
    private var usersCollection: CollectionReference? = null
    private var auth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null

    //Global variables
    private var id: String? = null
    private var title: String? = null
    private var content: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        initialise()
    }

    private fun initialise() {
        titleEditText = findViewById(R.id.title_edit_text)
        contentEditText = findViewById(R.id.content_edit_text)
        saveButton = findViewById(R.id.save_button)

        firestore = FirebaseFirestore.getInstance()
        eventsCollection = firestore!!.collection("events")
        usersCollection = firestore!!.collection("users")
        auth = FirebaseAuth.getInstance()
        currentUser = auth!!.currentUser

        saveButton!!.setOnClickListener {
            createNewEvent()
        }
    }

    private fun createNewEvent() {
        id = eventsCollection!!.document().id
        title = titleEditText?.text.toString()
        content = contentEditText?.text.toString()

        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java
            ).putExtra("message", "Please login to create event!"))
        }

        else if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {

            val event = hashMapOf(
                "id" to id,
                "title" to title,
                "content" to content,
                "created_by" to currentUser!!.email
            )

            eventsCollection!!.document(id!!).set(event).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e(TAG, "createEvent:success")
                    updateUI()
                } else {
                    Log.e(TAG, "createEvent:failure", task.exception)
                    Toast.makeText(
                        this,
                        "Event couldn't be created",
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

    private fun updateUI() {
        val intent = Intent(this, ViewEventActivity::class.java)
        intent.putExtra("id", id)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}