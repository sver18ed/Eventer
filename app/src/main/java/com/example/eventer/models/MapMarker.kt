package com.example.eventer.models

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

class MapMarker(
    val title: String = "",
    val location: LatLng? = null

) : Serializable

{
    override fun toString() = title
}