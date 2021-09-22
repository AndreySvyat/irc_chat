package ru.svyat.ircchat.command

import io.netty.channel.ChannelHandlerContext

fun interface Command {
    fun execute(channelHandlerContext: ChannelHandlerContext, vararg params: String): String
}