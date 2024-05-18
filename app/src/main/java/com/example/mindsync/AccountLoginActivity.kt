package com.example.mindsync

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class AccountLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_activity)

        auth = FirebaseAuth.getInstance()
    }

    fun onSignUpClick(view: View?) {
        // Handle the click event, e.g., navigate to the sign-up screen
        val signUpIntent = Intent(this, AccountCreationActivity::class.java)
        startActivity(signUpIntent)
    }

    fun onForgotPassword(view: View?) {
        // Handle the click event, e.g., navigate to the forgot password screen
        val forgotPasswordIntent = Intent(this, AccountForgotPasswordEmail::class.java)
        startActivity(forgotPasswordIntent)
    }

    fun onClickLogin(view: View?) {
        val editTextUsername: EditText = findViewById(R.id.editTextEmail)
        val editTextPassword: EditText = findViewById(R.id.editTextPassword)

        val username = editTextUsername.text.toString()
        val password = editTextPassword.text.toString()

        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show()
            return
        }
        else{
            // Authenticate user with email and password
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user: FirebaseUser? = auth.currentUser
                        showToast("Login Success")

                        // Proceed to the UserDashboardActivity
                        val userDashboardIntent = Intent(this, UserDashboardActivity::class.java)
                        startActivity(userDashboardIntent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        showToast("Invalid Credentials!Please check your email and password.")
                    }
                }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    // Handle back arrow click
    fun onBackArrowClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }
}
