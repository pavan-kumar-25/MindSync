package com.example.mindsync

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import java.util.Date
import java.util.Locale

class AccountCreateProfileCreation : AppCompatActivity() {

    private lateinit var countryCode: String
    private lateinit var mobileNumber: String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        // Retrieve values from Intent
        countryCode = intent.getStringExtra("countryCode") ?: ""
        mobileNumber = intent.getStringExtra("mobile") ?: ""

        // -------------- Populating Gender View------------------
        val genderOptions = arrayOf("Male", "Female", "Others")
        val gender = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewGender)
        // Create ArrayAdapter using a built-in layout for each dropdown item
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderOptions)
        // Set the adapter to the AutoCompleteTextView
        gender.setAdapter(adapter)

        // --------------Setting up Calendar----------------------
        val dateOfBirth = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView2DOB)

        // Create a MaterialDatePicker
        val builder = MaterialDatePicker.Builder.datePicker()
        val datePicker = builder.build()

        // Set an OnDateChangedListener to update the AutoCompleteTextView when the date is selected
        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = datePicker.selection?.let { Date(it) }
            // Format the selected date as needed
            val formattedDate =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
            dateOfBirth.setText(formattedDate)
        }

        // Set an OnEditTextClickListener to open the calendar when the AutoCompleteTextView is clicked
        dateOfBirth.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                datePicker.show(supportFragmentManager, datePicker.toString())
            }
            false
        }
        // ---------------END-------------------------

        var firstName: String
        var lastName: String

        findViewById<Button>(R.id.buttonContinue).setOnClickListener {
            firstName = findViewById<EditText>(R.id.editTextFirstName).text.toString().trim()
            lastName = findViewById<EditText>(R.id.editTextLastName).text.toString().trim()

            if (validateInputs(
                    firstName,
                    lastName,
                    gender.text.toString().trim(),
                    dateOfBirth.text.toString().trim()
                )
            ) {
                // Get the authenticated user
                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser
                val userFullName = "$firstName $lastName"

                // Ensure that the user is authenticated
                if (currentUser != null) {
                    // Construct UserProfile object with user input
                    val userProfile = UserProfile(
//                        firstName.text.toString().trim(),
//                        lastName.text.toString().trim(),
                        gender.text.toString().trim(),
                        dateOfBirth.text.toString().trim(),
                        countryCode, // Provide the country code
                        mobileNumber // Provide the mobile number
                    )

                    val profileChangeRequest = UserProfileChangeRequest.Builder()
                        .setDisplayName(userFullName)
                        .build()

                    currentUser.updateProfile(profileChangeRequest)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Update user information in the database
                                updateUserInfoInDatabase(currentUser.uid, userProfile)
                            } else {
                                // If the update fails, handle the error
                                val exception = task.exception
                                showToast("Some Error Occurred:${exception?.message}")
                            }
                        }
                }
            }
        }
    }

        private fun validateInputs(
            firstName: String,
            lastName: String,
            gender: String,
            dob: String
        ): Boolean {
            // Check if any field is empty
            if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) ||
                TextUtils.isEmpty(gender) || TextUtils.isEmpty(dob)
            ) {
                showToast("Please fill in all fields")
                return false
            }

            // Check if first name and last name contain only characters
            if (!isOnlyCharacters(firstName) || !isOnlyCharacters(lastName)) {
                showToast("First name and last name should contain only characters")
                return false
            }

            // Check if a gender value is chosen
            val genderOptions = arrayOf("Male", "Female", "Others")
            if (!genderOptions.contains(gender)) {
                showToast("Please choose a valid gender")
                return false
            }

            // Check if a date of birth is chosen
            if (dob == "Date of Birth") {
                showToast("Please choose a valid date of birth")
                return false
            }

            return true
        }

        private fun isOnlyCharacters(input: String): Boolean {
            val regex = "^[a-zA-Z]+\$"
            return input.matches(Regex(regex))
        }

        private fun showToast(message: String) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

    private fun updateUserInfoInDatabase(uid: String, userProfile: UserProfile) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users")
            .child(uid) // Use UID as a unique identifier for the user

        // Update user information in the database
        databaseReference.setValue(userProfile)
            .addOnSuccessListener {
                // Successfully updated user information
                showToast("Account Created successfully")
                // Proceed to the next activity
                val intent = Intent(this, CategoryTypesActivity::class.java)
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
//                finish()
            }
            .addOnFailureListener {
                // Handle the error
                showToast("User Registration Failed. Please try Again: ${it.message}")
            }
    }
        // Handle back arrow click
        fun onBackArrowClick(view: View) {
            onBackPressedDispatcher.onBackPressed()
        }
    }


