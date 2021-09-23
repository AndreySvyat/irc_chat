package ru.svyat.ircchat.data

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import ru.svyat.ircchat.model.User
import ru.svyat.ircchat.observer.TopicSubscription
import java.util.concurrent.ConcurrentHashMap

val topics: ConcurrentHashMap<String, TopicSubscription> = ConcurrentHashMap()

val users: ConcurrentHashMap<Channel, User> = ConcurrentHashMap()

fun findUser(channel: Channel) = users[channel] ?: throw RuntimeException("Please login to chat")
