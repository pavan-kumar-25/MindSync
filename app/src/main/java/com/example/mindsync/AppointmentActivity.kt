package com.example.mindsync

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class AppointmentActivity : AppCompatActivity() {

    private val TAG = "AppointmentActivity"
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)

        // Get the current user's ID
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val therapistName = intent.getStringExtra("therapistName")
        val therapistDesignation = intent.getStringExtra("therapistDesignation")

        val textViewTherapistInfo : TextView = findViewById(R.id.textViewTherapistInfo)
        val spinnerDate : Spinner = findViewById(R.id.spinnerDate)
        val spinnerTime : Spinner = findViewById(R.id.spinnerTime)
        val radioGroupCommunicationMode : RadioGroup = findViewById(R.id.radioGroupCommunicationMode)
        val confirmButton: Button = findViewById(R.id.buttonConfirm)

        // Display therapist information
        textViewTherapistInfo.text = "Therapist: $therapistName\nDesignation: $therapistDesignation"

        // Populate the Date Spinner with date options
        val dateOptions = getDateOptions()
        val dateAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dateOptions)
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDate.adapter = dateAdapter

        // Populate the Time Spinner with time options
        val timeOptions = getTimeOptions()
        val timeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeOptions)
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTime.adapter = timeAdapter

        // Add a click listener to the Confirm button
        confirmButton.setOnClickListener {
            val selectedDate = spinnerDate.selectedItem.toString()
            val selectedTime = spinnerTime.selectedItem.toString()
            val selectedRadioButtonId = radioGroupCommunicationMode.checkedRadioButtonId

            if (selectedDate.isEmpty() || selectedTime.isEmpty() || selectedRadioButtonId == -1) {
                // Display an error message if date, time, or communication mode is not selected
                Toast.makeText(this@AppointmentActivity,"Please select both date, time, and communication mode.",Toast.LENGTH_LONG).show()
            }else {
                val communicationMode = findViewById<RadioButton>(selectedRadioButtonId).text.toString()
                val appointment = Appointment(
                    userId,
                    therapistName ?: "",
                    therapistDesignation ?: "",
                    selectedDate,
                    selectedTime,
                    communicationMode
                )
//                saveAppointmentToDatabase(appointment)
                // Check if the appointment with the same therapist already exists
                checkExistingAppointment(appointment)
            }
        }
    }

    private fun checkExistingAppointment(appointment: Appointment) {
        val appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments")

        appointmentsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var therapistAlreadyBooked = false

                for (appointmentSnapshot in dataSnapshot.children) {
                    val existingAppointment = appointmentSnapshot.getValue(Appointment::class.java)

                    if (existingAppointment != null && existingAppointment.therapistName == appointment.therapistName) {
                        therapistAlreadyBooked = true
                        break
                    }
                }

                if (therapistAlreadyBooked) {
                    Toast.makeText(this@AppointmentActivity, "An appointment with this therapist is already booked. Please choose another therapist.", Toast.LENGTH_SHORT).show()
                } else {
                    // If therapist is not already booked, save the new appointment
                    saveAppointmentToDatabase(appointment)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error retrieving appointments: ${databaseError.message}")
            }
        })
    }


    private fun saveAppointmentToDatabase(appointment: Appointment) {
        val appointmentRef = FirebaseDatabase.getInstance().getReference("appointments").push()

        appointmentRef.setValue(appointment)
            .addOnSuccessListener {
                showConfirmationDialog()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this@AppointmentActivity,
                    "Failed to book appointment: Something Went Wrong!",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun showConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this, R.style.RoundedAlertDialog)
        val spinnerDate : Spinner = findViewById(R.id.spinnerDate)
        val spinnerTime : Spinner = findViewById(R.id.spinnerTime)
        val therapistName = intent.getStringExtra("therapistName")
        val therapistDesignation = intent.getStringExtra("therapistDesignation")

        alertDialogBuilder.setTitle("Booking Confirmation")
        alertDialogBuilder.setMessage("Booking Confirmed!")
        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            // Navigate back to the user dashboard
            val intent = Intent(this, UserDashboardActivity::class.java)
            intent.putExtra("therapistName", therapistName)
            intent.putExtra("therapistDesignation", therapistDesignation)
            intent.putExtra("selectedDate", spinnerDate.selectedItem.toString())
            intent.putExtra("selectedTime", spinnerTime.selectedItem.toString())
            startActivity(intent)
//            finish()
        }

        // Create and show the AlertDialog
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()

        // Set the positive button text color after showing the dialog
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(resources.getColor(R.color.black))
    }

    private fun getDateOptions(): List<String> {
        // Implement your logic to generate date options
        return listOf(
            "March 15, 2024",
            "March 16, 2024",
            "March 17, 2024",
            "March 18, 2024",
            "March 19, 2024",
            "March 20, 2024",
        )
    }

    private fun getTimeOptions(): List<String> {
        // Implement your logic to generate time options
        return listOf(
            "9:00 AM - 10:00 AM",
            "10:00 AM - 11:00 AM",
            "11:00 AM - 12:00 PM",
            "12:00 PM - 01:00 PM",
            "01:00 PM - 02:00 PM",
            "02:00 PM - 03:00 PM",
            "03:00 PM - 04:00 PM",

        )
    }
}
