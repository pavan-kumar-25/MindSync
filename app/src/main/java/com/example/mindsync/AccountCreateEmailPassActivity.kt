package com.example.mindsync

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AccountCreateEmailPassActivity : AppCompatActivity()  {

    private lateinit var countryCode: String
    private lateinit var mobileNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_email_pass)

        // Retrieve values from Intent
        countryCode = intent.getStringExtra("countryCode") ?: ""
        mobileNumber = intent.getStringExtra("mobile") ?: ""

        val email: EditText = findViewById(R.id.editTextEmail)
        val password: EditText = findViewById(R.id.editTextPassword)
        val confirmPassword: EditText = findViewById(R.id.editTextConfirmPassword)

        // Add click listener "Continue" button
        findViewById<TextView>(R.id.buttonContinue).setOnClickListener {
            if (validateInputs(
                    email.text.toString().trim(),
                    password.text.toString().trim(),
                    confirmPassword.text.toString().trim()
                )
            ) {
                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser

                // Ensure that the user is authenticated with the phone number
                if (currentUser != null && currentUser.phoneNumber != null) {
                    checkEmailAndLink(currentUser, email.text.toString().trim(), password.text.toString().trim())
                } else {
                    Toast.makeText(this, "User not authenticated with phone number", Toast.LENGTH_SHORT).show()
                    // Handle the case where the user is not authenticated with the phone number
                }
            }
        }
    }

    private fun checkEmailAndLink(user: FirebaseUser, email: String, password: String) {
        // Check if the email is already registered
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (!signInMethods.isNullOrEmpty()) {
                        // Email already exists, show an appropriate message
                        Toast.makeText(this@AccountCreateEmailPassActivity, "Email already linked with another account!\nUse Another Email.", Toast.LENGTH_LONG).show()
                    } else {
                        // Email doesn't exist, proceed to link
                        linkEmailAndPassword(user, email, password)
                    }
                } else {
                    // An error occurred while checking the email
                    Toast.makeText(this@AccountCreateEmailPassActivity, "Error checking email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun linkEmailAndPassword(user: FirebaseUser, email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        user.linkWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Remove this toast
                    Toast.makeText(
                        this@AccountCreateEmailPassActivity,
                        "Email and password linked successfully!",
                        Toast.LENGTH_LONG
                    ).show()
                // Now, the user has both phone and email authentication linked to the same account
                val profileCreationIntent = Intent(this, AccountCreateProfileCreation::class.java)
                profileCreationIntent.putExtra("countryCode", countryCode)
                profileCreationIntent.putExtra("mobile", mobileNumber)
                startActivity(profileCreationIntent)
//                finish()
                } else {
                    Toast.makeText(
                        this@AccountCreateEmailPassActivity,
                        "Failed to link email and password: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }


    private fun validateInputs(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Please fill in all fields")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Invalid email format")
            return false
        }

        if (password.length < 5 || password.length > 12 ||
            !password.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()-+]).+\$"))
        ) {
            showToast("Password should be 5-12 characters with at least one uppercase,\n one lowercase, one special character, and one number")
            return false
        }

        if (password != confirmPassword) {
            showToast("Password and Confirm Password do not match")
            return false
        }

        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Handle back arrow click
    fun onBackArrowClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }
}