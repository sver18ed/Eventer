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
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.Place.Field.NAME
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CreateEventFragment : Fragment() {

    private val TAG = "CreateEventFragment"

    private lateinit var placesClient: PlacesClient

    //Fragments
    private val viewEventFragment = ViewEventFragment()
    private val mapFragment = MapFragment()

    //UI elements
    private var titleEditText: EditText? = null
    private var contentEditText: EditText? = null
    private var addressFrameLayout: FrameLayout? = null
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
    private var address: String? = null


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
        return inflater.inflate(R.layout.fragment_create_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialise()
    }

    private fun initialise() {
        titleEditText = view!!.findViewById(R.id.title_edit_text)
        contentEditText = view!!.findViewById(R.id.content_edit_text)
        addressFrameLayout = view!!.findViewById(R.id.address_frame_layout)
        saveButton = view!!.findViewById(R.id.save_button)

        firestore = FirebaseFirestore.getInstance()
        eventsCollection = firestore!!.collection("events")
        usersCollection = firestore!!.collection("users")
        auth = FirebaseAuth.getInstance()
        currentUser = auth!!.currentUser

        saveButton!!.setOnClickListener {
            createNewEvent()
        }
        startMap()
        initializePlaces()
    }

    private fun createNewEvent() {
        id = eventsCollection!!.document().id
        title = titleEditText?.text.toString()
        content = contentEditText?.text.toString()
        address = placeName.toString()


        if (currentUser == null) {
            startActivity(
                Intent(activity, LoginActivity::class.java
                ).putExtra("message", "Please login to create event!"))
        }

        else if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content) && !TextUtils.isEmpty(address)) {

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
                        activity,
                        "Event couldn't be created",
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

    private fun updateUI() {
        val args = Bundle()
        args.putString("id", id)
        viewEventFragment.arguments = args

        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.myFragment, viewEventFragment)
        fragmentTransaction.commit()
    }

    private fun initializePlaces() {

        val apiKey = getString(R.string.google_maps_key)
        if (!Places.isInitialized()){
            Places.initialize(context!!, apiKey )
        }
        placesClient = Places.createClient(context!!)

        var autocompleteSupportFragment: AutocompleteSupportFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, NAME))

        autocompleteSupportFragment.setOnPlaceSelectedListener(object: PlaceSelectionListener{

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
        val args = Bundle()
        args.putParcelable("placeLatLng", placeLatLng)
        mapFragment.arguments = args

        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.test_map, mapFragment)
        transaction.commit()
    }
}