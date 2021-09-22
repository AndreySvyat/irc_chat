package ru.svyat.ircchat.command

enum class CommandName {
    LOGIN,
    JOIN,
    LEAVE,
    USERS
}

fun byVal(value: String): CommandName = when (value) {
    "/login" -> CommandName.LOGIN
    "/join" -> CommandName.JOIN
    "/leave" -> CommandName.LEAVE
    "/users" -> CommandName.USERS
    else -> throw Exception("Command $value not found")
}