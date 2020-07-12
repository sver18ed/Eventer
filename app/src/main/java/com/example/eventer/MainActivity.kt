package com.example.eventer

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.nav_header.view.*

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    MapFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val TAG = "MainActivity"

    //Fragments
    private val fragmentManager = supportFragmentManager
    private val mainFragment = MainFragment()
    private val viewProfileFragment = ViewProfileFragment()
    private val loginFragment = LoginFragment()

    //Navigation
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    //Firebase references
    private var firestore: FirebaseFirestore? = null
    private var usersCollection: CollectionReference? = null
    private var auth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        this.setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        initialise()

        val menu = navView.menu
        menu.findItem(R.id.nav_login).isVisible = true
        var navEmailText = navView.getHeaderView(0).nav_email_text

        auth!!.addAuthStateListener {
            if (auth!!.currentUser == null) {
                menu.findItem(R.id.nav_login).isVisible = true
                menu.findItem(R.id.nav_logout).isVisible = false
                navEmailText.text = getString(R.string.no_user_signed_in)
            } else {
                menu.findItem(R.id.nav_login).isVisible = false
                menu.findItem(R.id.nav_logout).isVisible = true
                navEmailText.text = "Logged in as: " + currentUser!!.email
            }
        }
    }

    private fun initialise() {
        Log.e("MainActivity", "initialise()")
        firestore = FirebaseFirestore.getInstance()
        usersCollection = firestore!!.collection("users")
        auth = FirebaseAuth.getInstance()
        currentUser = auth!!.currentUser

        /* Display Main Fragment initially */
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.myFragment, mainFragment)
        transaction.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_profile -> {
                if (auth!!.currentUser == null){
                    Toast.makeText(this, "Login to see your profile", Toast.LENGTH_SHORT).show()
                    val fragmentTransaction = fragmentManager!!.beginTransaction()
                    fragmentTransaction.replace(R.id.myFragment, loginFragment!!)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                } else {
                    val args = Bundle()
                    args.putString("email", currentUser!!.email)
                    viewProfileFragment!!.arguments = args

                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(R.id.myFragment, viewProfileFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }

            R.id.nav_login -> {
                val fragmentTransaction = fragmentManager!!.beginTransaction()
                fragmentTransaction.replace(R.id.myFragment, loginFragment!!)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }

            R.id.nav_logout -> {
                auth!!.signOut()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}


