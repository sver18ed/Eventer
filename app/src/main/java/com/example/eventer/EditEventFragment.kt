package com.example.eventer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import java.text.SimpleDateFormat
import java.util.*

class EditEventFragment : Fragment() {

    private val TAG = "EditEventFragment"

    private lateinit var placesClient: PlacesClient

    //Fragments
    private val mapFragment = MapFragment()

    //UI elements
    private var titleEditText: EditText? = null
    private var descriptionEditText: EditText? = null
    private var startDateEditText: EditText? = null
    private var endDateEditText: EditText? = null
    private var startTimeEditText: EditText? = null
    private var endTimeEditText: EditText? = null
    private var saveButton: Button? = null

    //Firebase references
    private var firestore: FirebaseFirestore? = null
    private var eventsCollection: CollectionReference? = null
    private var eventDocument: DocumentReference? = null
    private var auth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null

    //Global variables
    private var event: Event? = null
    private var id: String? = null
    private var title: String? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private var startTime: String? = null
    private var endTime: String? = null
    private var description: String? = null
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
        return inflater.inflate(R.layout.fragment_edit_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialise()
    }

    private fun initialise() {
        id = arguments?.getString("id")

        titleEditText = view!!.findViewById(R.id.title_edit_text)
        startDateEditText = view!!.findViewById(R.id.start_date_edit_text)
        endDateEditText = view!!.findViewById(R.id.end_date_edit_text)
        startTimeEditText = view!!.findViewById(R.id.start_time_edit_text)
        endTimeEditText = view!!.findViewById(R.id.end_time_edit_text)
        descriptionEditText = view!!.findViewById(R.id.description_edit_text)
        saveButton = view!!.findViewById(R.id.save_button)

        firestore = FirebaseFirestore.getInstance()
        eventsCollection = firestore!!.collection("events")
        eventDocument = eventsCollection!!.document(id!!)
        auth = FirebaseAuth.getInstance()
        currentUser = auth!!.currentUser

        getEventFromFirestore()

        val dateFormat = "yyyy.MM.dd"
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.ENGLISH)
        val simpleTimeFormat = SimpleDateFormat("HH.mm")

        var calendar = Calendar.getInstance()
        var dateEditText: EditText? = null
        var timeEditText: EditText? = null

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            dateEditText!!.setText(simpleDateFormat.format(calendar.time))
        }

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            timeEditText!!.setText(simpleTimeFormat.format(calendar.time))
        }

        startDateEditText!!.setOnClickListener {
            dateEditText = startDateEditText
            DatePickerDialog(activity!!, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        endDateEditText!!.setOnClickListener {
            dateEditText = endDateEditText
            DatePickerDialog(activity!!, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        startTimeEditText!!.setOnClickListener {
            timeEditText = startTimeEditText
            TimePickerDialog(activity!!, timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true).show()
        }

        endTimeEditText!!.setOnClickListener {
            timeEditText = endTimeEditText
            TimePickerDialog(activity!!, timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true).show()
        }

        saveButton!!.setOnClickListener {
            updateEvent()
        }
        //startMap()
        initializePlaces()
    }

    private fun getEventFromFirestore() {
        eventDocument!!.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e(TAG, "eventDocument.get():success")
                event = task.result!!.toObject(Event::class.java)

                titleEditText!!.setText(event?.title)
                startDateEditText!!.setText(event?.start_date)
                endDateEditText!!.setText(event?.end_date)
                startTimeEditText!!.setText(event?.start_time)
                endTimeEditText!!.setText(event?.end_time)
                descriptionEditText!!.setText(event?.description)

                placeLatLng = LatLng(event!!.latitude, event!!.longitude)

                startMap()
            }
            else {
                Log.e(TAG, "eventDocument.get():failure", task.exception)
                Toast.makeText(
                    activity,
                    getString(R.string.msg_fetch_event_from_server_unsucessful),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateEvent() {
        title = titleEditText!!.editableText.toString()
        description = descriptionEditText!!.editableText.toString()
        startDate = startDateEditText!!.editableText.toString()
        endDate = endDateEditText!!.editableText.toString()
        startTime = startTimeEditText!!.editableText.toString()
        endTime = endTimeEditText!!.editableText.toString()
        address = placeName.toString()

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(startDate) &&
            !TextUtils.isEmpty(endDate) && !TextUtils.isEmpty(startTime) &&
            !TextUtils.isEmpty(endTime) && !TextUtils.isEmpty(description) &&
            !TextUtils.isEmpty(address)) {
            eventsCollection!!.document(event!!.id).update(mapOf(
                "title" to title,
                "start_date" to startDate,
                "end_date" to endDate,
                "start_time" to startTime,
                "end_time" to endTime,
                "description" to description,
                "latitude" to placeLatLng!!.latitude,
                "longitude" to placeLatLng!!.longitude,
                "location" to address
            )).addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "update:success")
                    Toast.makeText(
                        activity,
                        getString(R.string.msg_event_updated_sucess),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    Log.d(TAG, "update:failure", task.exception)
                    Toast.makeText(
                        activity,
                        getString(R.string.msg_event_update_unsucessful),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                activity,
                getString(R.string.msg_enter_all_fields),
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

                startMap()

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