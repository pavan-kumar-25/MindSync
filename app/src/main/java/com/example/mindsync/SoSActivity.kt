package com.example.mindsync

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class SoSActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sos)
    }

    // Handle back arrow click
    fun onBackArrowClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }

    fun onClickInternationalHelpline(view: View){
        val helplineIntent = Intent(this, InternationalCrisisHelplineActivity::class.java)
        startActivity(helplineIntent)
    }

    fun onClickChildHelpline(view: View){

        val childIntent = Intent(this,InternationalChildHelplineActivity::class.java)

        val categoryNameToScroll = "Child Helplines"

        childIntent.putExtra("categoryName", categoryNameToScroll)
        startActivity(childIntent)
    }
}