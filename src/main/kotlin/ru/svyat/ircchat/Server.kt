package ru.svyat.ircchat

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import ru.svyat.ircchat.handlers.CommandExecutor
import ru.svyat.ircchat.handlers.EchoHandler
import ru.svyat.ircchat.handlers.MessageExecutor
import java.nio.charset.Charset

fun startServer(port: Int) {
    val bossGroup: EventLoopGroup = NioEventLoopGroup()
    val workerGroup: EventLoopGroup = NioEventLoopGroup()
    val bootstrap = ServerBootstrap()
    try {
        bootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .childHandler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(channel: SocketChannel) {
                    channel.pipeline()
                        .addLast(CommandExecutor(Charset.defaultCharset()))
                        .addLast(MessageExecutor(Charset.defaultCharset()))
                        .addLast(EchoHandler())
                }
            })
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, false)
        val resultInfo: ChannelFuture = bootstrap.bind(port).sync()
        resultInfo.channel().closeFuture().sync()
    } finally {
        workerGroup.shutdownGracefully()
        bossGroup.shutdownGracefully()
    }
}