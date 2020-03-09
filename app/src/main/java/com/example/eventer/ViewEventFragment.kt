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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ViewEventFragment : Fragment() {

    private val TAG = "ViewEventFragment"

    //Fragments
    private val editEventFragment = EditEventFragment()

    //UI elements
    private var titleText: TextView? = null
    private var contentText: TextView? = null
    private var createdByText: TextView? = null
    private var editEventButton: Button? = null

    //Firebase references
    private var firestore: FirebaseFirestore? = null
    private var eventsCollection: CollectionReference? = null
    private var eventDocument: DocumentReference? = null
    private var auth: FirebaseAuth? = null

    //Global variables
    private var event: Event? = null
    private var id: String? = null

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
        contentText = view!!.findViewById(R.id.content_text)
        createdByText = view!!.findViewById(R.id.created_by_text)
        editEventButton = view!!.findViewById(R.id.edit_event_button)

        firestore = FirebaseFirestore.getInstance()
        eventsCollection = firestore!!.collection("events")
        eventDocument = eventsCollection!!.document(id!!)
        auth = FirebaseAuth.getInstance()

        getEventFromFirestore()

        editEventButton!!.setOnClickListener {
            val args = Bundle()
            args.putString("id", id)
            editEventFragment.arguments = args

            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.replace(R.id.myFragment, editEventFragment)
            fragmentTransaction.commit()

//            startActivity(Intent(activity, EditEventActivity::class.java
//            ).putExtra("event", event))
        }
    }

    private fun getEventFromFirestore() {
        eventDocument!!.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e(TAG, "eventDocument.get():success")
                event = task.result!!.toObject(Event::class.java)

                titleText!!.text = event!!.title
                contentText!!.text = event!!.content
                createdByText!!.text = event!!.created_by
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
}