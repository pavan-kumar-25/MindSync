package com.example.mindsync

data class Appointment(
    val userId: String = "",
    val therapistName: String = "",
    val therapistDesignation: String = "",
    val selectedDate: String = "",
    val selectedTime: String = "",
    val communicationMode: String = ""
)

