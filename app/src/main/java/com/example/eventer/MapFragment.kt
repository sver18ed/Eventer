package com.example.eventer

import android.content.Context
import android.content.IntentSender
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import com.example.eventer.models.MapMarker
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener  {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: LatLng
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false
    private var listener: OnFragmentInteractionListener? = null
    private var mapMarkers: ArrayList<MapMarker>? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
        @JvmStatic
        fun newInstance() = MapFragment()
    }

    override fun onMarkerClick(position: Marker?) = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("MapFragment", "onCreate()")
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(location: LocationResult) {
                super.onLocationResult(location)

                lastLocation = LatLng(location.lastLocation.latitude, location.lastLocation.longitude)
                animateToLocation(lastLocation!!.latitude, lastLocation!!.longitude)
            }
        }
        createLocationRequest()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        Log.e("MapFragment", "onCreateView()")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e("MapFragment", "onViewCreated()")

        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.fragment_map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.e("MapFragment", "onMapReady()")
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)

        setUpMap()
//        mapMarkers = ArrayList()
//
//        if (arguments?.getSerializable("mapMarkers") != null) {
//            mapMarkers = arguments?.getSerializable("mapMarkers") as ArrayList<MapMarker>
//        }
//        if (!mapMarkers.isNullOrEmpty()) {
//            map.clear()
//            for (mapMarker in mapMarkers!!) {
//                placeMarkerOnMap(mapMarker)
//            }
//            if ((mapMarkers!!).size == 1) {
//                map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapMarkers!![0].location, 12f))
//                map.animateCamera(CameraUpdateFactory.newLatLngZoom(mapMarkers!![0].location, 12f))
//            }
//        }
    }

    override fun onPause() {
        Log.e("MapFragment", "onPause()")
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        Log.e("MapFragment", "onResume()")
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }

//        val mapMarker: MapMarker
//
//        if (!mapMarkers.isNullOrEmpty() && ::map.isInitialized) {
//            //getFragmentManager()!!.beginTransaction().detach(this).attach(this).commit()
//            map.clear()
//            mapMarker = mapMarkers!![0]
//            placeMarkerOnMap(mapMarker)
//
//            map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapMarker.location, 12f))
//            map.animateCamera(CameraUpdateFactory.newLatLngZoom(mapMarker.location, 12f))
//        }
    }

    override fun onStart() {
        Log.e("MapFragment", "onStart()")
        super.onStart()
    }

    private fun setUpMap() {
        Log.e("MapFragment", "setUpMap()")

        if (checkSelfPermission(context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        map.isMyLocationEnabled = true

        map.mapType = GoogleMap.MAP_TYPE_HYBRID

//        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//            if (location == null) {
//                lastLocation = LatLng(0.0, 0.0)
//            }
//            else {
//                lastLocation = LatLng(location.latitude, location.longitude)
//                animateToLocation(lastLocation!!.latitude, lastLocation!!.longitude)
//            }
//        }
    }

    fun placeMarkerOnMap(mapMarker: MapMarker) {
        Log.e("MapFragment", "placeMarkerOnMap()")
        val location = mapMarker.location
        val address = getAddress(location!!)
        val markerOptions = MarkerOptions().position(location)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

        markerOptions.title(mapMarker.title)
        markerOptions.snippet(address)

        if (this::map.isInitialized) {
            map.addMarker(markerOptions)
        }
    }

    private fun getAddress(latLng: LatLng): String {
        Log.e("MapFragment", "getAddress()")

        val geocoder = Geocoder(context)
        val addresses: List<Address>?
        var address = ""

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            if (addresses.isNotEmpty()) {
                address = addresses[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            Log.e("MapFragment", e.localizedMessage)
        }
        return address
    }

    private fun startLocationUpdates() {
        Log.e("MapFragment", "startLocationUpdates()")

        if (checkSelfPermission(context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }

    private fun createLocationRequest() {
        Log.e("MapFragment", "createLocationRequest()")

        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(context!!)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    startIntentSenderForResult(e.resolution.intentSender,

                        REQUEST_CHECK_SETTINGS, null, 0, 0, 0, null
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    fun animateToLocation(latitude: Double, longitude: Double) {
        if (this::map.isInitialized) {
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        latitude,
                        longitude
                    ), 12.0f
                )
            )
        }
    }

    override fun onAttach(context: Context) {
        Log.e("MapFragment", "onAttach()")
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        Log.e("MapFragment", "onDetach()")
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }
}