package ru.svyat.ircchat.model

import java.util.concurrent.CopyOnWriteArrayList

data class Topic(
    val name: String,
    val history: CopyOnWriteArrayList<Message>
)
val EMPTY_TOPIC = Topic("empty", CopyOnWriteArrayList(emptyList<Message>()))