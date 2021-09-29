package ru.svyat.ircchat.observer

import io.netty.buffer.Unpooled
import ru.svyat.ircchat.model.EMPTY_TOPIC
import ru.svyat.ircchat.model.Message
import ru.svyat.ircchat.model.Topic
import ru.svyat.ircchat.model.User
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

class TopicSubscription(
    private val topic: Topic,
    private val historySize: Int
    ) {
    private val subscribers: ConcurrentHashMap<String, Subscriber> = ConcurrentHashMap()

    fun subscribe(user: User) {
        if (subscribers.size >= 10) {
            throw RuntimeException("There are to many clients on channel, wait for anyone leaves or choose another topic")
        }
        val sub = Subscriber(user)
        if (subscribers.contains(sub)) {
            throw RuntimeException("You are already joined to channel ${topic.name}")
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

        subscribers.forEach { (_, subscriber) ->
            if (user != subscriber.user) {
                subscriber.onMessageIncome(msg)
            }
        }
        topic.history.add(msg)
        if (topic.history.size >= historySize){
            for (i in 0..topic.history.size - historySize)
                topic.history.removeAt(i)
        }
    }

    fun leave(user: User) {
        subscribers.remove(user.login)
    }

    fun users(): List<String> {
        return subscribers.keys().toList()
    }

    fun isEmptyTopic() = topic == EMPTY_TOPIC

    fun getTopicName(): String = topic.name
}

class Subscriber(val user: User) {
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

val EMPTY_SUBSCRIPTION = TopicSubscription(EMPTY_TOPIC, 0)