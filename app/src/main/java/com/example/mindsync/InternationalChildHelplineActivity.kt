package com.example.mindsync

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InternationalChildHelplineActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_international_crisis_helplines)

        recyclerView = findViewById(R.id.recyclerViewHelplines)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val suicideHelplines = resources.getStringArray(R.array.suicide_helplines)
        val domesticAbuseHelplines = resources.getStringArray(R.array.domestic_abuse_helplines)
        val childHelplines = resources.getStringArray(R.array.child_helplines)

        val suicideHelplineList = mutableListOf<Helpline>()
        for (i in suicideHelplines.indices step 2) {
            val name = suicideHelplines[i]
            val number = suicideHelplines[i + 1]
            suicideHelplineList.add(Helpline("Suicide Helplines", name, number))
        }

        val domesticAbuseHelplineList = mutableListOf<Helpline>()
        for (i in domesticAbuseHelplines.indices step 2) {
            val name = domesticAbuseHelplines[i]
            val number = domesticAbuseHelplines[i + 1]
            domesticAbuseHelplineList.add(Helpline("Domestic Abuse Helplines", name, number))
        }

        val childHelplineList = mutableListOf<Helpline>()
        for (i in childHelplines.indices step 2) {
            val name = childHelplines[i]
            val number = childHelplines[i + 1]
            childHelplineList.add(Helpline("Child Helplines", name, number))
        }

        val categories = listOf(
            Category(
                name = "Suicide Helplines",
                helplines = suicideHelplineList
            ),
            Category(
                name = "Domestic Abuse Helplines",
                helplines = domesticAbuseHelplineList
            ),
            Category(
                name = "Child Helplines",
                helplines = childHelplineList
            )
        )

        adapter = CategoryAdapter(categories)
        recyclerView.adapter = adapter


        // Scrolling to Child Helpline
        // Retrieve the category name from the intent
        val categoryNameToScroll = intent.getStringExtra("categoryName")

        // Find the position of the category
        val positionToScroll = adapter.getCategories().indexOfFirst { it.name == categoryNameToScroll }

        // Log statements to identify the issue
        Log.d("ScrollTest", "Category to scroll: $categoryNameToScroll")
        Log.d("ScrollTest", "Position to scroll: $positionToScroll")

        // Scroll to the position if found
        if (positionToScroll != -1) {
            recyclerView.smoothScrollToPosition(positionToScroll)
            Log.d("ScrollTest", "Scrolled to position: $positionToScroll")
        }
    }

    fun onBackArrowClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }
}
