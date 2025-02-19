package com.example.gps.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.gps.R
import com.example.gps.db.SQLiteHelper

class SelectVehicleActivity : AppCompatActivity() {

    private lateinit var vehicleGroup: RadioGroup
    private lateinit var confirmButton: Button
    private lateinit var rideDetailsLayout: LinearLayout
    private lateinit var etaTextView: TextView
    private lateinit var dbHelper: SQLiteHelper
    private var eta: String = "--"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_vehicle)

        dbHelper = SQLiteHelper(this)

        vehicleGroup = findViewById(R.id.vehicle_group)
        confirmButton = findViewById(R.id.confirm_button)
        rideDetailsLayout = findViewById(R.id.ride_details_layout)
        etaTextView = findViewById(R.id.eta_text)

        rideDetailsLayout.visibility = View.GONE

        eta = intent.getStringExtra("selected_eta") ?: "--"
        etaTextView.text = "Estimated Arrival Time: $eta"

        insertDriversIfNeeded() // Insert drivers only if the DB is empty

        createNotificationChannel()

        updatePrice() // Update prices when screen loads

        vehicleGroup.setOnCheckedChangeListener { _, _ ->
            updatePrice() // Update price when vehicle selection changes
        }

        confirmButton.setOnClickListener {
            val selectedId = vehicleGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a vehicle type", Toast.LENGTH_SHORT).show()
            } else {
                startDriverDetailsActivity()
            }
        }
    }

    private fun updatePrice() {
        val totalMinutes = parseEtaToMinutes(eta)

        // Define price per minute for each vehicle
        val vehiclePrices = mapOf(
            "Bike" to 5,
            "Auto" to 7,
            "Cab" to 10
        )

        // Update each vehicle's price in the UI
        vehiclePrices.forEach { (vehicle, pricePerMinute) ->
            val totalPrice = totalMinutes * pricePerMinute
            val priceText = "Price Rs $totalPrice"

            when (vehicle) {
                "Bike" -> findViewById<TextView>(R.id.radio_bike_text).text = priceText
                "Auto" -> findViewById<TextView>(R.id.radio_auto_text).text = priceText
                "Cab"  -> findViewById<TextView>(R.id.radio_car_text).text = priceText
            }
        }
    }

    private fun parseEtaToMinutes(eta: String): Int {
        val regex = Regex("(\\d+) hour? (\\d+) mins?")
        val match = regex.find(eta)

        return if (match != null) {
            val (hours, minutes) = match.destructured
            hours.toInt() * 60 + minutes.toInt()
        } else {
            0
        }
    }

    private fun startDriverDetailsActivity() {
        val selectedId = vehicleGroup.checkedRadioButtonId
        val selectedRadioButton = findViewById<RadioButton>(selectedId)
        val selectedVehicleType = selectedRadioButton.text.toString() // Get selected vehicle type

        val driver = dbHelper.getRandomDriverByVehicleType(selectedVehicleType)

        if (driver == null) {
            Toast.makeText(this, "No drivers available for $selectedVehicleType", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, ProgressActivity::class.java).apply {
            putExtra("driver_name", driver.name)
            putExtra("vehicle_type", selectedVehicleType)
            putExtra("driver_contact", driver.contact)
            putExtra("driver_experience", driver.experience)
        }
        startActivity(intent)
    }

    private fun insertDriversIfNeeded() {
        if (dbHelper.getDriverCount() == 0) { // Check if DB is empty before inserting
            val drivers = listOf(
                // Bike drivers
                DriverDetails("Ravi Kumar", "Bike", "9876543210", 5),
                DriverDetails("Suresh Sharma", "Bike", "9876543211", 7),
                DriverDetails("Anil Verma", "Bike", "9876543212", 6),
                DriverDetails("Manoj Singh", "Bike", "9876543213", 8),
                DriverDetails("Rajesh Gupta", "Bike", "9876543214", 9),

                // Auto drivers
                DriverDetails("Sunil Yadav", "Auto", "9876543225", 6),
                DriverDetails("Dinesh Patel", "Auto", "9876543226", 10),
                DriverDetails("Ganesh Iyer", "Auto", "9876543227", 5),
                DriverDetails("Mahesh Babu", "Auto", "9876543228", 7),
                DriverDetails("Ramesh Thakur", "Auto", "9876543229", 12),

                // Cab drivers
                DriverDetails("Arvind Tiwari", "Cab", "9876543240", 15),
                DriverDetails("Sameer Khan", "Cab", "9876543241", 20),
                DriverDetails("Himanshu Bhatt", "Cab", "9876543242", 11),
                DriverDetails("Krishna Nair", "Cab", "9876543243", 8),
                DriverDetails("Santosh Kumar", "Cab", "9876543244", 13)
            )

            for (driver in drivers) {
                dbHelper.insertDriver(driver.first, driver.second, driver.third, driver.fourth)
            }
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

    data class DriverDetails<T1, T2, T3, T4>(
        val first: T1,
        val second: T2,
        val third: T3,
        val fourth: T4
    )
}
