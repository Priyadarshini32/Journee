package com.example.gps.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.gps.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import com.example.gps.BuildConfig


class HomeActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var fromLocation: LatLng? = null
    private var toLocation: LatLng? = null
    private val apiKey = BuildConfig.MAPS_API_KEY
    private val REQUEST_VEHICLE_SELECTION = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Find the button by ID
        val bookRideButton: Button = findViewById(R.id.book_ride_button)

        bookRideButton.setOnClickListener {
            if (fromLocation == null || toLocation == null) {
                Toast.makeText(this, "Please select both pickup and destination", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, SelectVehicleActivity::class.java)
                startActivityForResult(intent, REQUEST_VEHICLE_SELECTION)
            }
        }



        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        // Set up the map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Set up autocomplete for pickup location
        val fromAutoComplete = supportFragmentManager.findFragmentById(R.id.from_location_autocomplete) as AutocompleteSupportFragment
        fromAutoComplete.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        fromAutoComplete.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                fromLocation = place.latLng
                updateMap()
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                Toast.makeText(this@HomeActivity, "Error: $status", Toast.LENGTH_SHORT).show()
            }
        })

        // Set up autocomplete for destination location
        val toAutoComplete = supportFragmentManager.findFragmentById(R.id.to_location_autocomplete) as AutocompleteSupportFragment
        toAutoComplete.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        toAutoComplete.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                toLocation = place.latLng
                updateMap()
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                Toast.makeText(this@HomeActivity, "Error: $status", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
    }

    private fun updateMap() {
        mMap.clear()
        if (fromLocation != null) {
            mMap.addMarker(MarkerOptions().position(fromLocation!!).title("Pickup"))
        }
        if (toLocation != null) {
            mMap.addMarker(MarkerOptions().position(toLocation!!).title("Destination"))
        }
        if (fromLocation != null && toLocation != null) {
            getRoute(fromLocation!!, toLocation!!)
        }
    }

    private fun getRoute(origin: LatLng, destination: LatLng) {
        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${origin.latitude},${origin.longitude}" +
                "&destination=${destination.latitude},${destination.longitude}" +
                "&key=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@HomeActivity, "Failed to get route", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                val json = JSONObject(responseData!!)
                val routes = json.getJSONArray("routes")
                if (routes.length() > 0) {
                    val points = mutableListOf<LatLng>()
                    val steps = routes.getJSONObject(0)
                        .getJSONArray("legs")
                        .getJSONObject(0)
                        .getJSONArray("steps")

                    for (i in 0 until steps.length()) {
                        val polyline = steps.getJSONObject(i)
                            .getJSONObject("polyline")
                            .getString("points")
                        points.addAll(decodePolyline(polyline))
                    }

                    runOnUiThread {
                        mMap.addPolyline(PolylineOptions().addAll(points).width(10f).color(0xFF3D9040.toInt()))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 12f))
                    }
                }
            }
        })
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = mutableListOf<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1F shl shift)
                shift += 5
            } while (b >= 0x20)
            lat += if (result and 1 != 0) result.inv() shr 1 else result shr 1

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1F shl shift)
                shift += 5
            } while (b >= 0x20)
            lng += if (result and 1 != 0) result.inv() shr 1 else result shr 1

            poly.add(LatLng(lat / 1E5, lng / 1E5))
        }
        return poly
    }

    private fun bookRide() {
        if (fromLocation == null || toLocation == null) {
            Toast.makeText(this, "Please select both pickup and destination", Toast.LENGTH_SHORT).show()
            return
        }

        // Call function to get ETA
        getETA(fromLocation!!, toLocation!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_VEHICLE_SELECTION && resultCode == RESULT_OK) {
            val selectedVehicle = data?.getStringExtra("selected_vehicle")

            if (selectedVehicle != null) {
                Toast.makeText(this, "Selected: $selectedVehicle", Toast.LENGTH_SHORT).show()
                getETA(fromLocation!!, toLocation!!) // Fetch and show ETA after vehicle selection
            }
        }
    }


    private fun getETA(origin: LatLng, destination: LatLng) {
        val url = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
                "origins=${origin.latitude},${origin.longitude}" +
                "&destinations=${destination.latitude},${destination.longitude}" +
                "&key=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@HomeActivity, "Failed to get ETA", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                val json = JSONObject(responseData!!)
                val rows = json.getJSONArray("rows")
                if (rows.length() > 0) {
                    val elements = rows.getJSONObject(0).getJSONArray("elements")
                    if (elements.length() > 0) {
                        val duration = elements.getJSONObject(0).getJSONObject("duration")
                        val etaText = duration.getString("text") // Example: "15 mins"
                        val etaValue = duration.getInt("value") // Time in seconds

                        runOnUiThread {
                            Snackbar.make(findViewById(android.R.id.content), "Estimated Arrival: $etaText", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        })
    }

}
