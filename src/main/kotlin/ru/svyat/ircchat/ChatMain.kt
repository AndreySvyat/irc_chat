package ru.svyat.ircchat

import ru.svyat.ircchat.startServer

class ChatMain

fun main(args: Array<String>){
    val port = if (args.isEmpty()) 8080 else args[0].toInt()
    startServer(port)
}