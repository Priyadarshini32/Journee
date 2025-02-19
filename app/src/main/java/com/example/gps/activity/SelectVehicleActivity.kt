package com.example.gps.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gps.R

class SelectVehicleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_vehicle)

        val vehicleGroup: RadioGroup = findViewById(R.id.vehicle_group)
        val confirmButton: Button = findViewById(R.id.confirm_button)

        confirmButton.setOnClickListener {
            val selectedId = vehicleGroup.checkedRadioButtonId
            val vehicleType = when (selectedId) {
                R.id.radio_bike -> "Bike"
                R.id.radio_auto -> "Auto"
                R.id.radio_car -> "Car"
                else -> null
            }

            if (vehicleType == null) {
                Toast.makeText(this, "Please select a vehicle type", Toast.LENGTH_SHORT).show()
            } else {
                val resultIntent = Intent()
                resultIntent.putExtra("selected_vehicle", vehicleType)
                setResult(RESULT_OK, resultIntent)
                finish() // Close this activity and return to HomeActivity
            }
        }
    }
}
