package com.example.eventer

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.example.eventer.models.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = FirebaseFirestore.getInstance()

        val mAuth = FirebaseAuth.getInstance()
        val mUser = mAuth.currentUser

        val loggedInText = findViewById<TextView>(R.id.logged_in_text)

        if (mAuth.currentUser != null) {
            loggedInText.text = "Logged in as: "+mUser?.email
        } else {
            loggedInText.text = "No user signed in"
        }

        val events = db.collection("events")
        events.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val listOfEvents = ArrayList<Event>()
                for (e in task.result!!) {
                    val event = e.toObject(Event::class.java)
                    listOfEvents.add(event)
                }
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    listOfEvents
                )
                val listView = findViewById<ListView>(R.id.main_listView)
                listView.adapter = adapter
                listView.setOnItemClickListener { _, _, position, _ ->
                    val intent = Intent(this, ViewEventActivity::class.java)
                    intent.putExtra("event", listOfEvents[position])
                    startActivity(intent)
                }
            }
        }

        val createButton = this.findViewById<Button>(R.id.create_button)
        createButton.setOnClickListener {
            val intent = Intent(this, CreateEventActivity::class.java)
            startActivity(intent)
        }

        val loginButton = this.findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val logoutButton = this.findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener{
            mAuth.signOut()
            this.recreate()
        }

        val mapButton = this.findViewById<Button>(R.id.map_button)
        mapButton.setOnClickListener{
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onRestart() {
        super.onRestart()
        this.recreate()
    }
}
