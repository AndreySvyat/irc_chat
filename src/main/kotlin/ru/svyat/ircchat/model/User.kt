package ru.svyat.ircchat.model

import io.netty.channel.ChannelHandlerContext

data class User(
    val login: String,
    val password: String,
    val channelContext: ChannelHandlerContext
)