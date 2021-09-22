package ru.svyat.ircchat

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import java.util.*


fun main(args: Array<String>) {
    val host = args[0]
    val port = args[1].toInt()
    val workerGroup: EventLoopGroup = NioEventLoopGroup()
    try {
        val b = Bootstrap() // (1)
        b.group(workerGroup) // (2)
        b.channel(NioSocketChannel::class.java) // (3)
        b.option(ChannelOption.SO_KEEPALIVE, true) // (4)
        b.handler(object : ChannelInitializer<SocketChannel>() {
            override fun initChannel(ch: SocketChannel) {
                ch.pipeline().addLast(object : ChannelInboundHandlerAdapter() {
                    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
                        val m = msg as ByteBuf // (1)
                        try {
                            val currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L
                            println(Date(currentTimeMillis))
                            ctx!!.close()
                        } finally {
                            m.release()
                        }
                    }

                    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
                        cause.printStackTrace()
                        ctx.close()
                    }
                })
            }
        })

        // Start the client.
        val f: ChannelFuture = b.connect(host, port).sync() // (5)

        // Wait until the connection is closed.
        f.channel().closeFuture().sync()
    } finally {
        workerGroup.shutdownGracefully()
    }
}