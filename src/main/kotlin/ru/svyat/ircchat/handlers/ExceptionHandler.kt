package ru.svyat.ircchat.handlers

import io.netty.channel.ChannelHandlerContext
import ru.svyat.ircchat.data.users
import ru.svyat.ircchat.logger

fun handleException(ctx: ChannelHandlerContext, cause: Exception) {
    ctx.writeAndFlush("ERROR! ${cause.localizedMessage}\n")
    logger.warn(cause.localizedMessage, cause)
    removeUser(ctx)
    ctx.close()
}

fun removeUser(ctx: ChannelHandlerContext){
    val user = users.remove(ctx)
    user?.getLastSubscription()?.leave(user)
}