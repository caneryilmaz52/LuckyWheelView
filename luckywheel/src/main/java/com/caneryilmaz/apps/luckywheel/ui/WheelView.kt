package com.caneryilmaz.apps.luckywheel.ui

import android.animation.Animator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.annotation.FloatRange
import com.caneryilmaz.apps.luckywheel.R
import com.caneryilmaz.apps.luckywheel.constant.RotationDirection
import com.caneryilmaz.apps.luckywheel.constant.RotationSpeed
import com.caneryilmaz.apps.luckywheel.constant.RotationStatus
import com.caneryilmaz.apps.luckywheel.constant.TextOrientation
import com.caneryilmaz.apps.luckywheel.data.WheelData
import com.caneryilmaz.apps.luckywheel.listener.WheelViewListener
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random
import androidx.core.graphics.withSave
import androidx.core.graphics.withTranslation


internal class WheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var wheelItemBackgroundPaint: Paint
    private lateinit var wheelStrokePaint: Paint
    private lateinit var wheelItemSeparatorPaint: Paint
    private lateinit var wheelItemTextPaint: Paint

    private lateinit var wheelSize: RectF

    private var wheelRadius: Float = 0F
    private var wheelStrokeRadius: Float = 0F

    private var centerOfWheel: Float = 0F

    private var wheelStrokeColor: IntArray = intArrayOf(Color.BLACK)
    private var drawWheelStroke: Boolean = false
    private var wheelStrokeThickness: Float = 4F

    private var wheelItemSeparatorColor: IntArray = intArrayOf(Color.BLACK)
    private var drawItemSeparator: Boolean = false
    private var itemSeparatorThickness: Float = 2F

    private var drawCenterPoint: Boolean = false
    private var centerPointColor: Int = Color.WHITE
    private var centerPointRadius: Float = 40F

    private var wheelData: ArrayList<WheelData>

    private var stopCenterOfItem: Boolean = false

    private var rotationDirection: RotationDirection = RotationDirection.CLOCKWISE

    private var rotateTime: Long = 5000
    private var rotateSpeed: RotationSpeed = RotationSpeed.NORMAL
    private var rotateSpeedMultiplier: Float = 1F

    private var textOrientation: TextOrientation = TextOrientation.HORIZONTAL
    private var textPadding: Float = resources.getDimensionPixelSize(R.dimen.dp4).toFloat()
    private var itemTextSize: Float = resources.getDimensionPixelSize(R.dimen.sp16).toFloat()
    private var itemTextLetterSpacing: Float = 0.1F
    private var itemTextFont: Typeface = Typeface.SANS_SERIF
    private var textPositionFraction: Float = 0.7F

    private var iconSizeMultiplier: Float = 1.0F
    private var iconPositionFraction: Float = 0.5F

    private var drawCornerPoints: Boolean = false
    private var cornerPointsEachSlice: Int = 1
    private var cornerPointsColor: IntArray = intArrayOf()
    private var useRandomCornerPointsColor: Boolean = true
    private var useCornerPointsGlowEffect: Boolean = true
    private var cornerPointsColorChangeSpeedMs: Int = 500
    private var cornerPointsRadius: Float = 10F

    private var wheelCornerPointColors: IntArray = intArrayOf(Color.WHITE)

    private var wheelViewListener: WheelViewListener? = null

    init {
        setupPaints()
        wheelData = ArrayList()

        val pointsOnCircle = wheelData.size + (wheelData.size * cornerPointsEachSlice)
        wheelCornerPointColors = IntArray(pointsOnCircle) { Color.WHITE }
        postDelayed(object : Runnable {
            override fun run() {
                if (useRandomCornerPointsColor || cornerPointsColor.isEmpty()) {
                    wheelCornerPointColors.indices.forEach { i ->
                        wheelCornerPointColors[i] = Color.valueOf(
                            (0..255).random() / 255f,
                            (0..255).random() / 255f,
                            (0..255).random() / 255f
                        ).toArgb()
                    }
                } else {
                    wheelCornerPointColors.indices.forEach { i ->
                        wheelCornerPointColors[i] = cornerPointsColor[cornerPointsColor.indices.random()]
                    }
                }
                invalidate()
                postDelayed(this, cornerPointsColorChangeSpeedMs.toLong())
            }
        }, cornerPointsColorChangeSpeedMs.toLong())
    }

    private fun setupPaints() {
        wheelSize = RectF(
            0f,
            0f,
            wheelRadius,
            wheelRadius
        )

        wheelItemBackgroundPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }

        wheelStrokePaint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }

        wheelItemSeparatorPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }

        wheelItemTextPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            letterSpacing = itemTextLetterSpacing
            textSize = itemTextSize
            typeface = itemTextFont
            textAlign = Paint.Align.CENTER
        }
    }

    /**
     * @param wheelData is an ArrayList of [WheelData], check info for [WheelData] description
     */
    fun setWheelData(wheelData: ArrayList<WheelData>) {
        this.wheelData = wheelData
        invalidate()
    }

    /**
     * @param rotationDirection is wheel rotate direction [RotationDirection.CLOCKWISE], [RotationDirection.COUNTER_CLOCKWISE], default value [RotationDirection.CLOCKWISE]
     */
    fun setRotateDirection(rotationDirection: RotationDirection) {
        this.rotationDirection = rotationDirection
    }

    /**
     * @param stopCenterOfItem
     * * default value `false`
     * * if `true` the arrow points to the center of the slice
     * - if `false` the arrow points to a random point on the slice.
     */
    fun stopCenterOfItem(stopCenterOfItem: Boolean) {
        this.stopCenterOfItem = stopCenterOfItem
    }


    fun setRotateTime(rotateTime: Long, rotateSpeed: RotationSpeed, rotateSpeedMultiplier: Float) {
        setRotateTime(rotateTime = rotateTime)
        setRotateSpeed(rotateSpeed = rotateSpeed)
        setRotateSpeedMultiplier(rotateSpeedMultiplier = rotateSpeedMultiplier)
    }

    /**
     * @param rotateTime is wheel rotate duration, default value `5000ms`
     */
    fun setRotateTime(rotateTime: Long) {
        this.rotateTime = rotateTime
    }

    /**
     * @param rotateSpeed is wheel rotate speed [RotationSpeed.FAST], [RotationSpeed.NORMAL] or [RotationSpeed.SLOW], default value [RotationSpeed.NORMAL]
     */
    fun setRotateSpeed(rotateSpeed: RotationSpeed) {
        this.rotateSpeed = rotateSpeed
    }

    /**
     * @param rotateSpeedMultiplier is wheel rotate speed multiplier, default value `1F`
     */
    fun setRotateSpeedMultiplier(rotateSpeedMultiplier: Float) {
        this.rotateSpeedMultiplier = rotateSpeedMultiplier
    }


    fun drawWheelStroke(drawWheelStroke: Boolean, wheelStrokeColor: IntArray, wheelStrokeThickness: Float) {
        drawWheelStroke(drawWheelStroke = drawWheelStroke)
        setWheelStrokeColor(wheelStrokeColor = wheelStrokeColor)
        setWheelStrokeThickness(wheelStrokeThickness = wheelStrokeThickness)
    }

    /**
     * @param drawWheelStroke is enable or disable wheel corner stroke drawing, default value `false`
     */
    fun drawWheelStroke(drawWheelStroke: Boolean) {
        this.drawWheelStroke = drawWheelStroke
    }

    /**
     * @param wheelStrokeColor
     * * * is color of stroke line
     *  * - if [wheelStrokeColor] size = 1 then gradient stroke color disable and stroke color will be value of `wheelStrokeColor[0]`
     *  * - if [wheelStrokeColor] size > 1 then gradient stroke color enable
     *  * - if [wheelStrokeColor] is empty then gradient stroke color disable and stroke color will be [Color.BLACK]

     */
    fun setWheelStrokeColor(wheelStrokeColor: IntArray) {
        this.wheelStrokeColor = wheelStrokeColor
    }

    /**
     * @param wheelStrokeThickness is thickness of item stroke circle, default value `4dp`
     */
    fun setWheelStrokeThickness(wheelStrokeThickness: Float) {
        this.wheelStrokeThickness = wheelStrokeThickness
    }


    fun drawItemSeparator(drawItemSeparator: Boolean, wheelItemSeparatorColor: IntArray, itemSeparatorThickness: Float) {
        drawItemSeparator(drawItemSeparator = drawItemSeparator)
        setWheelItemSeparatorColor(wheelItemSeparatorColor = wheelItemSeparatorColor)
        setItemSeparatorThickness(itemSeparatorThickness = itemSeparatorThickness)
    }

    /**
     * @param drawItemSeparator is enable or disable wheel item separator drawing, default value `false`
     */
    fun drawItemSeparator(drawItemSeparator: Boolean) {
        this.drawItemSeparator = drawItemSeparator
    }

    /**
     * @param wheelItemSeparatorColor
     * * is color of item separator line
     * - if [wheelItemSeparatorColor] size = 1 then gradient separator color disable and separator color will be value of `wheelItemSeparatorColor[0]`
     * - if [wheelItemSeparatorColor] size > 1 then gradient separator color enable
     * - if [wheelItemSeparatorColor] is empty then gradient separator color disable and separator color will be [Color.BLACK]
     */
    fun setWheelItemSeparatorColor(wheelItemSeparatorColor: IntArray) {
        this.wheelItemSeparatorColor = wheelItemSeparatorColor
    }

    /**
     * @param itemSeparatorThickness is thickness of item separator line, default value `2dp`
     */
    fun setItemSeparatorThickness(itemSeparatorThickness: Float) {
        this.itemSeparatorThickness = itemSeparatorThickness
    }


    fun drawCenterPoint(drawCenterPoint: Boolean, centerPointColor: Int, centerPointRadius: Float) {
        drawCenterPoint(drawCenterPoint = drawCenterPoint)
        setCenterPointColor(centerPointColor = centerPointColor)
        setCenterPointRadius(centerPointRadius = centerPointRadius)
    }

    /**
     * @param drawCenterPoint is enable or disable center point drawing, default value `false`
     */
    fun drawCenterPoint(drawCenterPoint: Boolean) {
        this.drawCenterPoint = drawCenterPoint
    }

    /**
     * @param centerPointColor is color of center point, default value [Color.WHITE]
     */
    fun setCenterPointColor(centerPointColor: Int) {
        this.centerPointColor = centerPointColor
    }

    /**
     * @param centerPointRadius is radius of center point,  default value `20dp`
     */
    fun setCenterPointRadius(centerPointRadius: Float) {
        this.centerPointRadius = centerPointRadius
    }


    fun drawCornerPoints(drawCornerPoints: Boolean, cornerPointsEachSlice: Int, useRandomCornerPointsColor: Boolean, useCornerPointsGlowEffect: Boolean, cornerPointsColorChangeSpeedMs: Int, cornerPointsColor: IntArray, cornerPointsRadius: Float) {
        drawCornerPoints(drawCornerPoints)
        setCornerPointsEachSlice(cornerPointsEachSlice)
        setUseRandomCornerPointsColor(useRandomCornerPointsColor)
        setUseCornerPointsGlowEffect(useCornerPointsGlowEffect)
        setCornerPointsColorChangeSpeedMs(cornerPointsColorChangeSpeedMs)
        setCornerPointsColor(cornerPointsColor)
        setCornerPointsRadius(cornerPointsRadius)
    }

    /**
     * @param drawCornerPoints is enable or disable corner points drawing, default value `false`
     */
    fun drawCornerPoints(drawCornerPoints: Boolean) {
        this.drawCornerPoints = drawCornerPoints
    }

    /**
     * @param cornerPointsEachSlice is count of point in a slice,  default value `1`
     */
    fun setCornerPointsEachSlice(cornerPointsEachSlice: Int) {
        this.cornerPointsEachSlice = cornerPointsEachSlice
    }

    /**
     * @param useRandomCornerPointsColor is enable or disable random corner points colors,  default value `true`
     */
    fun setUseRandomCornerPointsColor(useRandomCornerPointsColor: Boolean) {
        this.useRandomCornerPointsColor = useRandomCornerPointsColor
    }

    /**
     * @param useCornerPointsGlowEffect is enable or disable corner points glow effect, default value `true`
     */
    fun setUseCornerPointsGlowEffect(useCornerPointsGlowEffect: Boolean) {
        this.useCornerPointsGlowEffect = useCornerPointsGlowEffect
    }

    /**
     * @param cornerPointsColorChangeSpeedMs is corner points color change duration, default value `500ms`
     */
    fun setCornerPointsColorChangeSpeedMs(cornerPointsColorChangeSpeedMs: Int) {
        this.cornerPointsColorChangeSpeedMs = cornerPointsColorChangeSpeedMs
    }

    /**
     * @param cornerPointsColor
     * * is colors of corner points
     * - if [cornerPointsColor] is empty and [setUseRandomCornerPointsColor] is `false` then corner colors will be randomly
     * - if [cornerPointsColor] is not empty and [setUseRandomCornerPointsColor] is `true` then corner colors will be randomly
     */
    fun setCornerPointsColor(cornerPointsColor: IntArray) {
        this.cornerPointsColor = cornerPointsColor
    }

    /**
     * @param cornerPointsRadius is radius of corner point, default value `4dp`
     */
    fun setCornerPointsRadius(cornerPointsRadius: Float) {
        this.cornerPointsRadius = cornerPointsRadius
    }


    fun setWheelItemText(textOrientation: TextOrientation, textPadding: Float, textSize: Float, letterSpacing: Float, typeface: Typeface) {
        setTextOrientation(textOrientation = textOrientation)
        setTextPadding(textPadding = textPadding)
        setTextSize(textSize = textSize)
        setTextLetterSpacing(letterSpacing = letterSpacing)
        setTextFont(typeface = typeface)
    }

    /**
     * @param textOrientation is text orientation of wheel items [TextOrientation.HORIZONTAL], [TextOrientation.VERTICAL], [TextOrientation.VERTICAL_TO_CENTER] or [TextOrientation.VERTICAL_TO_CORNER] default value [TextOrientation.HORIZONTAL]
     */
    fun setTextOrientation(textOrientation: TextOrientation) {
        this.textOrientation = textOrientation
    }

    /**
     * @param textPadding is text padding from wheel corner, default value `4dp`
     */
    fun setTextPadding(textPadding: Float) {
        this.textPadding = textPadding
    }

    /**
     * @param textSize is text size of wheel items, default value `16sp`
     */
    fun setTextSize(textSize: Float) {
        itemTextSize = textSize
    }

    /**
     * @param letterSpacing
     * * is letter spacing of wheel items text
     * - letterSpacing must be in range `0.0F` - `1.0F`
     * - letterSpacing is not in range then letter spacing be `0.1F`
     * - default value `0.1F`
     */
    fun setTextLetterSpacing(letterSpacing: Float) {
        itemTextLetterSpacing = letterSpacing
    }

    /**
     * @param typeface is custom font typeface of wheel items text
     */
    fun setTextFont(typeface: Typeface) {
        itemTextFont = typeface
    }

    /**
     * @param textPositionFraction
     * * is text vertical position fraction in wheel slice only effect when [TextOrientation] is [TextOrientation.VERTICAL_TO_CENTER] or [TextOrientation.VERTICAL_TO_CORNER]
     * - The smaller the value, the closer to the center
     * - The larger the value, the closer to the corners
     * - default value `0.7F`
     */
    fun setTextPositionFraction(@FloatRange(from = 0.1, to = 0.9) textPositionFraction: Float) {
        this.textPositionFraction = textPositionFraction
    }

    /**
     * @param sizeMultiplier is item icon size multiplier value, default value `1.0F` and default icon size `36dp`
     */
    fun setIconSizeMultiplier(sizeMultiplier: Float) {
        iconSizeMultiplier = sizeMultiplier
    }

    /**
     * @param iconPositionFraction
     * * is icon vertical position fraction in wheel slice
     * - The smaller the value, the closer to the center
     * - The larger the value, the closer to the corners
     * - default value `0.5F`
     */
    fun setIconPositionFraction(@FloatRange(from = 0.1, to = 0.9) iconPositionFraction: Float) {
        this.iconPositionFraction = iconPositionFraction
    }

    /**
     * this function set rotation listener to wheel view
     * also if this function don't call then wheel view is not notify user
     */
    fun setWheelViewListener(wheelViewListener: WheelViewListener) {
        this.wheelViewListener = wheelViewListener
    }

    /**
     * this function rotate wheel to given target
     */
    fun rotateWheelToTarget(target: Int) {

        if (target < 0) {
            throw IllegalArgumentException("WheelView target must be bigger than 0 (zero). Provided target: $target")
        } else if (target > wheelData.size) {
            throw IndexOutOfBoundsException("WheelView target must be between 0 and wheelItems last index ${wheelData.size - 1} (exclusive). Provided target: $target")
        } else {
            val animatorListener = object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    wheelViewListener?.onRotationStatus(RotationStatus.ROTATING)
                }

                override fun onAnimationEnd(animation: Animator) {
                    wheelViewListener?.onRotationStatus(RotationStatus.COMPLETED)

                    wheelViewListener?.onRotationComplete(wheelData[target])

                    animation.removeAllListeners()
                    clearAnimation()
                }

                override fun onAnimationCancel(animation: Animator) {
                    wheelViewListener?.onRotationStatus(RotationStatus.CANCELED)
                }

                override fun onAnimationRepeat(animation: Animator) { // no need
                }
            }

            animate().apply {
                interpolator = DecelerateInterpolator()
                duration = rotateTime
                rotation(getRotationValueOfTarget(target = target))
                setListener(animatorListener)
                start()
            }
        }
    }

    /**
     * this function rotate wheel to given random target
     */
    fun rotateWheelRandomTarget() {
        val randomTarget: Int = Random.nextInt(0, wheelData.size)

        val animatorListener = object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                wheelViewListener?.onRotationStatus(RotationStatus.ROTATING)
            }

            override fun onAnimationEnd(animation: Animator) {
                wheelViewListener?.onRotationStatus(RotationStatus.COMPLETED)

                wheelViewListener?.onRotationComplete(wheelData[randomTarget])

                animation.removeAllListeners()
                clearAnimation()
            }

            override fun onAnimationCancel(animation: Animator) {
                wheelViewListener?.onRotationStatus(RotationStatus.CANCELED)
            }

            override fun onAnimationRepeat(animation: Animator) { // no need
            }
        }

        animate().apply {
            interpolator = DecelerateInterpolator()
            duration = rotateTime
            rotation(getRotationValueOfTarget(target = randomTarget))
            setListener(animatorListener)
            start()
        }
    }

    fun resetWheel() {
        val animatorListener = object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) { // no need
            }

            override fun onAnimationEnd(animation: Animator) {
                wheelViewListener?.onRotationStatus(RotationStatus.IDLE)
                animation.removeAllListeners()
                clearAnimation()
            }

            override fun onAnimationCancel(animation: Animator) { // no need
            }

            override fun onAnimationRepeat(animation: Animator) { // no need
            }
        }
        animate().apply {
            duration = 0
            rotation(0F)
            setListener(animatorListener)
            start()
        }
    }

    /**
     * this function provide wheel rotate value
     */
    private fun getRotationValueOfTarget(target: Int): Float {
        val sweepAngle: Float = (360 / wheelData.size.toFloat())
        val halfOfWheelItem: Float = sweepAngle / 2
        val targetItemAngle: Float = sweepAngle * (target + 1)

        val rotationSpeed = when (rotateSpeed) {
            RotationSpeed.FAST -> 15 * rotateSpeedMultiplier
            RotationSpeed.NORMAL -> 10 * rotateSpeedMultiplier
            RotationSpeed.SLOW -> 5 * rotateSpeedMultiplier
        }

        return when (rotationDirection) {
            RotationDirection.CLOCKWISE -> {
                val rotationAngleOfTarget = if (stopCenterOfItem) {
                    270 - targetItemAngle + halfOfWheelItem
                } else {
                    val maxRange: Int = sweepAngle.toInt() - 1
                    val stopPosition = Random.nextInt(1, maxRange)
                    270 - targetItemAngle + stopPosition
                }

                (360 * rotationSpeed) + rotationAngleOfTarget
            }
            RotationDirection.COUNTER_CLOCKWISE -> {
                val rotationAngleOfTarget = if (stopCenterOfItem) {
                    -270 + targetItemAngle - halfOfWheelItem
                } else {
                    val maxRange: Int = sweepAngle.toInt() - 1
                    val stopPosition = Random.nextInt(1, maxRange)
                    -270 + targetItemAngle - stopPosition
                }

                -((360 * rotationSpeed) + rotationAngleOfTarget)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val minDimension = min(measuredWidth, measuredHeight)

        var finalWheelStrokeThickness = 0f
        if (drawWheelStroke) {
            finalWheelStrokeThickness = wheelStrokeThickness

            wheelStrokeRadius = minDimension / 2F
        }

        wheelRadius = minDimension / 2F - finalWheelStrokeThickness

        centerOfWheel = minDimension / 2F

        setMeasuredDimension(minDimension, minDimension)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        setupPaints()

        drawWheelStroke(canvas)

        drawWheelBackground(canvas = canvas)

        if (wheelData.isNotEmpty()) {
            drawWheelItems(canvas = canvas)
            drawItemSeparator(canvas = canvas)
        }

        drawCornerPoints(canvas = canvas)

        drawCenterPoint(canvas = canvas)
    }

    /**
     * this function is draw a wheel items with given wheelData list
     * if wheelData list is empty then wheel items are not drawn
     */
    private fun drawWheelItems(canvas: Canvas) {
        var startAngle = 0F
        val sweepAngle: Float = 360F / wheelData.size

        wheelData.forEach { item ->
            val wheelBackgroundShader = if (item.backgroundColor.size == 1) {
                RadialGradient(
                    centerOfWheel,
                    centerOfWheel,
                    wheelRadius,
                    intArrayOf(item.backgroundColor[0], item.backgroundColor[0]),
                    null,
                    Shader.TileMode.CLAMP
                )
            } else if (item.backgroundColor.isEmpty()) {
                throw IllegalArgumentException("At least one color value is required: backgroundColor array is empty.")
            } else {
                RadialGradient(
                    centerOfWheel,
                    centerOfWheel,
                    wheelRadius,
                    item.backgroundColor,
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            wheelItemBackgroundPaint.shader = wheelBackgroundShader

            val itemArc = RectF(
                centerOfWheel - wheelRadius,
                centerOfWheel - wheelRadius,
                centerOfWheel + wheelRadius,
                centerOfWheel + wheelRadius,
            )

            canvas.drawArc(itemArc, startAngle, sweepAngle, true, wheelItemBackgroundPaint)

            val itemTextTypeface = item.textFontTypeface ?: itemTextFont

            val adjustedRadius = (wheelRadius - wheelItemTextPaint.textSize - textPadding)

            val textRectF = RectF(
                centerOfWheel - adjustedRadius,
                centerOfWheel - adjustedRadius,
                centerOfWheel + adjustedRadius,
                centerOfWheel + adjustedRadius,
            )

            val path = Path().apply {
                addArc(textRectF, startAngle, sweepAngle)
            }

            val bounds = RectF()
            path.computeBounds(bounds, true)

            val textPaintShader = if (item.textColor.size == 1) {
                LinearGradient(
                    bounds.left,
                    bounds.left,
                    bounds.right,
                    bounds.right,
                    intArrayOf(item.textColor[0], item.textColor[0]),
                    null,
                    Shader.TileMode.CLAMP
                )
            } else if (item.textColor.isEmpty()) {
                throw IllegalArgumentException("At least one color value is required: textColor list is empty.")
            } else {
                LinearGradient(
                    bounds.left,
                    bounds.left,
                    bounds.right,
                    bounds.right,
                    item.textColor,
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            wheelItemTextPaint.apply {
                typeface = itemTextTypeface
                shader = textPaintShader
            }

            when (textOrientation) {
                TextOrientation.HORIZONTAL -> {
                    val separatedText = item.text.split("\n")

                    val horizontalOffset = (((wheelRadius * Math.PI) / wheelRadius)).toFloat()
                    val verticalOffset = ((wheelRadius / 2 / 3) - 75)

                    if (separatedText.size > 1) {
                        separatedText.forEachIndexed { lineIndex, lineText ->
                            canvas.drawTextOnPath(
                                lineText,
                                path,
                                horizontalOffset,
                                verticalOffset + ((wheelItemTextPaint.textSize + wheelItemTextPaint.letterSpacing) * lineIndex),
                                wheelItemTextPaint
                            )
                        }
                    } else {
                        canvas.drawTextOnPath(
                            item.text,
                            path,
                            horizontalOffset,
                            verticalOffset,
                            wheelItemTextPaint
                        )
                    }
                }
                TextOrientation.VERTICAL -> {
                    val textArray = item.text.toCharArray()

                    val horizontalOffset = (((wheelRadius * Math.PI) / wheelRadius)).toFloat()
                    val verticalOffset = ((wheelRadius / 2 / 3) - 75)

                    textArray.forEachIndexed { index, char ->
                        canvas.drawTextOnPath(
                            char.toString(),
                            path,
                            horizontalOffset,
                            verticalOffset + (index * wheelItemTextPaint.textSize),
                            wheelItemTextPaint
                        )
                    }
                }
                TextOrientation.VERTICAL_TO_CENTER -> {
                    val middleAngle = startAngle + sweepAngle / 2
                    val middleAngleRad = Math.toRadians(middleAngle.toDouble()).toFloat()
                    val textRadius = wheelRadius * textPositionFraction

                    val x = centerOfWheel + textRadius * cos(middleAngleRad)
                    val y = centerOfWheel + textRadius * sin(middleAngleRad)

                    canvas.withSave {
                        val rotationAngle = middleAngle + 180

                        rotate(rotationAngle, x, y)

                        drawText(item.text, x, y + (wheelItemTextPaint.textSize / 3), wheelItemTextPaint)
                    }
                }
                TextOrientation.VERTICAL_TO_CORNER -> {
                    val middleAngle = startAngle + sweepAngle / 2
                    val middleAngleRad = Math.toRadians(middleAngle.toDouble()).toFloat()
                    val textRadius = wheelRadius * textPositionFraction

                    val x = centerOfWheel + textRadius * cos(middleAngleRad)
                    val y = centerOfWheel + textRadius * sin(middleAngleRad)

                    canvas.withSave {
                        rotate(middleAngle, x, y)

                        drawText(item.text, x, y + (wheelItemTextPaint.textSize / 3), wheelItemTextPaint)
                    }
                }
            }

            item.icon?.let { icon ->
                val imgWidth: Int = (resources.getDimensionPixelSize(R.dimen.dp36) * iconSizeMultiplier).toInt()
                val angle = sweepAngle * wheelData.indexOf(item).toFloat() + sweepAngle / 2
                val radians = Math.toRadians(angle.toDouble())

                val sliceCenterX = (centerOfWheel + (wheelRadius * iconPositionFraction) * cos(radians)).toFloat()
                val sliceCenterY = (centerOfWheel + (wheelRadius * iconPositionFraction) * sin(radians)).toFloat()

                canvas.withTranslation(sliceCenterX, sliceCenterY) {
                    rotate(angle + 90F)

                    val rect = Rect(
                        -imgWidth / 2,
                        -imgWidth / 2,
                        imgWidth / 2,
                        imgWidth / 2
                    )

                    drawBitmap(icon, null, rect, null)
                }
            }

            startAngle += sweepAngle
        }
    }

    /**
     * this function is draw a wheel stroke with given stroke color
     * if wheel stroke color is not given then wheel stroke color be black(default)
     */
    private fun drawWheelStroke(canvas: Canvas) {
        if (drawWheelStroke) {
            val wheelStrokeShader = if (wheelStrokeColor.size == 1) {
                LinearGradient(
                    0F,
                    0F,
                    wheelStrokeRadius,
                    wheelStrokeRadius,
                    intArrayOf(wheelStrokeColor[0], wheelStrokeColor[0]),
                    null,
                    Shader.TileMode.CLAMP
                )
            } else if (wheelStrokeColor.isEmpty()) {
                LinearGradient(
                    0F,
                    0F,
                    wheelStrokeRadius,
                    wheelStrokeRadius,
                    intArrayOf(Color.BLACK, Color.BLACK),
                    null,
                    Shader.TileMode.CLAMP
                )
            } else {
                LinearGradient(
                    0F,
                    0F,
                    wheelStrokeRadius,
                    wheelStrokeRadius,
                    wheelStrokeColor,
                    null,
                    Shader.TileMode.CLAMP
                )
            }

            wheelStrokePaint.shader = wheelStrokeShader
            canvas.drawCircle(centerOfWheel, centerOfWheel, wheelStrokeRadius, wheelStrokePaint)
        }
    }

    /**
     * this function is draw a wheel with given wheel color
     * if wheel color is not given then wheel color be black(default)
     */
    private fun drawWheelBackground(canvas: Canvas) {
        val wheelBackgroundPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = Color.BLACK
        }

        canvas.drawCircle(
            centerOfWheel, centerOfWheel, wheelRadius, wheelBackgroundPaint
        )
    }

    /**
     * this function is draw a wheel item separator with given color
     * if wheel item separator color is not given then wheel stroke color be black(default)
     */
    private fun drawItemSeparator(canvas: Canvas) {
        if (drawItemSeparator) {
            val itemSeparatorShader = if (wheelItemSeparatorColor.size == 1) {
                RadialGradient(
                    centerOfWheel,
                    centerOfWheel,
                    wheelRadius,
                    intArrayOf(wheelItemSeparatorColor[0], wheelItemSeparatorColor[0]),
                    null,
                    Shader.TileMode.CLAMP
                )
            } else if (wheelItemSeparatorColor.isEmpty()) {
                RadialGradient(
                    centerOfWheel,
                    centerOfWheel,
                    wheelRadius,
                    intArrayOf(Color.BLACK, Color.BLACK),
                    null,
                    Shader.TileMode.CLAMP
                )
            } else {
                RadialGradient(
                    centerOfWheel,
                    centerOfWheel,
                    wheelRadius,
                    wheelItemSeparatorColor,
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            wheelItemSeparatorPaint.shader = itemSeparatorShader
            wheelItemSeparatorPaint.strokeWidth = itemSeparatorThickness

            wheelData.forEachIndexed { index, _ ->
                val angle = index * (360F / wheelData.size)
                val radians = Math.toRadians(angle.toDouble())
                val endX = centerOfWheel + wheelRadius * cos(radians).toFloat()
                val endY = centerOfWheel + wheelRadius * sin(radians).toFloat()
                canvas.drawLine(centerOfWheel, centerOfWheel, endX, endY, wheelItemSeparatorPaint)
            }
        }
    }

    /**
     * this function is draw a small point with given color
     * if point color is not given then point color be white(default)
     */
    private fun drawCenterPoint(canvas: Canvas) {
        if (drawCenterPoint) {
            val paint = Paint().apply {
                isAntiAlias = true
                isDither = true
                color = centerPointColor
            }

            canvas.drawCircle(centerOfWheel, centerOfWheel, centerPointRadius, paint)
        }
    }

    /**
     * this function is draw a small points around wheel corners with given color
     */
    private fun drawCornerPoints(canvas: Canvas) {
        if (drawCornerPoints) {
            if (wheelCornerPointColors.isEmpty()) {
                val pointsOnCircle = wheelData.size + (wheelData.size * cornerPointsEachSlice)
                wheelCornerPointColors = IntArray(pointsOnCircle) { Color.WHITE }
            }

            var finalWheelStrokeThickness = 0f
            if (drawWheelStroke) {
                finalWheelStrokeThickness = wheelStrokeThickness
            }
            val minDimension = min(measuredWidth, measuredHeight)

            val wheelPointsRadius = if (finalWheelStrokeThickness == 0F) {
                (minDimension / 2F - (finalWheelStrokeThickness / 2)) - (cornerPointsRadius * 2)
            } else {
                minDimension / 2F - (finalWheelStrokeThickness / 2)
            }

            val pointsOnCircle = wheelData.size + (wheelData.size * cornerPointsEachSlice)
            val angleStep = (2 * Math.PI / pointsOnCircle).toFloat()

            val cornerPointsPaint = Paint().apply {
                isAntiAlias = true
                isDither = true
            }

            for (i in 0 until pointsOnCircle) {
                val angle = i * angleStep
                val pointX = centerOfWheel + wheelPointsRadius * cos(angle)
                val pointY = centerOfWheel + wheelPointsRadius * sin(angle)

                if (useCornerPointsGlowEffect) {
                    cornerPointsPaint.color = wheelCornerPointColors[i]
                    cornerPointsPaint.alpha = 77

                    canvas.drawCircle(pointX, pointY, cornerPointsRadius * 1.5F, cornerPointsPaint)
                }

                cornerPointsPaint.color = wheelCornerPointColors[i]
                cornerPointsPaint.alpha = 255
                canvas.drawCircle(pointX, pointY, cornerPointsRadius, cornerPointsPaint)
            }
        }
    }
}