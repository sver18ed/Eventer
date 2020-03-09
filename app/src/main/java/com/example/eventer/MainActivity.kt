package com.example.eventer

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.eventer.models.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    //UI elements
    private var createEventButton: Button? = null
    private var loginButton: Button? = null
    private var logoutButton: Button? = null
    private var mapButton: Button? = null
    private var loggedInText: TextView? = null
    private var eventsListView: ListView? = null

    //Firebase references
    private var firestore: FirebaseFirestore? = null
    private var eventsCollection: CollectionReference? = null
    private var auth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null

    //Global variables
    private var listOfEvents: ArrayList<Event>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialise()
    }

    private fun initialise() {
        //Initialising UIs
        createEventButton = findViewById(R.id.create_event_button)
        loginButton = findViewById(R.id.login_button)
        logoutButton = findViewById(R.id.logout_button)
        mapButton = findViewById(R.id.map_button)
        loggedInText = findViewById(R.id.logged_in_text)
        eventsListView = findViewById(R.id.events_list_view)

        firestore = FirebaseFirestore.getInstance()
        eventsCollection = firestore!!.collection("events")
        auth = FirebaseAuth.getInstance()
        currentUser = auth!!.currentUser

        populateEventsListView()

        createEventButton!!.setOnClickListener {
            if (auth!!.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java
                ).putExtra("message", "Please login to create event!"))
            } else {
                startActivity(Intent(this, CreateEventActivity::class.java))
            }
        }

        loginButton!!.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        logoutButton!!.setOnClickListener {
            auth!!.signOut()
            this.recreate()
        }

        mapButton!!.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        eventsListView!!.setOnItemClickListener { _, _, position, _ ->
            Log.e(TAG, listOfEvents!![position].id)
            startActivity(Intent(this, ViewEventActivity::class.java
            ).putExtra("id", listOfEvents!![position].id))
        }
    }

    private fun populateEventsListView() {
        eventsCollection!!.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e(TAG, "eventsCollection.get():success")
                listOfEvents = ArrayList()
                for (e in task.result!!) {
                    val event = e.toObject(Event::class.java)
                    listOfEvents!!.add(event)
                }
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    listOfEvents!!
                )
                eventsListView?.adapter = adapter
            }
            else {
                Log.e(TAG, "eventsCollection.get():failure", task.exception)
                Toast.makeText(
                    this,
                    "Couldn't fetch list of events from server",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        this.recreate()
    }

    override fun onStart() {
        super.onStart()

        if (auth!!.currentUser != null) {
            loggedInText!!.text = "Logged in as: "+currentUser!!.email
        } else {
            loggedInText!!.text = "No user signed in"
        }
    }
}
