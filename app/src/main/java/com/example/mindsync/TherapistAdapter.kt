package com.example.mindsync// com.example.mindsync.TherapistAdapter.kt
import Therapist
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TherapistAdapter(private var therapistList: List<Therapist>) :
    RecyclerView.Adapter<TherapistAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewTherapist: ImageView = itemView.findViewById(R.id.imageViewTherapist)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewDesignation: TextView = itemView.findViewById(R.id.textViewDesignation)
        val buttonBook: Button = itemView.findViewById(R.id.buttonBook)
    }

    // Function to update dataset based on filtered list
    fun updateDataset(newList: List<Therapist>) {
        therapistList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.therapist_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val therapist = therapistList[position]

//        Log.d("TherapistAdapter", "Therapist Name: ${therapist.name}, Designation: ${therapist.designation}")

        holder.imageViewTherapist.setImageResource(R.drawable.profile_dp)
        holder.textViewName.text = therapist.name
        holder.textViewDesignation.text = therapist.designation

        holder.buttonBook.setOnClickListener {
            // Handle the "Talk" button click
            val context = holder.itemView.context
            val intent = Intent(context, AppointmentActivity::class.java).apply {
                putExtra("therapistName", therapist.name)
                putExtra("therapistDesignation", therapist.designation)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return therapistList.size
    }
}
