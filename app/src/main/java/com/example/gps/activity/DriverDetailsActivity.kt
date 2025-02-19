package com.example.gps.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.gps.R

class DriverDetailsActivity : AppCompatActivity() {

    private lateinit var driverImage: ImageView
    private lateinit var driverName: TextView
    private lateinit var driverVehicle: TextView
    private lateinit var driverContact: TextView
    private lateinit var driverRidesAttended: TextView
    private lateinit var endRideButton: Button
    private lateinit var ratingSection: LinearLayout
    private lateinit var submitRatingButton: Button
    private lateinit var driverRatingBar: RatingBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_details)

        // Initialize UI elements
        driverImage = findViewById(R.id.driver_image)
        driverName = findViewById(R.id.driver_name)
        driverVehicle = findViewById(R.id.driver_vehicle)
        driverContact = findViewById(R.id.driver_contact)
        driverRidesAttended = findViewById(R.id.driver_rides_attended)
        endRideButton = findViewById(R.id.end_ride_button)
        ratingSection = findViewById(R.id.rating_section)
        submitRatingButton = findViewById(R.id.submit_rating_button)
        driverRatingBar = findViewById(R.id.driver_rating_bar)

        // Retrieve driver details from intent (Corrected key names)
        val name = intent.getStringExtra("driver_name") ?: "Unknown"
        val vehicleType = intent.getStringExtra("vehicle_type") ?: "Unknown"
        val contact = intent.getStringExtra("driver_contact") ?: "Unknown"
        val experience = intent.getIntExtra("driver_experience", 0) // Corrected key

        // Set driver details in UI
        driverName.text = "Driver Name : $name"
        driverVehicle.text = "Vehicle : $vehicleType"
        driverContact.text = "Contact Details : $contact"
        driverRidesAttended.text = "Experience : $experience years"

        // Hide rating section initially
        ratingSection.visibility = View.GONE

        // End ride button listener
        endRideButton.setOnClickListener {
            // Show rating section when the ride ends
            ratingSection.visibility = View.VISIBLE
            endRideButton.visibility = View.GONE
        }

        // Submit rating button listener
        submitRatingButton.setOnClickListener {
            val rating = driverRatingBar.rating
            Toast.makeText(this, "You rated the driver $rating stars!", Toast.LENGTH_SHORT).show()
            finishRide()
        }
    }

    private fun finishRide() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
