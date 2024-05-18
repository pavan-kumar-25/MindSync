package com.example.mindsync

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AppointmentAdapter(
    private val appointments: List<Appointment>,
    private val context: Context,
    private val cancelAppointmentListener: CancelAppointmentListener
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val therapistNameTextView: TextView = itemView.findViewById(R.id.therapistNameTextView)
        val therapistDesignationTextView: TextView = itemView.findViewById(R.id.therapistDesignationTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val communicationModeTextView: TextView = itemView.findViewById(R.id.communicationModeTextView)
        val cancelAppointmentButton: Button = itemView.findViewById(R.id.cancelAppointmentButton)
        val startSessionButton: Button = itemView.findViewById(R.id.startSessionButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.appointment_item, parent, false)
        return AppointmentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val currentItem = appointments[position]

        holder.therapistNameTextView.text = currentItem.therapistName
        holder.therapistDesignationTextView.text = currentItem.therapistDesignation
        holder.dateTextView.text = currentItem.selectedDate
        holder.timeTextView.text = currentItem.selectedTime
        holder.communicationModeTextView.text = currentItem.communicationMode

        // Add click listeners for buttons
        holder.cancelAppointmentButton.setOnClickListener {
            showCancelConfirmationDialog(currentItem)
        }

        holder.startSessionButton.setOnClickListener {
            // Handle start session action
            val communicationMode = currentItem.communicationMode

            // Show a dialog to confirm starting the session based on communication mode
            val confirmationMessage = when (communicationMode) {
                "Audio" -> "Start audio session with ${currentItem.therapistName}?"
                "Video" -> "Start video session with ${currentItem.therapistName}?"
                "Chat" -> "Start chat session with ${currentItem.therapistName}?"
                else -> "Start session with ${currentItem.therapistName}?"
            }

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Start Session")
            builder.setMessage(confirmationMessage)
            builder.setPositiveButton("Yes") { _, _ ->
                // Handle start session action based on communication mode
                when (communicationMode) {
                    "Audio" -> startAudioSession(currentItem)
                    "Video" -> startVideoSession(currentItem)
                    "Chat" -> startChatSession(currentItem)
                    else -> Log.e("AppointmentAdapter", "Invalid communication mode")
                }
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun startAudioSession(appointment: Appointment) {
        val counselorName = appointment.therapistName // Assuming therapistName represents the counselor's name

        // Get a reference to your Firebase Realtime Database
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("counselors")

        // Query the database to fetch the counselor details
        ref.orderByChild("name").equalTo(counselorName).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming only one counselor is found with the given name
                    for (snapshot in dataSnapshot.children) {
                        val counselor = snapshot.getValue(Counselor::class.java)

                        // Extracting mobile number from counselor details
                        val mobileNumber = counselor?.mobileNumber

                        if (mobileNumber != null) {
                            // Open phone call intent with the fetched mobile number
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:$mobileNumber")
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "Mobile number not found for the counselor", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Counselor not found!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Error handling
                Toast.makeText(context, "Failed to start audio session: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun startVideoSession(appointment: Appointment) {
        // Implement logic to start a video session
        val meetingUrl = "https://meet.google.com/nbr-ogij-ukj"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(meetingUrl))
        context.startActivity(intent)
    }

    private fun startChatSession(appointment: Appointment) {
        // Implement logic to start a chat session
        val intent = Intent(context, ChatScreenActivity::class.java)
        intent.putExtra("counselorName", appointment.therapistName) // Pass counselor's name as an extra
        context.startActivity(intent)
    }

    override fun getItemCount() = appointments.size

    private fun showCancelConfirmationDialog(appointment: Appointment) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Cancel Appointment")
        builder.setMessage("Are you sure you want to cancel the appointment with ${appointment.therapistName}?")
        builder.setPositiveButton("Yes") { _, _ ->
            // Handle cancel appointment action
            cancelAppointmentListener.onCancelAppointment(appointment)
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    interface CancelAppointmentListener {
        fun onCancelAppointment(appointment: Appointment)
    }
}

