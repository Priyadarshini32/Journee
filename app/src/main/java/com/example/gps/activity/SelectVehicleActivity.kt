package com.example.gps.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gps.R

class SelectVehicleActivity : AppCompatActivity() {

    private lateinit var vehicleGroup: RadioGroup
    private lateinit var confirmButton: Button
    private lateinit var rideDetailsLayout: View
    private lateinit var selectedVehicleText: TextView
    private lateinit var startRideButton: Button
    private lateinit var cancelRideButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_vehicle)

        vehicleGroup = findViewById(R.id.vehicle_group)
        confirmButton = findViewById(R.id.confirm_button)
        rideDetailsLayout = findViewById(R.id.ride_details_layout)
        selectedVehicleText = findViewById(R.id.selected_vehicle_text)
        startRideButton = findViewById(R.id.start_ride_button)
        cancelRideButton = findViewById(R.id.cancel_ride_button)

        val eta = intent.getStringExtra("selected_eta") ?: "Unknown ETA"

        // Assuming you have a TextView for ETA
        val etaTextView: TextView = findViewById(R.id.eta_text_view)
        etaTextView.text = "Estimated Arrival Time: $eta"

        confirmButton.setOnClickListener {
            val selectedId = vehicleGroup.checkedRadioButtonId
            val vehicleType = if (selectedId != -1) {
                findViewById<RadioButton>(selectedId)?.text.toString()
            } else {
                null
            }

            if (vehicleType == null) {
                Toast.makeText(this, "Please select a vehicle type", Toast.LENGTH_SHORT).show()
            } else {
                // Hide vehicle selection and show ride details
                vehicleGroup.visibility = View.GONE
                confirmButton.visibility = View.GONE
                rideDetailsLayout.visibility = View.VISIBLE
                selectedVehicleText.text = "Vehicle: $vehicleType"
            }
        }

        startRideButton.setOnClickListener {
            Toast.makeText(this, "Ride started successfully!", Toast.LENGTH_SHORT).show()
        }

        cancelRideButton.setOnClickListener {
            setResult(RESULT_CANCELED, Intent().apply {
                putExtra("selected_vehicle", "Cancelled")
            })
            finish()
        }
    }

}
