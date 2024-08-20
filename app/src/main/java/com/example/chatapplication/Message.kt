package com.example.chatapplication

data class Message(
    val roomId: String,
    val sentBy: String,
    val broadcaster: String,
    val viewer: String,
    val message: String
)
