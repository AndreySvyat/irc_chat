package ru.svyat.ircchat

class ChatMain

fun main(args: Array<String>) {
    val port = if (args.isEmpty()) 8080 else args[0].toInt()
    val server = Server(port)
    server.daemonStart()
    while (System.`in`.available() <= 0) {
        continue
    }
    server.terminate()
}