package com.caneryilmaz.apps.luckywheel.listener

import com.caneryilmaz.apps.luckywheel.constant.RotationStatus
import com.caneryilmaz.apps.luckywheel.data.WheelData

internal interface WheelViewListener {
    fun onRotationComplete(wheelData: WheelData)
    fun onRotationStatus(rotationStatus: RotationStatus)
}