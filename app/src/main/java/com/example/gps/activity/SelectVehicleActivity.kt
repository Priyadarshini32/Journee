package com.example.gps.activity

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.gps.R

class SelectVehicleActivity : AppCompatActivity() {

    private lateinit var vehicleGroup: RadioGroup
    private lateinit var confirmButton: Button
    private lateinit var rideDetailsLayout: View
    private lateinit var selectedVehicleText: TextView
    private lateinit var etaTextView: TextView
    private lateinit var fareTextView: TextView // Added fare text view
    private lateinit var startRideButton: Button
    private lateinit var cancelRideButton: Button

    private lateinit var bikeTextView: TextView
    private lateinit var autoTextView: TextView
    private lateinit var cabTextView: TextView

    private val vehiclePricing = mapOf(
        "Bike" to Pair(50, 10),  // Base fare: 50, Hourly rate: 10
        "Auto" to Pair(70, 12),  // Base fare: 70, Hourly rate: 12
        "Cab" to Pair(100, 15)   // Base fare: 100, Hourly rate: 15
    )

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_vehicle)

        vehicleGroup = findViewById(R.id.vehicle_group)
        confirmButton = findViewById(R.id.confirm_button)
        rideDetailsLayout = findViewById(R.id.ride_details_layout)
        selectedVehicleText = findViewById(R.id.selected_vehicle_text)
        etaTextView = findViewById(R.id.eta_text_view)
        fareTextView = findViewById(R.id.fare_text_view) // Initialize fare text view
        startRideButton = findViewById(R.id.start_ride_button)
        cancelRideButton = findViewById(R.id.cancel_ride_button)

        bikeTextView = findViewById(R.id.radio_bike_text)
        autoTextView = findViewById(R.id.radio_auto_text)
        cabTextView = findViewById(R.id.radio_car_text)

        rideDetailsLayout.visibility = View.GONE

        val eta = intent.getStringExtra("selected_eta") ?: "0"
        val etaInMinutes = eta.split(" ")[0].toIntOrNull() ?: 0

        updateFareUI(eta, etaInMinutes)

        createNotificationChannel()

        confirmButton.setOnClickListener {
            val selectedId = vehicleGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a vehicle type", Toast.LENGTH_SHORT).show()
            } else {
                val selectedRadioButton = findViewById<RadioButton>(selectedId)
                val vehicleType = selectedRadioButton.text.toString()

                val fare = calculateFare(vehicleType, etaInMinutes) // Get fare for selected vehicle

                vehicleGroup.visibility = View.GONE
                confirmButton.visibility = View.GONE
                rideDetailsLayout.visibility = View.VISIBLE

                selectedVehicleText.text = "Vehicle: $vehicleType"
                etaTextView.text = "Estimated Arrival Time: $eta"
                fareTextView.text = "Estimated Fare: ₹$fare" // Display fare
            }
        }

        startRideButton.setOnClickListener {
            Toast.makeText(this, "Ride started successfully!", Toast.LENGTH_SHORT).show()
            sendRideNotification()
        }

        cancelRideButton.setOnClickListener {
            setResult(RESULT_CANCELED, Intent().apply {
                putExtra("selected_vehicle", "Cancelled")
            })
            finish()
        }
    }

    private fun updateFareUI(eta: String, etaInMinutes: Int) {
        val bikeFare = calculateFare("Bike", etaInMinutes)
        val autoFare = calculateFare("Auto", etaInMinutes)
        val cabFare = calculateFare("Cab", etaInMinutes)

        bikeTextView.text = "Estimated time: $eta \nPrice Rs $bikeFare"
        autoTextView.text = "Estimated time: $eta \nPrice Rs $autoFare"
        cabTextView.text = "Estimated time: $eta \nPrice Rs $cabFare"
    }

    private fun calculateFare(vehicleType: String, etaInMinutes: Int): Int {
        val (baseFare, hourlyRate) = vehiclePricing[vehicleType] ?: Pair(50, 10)

        // Convert minutes into hours properly and apply hourly rate
        val hours = etaInMinutes / 60  // Convert ETA to hours
        val remainingMinutes = etaInMinutes % 60  // Get remaining minutes

        // Fare is base fare + hourly rate for each full hour + prorated rate for remaining minutes
        val totalFare = baseFare + (hours * hourlyRate) + ((remainingMinutes / 60.0) * hourlyRate).toInt()

        return totalFare * 60
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "ride_updates_channel",
                "Ride Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for ride updates"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendRideNotification() {
        val notificationBuilder = NotificationCompat.Builder(this, "ride_updates_channel")
            .setSmallIcon(android.R.drawable.ic_menu_directions)
            .setContentTitle("Ride Started")
            .setContentText("Your ride is on the way!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(1, notificationBuilder.build())
    }
}
