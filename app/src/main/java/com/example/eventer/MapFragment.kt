package com.example.eventer

import android.content.Context
import android.content.IntentSender
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener  {


    // TODO: Rename and change types of parameters
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false
    private var listener: OnFragmentInteractionListener? = null
    private var placeLatLng: LatLng? = null

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapFragment.
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = MapFragment()

    }

    override fun onMarkerClick(p0: Marker?) = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("MapFragment", "onCreate()")
        super.onCreate(savedInstanceState)

        fusedLocationClient = context?.let { LocationServices.getFusedLocationProviderClient(it) }!!
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                //placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
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
        //placeLatLng = arguments?.getParcelable("placeLatLng")

        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.fragment_map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.e("MapFragment", "onMapReady()")
        map = googleMap
        map.getUiSettings().setZoomControlsEnabled(true)
        map.setOnMarkerClickListener(this)

        setUpMap()
        var locations = arguments?.getParcelableArrayList<LatLng>("locations")
        placeLatLng = arguments?.getParcelable("placeLatLng")
        if (placeLatLng != null) {
            map.clear()
            placeMarkerOnMap(placeLatLng!!)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 12f))
            Log.e("MapFragment", "moveCamera() in onMapReady")
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 12f))

        } else if (locations != null) {
            map.clear()
            for (location in locations) {
                placeMarkerOnMap(location)
            }
        }
    }

    override fun onPause() {
        Log.e("MapFragment", "onPause()")
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // 3
    override fun onResume() {
        Log.e("MapFragment", "onResume()")
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }

        placeLatLng = arguments?.getParcelable("placeLatLng")
        if (placeLatLng != null && ::map.isInitialized) {
            //getFragmentManager()!!.beginTransaction().detach(this).attach(this).commit()
            map.clear()
            placeMarkerOnMap(placeLatLng!!)
            //map.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 12f))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 12f))
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 12f))
        }
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

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                //placeMarkerOnMap(currentLatLng)
                if (placeLatLng == null) {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                }
            }
        }
    }

    private fun placeMarkerOnMap(location: LatLng ) {
        Log.e("MapFragment", "placeMarkerOnMap()")

        val markerOptions = MarkerOptions().position(location)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))


        val titleStr = getAddress(location)
        markerOptions.title(titleStr)
        markerOptions.snippet(titleStr)

        map.addMarker(markerOptions)
    }

    private fun getAddress(latLng: LatLng): String {
        Log.e("MapFragment", "getAddress()")

        // 1
        val geocoder = Geocoder(context)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            // 2
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            // 3
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
                addressText = address.getAddressLine(0)
            }
        } catch (e: IOException) {
            Log.e("MapFragment", e.localizedMessage)
        }

        return addressText
    }

    private fun startLocationUpdates() {
        Log.e("MapFragment", "startLocationUpdates()")

        //1
        if (checkSelfPermission(context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        //2
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }

    private fun createLocationRequest() {
        Log.e("MapFragment", "createLocationRequest()")

        // 1
        locationRequest = LocationRequest()
        // 2
        locationRequest.interval = 10000
        // 3
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        // 4
        val client = LocationServices.getSettingsClient(context!!)
        val task = client.checkLocationSettings(builder.build())

        // 5
        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            // 6
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    startIntentSenderForResult(e.getResolution().getIntentSender(),

                        REQUEST_CHECK_SETTINGS, null, 0, 0, 0, null
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        Log.e("MapFragment", "onAttach()")
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        Log.e("MapFragment", "onDetach()")
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }
}
