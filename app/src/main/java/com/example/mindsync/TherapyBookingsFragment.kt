package com.example.mindsync

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class TherapyBookingsFragment : Fragment(), AppointmentAdapter.CancelAppointmentListener{

    private lateinit var recyclerView: RecyclerView
    private lateinit var noSessionsTextView: TextView
    private lateinit var noSessionsImage : ImageView
    private lateinit var appointmentAdapter: AppointmentAdapter
    private lateinit var appointmentsRef: DatabaseReference
    private lateinit var userId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_therapy_bookings, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        noSessionsTextView = view.findViewById(R.id.noAppointmentsTextView)
        noSessionsImage = view.findViewById(R.id.noAppointmentsImage)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFirebase()
        setupRecyclerView()
    }

    private fun setupFirebase() {
        userId = Firebase.auth.currentUser?.uid ?: ""
        appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments")
            .orderByChild("userId")
            .equalTo(userId).ref
    }

    private fun setupRecyclerView() {
        val appointments = mutableListOf<Appointment>()
        appointmentAdapter = AppointmentAdapter(appointments, requireContext(), this)
        recyclerView.adapter = appointmentAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        appointmentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                appointments.clear()
                for (appointmentSnapshot in snapshot.children) {
                    val appointment = appointmentSnapshot.getValue(Appointment::class.java)
                    if (appointment != null) {
                        appointments.add(appointment)
                    }
                }
                appointmentAdapter.notifyDataSetChanged()

                // Show/hide 'No Upcoming Therapy Sessions' TextView based on data availability
                if (appointments.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    noSessionsTextView.visibility = View.VISIBLE
                    noSessionsImage.visibility = View.VISIBLE
                    Log.d("TherapyBookingsFragment", "No appointments found")
                } else {
                    recyclerView.visibility = View.VISIBLE
                    noSessionsTextView.visibility = View.GONE
                    noSessionsImage.visibility = View.GONE
                    Log.d("TherapyBookingsFragment", "Appointments found: ${appointments.size}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Log.e("TherapyBookingsFragment", "Database error: ${error.message}")
            }
        })
    }

    override fun onCancelAppointment(appointment: Appointment) {
        // Display confirmation toast
//        Toast.makeText(requireContext(), "Appointment with ${appointment.therapistName} has been cancelled", Toast.LENGTH_SHORT).show()
        // Remove the appointment from the database and update the UI
        removeAppointmentFromDatabase(appointment)
    }

//    private fun removeAppointmentFromDatabase(appointment: Appointment) {
//        // Implement logic to remove the appointment from the database
//    }

    private fun removeAppointmentFromDatabase(appointment: Appointment) {
        appointmentsRef.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (appointmentSnapshot in dataSnapshot.children) {
                        val therapistName = appointmentSnapshot.child("therapistName").getValue(String::class.java)
                        if (therapistName == appointment.therapistName) {
                            appointmentSnapshot.ref.removeValue()
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Appointment with ${appointment.therapistName} has been cancelled successfully", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("TherapyBookingsFragment", "Failed to cancel appointment: ${e.message}")
                                    Toast.makeText(requireContext(), "Something Went Wrong!Failed to cancel appointment", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("TherapyBookingsFragment", "Database error: ${databaseError.message}")
                    Toast.makeText(requireContext(), "Database error", Toast.LENGTH_SHORT).show()
                }
            })
    }

}
