package com.example.gps.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.gps.R
import com.example.gps.db.SQLiteHelper

class MainActivity : ComponentActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpTextView: TextView
    private lateinit var sqliteHelper: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize the SQLite helper
        sqliteHelper = SQLiteHelper(this)

        // Find views by ID
        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.book_ride_button)
        signUpTextView = findViewById(R.id.catchy_tagline2) // TextView for "New User? Sign up Here"

        // Set click listener for login button
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (sqliteHelper.isUserExists(username, password)) {
                // Login successful, redirect to home page
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid login credentials", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for sign up text to navigate to RegisterActivity
        signUpTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java) // Navigate to RegisterActivity
            startActivity(intent)
        }
    }
}
