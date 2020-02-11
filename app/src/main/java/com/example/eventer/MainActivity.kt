package com.example.eventer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.eventer.models.Events
import com.example.eventer.models.eventsRepository

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView = this.findViewById<ListView>(R.id.main_listview)
        listView.adapter = ArrayAdapter<Events>(
            this,
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            eventsRepository.getAllEvents()
        )

    }
}
