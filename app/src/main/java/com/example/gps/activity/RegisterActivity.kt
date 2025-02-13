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

class RegisterActivity : ComponentActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var signInTextView: TextView
    private lateinit var sqliteHelper: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize the SQLite helper
        sqliteHelper = SQLiteHelper(this)

        // Find views by ID
        usernameEditText = findViewById(R.id.username)
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        registerButton = findViewById(R.id.book_ride_button)
        signInTextView = findViewById(R.id.catchy_tagline) // TextView for "Already a User? Sign in Here"

        // Set click listener for register button
        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                val result = sqliteHelper.insertUser(username, email, password)
                if (result != -1L) {
                    // Registration successful, redirect to login page
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java) // Navigate to LoginActivity
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for sign in text to navigate to LoginActivity
        signInTextView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // Navigate to LoginActivity
            startActivity(intent)
        }
    }
}
