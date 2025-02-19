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
    private lateinit var startRideButton: Button
    private lateinit var cancelRideButton: Button

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_vehicle)

        vehicleGroup = findViewById(R.id.vehicle_group)
        confirmButton = findViewById(R.id.confirm_button)
        rideDetailsLayout = findViewById(R.id.ride_details_layout)
        selectedVehicleText = findViewById(R.id.selected_vehicle_text)
        etaTextView = findViewById(R.id.eta_text_view)
        startRideButton = findViewById(R.id.start_ride_button)
        cancelRideButton = findViewById(R.id.cancel_ride_button)

        rideDetailsLayout.visibility = View.GONE

        val eta = intent.getStringExtra("selected_eta") ?: "--"
        etaTextView.text = "Estimated Arrival Time: $eta"

        createNotificationChannel() // Ensure notification channel is set up

        confirmButton.setOnClickListener {
            val selectedId = vehicleGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a vehicle type", Toast.LENGTH_SHORT).show()
            } else {
                val selectedRadioButton = findViewById<RadioButton>(selectedId)
                val vehicleType = selectedRadioButton.text.toString()

                vehicleGroup.visibility = View.GONE
                confirmButton.visibility = View.GONE
                rideDetailsLayout.visibility = View.VISIBLE
                selectedVehicleText.text = "Vehicle: $vehicleType"
            }
        }

        startRideButton.setOnClickListener {
            Toast.makeText(this, "Ride started successfully!", Toast.LENGTH_SHORT).show()
            sendRideNotification() // Send ride started notification
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
            .setSmallIcon(android.R.drawable.ic_menu_directions)
            .setContentTitle("Ride Started")
            .setContentText("Your ride is on the way!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(1, notificationBuilder.build())
    }
}
