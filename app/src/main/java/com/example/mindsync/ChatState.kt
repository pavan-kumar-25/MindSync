package com.example.mindsync

import android.graphics.Bitmap
import com.example.mindsync.data.Chat

data class ChatState (
    val chatList: MutableList<Chat> = mutableListOf(),
    val prompt: String = "",
    val bitmap: Bitmap? = null
)