package ru.svyat.ircchat.data

import ru.svyat.ircchat.model.Topic
import ru.svyat.ircchat.model.User
import java.util.concurrent.ConcurrentHashMap

val topics: ConcurrentHashMap<String, Topic> = ConcurrentHashMap()

val users: ConcurrentHashMap<String, User> = ConcurrentHashMap()