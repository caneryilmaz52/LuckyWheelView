package com.caneryilmaz.apps.luckywheel.listener

import com.caneryilmaz.apps.luckywheel.data.WheelData

fun interface TargetReachListener {
    fun onTargetReached(wheelData: WheelData)
}