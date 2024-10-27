package com.p3kIndustries.P3kBT2PC.models

import kotlinx.serialization.Serializable

@Serializable
data class Input(
    var leftStickX: Float = 0f,
    var leftStickY: Float = 0f,
    var rightStickX: Float = 0f,
    var rightStickY: Float = 0f,
    var l2: Float = 0f,
    var r2: Float = 0f,
    var dpadX: Float = 0f,
    var dpadY: Float = 0f,
    var keyEvents: MutableList<String> = mutableListOf(),

    var buttonA: Int= 0,
    var buttonB: Int= 0,
    var buttonX: Int= 0,
    var buttonY: Int= 0,
    var l1: Int= 0,
    var r1: Int= 0,
    var select: Int= 0,
    var start: Int= 0,
    var leftStickButton: Int= 0,
    var rightStickButton: Int= 0,
    var psButton: Int= 0
)
{

}