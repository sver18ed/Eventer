package com.example.eventer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.eventer.models.Event
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ViewEventFragment : Fragment() {

    private val TAG = "ViewEventFragment"

    //Fragments
    private val editEventFragment = EditEventFragment()
    private val mapFragment = MapFragment()

    //UI elements
    private var titleText: TextView? = null
    private var descriptionText: TextView? = null
    private var createdByText: TextView? = null
    private var editEventButton: Button? = null
    private var joinEventButton: Button? = null

    //Firebase references
    private var firestore: FirebaseFirestore? = null
    private var eventsCollection: CollectionReference? = null
    private var eventDocument: DocumentReference? = null
    private var auth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null

    //Global variables
    private var event: Event? = null
    private var id: String? = null

    private var placeLatLng: LatLng? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialise()
    }

    private fun initialise() {
        id = arguments?.getString("id")

        titleText = view!!.findViewById(R.id.title_text)
        descriptionText = view!!.findViewById(R.id.description_text)
        createdByText = view!!.findViewById(R.id.created_by_text)
        editEventButton = view!!.findViewById(R.id.edit_event_button)
        joinEventButton = view!!.findViewById(R.id.join_button)

        firestore = FirebaseFirestore.getInstance()
        eventsCollection = firestore!!.collection("events")
        eventDocument = eventsCollection!!.document(id!!)
        auth = FirebaseAuth.getInstance()
        currentUser = auth!!.currentUser

        getEventFromFirestore()

        editEventButton!!.setOnClickListener {
            val args = Bundle()
            args.putString("id", id)
            editEventFragment.arguments = args

            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.myFragment, editEventFragment)
            fragmentTransaction.commit()

//            startActivity(Intent(activity, EditEventActivity::class.java
//            ).putExtra("event", event))
        }

        joinEventButton!!.setOnClickListener {
            eventsCollection!!.document(event!!.id).update(
                "participants", FieldValue.arrayUnion(currentUser!!.email)
            ).addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "join:success")
                    Toast.makeText(
                        activity,
                        "You join the event!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    Log.d(TAG, "join:failure", task.exception)
                    Toast.makeText(
                        activity,
                        "Failed to join event...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        //startMap()
    }

    private fun getEventFromFirestore() {
        eventDocument!!.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e(TAG, "eventDocument.get():success")
                event = task.result!!.toObject(Event::class.java)

                titleText!!.text = event!!.title
                descriptionText!!.text = event!!.description
                createdByText!!.text = event!!.created_by

                placeLatLng = LatLng(event!!.latitude, event!!.longitude)
                Log.e("MapFragment", "place: Lat: "+placeLatLng!!.latitude+" Long: "+placeLatLng!!.longitude)

                startMap()
            }
            else {
                Log.e(TAG, "eventDocument.get():failure", task.exception)
                Toast.makeText(
                    activity,
                    "Couldn't fetch event from server",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun startMap() {

        val args = Bundle()
        args.putParcelable("placeLatLng", placeLatLng)
        mapFragment.arguments = args

        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.test_map, mapFragment)
        transaction.commit()
    }
}