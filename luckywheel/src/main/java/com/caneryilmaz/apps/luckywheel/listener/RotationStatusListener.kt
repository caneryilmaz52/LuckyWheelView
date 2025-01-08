package com.caneryilmaz.apps.luckywheel.listener

import com.caneryilmaz.apps.luckywheel.constant.RotationStatus

fun interface RotationStatusListener {
    fun onRotationStatus(rotationStatus: RotationStatus)
}