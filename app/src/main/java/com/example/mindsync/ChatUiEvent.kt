package com.example.mindsync

import android.graphics.Bitmap

sealed class ChatUiEvent {
    data class UpdatePrompt(val newPrompt: String) : ChatUiEvent()
    data class SendPrompt(
        val prompt: String,
        val bitmap: Bitmap?
    ) : ChatUiEvent()

    data class UpdateBitmap(val newBitmap: Bitmap?) : ChatUiEvent()
    data object StartChat : ChatUiEvent()
}