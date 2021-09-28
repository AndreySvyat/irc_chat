package ru.svyat.ircchat.handlers

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import ru.svyat.ircchat.data.users
import ru.svyat.ircchat.logger

fun handleException(ctx: ChannelHandlerContext, cause: Exception) {
    ctx.writeAndFlush(Unpooled.wrappedBuffer("ERROR! ${cause.localizedMessage}\n".toByteArray()))
    logger.warn(cause.localizedMessage, cause)
    removeUser(ctx)
}

fun removeUser(ctx: ChannelHandlerContext){
    val user = users.remove(ctx.channel())
    user?.getLastSubscription()?.leave(user)
}