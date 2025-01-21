package com.caneryilmaz.apps.luckywheel.data

import android.graphics.Bitmap
import android.graphics.Typeface
import java.io.Serializable

/**
 * @param text is wheel item text
 * @param textColor
 * * is color of item text
 * - if [textColor] size = 1 then gradient text color disable and text color will be value of `textColor[0]`
 * - if [textColor] size > 1 then gradient text color enable
 * - if [textColor] is empty then wheel view is not drawn
 * @param backgroundColor
 * * is background color of item
 * - if [backgroundColor] size = 1 then gradient background color disable and background color will be value of `backgroundColor[0]`
 * - if [backgroundColor] size > 1 then gradient background color enable
 * - if [backgroundColor] is empty then wheel view is not drawn
 * @param textFontTypeface is custom font typeface of item text
 * @param icon
 * * is item icon [Bitmap], recommended to use PNG format icon
 * - if not null then icon will be drawn
 * - use only one of [icon] and [iconURL]
 * - if both are used, [iconURL] takes priority
 * @param iconURL
 * * is item icon URL, recommended to use PNG format icon
 * - if not null then icon will be drawn
 * - use only one of [icon] and [iconURL]
 * - if both are used, [iconURL] takes priority
 */
data class WheelData(
    val text: String,
    val textColor: IntArray,
    val backgroundColor: IntArray,
    val textFontTypeface: Typeface? = null,
    val icon: Bitmap? = null,
    val iconURL: String? = null
) : Serializable
