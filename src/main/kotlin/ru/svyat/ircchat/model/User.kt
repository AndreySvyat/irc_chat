package ru.svyat.ircchat.model

import io.netty.channel.ChannelHandlerContext
import ru.svyat.ircchat.observer.TopicSubscription

data class User(
    val login: String,
    val password: String,
    private var lastSubscription: TopicSubscription,
    val channelContext: ChannelHandlerContext
) {
    fun setLastSubscription(topicSubscription: TopicSubscription){
        this.lastSubscription = topicSubscription
    }

    fun getLastSubscription(): TopicSubscription {
        if (lastSubscription.isEmptyTopic()) throw RuntimeException("Please join to any channel")
        else return lastSubscription
    }
}