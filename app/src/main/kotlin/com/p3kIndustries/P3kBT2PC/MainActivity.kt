package com.p3kIndustries.P3kBT2PC

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.p3kIndustries.P3kBT2PC.ui.MainUiReferences
import com.p3kIndustries.P3kBT2PC.events.Device
import com.p3kIndustries.P3kBT2PC.services.Server
import com.p3kIndustries.P3kBT2PC.models.Permissions
import com.p3kIndustries.P3kBT2PC.services.Logger
import com.p3kIndustries.P3kBT2PC.services.Udp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity()
{
    // Managers
    private lateinit var device: Device
    private lateinit var server: Server
    private lateinit var permissions: Permissions

    // Services & Helpers
    private lateinit var logger: Logger
    private lateinit var udp: Udp
    private lateinit var mainUiReferences: MainUiReferences

    // Thread specific
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var findClientJob: Job? = null

    private var isAutoSearching = false


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)


        initialiseUI()
        initialiseServices()
        initializeManagers()
        initializeButtons()
    }

    private fun initialiseUI()
    {
        mainUiReferences = MainUiReferences()
        mainUiReferences.initialize(this)
    }

    private fun initialiseServices()
    {
        logger = Logger(mainUiReferences)
        udp = Udp(logger)
    }

    private fun initializeManagers()
    {
        device = Device(logger)

        server = Server(logger, mainUiReferences, device)

        permissions = Permissions(this, logger)
        permissions.checkPermissions()
    }

    private fun initializeButtons()
    {
        // Set auto connect button OnClick listener
        mainUiReferences.autoDetectButton.setOnClickListener {
            if(isAutoSearching)
            {
                findClientJob!!.cancel()

                return@setOnClickListener
            }else  if(server.isSendingData)
            {
                server.stopSendingData(coroutineScope, this)
                mainUiReferences.autoDetectButton.text= "Connect To Client"
                return@setOnClickListener
            }

            findClientJob = coroutineScope.launch {
                isAutoSearching=true
                mainUiReferences.autoDetectButton.text= "Cancel..."
                udp.findService()
            }
            coroutineScope.launch {
                while (!findClientJob!!.isCompleted)
                {
                    if(findClientJob!!.isCancelled)
                    {

                        mainUiReferences.autoDetectButton.text= "Connect To Client"
                        isAutoSearching=false
                        udp.closeConnection()
                        logger.addLog("Cancelled search")
                        return@launch
                    }

                    delay(500)
                    if(!findClientJob!!.isCompleted)
                    {
                        logger.addLog("Searching...")
                    }

                }
                mainUiReferences.autoDetectButton.text= "Disconnect"
                isAutoSearching=false
                udp.closeConnection()
                server.startSendingData(udp.foundIpAddress,coroutineScope, this@MainActivity)
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }

        }

    }

    override fun onDestroy()
    {
        super.onDestroy()
        coroutineScope.cancel()
    }

    override fun onGenericMotionEvent(event: MotionEvent?): Boolean
    {
        coroutineScope.launch {
            device.handleMotionEvent(event)
        }
        return super.onGenericMotionEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean
    {
        coroutineScope.launch {
            device.handleKeyEvent(event, isKeyDown = true)
        }
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean
    {
        coroutineScope.launch {
            device.handleKeyEvent(event, isKeyDown = false)
        }
        return true
    }

}