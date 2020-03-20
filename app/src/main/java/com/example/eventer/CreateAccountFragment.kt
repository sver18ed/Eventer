package com.example.eventer

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.eventer.models.hideKeyboard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class CreateAccountFragment : Fragment() {

    private val TAG = "CreateAccountFragment"

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

        val returnButton = view!!.findViewById<ImageButton>(R.id.return_button_create)
        returnButton.setOnClickListener{
            if ( fragmentManager!!.backStackEntryCount > 0)
                fragmentManager!!.popBackStack()
                hideKeyboard(activity!!)
        }

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

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
            && !TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)) {

            auth!!.createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(activity!!) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
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
                        updateUserInfoAndUI()
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            activity!!,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(
                activity!!,
                "You must enter all fields...",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateUserInfoAndUI() {
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.myFragment, mainFragment)
        fragmentTransaction.commit()
     //   val intent = Intent(this, MainActivity::class.java)
    //    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
     //   startActivity(intent)
    }
}
