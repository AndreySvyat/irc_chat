package ru.svyat.ircchat.command

import ru.svyat.ircchat.logger
import java.util.*

class Commander {
    private val commands: EnumMap<CommandName, Command> = EnumMap(CommandName::class.java)
    init {
        commands[CommandName.LOGIN] = Command { args ->
            val msg = "login user ${args[0]} password ${args[1].replace(Regex("."), "*")}"
            logger.info(msg)
            args[0]
        }
        commands[CommandName.JOIN] = Command { args ->
            val msg = "user ${args[0]} joined to ${args[1]}"
            logger.info(msg)
            msg
        }
        commands[CommandName.LEAVE] = Command {
            val msg = "user ${it[0]} left ${it[1]}"
            logger.info(msg)
            msg
        }
        commands[CommandName.USERS] = Command {
            val msg = "show users"
            logger.info(msg)
            msg
        }
    }

    fun executeCommand(command: CommandName, vararg args: String) = commands[command]?.execute(*args)
}