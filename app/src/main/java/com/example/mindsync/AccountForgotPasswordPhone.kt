package com.example.mindsync

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class AccountForgotPasswordPhone : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_phone)
    }

    // Handle back arrow click
    fun onBackArrowClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }

    fun onViewEmail(view: View?) {
        // Handle the click event, e.g., navigate to the sign-up screen
        val viewEmailIntent = Intent(this, AccountForgotPasswordEmail::class.java)
        startActivity(viewEmailIntent)
    }

}