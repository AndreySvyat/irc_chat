package ru.svyat.ircchat

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import ru.svyat.ircchat.handlers.CommandExecutor
import ru.svyat.ircchat.handlers.MessageExecutor
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class Server(private val port: Int){
    private var channelFeature: ChannelFuture = object : ChannelFuture {
        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun isCancelled(): Boolean {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun isDone(): Boolean {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun get(): Void {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun get(timeout: Long, unit: TimeUnit): Void {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun isSuccess(): Boolean {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun isCancellable(): Boolean {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun cause(): Throwable {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun addListener(listener: GenericFutureListener<out Future<in Void>>?): ChannelFuture {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun addListeners(vararg listeners: GenericFutureListener<out Future<in Void>>?): ChannelFuture {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun removeListener(listener: GenericFutureListener<out Future<in Void>>?): ChannelFuture {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun removeListeners(vararg listeners: GenericFutureListener<out Future<in Void>>?): ChannelFuture {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun sync(): ChannelFuture {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun syncUninterruptibly(): ChannelFuture {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun await(): ChannelFuture {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun await(timeout: Long, unit: TimeUnit?): Boolean {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun await(timeoutMillis: Long): Boolean {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun awaitUninterruptibly(): ChannelFuture {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun awaitUninterruptibly(timeout: Long, unit: TimeUnit?): Boolean {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun awaitUninterruptibly(timeoutMillis: Long): Boolean {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun getNow(): Void {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun channel(): Channel {
            throw UnsupportedOperationException("Empty Feature")
        }

        override fun isVoid(): Boolean {
            throw UnsupportedOperationException("Empty Feature")
        }

    }
    private val bootstrap = ServerBootstrap()
    private val bossGroup: EventLoopGroup = NioEventLoopGroup()
    private val workerGroup: EventLoopGroup = NioEventLoopGroup()

    fun start() {
        try {
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(channel: SocketChannel) {
                        channel.pipeline()
                            .addLast(CommandExecutor(Charset.defaultCharset()))
                            .addLast(MessageExecutor(Charset.defaultCharset()))
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 5)
                .childOption(ChannelOption.SO_KEEPALIVE, false)
            channelFeature = bootstrap.bind(port).sync()
        } catch (exception: Exception){
            logger.warn(exception.message, exception)
        }
    }
    
    fun daemonStart(): Thread {
        val thread = Thread { start() }
        thread.isDaemon = true
        thread.start()
        return thread
    }

    fun terminate() {
        workerGroup.shutdownGracefully()
        bossGroup.shutdownGracefully()
        channelFeature.channel().closeFuture().sync()
    }
}