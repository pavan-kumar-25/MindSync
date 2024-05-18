package com.example.mindsync

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoryTypesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_types)

        val recyclerViewChallenges: RecyclerView = findViewById(R.id.recyclerViewChallenges)

        val challengesList = listOf(
            Challenge("Anxiety", Color.CYAN),
            Challenge("Motivation", Color.GREEN),
            Challenge("Confidence", Color.RED),
            Challenge("Sleep", Color.BLACK),
            Challenge("Depression", Color.BLUE),
            Challenge("Work Stress", Color.MAGENTA),
            Challenge("Relationships", Color.BLACK),
            Challenge("Exam Stress", Color.YELLOW),
            Challenge("Educational", Color.DKGRAY),
            Challenge("Career", Color.CYAN),
            Challenge("Addiction", Color.rgb(220,212,212)),

        )

        val layoutManager = GridLayoutManager(this, 2)
        recyclerViewChallenges.layoutManager = layoutManager

        val adapter = ChallengeAdapter(challengesList)
        recyclerViewChallenges.adapter = adapter
    }

    // Handle back arrow click
    fun onBackArrowClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }

    fun onClickSkip(view: View?) {

        val skipIntent = Intent(this, UserDashboardActivity::class.java)
        startActivity(skipIntent)
    }

}