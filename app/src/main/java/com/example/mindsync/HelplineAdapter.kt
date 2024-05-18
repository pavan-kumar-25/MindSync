package com.example.mindsync

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HelplineAdapter(private val helplines: List<Helpline>) :
    RecyclerView.Adapter<HelplineAdapter.HelplineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelplineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_helpline, parent, false)
        return HelplineViewHolder(view)
    }

    override fun onBindViewHolder(holder: HelplineViewHolder, position: Int) {
        val helpline = helplines[position]
        holder.bind(helpline)
    }

    override fun getItemCount(): Int = helplines.size

    class HelplineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewHelpline: TextView = itemView.findViewById(R.id.textViewHelpline)
        private val imageViewCall: ImageView = itemView.findViewById(R.id.imageViewCall)

        fun bind(helpline: Helpline) {
            textViewHelpline.text = helpline.name + "\n" + helpline.number

            // Set click listener for the call icon
            imageViewCall.setOnClickListener {
                // Extract the mobile number from the Helpline object
                val phoneNumber = helpline.number
                initiateCall(itemView.context, phoneNumber)
            }
        }
        // Function to initiate a call
        private fun initiateCall(context: Context, phoneNumber: String) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            context.startActivity(intent)
        }
    }
}
