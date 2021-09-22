package ru.svyat.ircchat.command

import io.netty.channel.ChannelHandlerContext
import ru.svyat.ircchat.data.findUser
import ru.svyat.ircchat.data.topics
import ru.svyat.ircchat.data.users
import ru.svyat.ircchat.logger
import ru.svyat.ircchat.model.*
import ru.svyat.ircchat.observer.TopicSubscription
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class Commander {
    private val commands: EnumMap<CommandName, Command> = EnumMap(CommandName::class.java)

    init {
        commands[CommandName.LOGIN] = Command { context, args ->
            val login = args[0]
            if (users.contains(login)) {
                throw RuntimeException("User $login already exists. Please select another login")
            }
            val password = args[1]
            users[context] = User(login, password, TopicSubscription(EMPTY_TOPIC), context)
            val log = "login user $login password ${password.replace(Regex("."), "*")}"
            logger.info(log)
            "Welcome ${args[0]}"
        }

        commands[CommandName.JOIN] = Command { context, args ->
            val topicName = args[0]
            val user = findUser(context)
            if (topics[topicName] == null) {
                topics[topicName] = TopicSubscription(Topic(topicName, CopyOnWriteArrayList(mutableListOf<Message>())))
            }
            val subscription = topics[topicName]
            subscription!!.subscribe(user)
            user.setLastSubscription(subscription)
            val msg = "You have joined to channel $topicName"
            logger.info(msg)
            msg
        }

        commands[CommandName.LEAVE] = Command { context, _ ->
            val user = findUser(context)
            val subscription = user.getLastSubscription()
            subscription.leave(user)
            val msg = "You have left ${subscription.getTopicName()}"
            logger.info(msg)
            msg
        }

        commands[CommandName.USERS] = Command { context, _ ->
            val msg = "show users"
            logger.info(msg)
            val user = findUser(context)
            user.getLastSubscription().users(user)
            "That's all"
        }
    }

    fun executeCommand(command: CommandName, channelContext: ChannelHandlerContext, vararg args: String) =
        commands[command]?.execute(channelContext, *args)
}