package com.example.mindsync

// CardAdapter.kt
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(private val context: Context, private val cardItems: List<CardItem>) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.background_image)
        val textView: TextView = itemView.findViewById(R.id.card_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.card_item_layout, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = cardItems[position]
        holder.imageView.setImageResource(currentItem.imageResource)
        holder.textView.text = currentItem.text
    }

    override fun getItemCount() = cardItems.size
}
