package ru.svyat.ircchat.command

import io.netty.channel.ChannelHandlerContext
import ru.svyat.ircchat.data.findUser
import ru.svyat.ircchat.data.topics
import ru.svyat.ircchat.data.users
import ru.svyat.ircchat.logger
import ru.svyat.ircchat.model.*
import ru.svyat.ircchat.observer.EMPTY_SUBSCRIPTION
import ru.svyat.ircchat.observer.TopicSubscription
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class Commander {
    private val commands: EnumMap<CommandName, Command> = EnumMap(CommandName::class.java)

    init {
        commands[CommandName.LOGIN] = Command { context, args ->
            val login = args[0]
            if (users.values.any { v -> v.login == login }) {
                throw RuntimeException("User $login already exists. Please select another login")
            }
            val password = args[1]
            users[context.channel()] = User(login, password, EMPTY_SUBSCRIPTION, context)
            val log = "login user $login"
            logger.info(log)
            "Welcome ${args[0]}"
        }

        commands[CommandName.JOIN] = Command { context, args ->
            val topicName = args[0]
            val user = findUser(context.channel())
            if (topics[topicName] == null) {
                topics[topicName] = TopicSubscription(Topic(topicName, CopyOnWriteArrayList(mutableListOf<Message>())), 10)
            }
            val subscription = topics[topicName]
            subscription!!.subscribe(user)
            user.lastSubscription = subscription
            logger.info("${user.login} joined to $topicName")
            "You joined to channel $topicName"
        }

        commands[CommandName.LEAVE] = Command { context, _ ->
            val user = findUser(context.channel())
            val subscription = user.getLastNotEmptySubscription()
            subscription.leave(user)
            logger.info("${user.login} left ${subscription.getTopicName()}")
            "You have left ${subscription.getTopicName()}"
        }

        commands[CommandName.USERS] = Command { context, _ ->
            val msg = "show users"
            logger.info(msg)
            val user = findUser(context.channel())
            user.getLastNotEmptySubscription().users().joinToString("\n") + "\n"
        }
    }

    fun executeCommand(command: CommandName, channelContext: ChannelHandlerContext, vararg args: String) =
        commands[command]?.execute(channelContext, *args)
}