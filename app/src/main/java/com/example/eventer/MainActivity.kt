package com.example.eventer

import androidx.appcompat.app.AppCompatActivity
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

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

    //Navigation
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    //Firebase references
    private var firestore: FirebaseFirestore? = null
    private var usersCollection: CollectionReference? = null
    private var userDocument: DocumentReference? = null
    private var auth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("MainActivity", "onCreate()")
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
    }

    private fun initialise() {
        Log.e("MainActivity", "initialise()")
        firestore = FirebaseFirestore.getInstance()
        //eventsCollection = firestore!!.collection("events")
        usersCollection = firestore!!.collection("users")
        auth = FirebaseAuth.getInstance()
        currentUser = auth!!.currentUser


        /* Display First Fragment initially */
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.myFragment, mainFragment)
        transaction.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.e("MainActivity", "onNavigationItemSelected()")

        when (item.itemId) {
            R.id.nav_profile -> {
                if (auth!!.currentUser == null){
                    Toast.makeText(this, "Login to see your profile", Toast.LENGTH_SHORT).show()
                }else {
                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(R.id.myFragment, viewProfileFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }
            R.id.nav_login -> {
                Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_friends -> {
                Toast.makeText(this, "Friends clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_update -> {
                Toast.makeText(this, "Update clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_logout -> {
                Toast.makeText(this, "Sign out clicked", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}


