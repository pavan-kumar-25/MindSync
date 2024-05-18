package com.example.mindsync

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class HomepageFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: CardAdapter
    private lateinit var mCardItems: MutableList<CardItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_homepage, container, false)

        // Find the RecyclerView in the fragment's layout
        mRecyclerView = view.findViewById(R.id.recycler_view)
        // Disable scrolling for the RecyclerView
        mRecyclerView.isNestedScrollingEnabled = false

        // Set up RecyclerView layout manager
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Create and set up adapter
        mCardItems = mutableListOf()
        mCardItems.add(CardItem(R.drawable.home_backg1, "rediscover your strengths"))
        mCardItems.add(CardItem(R.drawable.home_backg2, "set a healthy sleep habit"))
        mAdapter = CardAdapter(requireContext(), mCardItems)
        mRecyclerView.adapter = mAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userName = currentUser?.displayName

        // Set the text dynamically in your fragment
        val greetingTextView: TextView = view.findViewById(R.id.greetingTextView)
        greetingTextView.text = "Hi $userName!"

        // Get the LinearLayout reference
        val imageContainer = view.findViewById<LinearLayout>(R.id.imageContainer)

        // Create a list of drawable resource IDs and corresponding text (replace with your own drawables and texts)
        val imageInfoList = listOf(
            Pair(R.drawable.home_assess, "Assess\nYourself"),
            Pair(R.drawable.home_health, "Manage\nHealth"),
            Pair(R.drawable.home_happiness, "Happiness"),
            Pair(R.drawable.home_stress, "Stress"),
            Pair(R.drawable.home_anxiety, "Manage\nAnxiety"),
            Pair(R.drawable.home_sleep, "Sleep"),
            Pair(R.drawable.home_trauma, "Trauma"),
            Pair(R.drawable.home_relationship, "Relationships"),
        )

        // Loop through the list and add ImageViews with TextViews dynamically
        for ((drawableId, text) in imageInfoList) {
            // Create the ImageView
            val imageView = ImageView(requireContext())
            imageView.setImageResource(drawableId)

            // Set layout parameters for the ImageView
            val layoutParams = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.image_width),
                resources.getDimensionPixelSize(R.dimen.image_height)
            )
            layoutParams.marginEnd = resources.getDimensionPixelSize(R.dimen.image_margin)
            imageView.layoutParams = layoutParams

            // Create the TextView
            val textView = TextView(requireContext())
            textView.text = text
            textView.gravity = Gravity.CENTER
            textView.setPadding(0, 0, 0, 0)
            textView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            // Add the ImageView and TextView to a vertical LinearLayout
            val verticalLayout = LinearLayout(requireContext())
            verticalLayout.orientation = LinearLayout.VERTICAL
            verticalLayout.addView(imageView)
            verticalLayout.addView(textView)

            // Add the vertical LinearLayout to the main imageContainer
            imageContainer.addView(verticalLayout)
        }
    }
}

