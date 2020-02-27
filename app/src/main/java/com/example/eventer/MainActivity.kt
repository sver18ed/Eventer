package com.example.eventer

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.eventer.models.Events
import com.example.eventer.models.eventsRepository

class MainActivity : AppCompatActivity() {

    lateinit var adapter: ArrayAdapter<Events>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView = findViewById<ListView>(R.id.main_listView)
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            eventsRepository.getAllEvents()
        )
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as Events
            val intent = Intent(this, ViewEventActivity::class.java)
            intent.putExtra("id", selectedItem.id)
            startActivity(intent)
        }

        val createButton = this.findViewById<Button>(R.id.create_button)
        createButton.setOnClickListener {
            val intent = Intent(this, CreateEventActivity::class.java)
            // start your next activity
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

    override fun onStart() {
        super.onStart()
        adapter.notifyDataSetChanged()
    }
}
