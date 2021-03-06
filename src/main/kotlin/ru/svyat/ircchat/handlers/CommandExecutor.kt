package ru.svyat.ircchat.handlers

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import ru.svyat.ircchat.command.Commander
import ru.svyat.ircchat.command.byVal
import ru.svyat.ircchat.logger
import java.nio.charset.Charset

class CommandExecutor (private val charSet: Charset) : ChannelInboundHandlerAdapter() {

    private val commander: Commander = Commander()

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg !is ByteBuf) throw RuntimeException("Unsupported message type")

        val commandMsg = msg.toString(charSet).trim()
        if (commandMsg.isEmpty()) {
            ctx.fireChannelRead(msg)
            return
        }
        if (commandMsg.first() != '/') {
            ctx.fireChannelRead(msg)
            return
        }
        logger.info("Get command $commandMsg")
        val commandEnd = commandMsg.indexOfFirst { it == ' ' }
        val command = commandMsg.substring(0, if (commandEnd < 0) commandMsg.length else commandEnd)
        var args = emptyArray<String>()
        if (commandEnd > 0) {
            args = commandMsg.substring(commandMsg.indexOfFirst { it == ' ' } + 1).split(' ').toTypedArray()
        }
        val outMsg =
            Unpooled.wrappedBuffer(("${commander.executeCommand(byVal(command), ctx, *args)}\n").toByteArray(charSet))
        ctx.writeAndFlush(outMsg)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        handleException(ctx, cause as Exception)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        removeUser(ctx)
    }
}