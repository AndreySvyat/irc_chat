package ru.svyat.ircchat.model

import java.util.concurrent.CopyOnWriteArrayList

data class Topic(
    val name: String,
    val history: CopyOnWriteArrayList<Message>
)
