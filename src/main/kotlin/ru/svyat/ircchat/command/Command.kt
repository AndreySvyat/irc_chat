package ru.svyat.ircchat.command

fun interface Command {
    fun execute(vararg params: String): String
}