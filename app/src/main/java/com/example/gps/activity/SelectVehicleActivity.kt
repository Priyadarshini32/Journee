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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_vehicle)

        dbHelper = SQLiteHelper(this)

        vehicleGroup = findViewById(R.id.vehicle_group)
        confirmButton = findViewById(R.id.confirm_button)
        rideDetailsLayout = findViewById(R.id.ride_details_layout)
        etaTextView = findViewById(R.id.eta_text)

        rideDetailsLayout.visibility = View.GONE

        val eta = intent.getStringExtra("selected_eta") ?: "--"
        etaTextView.text = "Estimated Arrival Time: $eta"

        insertDriversIfNeeded() // Insert drivers only if the DB is empty

        createNotificationChannel()

        confirmButton.setOnClickListener {
            val selectedId = vehicleGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a vehicle type", Toast.LENGTH_SHORT).show()
            } else {
                startDriverDetailsActivity()
            }
        }
    }

    private fun insertDriversIfNeeded() {
        if (dbHelper.getDriverCount() == 0) { // Check if DB is empty before inserting
            val drivers = listOf(
                // Bike drivers
                Quadruple("Ravi Kumar", "Bike", "9876543210", 5),
                Quadruple("Suresh Sharma", "Bike", "9876543211", 7),
                Quadruple("Anil Verma", "Bike", "9876543212", 6),
                Quadruple("Manoj Singh", "Bike", "9876543213", 8),
                Quadruple("Rajesh Gupta", "Bike", "9876543214", 9),

                // Auto drivers
                Quadruple("Sunil Yadav", "Auto", "9876543225", 6),
                Quadruple("Dinesh Patel", "Auto", "9876543226", 10),
                Quadruple("Ganesh Iyer", "Auto", "9876543227", 5),
                Quadruple("Mahesh Babu", "Auto", "9876543228", 7),
                Quadruple("Ramesh Thakur", "Auto", "9876543229", 12),

                // Cab drivers
                Quadruple("Arvind Tiwari", "Cab", "9876543240", 15),
                Quadruple("Sameer Khan", "Cab", "9876543241", 20),
                Quadruple("Himanshu Bhatt", "Cab", "9876543242", 11),
                Quadruple("Krishna Nair", "Cab", "9876543243", 8),
                Quadruple("Santosh Kumar", "Cab", "9876543244", 13)
            )

            for (driver in drivers) {
                dbHelper.insertDriver(driver.first, driver.second, driver.third, driver.fourth)
            }
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


data class Quadruple<T1, T2, T3, T4>(
    val first: T1,
    val second: T2,
    val third: T3,
    val fourth: T4
)
