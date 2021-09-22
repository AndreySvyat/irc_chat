package ru.svyat.ircchat.handlers

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import ru.svyat.ircchat.data.findUser
import java.nio.charset.Charset

class MessageExecutor(private val charSet: Charset) : ChannelInboundHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg !is ByteBuf) throw RuntimeException("Unsupported message type")

        val user = findUser(ctx)
        val subscription = user.getLastSubscription()
        subscription.addMessage(msg.toString(charSet).trim(), user)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.writeAndFlush("ERROR! ${cause.localizedMessage}\n")
        handleException(ctx, cause as Exception)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        removeUser(ctx)
        super.channelInactive(ctx)
    }
}