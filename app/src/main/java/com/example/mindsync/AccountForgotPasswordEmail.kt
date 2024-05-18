package com.example.mindsync

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class AccountForgotPasswordEmail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_email)
    }

    // Handle back arrow click
    fun onBackArrowClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }

    fun onViewPhone(view: View?) {
        // Handle the click event, e.g., navigate to the phone screem
        val viewPhoneIntent = Intent(this, AccountForgotPasswordPhone::class.java)
        startActivity(viewPhoneIntent)
    }
}