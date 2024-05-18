package com.example.mindsync

import CircleTransform
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var currentUser: FirebaseUser
    private lateinit var imageView_dp : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users")

        imageView_dp = findViewById(R.id.imageView_profile_dp)
        // Retrieve user information from Firebase and set the name
        val nameEditText: EditText = findViewById(R.id.editTextName)
        nameEditText.setText(currentUser.displayName)

        // Spinner for Pronouns
        val spinnerPronouns: Spinner = findViewById(R.id.spinnerPronouns)
        val pronounArray = arrayOf("Pronouns", "He/Him", "She/Her", "They/Them", "Other")
        val pronounAdapter = ArrayAdapter(this, R.layout.spinner_item, pronounArray)
        pronounAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPronouns.adapter = pronounAdapter

        spinnerPronouns.setSelection(0, false)

        spinnerPronouns.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                // Handle item selection
                val selectedPronoun = pronounArray[position]
                if (position == 0) {
                    // This is the hint item, handle accordingly
                    // For example, you can display a message or set a default value
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing here
            }
        }

        // Spinner for Gender
        val spinnerGender: Spinner = findViewById(R.id.spinnerGender)

        // Create an ArrayAdapter for gender options
        val genderArray = arrayOf("Gender", "Male", "Female", "Non-Binary", "Other")
        val genderAdapter = ArrayAdapter(this, R.layout.spinner_item, genderArray)

        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = genderAdapter
        spinnerGender.setSelection(0, false)

        // Retrieve gender from Firebase and set it to the Spinner
        databaseReference.child(currentUser.uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val gender = dataSnapshot.child("gender").getValue(String::class.java)
                setSpinnerSelection(spinnerGender, genderArray, gender)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })

        spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                // Handle item selection
                val selectedGender = genderArray[position]
                if (position == 0) {
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {

            }
        }

        // onClick Listener for Uploading image or display profile
        findViewById<ImageView>(R.id.imageView_profile_dp).setOnClickListener{
            val intentUploadProfilePicActivity = Intent(this@ProfileActivity, UploadProfilePicActivity::class.java)
            startActivity(intentUploadProfilePicActivity)
        }

        //Setting User DP (After user has uploaded
        val uri : Uri? = currentUser.photoUrl

        //ImageViewer setImageURI(). We are using Picasso
        Picasso.with(this@ProfileActivity).load(uri).transform(CircleTransform()).into(imageView_dp)
    }

    // Function to set spinner selection based on the retrieved value from Firebase
    private fun setSpinnerSelection(spinner: Spinner, items: Array<String>, value: String?) {
        if (!value.isNullOrEmpty()) {
            val index = items.indexOf(value)
            if (index != -1) {
                spinner.setSelection(index)
            }
        }
    }

    // Handle back arrow click
    fun onBackArrowClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }

}