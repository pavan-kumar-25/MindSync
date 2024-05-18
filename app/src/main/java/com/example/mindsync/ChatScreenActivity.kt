package com.example.mindsync

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ChatScreenActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_screen)

        // Retrieve counselor's name from intent extras
        val counselorName = intent.getStringExtra("counselorName")

        // Set counselor's name to the TextView
        val counselorNameTextView = findViewById<TextView>(R.id.counselorName)
        counselorNameTextView.text = counselorName

        val optionsButton = findViewById<ImageButton>(R.id.optionsButton)
        optionsButton.setOnClickListener { view ->
            val popupMenu = PopupMenu(this@ChatScreenActivity, view)
            popupMenu.menuInflater.inflate(R.menu.call_options_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.end_call_option -> {
                        // Call the function to end the call here
                        endAudioSession()
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }

        // Initialize RecyclerView and Adapter
        val recyclerView: RecyclerView = findViewById(R.id.chatRecyclerView)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        chatAdapter = ChatAdapter(chatMessages)
        recyclerView.adapter = chatAdapter
//        recyclerView.layoutManager = LinearLayoutManager(this)



        // Find views
        val messageEditText: EditText = findViewById(R.id.messageEditText)
        val sendButton: Button = findViewById(R.id.sendButton)

        // Send button click listener
        sendButton.setOnClickListener {
            val messageContent = messageEditText.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                val message = ChatMessage(messageContent, "You") // Sender name can be the user
                addMessage(message)
                messageEditText.text.clear()
            }
        }
    }

    private fun addMessage(message: ChatMessage) {
        chatMessages.add(message)
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
    }

    // Function to handle ending the audio call
    private fun endAudioSession() {
        // Build the alert dialog
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("End Call")
        alertDialogBuilder.setMessage("Are you sure you want to end the chat?")

        // Set positive button and its click listener
        alertDialogBuilder.setPositiveButton("Yes") { dialog, _ ->
            // Close the current activity
            finish()
        }

        // Set negative button and its click listener
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Dismiss the dialog if "No" is clicked
        }

        // Create and show the alert dialog
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    // Handle back arrow click
    fun onBackArrowClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }
}