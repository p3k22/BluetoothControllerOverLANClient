package com.p3kIndustries.P3kBT2PC.events

import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import com.p3kIndustries.P3kBT2PC.models.Input
import com.p3kIndustries.P3kBT2PC.services.Logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class Device(private val logger: Logger?) {

    private val mutex = Mutex()
    private val input = Input()

    // Thread-safe access to controller input data
    suspend fun getControllerInput(): Input
    {
        return mutex.withLock {
            val copy = input.copy(keyEvents = input.keyEvents.toMutableList())
            input.keyEvents.clear()
            copy
        }
    }

    suspend fun handleMotionEvent(event: MotionEvent?) {
        event?.let {
            val inputDevice = event.device
            val deviceName = inputDevice.name
            //logger.addLog("Input from device: $deviceName (ID: $deviceId)")
            if (event.source and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK) {
                mutex.withLock {
                    input.leftStickX = event.getAxisValue(MotionEvent.AXIS_X)
                    input.leftStickY = event.getAxisValue(MotionEvent.AXIS_Y)
                    input.rightStickX = event.getAxisValue(MotionEvent.AXIS_Z)
                    input.rightStickY = event.getAxisValue(MotionEvent.AXIS_RZ)
                    input.l2 = event.getAxisValue(MotionEvent.AXIS_LTRIGGER)
                    input.r2 = event.getAxisValue(MotionEvent.AXIS_RTRIGGER)
                    input.dpadX = event.getAxisValue(MotionEvent.AXIS_HAT_X)
                    input.dpadY = event.getAxisValue(MotionEvent.AXIS_HAT_Y)
                }
            }
        }
    }

    suspend fun handleKeyEvent(event: KeyEvent?, isKeyDown: Boolean) {
        event?.let {
            mutex.withLock {
                when (event.keyCode) {
                    KeyEvent.KEYCODE_BUTTON_A -> input.buttonA = if (isKeyDown) 1 else 0
                    KeyEvent.KEYCODE_BUTTON_B -> input.buttonB = if (isKeyDown) 1 else 0
                    KeyEvent.KEYCODE_BUTTON_X -> input.buttonX = if (isKeyDown) 1 else 0
                    KeyEvent.KEYCODE_BUTTON_Y -> input.buttonY = if (isKeyDown) 1 else 0
                    KeyEvent.KEYCODE_BUTTON_L1 -> input.l1 = if (isKeyDown) 1 else 0
                    KeyEvent.KEYCODE_BUTTON_R1 -> input.r1 = if (isKeyDown) 1 else 0
                    KeyEvent.KEYCODE_BUTTON_SELECT -> input.select = if (isKeyDown) 1 else 0
                    KeyEvent.KEYCODE_BUTTON_START -> input.start = if (isKeyDown) 1 else 0
                    KeyEvent.KEYCODE_BUTTON_THUMBL -> input.leftStickButton = if (isKeyDown) 1 else 0
                    KeyEvent.KEYCODE_BUTTON_THUMBR -> input.rightStickButton = if (isKeyDown) 1 else 0
                    // Uncomment if you want to handle D-pad button events
                    // KeyEvent.KEYCODE_DPAD_UP -> input.dpadUp = if (isKeyDown) 1 else 0
                    // KeyEvent.KEYCODE_DPAD_DOWN -> input.dpadDown = if (isKeyDown) 1 else 0
                    // KeyEvent.KEYCODE_DPAD_LEFT -> input.dpadLeft = if (isKeyDown) 1 else 0
                    // KeyEvent.KEYCODE_DPAD_RIGHT -> input.dpadRight = if (isKeyDown) 1 else 0
                    KeyEvent.KEYCODE_BUTTON_MODE -> input.psButton = if (isKeyDown) 1 else 0
                    else -> {
                        // Handle other keys if necessary
                    }
                }
            }
        }
    }

}