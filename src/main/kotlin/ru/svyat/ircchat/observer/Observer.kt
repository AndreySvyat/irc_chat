package ru.svyat.ircchat.observer

import io.netty.buffer.Unpooled
import ru.svyat.ircchat.model.EMPTY_TOPIC
import ru.svyat.ircchat.model.Message
import ru.svyat.ircchat.model.Topic
import ru.svyat.ircchat.model.User
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

class TopicSubscription(private val topic: Topic) {
    private val subscribers: ConcurrentHashMap<String, Subscriber> = ConcurrentHashMap()

    fun subscribe(user: User) {
        if (subscribers.size >= 10) {
            user.channelContext.writeAndFlush("There are to many clients on channel, wait for anyone leaves or choose another topic".toByteArray())
            return
        }
        val sub = Subscriber(user)
        if (subscribers.contains(sub)) {
            user.channelContext.writeAndFlush("You are already joined to channel ${topic.name}")
            return
        }
        subscribers[user.login] = sub
        sub.onSubscribe(topic.history)
    }

    fun addMessage(message: String, user: User) {
        val msg = Message(
            Instant.now().toEpochMilli(),
            message,
            user
        )

        subscribers.forEach { (_, subscriber) -> subscriber.onMessageIncome(msg) }
        topic.history.add(msg)
    }

    fun leave(user: User) {
        subscribers.remove(user.login)
    }

    fun users(user: User) {
        subscribers.forEach {
            user.channelContext.writeAndFlush(it.key)
        }
    }

    fun isEmptyTopic() = topic == EMPTY_TOPIC

    fun getTopicName(): String = topic.name
}

class Subscriber(private val user: User) {
    fun onSubscribe(messages: List<Message>) {
        messages.forEach { message -> onMessageIncome(message) }
    }

    fun onMessageIncome(message: Message) {
        val prettyTime = Instant.ofEpochSecond(message.timeInMillis).toString()
        val userName = message.author.login
        val msg = "$prettyTime: $userName: ${message.message}"
        user.channelContext.writeAndFlush(Unpooled.wrappedBuffer(msg.toByteArray()))
    }
}