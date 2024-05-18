package com.example.mindsync

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if the user is authenticated
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // User is already logged in, redirect to DashboardActivity
            val intent = Intent(this, UserDashboardActivity::class.java)
            startActivity(intent)
            return
        }

        // Find the "Create account" button by its ID
        val createAccountButton: Button = findViewById(R.id.buttonCreateAccount)
        val loginAccountTextView: TextView = findViewById(R.id.textViewLinkSignIn)

        // Set an OnClickListener to handle the button click
        createAccountButton.setOnClickListener {
            // Create an Intent to start the account creation activity
            val intent = Intent(this, AccountCreationActivity::class.java)

            // Start the account creation activity
            startActivity(intent)
        }

        loginAccountTextView.setOnClickListener {
            val intent = Intent(this, AccountLoginActivity::class.java)
            startActivity(intent)
        }
    }
}