package com.example.mindsync

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit

class AccountCreationActivity : AppCompatActivity() {

    private lateinit var progressBar : ProgressBar
    private lateinit var buttonGetOTP : Button
    private lateinit var selectedCountryCode : String
    private lateinit var phoneNumber: String
    private lateinit var inputMobile: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_creation)

        buttonGetOTP = findViewById((R.id.btnSendOtp))
        inputMobile = findViewById(R.id.editTextPhoneNumber)
        val countryCodePicker: CountryCodePicker = findViewById(R.id.countryCodePicker)
        progressBar = findViewById(R.id.progressBar)

        buttonGetOTP.setOnClickListener{
            // Get selected country code
            selectedCountryCode = countryCodePicker.selectedCountryCode
            phoneNumber = "+" + selectedCountryCode + inputMobile.text.toString().trim()
            if(inputMobile.text.toString().trim().isEmpty()){
                Toast.makeText(this, "Enter Mobile number!",Toast.LENGTH_LONG).show()
            }
            else{
                // Check if the phone number is already registered
                checkIfPhoneNumberExists(inputMobile.text.toString().trim(), phoneNumber)
            }
        }
    }

    // Function to check if the phone number is already registered
    private fun checkIfPhoneNumberExists(checkPhoneNumber: String, phoneNumber: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users")

        databaseReference.orderByChild("mobileNumber").equalTo(checkPhoneNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.childrenCount > 0) {
                        // Phone number is already registered
                        Toast.makeText(
                            this@AccountCreationActivity,
                            "This number is already registered. Use another number.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(this@AccountCreationActivity,"OTP Sent", Toast.LENGTH_LONG).show()
                        // Phone number is not registered, proceed with sending OTP
                        sendOTP(phoneNumber)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error
                    Toast.makeText(
                        this@AccountCreationActivity,
                        "Error checking phone number existence: ${databaseError.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    // Function to send OTP
    private fun sendOTP(phoneNumber: String) {
        progressBar.visibility = View.VISIBLE
        buttonGetOTP.visibility = View.INVISIBLE

        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(phoneNumber) // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout duration
                .setActivity(this@AccountCreationActivity) // Activity (context) to use for launching the verification process
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        progressBar.visibility = View.GONE
                        buttonGetOTP.visibility = View.VISIBLE
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        progressBar.visibility = View.GONE
                        buttonGetOTP.visibility = View.VISIBLE
                        Toast.makeText(this@AccountCreationActivity, e.message,Toast.LENGTH_LONG).show()
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        progressBar.visibility = View.GONE
                        buttonGetOTP.visibility = View.VISIBLE
                        val sendOtpIntent = Intent(applicationContext, OtpVerificationActivity::class.java)
                        sendOtpIntent.putExtra("countryCode",selectedCountryCode)
                        sendOtpIntent.putExtra("mobile",inputMobile.text.toString().trim())
                        sendOtpIntent.putExtra("verificationId",verificationId)
                        startActivity(sendOtpIntent)
                    }
                })
                .build()
        )
    }
    // Handle back arrow click
    fun onBackArrowClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }

}
