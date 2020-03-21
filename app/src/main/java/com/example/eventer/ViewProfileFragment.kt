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
import com.example.eventer.models.User
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ViewProfileFragment : Fragment() {
    private val TAG = "ViewProfileFragment"

    //Fragments
    //private val editEventFragment = EditEventFragment()


    //UI elements
    private var firstNameText: TextView? = null
    private var lastNameText: TextView? = null
    private var emailText: TextView? = null
    //private var editEventButton: Button? = null

    //Firebase references
    private var firestore: FirebaseFirestore? = null
    private var usersCollection: CollectionReference? = null
    private var userDocument: DocumentReference? = null
    private var auth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null

    //Global variables
    private var user: User? = null
    private var id: String? = null




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialise()
    }

    private fun initialise() {

        firstNameText = view!!.findViewById(R.id.first_name_text)
        lastNameText = view!!.findViewById(R.id.last_name_text)
        emailText = view!!.findViewById(R.id.email_text)
        //editEventButton = view!!.findViewById(R.id.edit_event_button)

        firestore = FirebaseFirestore.getInstance()
        usersCollection = firestore!!.collection("users")
        auth = FirebaseAuth.getInstance()
        currentUser = auth!!.currentUser
        userDocument = usersCollection!!.document(currentUser?.email!!)

        getUserFromFirestore()

        /* editEventButton!!.setOnClickListener {
            val args = Bundle()
            args.putString("id", id)
            editEventFragment.arguments = args

            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.myFragment, editEventFragment)
            fragmentTransaction.commit()


        }

    }

        */
    }

    private fun getUserFromFirestore() {
        userDocument!!.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e(TAG, "eventDocument.get():success")
                user = task.result!!.toObject(User::class.java)

                firstNameText!!.text = user!!.firstName
                lastNameText!!.text = user!!.lastName
                emailText!!.text = user!!.email

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