package ru.svyat.ircchat.model

import io.netty.channel.ChannelHandlerContext
import ru.svyat.ircchat.observer.TopicSubscription

data class User(
    val login: String,
    val password: String,
    var lastSubscription: TopicSubscription,
    val channelContext: ChannelHandlerContext
) {
    fun getLastNotEmptySubscription(): TopicSubscription {
        if (lastSubscription.isEmptyTopic()) throw RuntimeException("User is not subscribed to any channel!")
        else return lastSubscription
    }
}