package com.example.mindsync

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hbb20.CountryCodePicker
import java.util.*


class SettingsActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var editTextDOB: EditText
    private lateinit var databaseReference: DatabaseReference
    private lateinit var editTextCurrentPassword: EditText
    private lateinit var editTextNewPassword: EditText
    private lateinit var countryCodePicker: CountryCodePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextDOB = findViewById(R.id.editTextDOB)
        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword)
        editTextNewPassword = findViewById(R.id.editTextNewPassword)
        countryCodePicker = findViewById(R.id.countryCodePicker)

        // Retrieve and set user's data
        retrieveAndSetUserData()
        // Set click listener for the "Save" button
        findViewById<View>(R.id.textViewSaveEmail).setOnClickListener {
            updateEmail()
        }

        // Set click listener for the "Save" button for changing password
        findViewById<View>(R.id.textViewSavePassword).setOnClickListener {
            changePassword()
        }

        findViewById<View>(R.id.textViewSaveDOB).setOnClickListener {
            updateDOB()
        }

        // Inside onCreate() or your initialization method
//        findViewById<View>(R.id.textViewSavePhone).setOnClickListener {
//            updateMobileNumber()
//        }

    }

//    // Function to update user's mobile number
//    private fun updateMobileNumber() {
//        val newMobileNumber = editTextPhone.text.toString().trim()
//        val countryCode = countryCodePicker.selectedCountryCodeWithPlus
//
//        // Check if the new mobile number is valid
//        if (newMobileNumber.isEmpty()) {
//            editTextPhone.error = "Mobile Number cannot be empty"
//            return
//        }
//
//        // Update mobile number in Firebase Authentication
//        val phoneNumberWithCountryCode = countryCode + newMobileNumber
//        val user = FirebaseAuth.getInstance().currentUser
//
//        // Create PhoneAuthCredential with the new phone number
//        val credential = PhoneAuthProvider.getCredential(user?.phoneNumber ?: "", phoneNumberWithCountryCode)
//
//        user?.updatePhoneNumber(credential)
//            ?.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    // Mobile number updated successfully in Authentication
//                    // Now update mobile number in Realtime Database
//                    updateMobileNumberInDatabase(newMobileNumber, countryCode)
//                } else {
//                    // Mobile number update failed in Authentication
//                    Toast.makeText(this, "Failed to update Mobile Number in Authentication: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }


    // Function to update user's mobile number in Realtime Database
//    private fun updateMobileNumberInDatabase(newMobileNumber: String, countryCode: String) {
//        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
//        databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users").child(currentUserUid)
//
//        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    // Update mobile number in the database
//                    dataSnapshot.ref.child("mobileNumber").setValue(newMobileNumber)
//                        .addOnSuccessListener {
//                            // Mobile number updated successfully in the database
//                            Toast.makeText(this@SettingsActivity, "Mobile Number Updated Successfully", Toast.LENGTH_SHORT).show()
//                        }
//                        .addOnFailureListener { e ->
//                            // Mobile number update failed in the database
//                            Toast.makeText(this@SettingsActivity, "Failed to update Mobile Number in Database: ${e.message}", Toast.LENGTH_SHORT).show()
//                        }
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Handle the error
//                Toast.makeText(this@SettingsActivity, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
    // Function to update user's date of birth
    private fun updateDOB() {
        val newDOB = editTextDOB.text.toString().trim()

        // Check if the new DOB is valid
        if (newDOB.isEmpty()) {
            Toast.makeText(this, "Date of Birth cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Update DOB in Firebase Realtime Database
        val userUid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users").child(userUid)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Update DOB only if the current user UID matches with the database UID
                    if (dataSnapshot.key == userUid) {
                        dataSnapshot.ref.child("dateOfBirth").setValue(newDOB)
                            .addOnSuccessListener {
                                // DOB updated successfully
                                Toast.makeText(this@SettingsActivity, "Date of Birth Updated Successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                // DOB update failed
                                Toast.makeText(this@SettingsActivity, "Failed to update Date of Birth: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // User UID mismatch, cannot update DOB
                        Toast.makeText(this@SettingsActivity, "Unauthorized access", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // User data not found
                    Toast.makeText(this@SettingsActivity, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                Toast.makeText(this@SettingsActivity, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    // Function to change user's password
    private fun changePassword() {
        val currentPassword = editTextCurrentPassword.text.toString()
        val newPassword = editTextNewPassword.text.toString()

        // Validate if current password and new password are entered
        if (currentPassword.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Please enter current and new passwords", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if current and new passwords are same
        if (currentPassword == newPassword) {
            Toast.makeText(this, "Current and new password cannot be the same", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate current password
        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(user?.email ?: "", currentPassword)
        user?.reauthenticate(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Current password is correct, update password
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { passwordTask ->
                            if (passwordTask.isSuccessful) {
                                // Password updated successfully
                                Toast.makeText(this, "Password Updated Successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                // Password update failed
                                Toast.makeText(this, "Failed to update password: ${passwordTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    // Current password is incorrect
                    Toast.makeText(this, "Incorrect current password", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Function to update user's email
    private fun updateEmail() {
        val newEmail = editTextEmail.text.toString().trim()

        // Check if the new email is valid
        if (newEmail.isEmpty()) {
            editTextEmail.error = "Email cannot be empty"
            return
        }

        // Update email in Firebase Authentication
        val user = FirebaseAuth.getInstance().currentUser
        user?.updateEmail(newEmail)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Email updated successfully
                    Toast.makeText(this, "Email Updated Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Email update failed
                    Toast.makeText(this, "Failed to update email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Function to show the DatePickerDialog
    fun onDateOfBirthClick(view: android.view.View) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                editTextDOB.setText(selectedDate)
            },
            year,
            month,
            day
        )

        // Show DatePickerDialog
        datePickerDialog.show()
    }

    // Function to retrieve and set user's data from Firebase
    private fun retrieveAndSetUserData() {
        // Retrieve email from Firebase Authentication
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email

        // Set email to corresponding EditText field
        editTextEmail.setText(userEmail)

        // Retrieve phone number from Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users")
            .child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userPhone = dataSnapshot.child("mobileNumber").getValue(String::class.java)
                    val userDOB = dataSnapshot.child("dateOfBirth").getValue(String::class.java)

                    // Set data to corresponding EditText fields
                    editTextPhone.setText(userPhone)
                    editTextDOB.setText(userDOB)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
            }
        })
    }


    // Handle back arrow click
    fun onBackArrowClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }
}