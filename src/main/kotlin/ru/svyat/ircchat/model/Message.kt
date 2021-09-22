package ru.svyat.ircchat.model

data class Message(
    val timeInMillis: Long,
    val message: String,
    val author: User
)