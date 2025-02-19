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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        progressBar = findViewById(R.id.progress_bar)
        loadingText = findViewById(R.id.loading_text)
        cancelLoadingButton = findViewById(R.id.cancel_loading_button)

        progressBar.progress = 0
        startProgressBar()

        cancelLoadingButton.setOnClickListener {
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
                    progress++
                    progressBar.progress = progress
                    handler.postDelayed(this, interval.toLong())
                } else {
                    rideStarted()
                }
            }
        }
        handler.post(runnable)
    }

    private fun rideStarted() {
        val intent = Intent(this, DriverDetailsActivity::class.java)
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
