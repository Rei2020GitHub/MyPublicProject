package com.sample.hmssample.mapdemo.ui.main

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hms.location.*
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.model.*
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchService
import com.huawei.hms.site.api.model.*
import com.huawei.hms.site.widget.SearchIntent
import com.sample.hmssample.mapdemo.Utils


class MainViewModel() : ViewModel() {

    companion object {
        private const val PREFERENCE_NAME = "HMS_MAP_DEMO"
        private const val KEY_LAT = "lat"
        private const val KEY_LNG = "lng"
        private const val KEY_BEARING = "bearing"
        private const val KEY_TILT = "tilt"
        private const val KEY_ZOOM = "zoom"
        private const val CAMERA_PADDING = 100
    }

    var map: HuaweiMap? = null
    var searchService: SearchService? = null
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var settingsClient: SettingsClient? = null

    var searchIntent: SearchIntent? = null
    var mapViewBundle: Bundle? = null

    val isMyLocationEnabled = MutableLiveData<Boolean>(false)
    val isMyLocationButtonEnabled = MutableLiveData<Boolean>(false)

    val isRecording = MutableLiveData<Boolean>(false)
    val action: String = ""

    private val markerList: MutableList<Marker> = mutableListOf()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.let { locationResult ->
                addMarker(
                    LatLng(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude),
                    Utils.getDateString(locationResult.lastLocation.time),
                    action
                )

            }
        }
    }

    fun initMap(context: Context) {
        map?.let { map ->
            map.mapType = HuaweiMap.MAP_TYPE_NORMAL
            map.isBuildingsEnabled = true
            map.isIndoorEnabled = true
            map.isMyLocationEnabled = isMyLocationEnabled.value ?: false
            map.isTrafficEnabled = false
            map.uiSettings.let { setting ->
                setting.isCompassEnabled = true
                setting.isIndoorLevelPickerEnabled = true
                setting.isMapToolbarEnabled = true
                setting.isMyLocationButtonEnabled = isMyLocationButtonEnabled.value ?: false
                setting.isRotateGesturesEnabled = true
                setting.isScrollGesturesEnabled = true
                setting.isScrollGesturesEnabledDuringRotateOrZoom = true
                setting.isTiltGesturesEnabled= true
                setting.isZoomControlsEnabled = true
                setting.isZoomGesturesEnabled = true
                setting.setAllGesturesEnabled(true)
            }

            map.setOnMapLoadedCallback {
                context.let { context ->
                    val cameraPosition = getCameraPosition(context)
                    cameraPosition?.let { cameraPosition ->
                        moveCamera(cameraPosition)
                    }
                }
            }

            map.setOnMapClickListener {
                removeMarkers()
            }

            map.setOnMapLongClickListener { latLng ->
                searchByLatLng(latLng, true)
            }
        }
    }

    fun saveCameraPosition(context: Context) {
        map?.let {
            context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                ?.edit()
                ?.putFloat(KEY_LAT, it.cameraPosition.target.latitude.toFloat())
                ?.putFloat(KEY_LNG, it.cameraPosition.target.longitude.toFloat())
                ?.putFloat(KEY_BEARING, it.cameraPosition.bearing)
                ?.putFloat(KEY_TILT, it.cameraPosition.tilt)
                ?.putFloat(KEY_ZOOM, it.cameraPosition.zoom)
                ?.apply()
        }
    }

    private fun getCameraPosition(context: Context): CameraPosition? {
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)?.let { pref ->
            when {
                pref.contains(KEY_LAT)
                        && pref.contains(KEY_LNG)
                        && pref.contains(KEY_ZOOM)
                        && pref.contains(KEY_TILT)
                        && pref.contains(KEY_BEARING) -> {
                    return CameraPosition(
                        LatLng(
                            pref.getFloat(KEY_LAT, 0f).toDouble(),
                            pref.getFloat(KEY_LNG, 0f).toDouble()
                        ),
                        pref.getFloat(KEY_ZOOM, 0f),
                        pref.getFloat(KEY_TILT, 0f),
                        pref.getFloat(KEY_BEARING, 0f)
                    )
                }
                else -> {
                    return null
                }
            }
        } ?: run {
            return null
        }
    }

    private fun moveCamera(cameraPosition: CameraPosition) {
        cameraPosition.let { cameraPosition ->
            val cameraUpdate =
                CameraUpdateFactory.newCameraPosition(cameraPosition)
            map?.animateCamera(cameraUpdate)
        }
    }

    fun moveCamera(site: Site) {
        val latLngBounds = LatLngBounds(
            LatLng(site.viewport.southwest.lat, site.viewport.southwest.lng),
            LatLng(site.viewport.northeast.lat, site.viewport.northeast.lng)
        )
        map?.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, CAMERA_PADDING))
    }

    fun removeMarkers() {
        markerList.forEach { marker ->
            marker.remove()
        }
        markerList.clear()
    }

    fun addMarker(site: Site, latLng: LatLng? = null) {
        addMarker(
            latLng ?: LatLng(site.location.lat, site.location.lng),
            site.name,
            site.formatAddress)
    }

    private fun addMarker(latLng: LatLng, title: String, snippet: String) {
        val options = MarkerOptions()
            .position(latLng)
            .title(title)
            .snippet(snippet)
        map?.addMarker(options)?.let { marker ->
            markerList.add(marker)
        }
    }

    private fun searchByLatLng(latLng: LatLng, customLatLng: Boolean) {
        val request = NearbySearchRequest().apply {
            location = Coordinate(latLng.latitude, latLng.longitude)
        }

        searchService?.nearbySearch(request, object : SearchResultListener<NearbySearchResponse>{
            override fun onSearchResult(results: NearbySearchResponse?) {
                results?.let { results->
                    removeMarkers()

                    val siteList: MutableList<Site> = mutableListOf()
                    if (customLatLng) {
                        results.sites.getOrNull(0)?.let {
                            siteList.add(it)
                        }
                    } else {
                        siteList.addAll(results.sites)
                    }

                    siteList.forEach { site ->
                        addMarker(site, if (customLatLng) latLng else null)
                    }
                }
            }

            override fun onSearchError(status: SearchStatus?) {
                status?.let {
                    Log.e("TAG", "Error : " + it.errorCode + " " + it.errorMessage)
                }
            }
        })
    }

    fun startRecord() {
        fusedLocationProviderClient?.let { fusedLocationProviderClient ->
            settingsClient?.let { settingsClient ->
                val locationSettingsRequest: LocationSettingsRequest  = LocationSettingsRequest.Builder().apply {
                    addLocationRequest(LocationRequest())
                }.build()
                settingsClient.checkLocationSettings(locationSettingsRequest)
                    .addOnSuccessListener(object : OnSuccessListener<LocationSettingsResponse>{
                        override fun onSuccess(p0: LocationSettingsResponse?) {
                            val locationRequest = LocationRequest().apply {
                                interval = 10000
                                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                            }

                            fusedLocationProviderClient
                                .requestLocationUpdates(
                                    locationRequest,
                                    locationCallback,
                                    Looper.getMainLooper()
                                )
                        }
                    })
            }
        }
    }

    fun stopRecord() {
        fusedLocationProviderClient?.let {
            it.removeLocationUpdates(locationCallback)
        }
    }
}