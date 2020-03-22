package com.example.eventer

import androidx.appcompat.app.AppCompatActivity
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
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
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T






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

    override fun onStart() {
        super.onStart()
        Log.e("MainActivity", "onStart")

    }

    override fun onResume() {
        super.onResume()
        Log.e("MainActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.e("MainActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.e("MainActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("MainActivity", "onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.e("MainActivity", "onRestart")
    }

    private fun initialise() {
        Log.e("MainActivity", "initialise()")
        firestore = FirebaseFirestore.getInstance()
        //eventsCollection = firestore!!.collection("events")
        usersCollection = firestore!!.collection("users")
        auth = FirebaseAuth.getInstance()
        currentUser = auth!!.currentUser

        checkLogin()


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

            R.id.nav_friends -> {
                Toast.makeText(this, "Friends clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_update -> {
                Toast.makeText(this, "Update clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_login_logout -> {
                if (auth!!.currentUser == null) {
                    val menu = navView.getMenu()
                    val menuItem = menu.findItem(R.id.nav_login_logout)
                    menuItem.title = "Login"
                    Log.e("MainActivity", "Now it should say Login")
                    val fragmentTransaction = fragmentManager!!.beginTransaction()
                    fragmentTransaction.replace(R.id.myFragment, loginFragment!!)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }else {
                    val menu = navView.getMenu()
                    val menuItem = menu.findItem(R.id.nav_login_logout)
                    menuItem.title = "Logout"
                    Log.e("MainActivity", "Now it should say Logout")
                    auth!!.signOut()
                    //val fragmentTransaction = fragmentManager!!.beginTransaction()
                    //fragmentTransaction.detach(this).attach(this).commit()
                }

            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun checkLogin() {
        if (auth!!.currentUser == null) {
            val menu = navView.getMenu()
            val menuItem = menu.findItem(R.id.nav_login_logout)
            menuItem.title = "Login"
        } else {
            val menu = navView.getMenu()
            val menuItem = menu.findItem(R.id.nav_login_logout)
            menuItem.title = "Logout"
        }
    }
}


