package com.example.gps.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.gps.R

class SelectVehicleActivity : AppCompatActivity() {

    private lateinit var vehicleGroup: RadioGroup
    private lateinit var confirmButton: Button
    private lateinit var rideDetailsLayout: LinearLayout
    private lateinit var selectedVehicleText: TextView
    private lateinit var startRideButton: Button
    private lateinit var cancelRideButton: Button
    private lateinit var etaTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_vehicle)

        vehicleGroup = findViewById(R.id.vehicle_group)
        confirmButton = findViewById(R.id.confirm_button)
        rideDetailsLayout = findViewById(R.id.ride_details_layout)
        selectedVehicleText = findViewById(R.id.selected_vehicle_text)
        startRideButton = findViewById(R.id.start_ride_button)
        cancelRideButton = findViewById(R.id.cancel_ride_button)
        etaTextView = findViewById(R.id.eta_text_view)

        val eta = intent.getStringExtra("selected_eta") ?: "Unknown ETA"
        etaTextView.text = "Estimated Arrival Time: $eta"

        createNotificationChannel() // Ensure the notification channel is created

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
                vehicleGroup.visibility = RadioGroup.GONE
                confirmButton.visibility = Button.GONE
                rideDetailsLayout.visibility = LinearLayout.VISIBLE
                selectedVehicleText.text = "Vehicle: $vehicleType"
            }
        }

        startRideButton.setOnClickListener {
            Toast.makeText(this, "Ride started successfully!", Toast.LENGTH_SHORT).show()
            sendRideNotification() // Send the initial notification
        }

        cancelRideButton.setOnClickListener {
            setResult(RESULT_CANCELED, Intent().apply {
                putExtra("selected_vehicle", "Cancelled")
            })
            finish()
        }
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
            .setSmallIcon(android.R.drawable.ic_menu_directions) // Default Android icon
            .setContentTitle("Ride Started")
            .setContentText("Your ride is on the way!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(1, notificationBuilder.build()) // Send notification
    }
}
