package com.p3kIndustries.P3kBT2PC.services

import android.os.Handler
import android.os.Looper
import android.view.View
import com.p3kIndustries.P3kBT2PC.ui.MainUiReferences
import java.util.concurrent.LinkedBlockingDeque

class Logger(private var ui: MainUiReferences)
{

    companion object
    {
        private const val MAX_LOG_LINES = 500
    }

    private val logLines = LinkedBlockingDeque<String>(MAX_LOG_LINES)
    private val mainHandler = Handler(Looper.getMainLooper())

    fun addLog(log: String)
    {
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            // On main thread
            updateLogs(log)
        }
        else
        {
            // Not on main thread
            mainHandler.post {
                updateLogs(log)
            }
        }
    }

    private fun updateLogs(log: String)
    {
        val scrollView=ui.logScrollView
        val textView = ui.logsTextView

        if (logLines.size >= MAX_LOG_LINES)
        {
            logLines.pollFirst()
        }
        logLines.offerLast(log)
        textView.text = logLines.joinToString("\n")

        // Scroll to the bottom of the ScrollView
        scrollView.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }
}