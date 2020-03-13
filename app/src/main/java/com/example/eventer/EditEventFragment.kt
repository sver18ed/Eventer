package com.example.eventer

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.eventer.models.Event
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class EditEventFragment : Fragment() {

    private val TAG = "EditEventFragment"

    private lateinit var placesClient: PlacesClient

    //UI elements
    private var titleEditText: EditText? = null
    private var contentEditText: EditText? = null
    private var saveButton: Button? = null

    //Firebase references
    private var firestore: FirebaseFirestore? = null
    private var eventsCollection: CollectionReference? = null
    private var eventDocument: DocumentReference? = null
    private var auth: FirebaseAuth? = null

    //Global variables
    private var event: Event? = null
    private var id: String? = null
    private var title: String? = null
    private var content: String? = null

    //Global variables for places
    private var placeLatLng: LatLng? = null
    private var placeName: String? = null
    private var placeId: String? = null
    private var placeAddress: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialise()
    }

    private fun initialise() {
        id = arguments?.getString("id")

        titleEditText = view!!.findViewById(R.id.title_edit_text)
        contentEditText = view!!.findViewById(R.id.content_edit_text)
        saveButton = view!!.findViewById(R.id.save_button)

        firestore = FirebaseFirestore.getInstance()
        eventsCollection = firestore!!.collection("events")
        eventDocument = eventsCollection!!.document(id!!)
        auth = FirebaseAuth.getInstance()

        getEventFromFirestore()

        saveButton!!.setOnClickListener {
            updateEvent()
        }
        startMap()
        initializePlaces()
    }

    private fun getEventFromFirestore() {
        eventDocument!!.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e(TAG, "eventDocument.get():success")
                event = task.result!!.toObject(Event::class.java)

                titleEditText!!.setText(event?.title)
                contentEditText!!.setText(event?.content)
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

    private fun updateEvent() {
        title = titleEditText!!.editableText.toString()
        content = contentEditText!!.editableText.toString()

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
            eventsCollection!!.document(event!!.id).update(mapOf(
                "title" to title,
                "content" to content
            )).addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "update:success")
                    Toast.makeText(
                        activity,
                        "Event successfully updated!",
                        Toast.LENGTH_SHORT
                    ).show()
                    //this.finish()
                }
                else {
                    Log.d(TAG, "update:failure", task.exception)
                    Toast.makeText(
                        activity,
                        "Event failed to update...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                activity,
                "You must enter all fields...",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun initializePlaces() {

        val apiKey = getString(R.string.google_maps_key)
        if (!Places.isInitialized()){
            Places.initialize(context!!, apiKey )
        }
        placesClient = Places.createClient(context!!)

        var autocompleteSupportFragment: AutocompleteSupportFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteSupportFragment.setPlaceFields(
            Arrays.asList(
                Place.Field.ID, Place.Field.LAT_LNG,
                Place.Field.NAME
            ))

        autocompleteSupportFragment.setOnPlaceSelectedListener(object: PlaceSelectionListener {

            override fun onPlaceSelected(place: Place) {

                placeLatLng = place.latLng
                placeName = place.name
                placeId = place.id
                placeAddress = place.address

                Log.e("PlaceApi","onPlaceSelected: "+placeLatLng?.latitude+"\n"+placeLatLng?.longitude)
            }

            override fun onError(p0: Status) {
                Log.e("PlaceApi", "Error") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun startMap() {

        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.test_map, MapFragment.newInstance())
        transaction.commit()
    }
}