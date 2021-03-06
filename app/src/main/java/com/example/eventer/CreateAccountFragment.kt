package com.example.eventer

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class CreateAccountFragment : Fragment() {

    //Fragments
    private val mainFragment = MainFragment()

    //UI elements
    private var firstNameEditText: EditText? = null
    private var lastNameEditText: EditText? = null
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var createAccountButton: Button? = null

    //Firebase references
    private var firestore: FirebaseFirestore? = null
    private var usersCollection: CollectionReference? = null
    private var auth: FirebaseAuth? = null

    //Global variables
    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var password: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialise()
    }

    private fun initialise() {
        firstNameEditText = view!!.findViewById(R.id.first_name_edit_text)
        lastNameEditText = view!!.findViewById(R.id.last_name_edit_text)
        emailEditText = view!!.findViewById(R.id.email_edit_text)
        passwordEditText = view!!.findViewById(R.id.password_edit_text)
        createAccountButton = view!!.findViewById(R.id.create_event_button)

        firestore = FirebaseFirestore.getInstance()
        usersCollection = firestore!!.collection("users")
        auth = FirebaseAuth.getInstance()

        createAccountButton!!.setOnClickListener {
            createNewAccount()
        }
    }

    private fun createNewAccount() {
        firstName = firstNameEditText?.text.toString()
        lastName = lastNameEditText?.text.toString()
        email = emailEditText?.text.toString()
        password = passwordEditText?.text.toString()

        if (firstName!!.isEmpty()) {
            firstNameEditText!!.error = getString(R.string.msg_enter_first_name)
            firstNameEditText!!.requestFocus()
            return
        }

        if (lastName!!.isEmpty()) {
            lastNameEditText!!.error = getString(R.string.msg_enter_last_name)
            lastNameEditText!!.requestFocus()
            return
        }

        if (email!!.isEmpty()) {
            emailEditText!!.error = getString(R.string.msg_enter_email)
            emailEditText!!.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email!!).matches()) {
            emailEditText!!.error = getString(R.string.msg_enter_valid_email)
            emailEditText!!.requestFocus()
            return
        }

        if (password!!.isEmpty()) {
            passwordEditText!!.error = getString(R.string.msg_enter_password)
            passwordEditText!!.requestFocus()
            return
        }

        auth!!.createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful) {
                    val userId = auth!!.currentUser!!.uid
                    //update user profile information
                    val currentUserDb = usersCollection!!.document(email!!)
                    val user = hashMapOf(
                        "userId" to userId,
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "email" to email,
                        "password" to password
                    )
                    currentUserDb.set(user)
                    loginUser()
                    updateUI()
                } else {
                    Toast.makeText(
                        activity!!,
                        getString(R.string.msg_create_user_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun loginUser() {
        auth!!.signInWithEmailAndPassword(email!!, password!!)
    }

    private fun updateUI() {
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.myFragment, mainFragment)
        fragmentTransaction.commit()
    }
}
