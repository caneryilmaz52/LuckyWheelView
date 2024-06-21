package com.caneryilmaz.apps.luckywheel.data

import android.graphics.Bitmap
import java.io.Serializable

data class WheelData(
    val text: String,
    val textColor: Int,
    val backgroundColor: Int,
    val icon: Bitmap? = null
): Serializable
