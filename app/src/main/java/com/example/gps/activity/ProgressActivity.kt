package com.example.gps.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gps.R

class ProgressActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var loadingText: TextView
    private lateinit var cancelLoadingButton: Button
    private val handler = Handler(Looper.getMainLooper())
    private var isCancelled = false  // Flag to track cancellation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        progressBar = findViewById(R.id.progress_bar)
        loadingText = findViewById(R.id.loading_text)
        cancelLoadingButton = findViewById(R.id.cancel_loading_button)

        progressBar.progress = 0
        startProgressBar()

        cancelLoadingButton.setOnClickListener {
            isCancelled = true  // Mark as cancelled
            redirectToHome()
        }
    }

    private fun startProgressBar() {
        val duration = 30 * 1000 // 30 seconds
        val interval = 1000 // 1-second intervals

        progressBar.max = duration / interval
        var progress = 0

        val runnable = object : Runnable {
            override fun run() {
                if (progress < progressBar.max) {
                    if (!isCancelled) { // Check if the process was not cancelled
                        progress++
                        progressBar.progress = progress
                        handler.postDelayed(this, interval.toLong())
                    }
                } else if (!isCancelled) { // Only start ride if not cancelled
                    rideStarted()
                }
            }
        }
        handler.post(runnable)
    }

    private fun rideStarted() {
        if (isCancelled) return // Prevent navigation if cancelled

        val driverName = intent.getStringExtra("driver_name")
        val vehicleType = intent.getStringExtra("vehicle_type")
        val driverContact = intent.getStringExtra("driver_contact")
        val driverExperience = intent.getIntExtra("driver_experience", 0)

        val intent = Intent(this, DriverDetailsActivity::class.java).apply {
            putExtra("driver_name", driverName)
            putExtra("vehicle_type", vehicleType)
            putExtra("driver_contact", driverContact)
            putExtra("driver_experience", driverExperience)
        }
        startActivity(intent)
        finish()
    }

    private fun redirectToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
