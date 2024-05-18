package com.example.mindsync

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class ChallengeAdapter(private val challenges: List<Challenge>) :
    RecyclerView.Adapter<ChallengeAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardview)
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewCardTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val challenge = challenges[position]
        holder.cardView.setCardBackgroundColor(challenge.backgroundColor)
        holder.textViewTitle.text = challenge.title
    }

    override fun getItemCount(): Int {
        return challenges.size
    }
}
