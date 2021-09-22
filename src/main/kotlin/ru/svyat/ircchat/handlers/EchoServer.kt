package ru.svyat.ircchat.handlers

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter


class EchoServer: ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any) {
        ctx?.writeAndFlush(msg)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}