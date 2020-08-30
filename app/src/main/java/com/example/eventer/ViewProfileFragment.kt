package com.example.eventer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.eventer.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ViewProfileFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        firestore = FirebaseFirestore.getInstance()
        usersCollection = firestore!!.collection("users")
        auth = FirebaseAuth.getInstance()
        currentUser = auth!!.currentUser
        userDocument = usersCollection!!.document(arguments?.getString("email")!!)

        getUserFromFirestore()
    }

    private fun getUserFromFirestore() {
        userDocument!!.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user = task.result!!.toObject(User::class.java)

                firstNameText!!.text = user!!.firstName
                lastNameText!!.text = user!!.lastName
                emailText!!.text = user!!.email

            }
            else {
                Toast.makeText(
                    activity,
                    getString(R.string.msg_fetch_user_from_server_unsucessful),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}