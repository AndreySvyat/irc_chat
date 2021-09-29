package ru.svyat.ircchat

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import ru.svyat.ircchat.data.users
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ConnectException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors

internal open class Test {
    private val testClients = mapOf(
        Pair("login", Client("login", "123456")),
        Pair("join", Client("join", "123456")),
        Pair("leave", Client("leave", "123456")),
        Pair("users", Client("users", "121234"))
    )

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
        val client = testClients["login"]!!
        val result = client.login()
        assertTrue(result == "Welcome ${client.name}")
        client.close()
    }

    @Test
    fun testJoin() {
        val client = testClients["join"]!!
        client.login()
        val result = client.join("test_channel")
        assertTrue(result == "You have joined to channel test_channel")
        client.close()
    }

    @Test
    fun testLeave() {
        val client = testClients["leave"]!!
        client.login()
        client.join("test_channel")
        val result = client.leave()
        assertTrue(result == "You have left test_channel")
        client.close()
    }

    @Test
    fun testUsers() {
        for (client in testClients.values) {
            client.login()
            client.join("channel")
        }
        val result = testClients["users"]?.users()!!
        assertTrue(testClients.keys.toList().containsAll(result))
    }

    internal class Client(
        val name: String,
        val pwd: String
    ) {
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

        private fun sendMessage(msg: String): String {
            bufferedWriter.write(msg)
            bufferedWriter.flush()
            return bufferedReader.readLine()
        }

        fun login(): String = this.sendMessage("/login ${this.name} ${this.pwd}")
        fun join(channel: String): String = this.sendMessage("/join $channel")
        fun leave(): String = this.sendMessage("/leave")
        fun users(): List<String> {
            val users = mutableListOf<String>()
            var line = sendMessage("/users")
            while (line.isNotEmpty()){
                users.add(line)
                line = bufferedReader.readLine()
            }
            return users
        }

        fun close() {
            bufferedReader.close()
            bufferedWriter.close()
            socket.close()
        }

    }

}