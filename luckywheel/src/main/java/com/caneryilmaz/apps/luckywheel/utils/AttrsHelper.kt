package com.caneryilmaz.apps.luckywheel.utils

import android.content.res.TypedArray

class AttrsHelper(typedArray: TypedArray) {

    private val typedArray: TypedArray

    init {
        this.typedArray = typedArray
    }

    fun clear() {
        typedArray.recycle()
    }
}