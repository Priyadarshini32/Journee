package com.example.gps.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.gps.R

class SelectVehicleActivity : AppCompatActivity() {

    private lateinit var vehicleGroup: RadioGroup
    private lateinit var confirmButton: Button
    private lateinit var rideDetailsLayout: LinearLayout
    private lateinit var driverDetailsLayout: View
    private lateinit var etaTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_vehicle)

        vehicleGroup = findViewById(R.id.vehicle_group)
        confirmButton = findViewById(R.id.confirm_button)
        rideDetailsLayout = findViewById(R.id.ride_details_layout)
        driverDetailsLayout = findViewById(R.id.driver_details_layout)
        etaTextView = findViewById(R.id.radio_car_text)

        rideDetailsLayout.visibility = View.GONE
        driverDetailsLayout.visibility = View.GONE

        val eta = intent.getStringExtra("selected_eta") ?: "--"
        etaTextView.text = "Estimated Arrival Time: $eta"

        createNotificationChannel()

        confirmButton.setOnClickListener {
            val selectedId = vehicleGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a vehicle type", Toast.LENGTH_SHORT).show()
            } else {
                startProgressActivity()
            }
        }
    }

    private fun startProgressActivity() {
        val selectedId = vehicleGroup.checkedRadioButtonId
        val selectedRadioButton = findViewById<RadioButton>(selectedId)
        val selectedVehicleType = selectedRadioButton.text.toString()

        val intent = Intent(this, ProgressActivity::class.java)
        intent.putExtra("vehicle_type", selectedVehicleType)
        startActivity(intent)
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
}
