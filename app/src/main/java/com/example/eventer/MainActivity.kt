package com.example.eventer

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.example.eventer.models.Event
import com.google.firebase.firestore.FirebaseFirestore
import com.example.eventer.models.eventsRepository

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = FirebaseFirestore.getInstance()

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
                listView.setOnItemClickListener {parent, view, position, id ->
                    val intent = Intent(this, ViewEventActivity::class.java)
                    intent.putExtra("event", listOfEvents[position])
                    startActivity(intent)
                }
            }
        }

//        listView.setOnItemClickListener { parent, view, position, id ->
//            val selectedItem = parent.getItemAtPosition(position) as Events
//            val intent = Intent(this, ViewEventActivity::class.java)
//            intent.putExtra("id", selectedItem.id)
//            startActivity(intent)
//        }

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
