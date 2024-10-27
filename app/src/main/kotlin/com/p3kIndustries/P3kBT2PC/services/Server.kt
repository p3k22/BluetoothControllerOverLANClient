package com.p3kIndustries.P3kBT2PC.services

import android.app.Activity
import android.view.WindowManager
import com.p3kIndustries.P3kBT2PC.R
import com.p3kIndustries.P3kBT2PC.events.Device
import com.p3kIndustries.P3kBT2PC.models.Input
import com.p3kIndustries.P3kBT2PC.ui.MainUiReferences
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.OutputStreamWriter
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.Locale

class Server(
    private val logger: Logger,
    private val ui: MainUiReferences,
    private val device: Device
) {

    var isSendingData = false

    private var ipAddress = ""
    private var port = 8080
    private var sendDataJob: Job? = null
    private var socket: Socket? = null
    private var writer: OutputStreamWriter? = null

    fun startSendingData(thisIPAddress: String, coroutineScope: CoroutineScope, act: Activity) {
        if (isSendingData) {
            logger.addLog("Already sending data.")
            return
        }

        logger.addLog("Starting data transmission service")

        setNetworkIP(thisIPAddress)
        isSendingData = true

        sendDataJob = coroutineScope.launch(Dispatchers.IO) {
            try {
                socket = Socket(ipAddress, port)
                writer = OutputStreamWriter(socket!!.getOutputStream(), StandardCharsets.UTF_8)

                withContext(Dispatchers.Main) {
                    ui.statusTextView.text = "Status: Connected"
                    ui.statusDot.setBackgroundResource(R.drawable.greencircle)
                }

                while (isActive && isSendingData) {
                    val input = device.getControllerInput()
                    val json = Json.encodeToString(input) + "\n"  // Add newline delimiter
                    writer?.write(json)
                    writer?.flush()

                    // Optional delay
                    delay(10)
                }
            } catch (e: Exception) {
                logger.addLog("Network error: ${e.localizedMessage}")
                e.printStackTrace()
                stopSendingData(coroutineScope, act)
            } finally {
                writer?.close()
                socket?.close()
                withContext(Dispatchers.Main) {
                    ui.statusTextView.text = "Status: Disconnected"
                    ui.statusDot.setBackgroundResource(R.drawable.redcircle)
                    act.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            }
        }
    }

    fun stopSendingData(coroutineScope: CoroutineScope,act: Activity) {
        if (!isSendingData) {
            logger.addLog("Data transmission is not active.")
            return
        }

        isSendingData = false

        sendDataJob?.cancel()
        sendDataJob = null

        coroutineScope.launch(Dispatchers.Main) {
            ui.statusTextView.text = "Status: Disconnected"
            ui.autoDetectButton.text = "Connect To Client"
            ui.statusDot.setBackgroundResource(R.drawable.redcircle)
            act.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        coroutineScope.launch {
            delay(100)
            logger.addLog("Stopped data transmission service")
        }
    }

    private fun setNetworkIP(thisIPAddress: String, thisPort: Int = 8080) {
        ipAddress = thisIPAddress
        port = thisPort

        // Update UI fields on the main thread
        CoroutineScope(Dispatchers.Main).launch {
            ui.ipEditText.setText(ipAddress)
            ui.portEditText.setText(String.format(Locale.getDefault(), "%d", port))
        }
    }
}
