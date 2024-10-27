package com.p3kIndustries.P3kBT2PC.ui

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import com.p3kIndustries.P3kBT2PC.MainActivity
import com.p3kIndustries.P3kBT2PC.R

class MainUiReferences()
{
    lateinit var logsTextView: TextView
    lateinit var statusTextView: TextView
    lateinit var logScrollView: ScrollView
    lateinit var ipEditText: EditText
    lateinit var portEditText: EditText
    //lateinit var connectButton: Button
    lateinit var autoDetectButton: Button
    lateinit var statusDot: View

    fun initialize(mainActivity: MainActivity)
    {
        logsTextView = mainActivity.findViewById(R.id.logTextView)
        statusTextView = mainActivity.findViewById(R.id.statusTextView)
        statusDot = mainActivity.findViewById(R.id.statusDot)
        logScrollView = mainActivity.findViewById(R.id.logScrollView)
        ipEditText = mainActivity.findViewById(R.id.ipEditText)
        ipEditText.isEnabled=false
        portEditText = mainActivity.findViewById(R.id.portEditText)
        //connectButton = mainActivity.findViewById(R.id.connectButton)
        autoDetectButton = mainActivity.findViewById(R.id.autoDetectButton)
    }
}
