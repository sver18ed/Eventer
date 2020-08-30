package com.example.eventer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.contains
import androidx.core.view.isVisible
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
    private val loginFragment = LoginFragment()
    private val mapFragment = MapFragment()
    private val viewProfileFragment = ViewProfileFragment()

    //UI elements
    private var titleText: TextView? = null
    private var descriptionText: TextView? = null
    private var createdByText: TextView? = null
    private var startDateText: TextView? = null
    private var endDateText: TextView? = null
    private var startTimeText: TextView? = null
    private var endTimeText: TextView? = null
    private var participantsListView: ListView? = null
    private var participantsText: TextView? = null
    private var editEventButton: Button? = null
    private var joinEventButton: Button? = null
    private var leaveEventButton: Button? = null

    //Firebase references
    private var firestore: FirebaseFirestore? = null
    private var eventsCollection: CollectionReference? = null
    private var eventDocument: DocumentReference? = null
    private var auth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null

    //Global variables
    private var event: Event? = null
    private var id: String? = null
    private var participants: List<String>? = null

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
        startDateText = view!!.findViewById(R.id.start_date_text)
        endDateText = view!!.findViewById(R.id.end_date_text)
        startTimeText = view!!.findViewById(R.id.start_time_text)
        endTimeText = view!!.findViewById(R.id.end_time_text)
        participantsListView = view!!.findViewById(R.id.participants_list_view)
        participantsText = view!!.findViewById(R.id.participants_text)
        editEventButton = view!!.findViewById(R.id.edit_event_button)
        joinEventButton = view!!.findViewById(R.id.join_button)
        leaveEventButton = view!!.findViewById(R.id.leave_button)

        firestore = FirebaseFirestore.getInstance()
        eventsCollection = firestore!!.collection("events")
        eventDocument = eventsCollection!!.document(id!!)
        auth = FirebaseAuth.getInstance()
        currentUser = auth!!.currentUser

        editEventButton!!.isVisible = currentUser != null

        getEventFromFirestore()


        participantsListView!!.setOnItemClickListener { _, _, position, _ ->

            val args = Bundle()
            args.putString("email", participants!![position])
            viewProfileFragment!!.arguments = args

            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.myFragment, viewProfileFragment!!)
            fragmentTransaction.commit()
        }

        editEventButton!!.setOnClickListener {
            if (event!!.created_by == currentUser!!.email) {
                val args = Bundle()
                args.putString("id", id)
                editEventFragment.arguments = args

                val fragmentTransaction = fragmentManager!!.beginTransaction()
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.replace(R.id.myFragment, editEventFragment)
                fragmentTransaction.commit()
            }
            else {
                Toast.makeText(
                    activity,
                    getString(R.string.msg_not_auth_edit_event),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        joinEventButton!!.setOnClickListener {
            if (currentUser == null) {
                Toast.makeText(
                    activity,
                    getString(R.string.msg_login_to_join_event),
                    Toast.LENGTH_SHORT
                ).show()
                val fragmentTransaction = fragmentManager!!.beginTransaction()
                fragmentTransaction.replace(R.id.myFragment, loginFragment!!)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
            else {
                eventsCollection!!.document(event!!.id).update(
                    "participants", FieldValue.arrayUnion(currentUser!!.email)
                ).addOnCompleteListener(activity!!) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "join:success")
                        Toast.makeText(
                            activity,
                            getString(R.string.msg_join_event_successful),
                            Toast.LENGTH_SHORT
                        ).show()
                        refreshParticipantsListView()
                        leaveButtonVisible()
                    }
                    else {
                        Log.d(TAG, "join:failure", task.exception)
                        Toast.makeText(
                            activity,
                            getString(R.string.msg_join_event_unsucessful),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        leaveEventButton!!.setOnClickListener {
            if (currentUser == null) {
                Toast.makeText(
                    activity,
                    getString(R.string.msg_login_to_leave_event),
                    Toast.LENGTH_SHORT
                ).show()
                val fragmentTransaction = fragmentManager!!.beginTransaction()
                fragmentTransaction.replace(R.id.myFragment, loginFragment!!)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
            else {
                eventsCollection!!.document(event!!.id).update(
                    "participants", FieldValue.arrayRemove(currentUser!!.email)
                ).addOnCompleteListener(activity!!) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "leave:success")
                        Toast.makeText(
                            activity,
                            getString(R.string.msg_leave_event_sucessful),
                            Toast.LENGTH_SHORT
                        ).show()
                        refreshParticipantsListView()
                        joinButtonVisible()
                    }
                    else {
                        Log.d(TAG, "leave:failure", task.exception)
                        Toast.makeText(
                            activity,
                            getString(R.string.msg_leave_event_unsucessful),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun getEventFromFirestore() {
        eventDocument!!.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e(TAG, "eventDocument.get():success")
                event = task.result!!.toObject(Event::class.java)

                titleText!!.text = event!!.title
                descriptionText!!.text = event!!.description
                createdByText!!.text = event!!.created_by
                startDateText!!.text = event!!.start_date
                endDateText!!.text = event!!.end_date
                startTimeText!!.text = event!!.start_time
                endTimeText!!.text = event!!.end_time

                participants = task.result!!.get("participants") as List<String>

                participantsText!!.text = participants!!.size.toString() + " " + getString(R.string.participants)

                populateParticipantsListView()

                if (event!!.created_by != currentUser!!.email){
                    editEventButton!!.visibility = View.GONE
                }
                if (participants!!.contains(currentUser!!.email)){
                    leaveButtonVisible()
                } else {
                    joinButtonVisible()
                }

                placeLatLng = LatLng(event!!.latitude, event!!.longitude)
                Log.e("MapFragment", "place: Lat: "+placeLatLng!!.latitude+" Long: "+placeLatLng!!.longitude)

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

    private fun populateParticipantsListView() {
        val adapter = ArrayAdapter(
            activity!!,
            android.R.layout.simple_list_item_1,
            participants!!
        )
        participantsListView?.adapter = adapter
    }

    private fun startMap() {

        val args = Bundle()
        args.putParcelable("placeLatLng", placeLatLng)
        mapFragment.arguments = args

        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.test_map, mapFragment)
        transaction.commit()
    }
    private fun refreshParticipantsListView() {
        eventDocument!!.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e(TAG, "eventDocument.get():success")
                event = task.result!!.toObject(Event::class.java)
                participants = task.result!!.get("participants") as List<String>
                participantsText!!.text = participants!!.size.toString() + " " + getString(R.string.participants)
                populateParticipantsListView()
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
    private fun joinButtonVisible(){
        joinEventButton!!.visibility = View.VISIBLE
        leaveEventButton!!.visibility = View.GONE
    }
    private fun leaveButtonVisible(){
        joinEventButton!!.visibility = View.GONE
        leaveEventButton!!.visibility = View.VISIBLE
    }
}