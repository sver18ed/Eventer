package com.example.eventer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.eventer.models.Event
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class MainFragment : Fragment() {

    private val TAG = "MainFragment"

    //Fragments
    private var createEventFragment: Fragment? = null
    private var viewEventFragment: Fragment? = null
    private val mapFragment = MapFragment()
    private var loginFragment: Fragment? = null

    //UI elements
    private var createEventButton: Button? = null
    private var eventsListView: ListView? = null

    //Firebase references
    private var firestore: FirebaseFirestore? = null
    private var eventsCollection: CollectionReference? = null
    private var auth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null

    //Global variables
    private var listOfEvents: ArrayList<Event>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialise()
    }

    override fun onResume() {
        super.onResume()
        currentUser = auth!!.currentUser
    }

    private fun initialise() {
        //Initialising fragments
        createEventFragment = CreateEventFragment()
        viewEventFragment = ViewEventFragment()
        loginFragment = LoginFragment()

        //Initialising UIs
        createEventButton = view!!.findViewById(R.id.create_event_button)
        eventsListView = view!!.findViewById(R.id.events_list_view)

        firestore = FirebaseFirestore.getInstance()
        eventsCollection = firestore!!.collection("events")
        auth = FirebaseAuth.getInstance()
        currentUser = auth!!.currentUser

        populateEventsListView()

        //startMap()

        createEventButton!!.setOnClickListener {
            if (currentUser == null) {
                Toast.makeText(
                    activity,
                    getString(R.string.msg_login_to_create_event),
                    Toast.LENGTH_SHORT
                ).show()
                val fragmentTransaction = fragmentManager!!.beginTransaction()
                fragmentTransaction.replace(R.id.myFragment, loginFragment!!)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()

                //putExtra("message", "Please login to create event!"))
            } else {
                val fragmentTransaction = fragmentManager!!.beginTransaction()
                fragmentTransaction.replace(R.id.myFragment, createEventFragment!!)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }

        eventsListView!!.setOnItemClickListener { _, _, position, _ ->

            val args = Bundle()
            args.putString("id", listOfEvents!![position].id)
            viewEventFragment!!.arguments = args

            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.myFragment, viewEventFragment!!)
            fragmentTransaction.commit()
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
                    activity!!,
                    android.R.layout.simple_list_item_1,
                    listOfEvents!!
                )
                eventsListView?.adapter = adapter
                startMap()
            }
            else {
                Log.e(TAG, "eventsCollection.get():failure", task.exception)
                Toast.makeText(
                    activity,
                    getText(R.string.msg_fetch_list_events_from_server_unsucessful),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun startMap() {
        val args = Bundle()

        val locations = ArrayList<LatLng>()
        for (event in listOfEvents!!) {
            locations.add(LatLng(event.latitude, event.longitude))
        }
        args.putParcelableArrayList("locations", locations)
        mapFragment.arguments = args

        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.test_map, mapFragment)
        transaction.commit()
    }
}