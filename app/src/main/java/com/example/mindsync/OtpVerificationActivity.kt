package com.example.mindsync

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var editTexts: List<EditText>
    private var verificationId: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        val inputCode1: EditText = findViewById(R.id.inputCode1)
        val inputCode2: EditText = findViewById(R.id.inputCode2)
        val inputCode3: EditText = findViewById(R.id.inputCode3)
        val inputCode4: EditText = findViewById(R.id.inputCode4)
        val inputCode5: EditText = findViewById(R.id.inputCode5)
        val inputCode6: EditText = findViewById(R.id.inputCode6)

        // Initialize the editTexts list
        editTexts = listOf(inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6)


        // Retrieve values from Intent
        val countryCode = intent.getStringExtra("countryCode")
        val mobileNumber = intent.getStringExtra("mobile")

        // Now you can use these values to set text in your XML layout
        val textViewPhoneNumber: TextView = findViewById(R.id.textViewPhoneNumber)
        textViewPhoneNumber.text = "+$countryCode $mobileNumber"

        setupOTPInputs()

        val progressBar : ProgressBar = findViewById(R.id.progressBar)
        val buttonVerify : Button = findViewById(R.id.VerifyOtpButton)
        verificationId = intent.getStringExtra("verificationId")

        buttonVerify.setOnClickListener{
            if(inputCode1.text.toString().trim().isEmpty()
                ||inputCode2.text.toString().trim().isEmpty()
                ||inputCode3.text.toString().trim().isEmpty()
                ||inputCode4.text.toString().trim().isEmpty()
                ||inputCode5.text.toString().trim().isEmpty()
                ||inputCode6.text.toString().trim().isEmpty()){
                Toast.makeText(this, "Please Enter Valid Code!", Toast.LENGTH_LONG).show()
            }
            else{
                val code: String = inputCode1.text.toString() +
                                   inputCode2.text.toString() +
                                   inputCode3.text.toString() +
                                   inputCode4.text.toString() +
                                   inputCode5.text.toString() +
                                   inputCode6.text.toString()
                if (verificationId != null){
                    progressBar.visibility = View.VISIBLE
                    buttonVerify.visibility = View.INVISIBLE

                    val phoneAuthCredential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        verificationId!!, // The verification ID received from onCodeSent callback
                        code // The code entered by the user
                    )
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener { task ->
                            progressBar.visibility = View.GONE
                            buttonVerify.visibility = View.VISIBLE
                            if (task.isSuccessful) {
                                val accountUsernameIntent = Intent(applicationContext, AccountCreateEmailPassActivity::class.java)
                                Toast.makeText(this@OtpVerificationActivity, "OTP Verification Successful!",Toast.LENGTH_LONG).show()
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                accountUsernameIntent.putExtra("countryCode",countryCode)
                                accountUsernameIntent.putExtra("mobile",mobileNumber)
                                startActivity(accountUsernameIntent)
//                                finish()
                            } else {
                                Toast.makeText(this@OtpVerificationActivity, "The verification code entered was invalid!",Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }
        }

        findViewById<TextView>(R.id.textViewResendOtp).setOnClickListener{
            PhoneAuthProvider.verifyPhoneNumber(
                PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                    .setPhoneNumber("+$countryCode$mobileNumber") // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout duration
                    .setActivity(this@OtpVerificationActivity) // Activity (context) to use for launching the verification process
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            Toast.makeText(this@OtpVerificationActivity, e.message,Toast.LENGTH_LONG).show()
                        }

                        override fun onCodeSent(
                            newVerificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            verificationId = newVerificationId
                            Toast.makeText(this@OtpVerificationActivity, "OTP Sent",Toast.LENGTH_LONG).show()
                        }
                    })
                    .build()
            )
        }
    }

    private fun setupOTPInputs() {
        for ((index, editText) in editTexts.withIndex()) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1) {
                        // Move focus to the next EditText
                        moveToNextEditText(index)
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    // Move focus to the previous EditText
                    moveToPreviousEditText(index)
                    return@setOnKeyListener true
                }
                false
            }
        }
    }
    private fun moveToNextEditText(index: Int) {
        if (index < editTexts.size - 1) {
            editTexts[index + 1].requestFocus()
        }
    }

    private fun moveToPreviousEditText(index: Int) {
        if (index > 0) {
            editTexts[index].text.clear() // Clear the text in the current EditText
            editTexts[index - 1].requestFocus() // Move focus to the previous EditText
        } else if (index == 0) {
            editTexts[index].text.clear() // Clear the text in the first EditText
            editTexts[index].requestFocus() // Set focus to the first EditText
        }
    }


    // Handle back arrow click
    fun onBackArrowClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }

    fun onClickViewTerms(view: View?) {

    }
    fun onClickViewPolicy(view: View?) {

    }

}