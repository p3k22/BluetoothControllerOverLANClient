package com.p3kIndustries.P3kBT2PC.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket

class Udp(private val logger: Logger)
{
    private var socket: DatagramSocket? = null
    private val port = 8888
    var foundIpAddress = ""

    // Closes existing socket connections
    suspend fun closeConnection()
    {
        if (socket != null)
        {
            withContext(Dispatchers.Main) {
                logger.addLog("Closing existing search for broadcast service")
            }
            socket!!.close()
            socket = null
        }
    }

    // Coroutine function to listen for UDP broadcasts
    suspend fun findService()
    {
        closeConnection()
        withContext(Dispatchers.IO) {
            try
            {
                withContext(Dispatchers.Main) { logger.addLog("Searching for broadcast service on port $port") }

                if (socket == null)
                {
                    // Create the socket only once
                    socket = DatagramSocket(port).apply {
                        broadcast = true
                    }
                    withContext(Dispatchers.Main) { logger.addLog("Opening new socket") }

                }

                val buffer = ByteArray(1024)
                val packet = DatagramPacket(buffer, buffer.size)

                while (true)
                {
                    socket?.receive(packet)
                    val receivedMessage = String(packet.data, 0, packet.length)

                    if (receivedMessage == "DISCOVER_P3KBTPASSTHROUGH_CLIENT")
                    {
                        val senderAddress = packet.address
                        foundIpAddress = senderAddress?.hostAddress!!

                        val replyMessage = "REPLY_FROM_SERVER"
                        val replyPacket = DatagramPacket(
                            replyMessage.toByteArray(),
                            replyMessage.length,
                            senderAddress,
                            port
                        )
                        socket?.send(replyPacket)

                        withContext(Dispatchers.Main) {
                            logger.addLog("Received broadcast from: $foundIpAddress and sent reply")
                        }

                        break
                    }
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
                withContext(Dispatchers.Main)
                {
                    logger.addLog("Error receiving UDP packet: ${e.message}")
                }
            }
        }
    }
}
