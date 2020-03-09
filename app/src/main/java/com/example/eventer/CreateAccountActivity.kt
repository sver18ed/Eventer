package com.example.eventer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class CreateAccountActivity : AppCompatActivity() {

    private val TAG = "CreateAccountActivity"

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        initialise()

        val returnButton = this.findViewById<ImageButton>(R.id.return_button_create)
        returnButton.setOnClickListener{
            finish()
        }
    }

    private fun initialise() {
        firstNameEditText = findViewById(R.id.first_name_edit_text)
        lastNameEditText = findViewById(R.id.last_name_edit_text)
        emailEditText = findViewById(R.id.email_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        createAccountButton = findViewById(R.id.create_event_button)

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
                .addOnCompleteListener(this) { task ->
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
                            this,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(
                this,
                "You must enter all fields...",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateUserInfoAndUI() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
