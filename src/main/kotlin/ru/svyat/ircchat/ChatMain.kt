package ru.svyat.ircchat

class ChatMain

fun main(args: Array<String>){
    val port = if (args.isEmpty()) 8080 else args[0].toInt()
    startServer(port)
}