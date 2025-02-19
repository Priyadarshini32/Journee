package com.example.gps.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
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

        // Initialize SQLite helper
        sqliteHelper = SQLiteHelper(this)

        // Find views by ID
        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.book_ride_button)
        signUpTextView = findViewById(R.id.catchy_tagline2) // TextView for "New User? Sign up Here"

        // Apply styling to "Sign up" text
        val text = "New User? Sign up Here"
        val spannable = SpannableString(text)
        val startIndex = text.indexOf("Sign up")
        val endIndex = startIndex + "Sign up".length

        spannable.setSpan(ForegroundColorSpan(Color.BLACK), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(UnderlineSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        signUpTextView.text = spannable

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

        // Set click listener for "Sign up" text to navigate to RegisterActivity
        signUpTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
