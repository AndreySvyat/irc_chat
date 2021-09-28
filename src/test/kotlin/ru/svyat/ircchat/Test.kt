package ru.svyat.ircchat

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ConnectException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.atomic.AtomicInteger

internal open class Test {

    companion object {
        @BeforeAll
        @JvmStatic
        fun init() {
            val thread = Thread { Server(6666).start() }
            thread.isDaemon = true
            thread.start()
        }
    }

    @Test
    fun testLogin() {
        val client = Client()
        client.write("/login test 123456")
        val result = client.readLine()
        assertTrue(result == "Welcome test")
        client.close()
    }

    internal class Client {
        private val bufferedWriter: BufferedWriter
        private val bufferedReader: BufferedReader
        private var socket: Socket = Socket()

        init {
            val inetAddress = InetAddress.getByName("localhost")
            val socketAddress = InetSocketAddress(inetAddress, 6666)
            var connected = false
            val counter = AtomicInteger(0)
            while (!connected && counter.get() < 10) {
                try {
                    socket = Socket()
                    val timeoutInMs = 10 * 1000
                    socket.connect(socketAddress, timeoutInMs)
                    connected = true
                } catch (ex: ConnectException) {
                    val waitingTime = counter.incrementAndGet() * 1000L
                    println("Try to reconnect in ${waitingTime / 1000} seconds")
                    Thread.sleep(waitingTime)
                }
            }
            if (socket.isConnected) {
                bufferedWriter = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
            } else {
                throw RuntimeException("Socket is not connected")
            }

        }

        fun write(msg: String) {
            bufferedWriter.write(msg)
            bufferedWriter.flush()
        }

        fun readLine(): String {
            return bufferedReader.readLine()
        }

        fun close() {
            bufferedReader.close()
            bufferedWriter.close()
            socket.close()
        }

    }

}