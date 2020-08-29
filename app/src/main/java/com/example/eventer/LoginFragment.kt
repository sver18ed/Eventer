package com.example.eventer

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.eventer.models.hideKeyboard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginFragment : Fragment() {

    private val TAG = "LoginFragment"

    //Fragments
    private val mainFragment = MainFragment()
    private val createAccountFragment = CreateAccountFragment()

    //global variables
    private var email: String? = null
    private var password: String? = null

    //UI elements
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var loginButton: Button? = null
    private var createAccountButton: Button? = null

    //Firebase references
    private var auth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialise()
    }

    private fun initialise() {
        emailEditText = view!!.findViewById(R.id.email_edit_text)
        passwordEditText = view!!.findViewById(R.id.password_edit_text)
        loginButton = view!!.findViewById(R.id.login_button)
        createAccountButton = view!!.findViewById(R.id.create_account_button)

        auth = FirebaseAuth.getInstance()

        createAccountButton!!.setOnClickListener {
            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.replace(R.id.myFragment, createAccountFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        loginButton!!.setOnClickListener {
            loginUser()
            hideKeyboard(activity!!)
        }
    }

    private fun loginUser() {
        email = emailEditText?.text.toString()
        password = passwordEditText?.text.toString()
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            Log.d(TAG, "Logging in user.")
            auth!!.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(activity!!) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        updateUI()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            activity!!,
                            getString(R.string.msg_login_auth_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(
                activity!!,
                getString(R.string.msg_enter_all_fields),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateUI() {

        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.myFragment, mainFragment)
        fragmentTransaction.commit()
        //activity!!.recreate()
    }
}