package com.example.mindsync

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(private val categories: List<Category>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_content, parent, false)
        return CategoryViewHolder(view)
    }


    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    fun getCategories(): List<Category> {
        return categories
    }

    override fun getItemCount(): Int = categories.size

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewCategory: TextView = itemView.findViewById(R.id.textViewCategory)
        private val recyclerViewHelplines: RecyclerView = itemView.findViewById(R.id.recyclerViewHelplines)

        fun bind(category: Category) {
            textViewCategory.text = category.name

            val helplineAdapter = HelplineAdapter(category.helplines)
            recyclerViewHelplines.layoutManager = LinearLayoutManager(itemView.context)
            recyclerViewHelplines.adapter = helplineAdapter
        }
    }
}

