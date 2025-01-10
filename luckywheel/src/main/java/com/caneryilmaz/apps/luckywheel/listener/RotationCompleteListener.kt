package com.caneryilmaz.apps.luckywheel.listener

import com.caneryilmaz.apps.luckywheel.data.WheelData

fun interface RotationCompleteListener {
    fun onRotationComplete(wheelData: WheelData)
}