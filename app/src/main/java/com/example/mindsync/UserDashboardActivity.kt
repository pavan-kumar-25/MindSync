package com.example.mindsync

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class UserDashboardActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        // Check if the savedInstanceState is null to avoid replacing the fragment on orientation changes
        if (savedInstanceState == null) {
            loadHomepageFragment()
        }

        val logoutIcon: ImageView = findViewById(R.id.imageViewRightIcon)
        val imageViewNotification : ImageView = findViewById(R.id.imageViewNotification)

        // Switch between different view of dashboard
//        val viewFlipper: ViewFlipper = findViewById(R.id.viewFlipper)
        val layoutViewTherapist: LinearLayout = findViewById(R.id.layoutTherapist)
        val layoutViewHome: LinearLayout = findViewById(R.id.layoutHome)
        val layoutViewBookings: LinearLayout = findViewById(R.id.layoutMyBookings)
        val layoutViewReports: LinearLayout = findViewById(R.id.layoutReports)

        layoutViewHome.setOnClickListener {
            // Switch to the Home content when the image is clicked
//            viewFlipper.displayedChild = 0 // Home Content is the first child
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutContainer, HomepageFragment())
                .commit()
        }

        layoutViewTherapist.setOnClickListener {
            // Switch to the Therapist content when the image is clicked
//            viewFlipper.displayedChild = 1 // Therapist content is the second child
//            val intentTherapistActivity = Intent(this@UserDashboardActivity, TherapistActivity::class.java)
//            startActivity(intentTherapistActivity)
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutContainer, TherapistFragment())
                .commit()
        }

        layoutViewBookings.setOnClickListener {
            // Switch to the Bookings content when the image is clicked
//            viewFlipper.displayedChild = 2 // Bookings content is the third child
            // Switch to the Bookings content when the image is clicked
            val therapistName = intent.getStringExtra("therapistName")
            val therapistDesignation = intent.getStringExtra("therapistDesignation")
            val date = intent.getStringExtra("selectedDate")
            val time = intent.getStringExtra("selectedTime")

            val bundle = Bundle()
            bundle.putString("therapistName", therapistName)
            bundle.putString("therapistDesignation", therapistDesignation)
            bundle.putString("selectedDate", therapistDesignation)
            bundle.putString("selectedTime", therapistDesignation)

// Create an instance of TherapyBookingsFragment and set the arguments
            val therapyBookingsFragment = TherapyBookingsFragment()
            therapyBookingsFragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutContainer, TherapyBookingsFragment())
                .commit()
        }

        layoutViewReports.setOnClickListener {
//            viewFlipper.displayedChild = 3
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutContainer, TherapyReportsFragment())
                .commit()
        }

        // Navigation Drawer for user
        drawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val imageViewHamburger: ImageView = findViewById(R.id.imageViewHamburger)
        imageViewHamburger.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        imageViewNotification.setOnClickListener{
            val intentNotify = Intent(this, NotificationActivity::class.java)
            startActivity(intentNotify)
        }

        findViewById<TextView>(R.id.textViewContactFAQ).setOnClickListener{
            val intentContact = Intent(this, ContactFaqActivity::class.java)
            startActivity(intentContact)
        }

//        val scrollViewContent: ScrollView = findViewById(R.id.scrollViewContent)
//        scrollViewContent.setOnTouchListener { _, event ->
//            if (event.action == MotionEvent.ACTION_DOWN && drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                drawerLayout.closeDrawer(GravityCompat.START)
//                true
//            } else {
//                false
//            }
//        }

        //Profile Drawable Size
        val textViewProfile = findViewById<TextView>(R.id.textViewProfile)
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.user, null)
        val sizeInDp = 30
        val desiredSizeInPixels = (sizeInDp * resources.displayMetrics.density).toInt()
        // Set the desired size (width and height) for the drawable
        drawable?.setBounds(0, 0, desiredSizeInPixels, desiredSizeInPixels)
        // Set the drawable to the TextView
        textViewProfile.setCompoundDrawablesRelative(drawable, null, null, null)

        //Settings Drawable Size
        val textViewSettings = findViewById<TextView>(R.id.textViewSettings)
        val drawable2 = ResourcesCompat.getDrawable(resources, R.drawable.settings_icon, null)
        drawable2?.setBounds(0, 0, desiredSizeInPixels, desiredSizeInPixels)
        textViewSettings.setCompoundDrawablesRelative(drawable2, null, null, null)

        //Contact and FAQ Size
        val textViewContactFAQ = findViewById<TextView>(R.id.textViewContactFAQ)
        val drawable3 = ResourcesCompat.getDrawable(resources, R.drawable.faq_icon, null)
        drawable3?.setBounds(0, 0, desiredSizeInPixels, desiredSizeInPixels)
        textViewContactFAQ.setCompoundDrawablesRelative(drawable3, null, null, null)

        // Set an OnClickListener to handle the click event
        logoutIcon.setOnClickListener {
            // Call the logout function
            showLogoutConfirmationDialog()
        }


        findViewById<FloatingActionButton>(R.id.chatBot).setOnClickListener(){
//            Toast.makeText(this@UserDashboardActivity, "Entering Gemini Activity", Toast.LENGTH_LONG).show()
            val intentGeminiChatBot = Intent(this@UserDashboardActivity, GeminiChatBotActivity::class.java)
            startActivity(intentGeminiChatBot)
        }
    }


    private fun loadHomepageFragment() {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        val homepageFragment = HomepageFragment()

        // Replace the existing fragment container with the HomepageFragment
        fragmentTransaction.replace(R.id.frameLayoutContainer, homepageFragment)
        fragmentTransaction.addToBackStack(null)  // Optional: Add the transaction to the back stack

        fragmentTransaction.commit()
    }

    // Method to show a logout confirmation dialog
    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Logout") { _, _ ->
                // User clicked the Logout button
                logoutUser()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                // User clicked the Cancel button, close the dialog
                dialog.dismiss()
            }
            .create().show()
    }

    // Method to handle logout
    private fun logoutUser() {
        // Sign out the user using Firebase Auth
        FirebaseAuth.getInstance().signOut()

        // Show a popup indicating that the user has been logged out
        showLogoutPopup()

        // Redirect to the login page
        val intent = Intent(this, AccountLoginActivity::class.java)
        startActivity(intent)

        // Finish the current activity to prevent going back to the dashboard
        finish()
    }

    // Method to show a popup indicating that the user has been logged out
    private fun showLogoutPopup() {
//        val alertDialog = AlertDialog.Builder(this)
//            .setTitle("Logged Out")
//            .setMessage("You have been successfully logged out.")
//            .setPositiveButton("OK") { dialog, _ ->
//                // Close the dialog
//                dialog.dismiss()
//            }
//            .create()
//        alertDialog.show()
        Toast.makeText(this@UserDashboardActivity, "Logged Out Successfully", Toast.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun onClickLoginPage(view: View?) {
        // Handle the click event, e.g., navigate to the phone screen
        val loginPageIntent = Intent(this, AccountLoginActivity::class.java)
        startActivity(loginPageIntent)
    }

    fun onChatClick(view: View?){
        val chatPageIntent = Intent(this, ChatScreenActivity::class.java)
        startActivity(chatPageIntent)
    }

    fun onProfileClick(view: View){
        val profileIntent = Intent(this, ProfileActivity::class.java)
        startActivity(profileIntent)
    }

    fun onSettingsClick(view: View){
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        startActivity(settingsIntent)
    }

    fun onSOSClick(view: View){
        val sosIntent = Intent(this, SoSActivity::class.java)
        startActivity(sosIntent)
    }


}