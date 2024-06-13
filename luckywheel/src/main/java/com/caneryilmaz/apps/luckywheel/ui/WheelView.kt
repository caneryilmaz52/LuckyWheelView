package com.caneryilmaz.apps.luckywheel.ui

import android.animation.Animator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.caneryilmaz.apps.luckywheel.constant.RotationSpeed
import com.caneryilmaz.apps.luckywheel.constant.RotationStatus
import com.caneryilmaz.apps.luckywheel.constant.TextOrientation
import com.caneryilmaz.apps.luckywheel.data.WheelData
import com.caneryilmaz.apps.luckywheel.listener.WheelViewListener
import kotlin.math.min
import kotlin.random.Random

class WheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var wheelItemPaint: Paint
    private lateinit var wheelItemSeparatorPaint: Paint
    private lateinit var textPaint: Paint

    private lateinit var range: RectF

    private var padding: Int = 6
    private var radius: Int = 0

    private var centerOfWheel: Int = 0

    private var wheelColor: Int = Color.WHITE

    private var wheelItemSeparatorColor: Int = Color.BLACK
    private var drawItemSeparator: Boolean = false
    private var itemSeparatorThickness: Float = 1F

    private var drawCenterPoint: Boolean = false
    private var centerPointColor: Int = Color.WHITE
    private var centerPointRadius: Float = 30F

    private var wheelData: ArrayList<WheelData>

    private var stopCenterOfItem: Boolean = false

    private var rotateTime: Long = 5000
    private var rotateSpeed: Int = RotationSpeed.NORMAL
    private var rotateSpeedMultiplier: Float = 1F

    private var textOrientation: Int = TextOrientation.HORIZONTAL
    private var textPadding: Int = 55
    private var itemTextSize: Float = 44F
    private var itemTextLetterSpacing: Float = 0.1F
    private var itemTextFont: Typeface = Typeface.SANS_SERIF

    private var wheelViewListener: WheelViewListener? = null

    init {
        setupPaints()
        wheelData = ArrayList()
    }

    private fun setupPaints() {
        range = RectF(
            padding.toFloat(),
            padding.toFloat(),
            (padding + radius).toFloat(),
            (padding + radius).toFloat()
        )

        wheelItemPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }

        wheelItemSeparatorPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = wheelItemSeparatorColor
        }

        textPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            letterSpacing = itemTextLetterSpacing
            textSize = itemTextSize
            typeface = itemTextFont
        }
    }

    /**
     * this function set list of wheel data
     * also if this function don't call or given list is empty then wheel view is draw only a circle
     * @see WheelData
     */
    fun setWheelData(wheelData: ArrayList<WheelData>) {
        this.wheelData = wheelData
        invalidate()
    }

    /**
     * this function set rotation stop at center of item
     * also if this function don't call then stop center of item be false(default)
     */
    fun stopCenterOfItem(stopCenterOfItem: Boolean) {
        this.stopCenterOfItem = stopCenterOfItem
    }


    fun setRotateTime(rotateTime: Long, rotateSpeed: Int, rotateSpeedMultiplier: Float) {
        setRotateTime(rotateTime = rotateTime)
        setRotateSpeed(rotateSpeed = rotateSpeed)
        setRotateSpeedMultiplier(rotateSpeedMultiplier = rotateSpeedMultiplier)
    }

    /**
     * this function set wheel rotate time
     * also if this function don't call then wheel rotateTime be 5000ms(default)
     */
    fun setRotateTime(rotateTime: Long) {
        this.rotateTime = rotateTime
    }

    /**
     * this function set wheel rotate base speed
     * also if this function don't call then wheel rotateSpeed be normal(default)
     * @see RotationSpeed
     */
    fun setRotateSpeed(rotateSpeed: Int) {
        this.rotateSpeed = rotateSpeed
    }

    /**
     * this function set wheel rotate speed multiplier
     * also if this function don't call then wheel rotateSpeedMultiplier be 1F(default)
     */
    fun setRotateSpeedMultiplier(rotateSpeedMultiplier: Float) {
        this.rotateSpeedMultiplier = rotateSpeedMultiplier
    }

    /**
     * this function set wheel color
     * also if this function don't call then wheel color be white(default)
     */
    fun setWheelColor(wheelColor: Int) {
        this.wheelColor = wheelColor
    }

    /**
     * this function set wheel padding
     * also if this function don't call then wheel padding be 2dp(default)
     */
    fun setWheelPadding(padding: Int) {
        this.padding = padding
    }


    fun drawItemSeparator(drawItemSeparator: Boolean, wheelItemSeparatorColor: Int, itemSeparatorThickness: Float) {
        drawItemSeparator(drawItemSeparator = drawItemSeparator)
        setWheelItemSeparatorColor(wheelItemSeparatorColor = wheelItemSeparatorColor)
        setItemSeparatorThickness(itemSeparatorThickness = itemSeparatorThickness)
    }

    /**
     * this function set item separator visibility status
     * also if this function don't call then item separator don't draw(default)
     */
    fun drawItemSeparator(drawItemSeparator: Boolean) {
        this.drawItemSeparator = drawItemSeparator
    }

    /**
     * this function set wheel item separator color
     * also if this function don't call then wheel color be black(default)
     */
    fun setWheelItemSeparatorColor(wheelItemSeparatorColor: Int) {
        this.wheelItemSeparatorColor = wheelItemSeparatorColor
    }

    /**
     * this function set item separator thickness
     * also if this function don't call then item separator thickness be 1F(default)
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
     * this function set center point visibility status
     * also if this function don't call then center point don't draw(default)
     */
    fun drawCenterPoint(drawCenterPoint: Boolean) {
        this.drawCenterPoint = drawCenterPoint
    }

    /**
     * this function set center point color
     * also if this function don't call then center point color be white(default)
     */
    fun setCenterPointColor(centerPointColor: Int) {
        this.centerPointColor = centerPointColor
    }

    /**
     * this function set center point radius
     * also if this function don't call then center point radius be 30F(default)
     */
    fun setCenterPointRadius(centerPointRadius: Float) {
        this.centerPointRadius = centerPointRadius
    }


    fun setWheelItemText(textOrientation: Int, textPadding: Int, textSize: Float, letterSpacing: Float, typeface: Typeface) {
        setTextOrientation(textOrientation = textOrientation)
        setTextPadding(textPadding = textPadding)
        setTextSize(textSize = textSize)
        setTextLetterSpacing(letterSpacing = letterSpacing)
        setTextFont(typeface = typeface)
    }

    /**
     * this function set item text orientation
     * also if this function don't call then text orientation be horizontal(default)
     * @see TextOrientation
     */
    fun setTextOrientation(textOrientation: Int) {
        this.textOrientation = textOrientation
    }

    /**
     * this function set item text padding
     * also if this function don't call then text padding be 20dp(default)
     */
    fun setTextPadding(textPadding: Int) {
        this.textPadding = textPadding
    }

    /**
     * this function set item text size
     * also if this function don't call then text size be 16sp(default)
     */
    fun setTextSize(textSize: Float) {
        itemTextSize = textSize
    }

    /**
     * this function set item text letter spacing
     * @param letterSpacing must be in range 0.0F - 1.0F
     * @param letterSpacing is not in range then letter spacing be 1.0F
     * also if this function don't call then text letter spacing be 0.1F(default)
     */
    fun setTextLetterSpacing(letterSpacing: Float) {
        itemTextLetterSpacing = letterSpacing
    }

    /**
     * this function set item text font family
     * also if this function don't call then text font family be Sans Serif(default)
     */
    fun setTextFont(typeface: Typeface) {
        itemTextFont = typeface
    }

    /**
     * this function set rotation listener to wheel view
     * also if this function don't call then wheel view is not notify user
     * @see WheelViewListener
     */
    fun setWheelViewListener(wheelViewListener: WheelViewListener) {
        this.wheelViewListener = wheelViewListener
    }

    /**
     * this function rotate wheel to given index
     * @param target is index of the item to win
     * also if target a negative number then target throw IndexOutOfBoundsException
     * also if target bigger than wheel data size then throw IndexOutOfBoundsException
     */
    fun rotateWheelToTarget(target: Int) {

        if (target < 0) {
            throw IndexOutOfBoundsException("Wheel target must bigger than 0 (zero)")
        } else if (target > wheelData.size) {
            throw IndexOutOfBoundsException("Wheel target must smaller than wheel data size")
        } else {
            val animatorListener = object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    wheelViewListener?.onRotationStatus(RotationStatus.ROTATING)
                }

                override fun onAnimationEnd(animation: Animator) {
                    wheelViewListener?.onRotationStatus(RotationStatus.COMPLETED)

                    wheelViewListener?.onRotationComplete(wheelData[target])

                    clearAnimation()
                }

                override fun onAnimationCancel(animation: Animator) {
                    wheelViewListener?.onRotationStatus(RotationStatus.CANCELED)
                }

                override fun onAnimationRepeat(animation: Animator) { // no need
                }
            }

            val rotation = getCenterOfWheelItem(target)
            val rotationSpeed: Int = (rotateSpeed * rotateSpeedMultiplier).toInt()
            animate().apply {
                interpolator = DecelerateInterpolator()
                duration = rotateTime
                rotation((360 * rotationSpeed) + rotation)
                setListener(animatorListener)
                start()
            }
        }
    }

    /**
     * this function rotate wheel to random index
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

                clearAnimation()
            }

            override fun onAnimationCancel(animation: Animator) {
                wheelViewListener?.onRotationStatus(RotationStatus.CANCELED)
            }

            override fun onAnimationRepeat(animation: Animator) { // no need
            }
        }

        val rotation = getCenterOfWheelItem(randomTarget)
        val rotationSpeed: Int = (rotateSpeed * rotateSpeedMultiplier).toInt()
        animate().apply {
            interpolator = DecelerateInterpolator()
            duration = rotateTime
            rotation((360 * rotationSpeed) + rotation)
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
     * this function find center of a wheel item
     * also provide arrow align to center of wheel item
     */
    private fun getCenterOfWheelItem(target: Int): Float {
        val sweepAngle: Float = (360 / wheelData.size).toFloat()
        val halfOfWheelItem: Float = sweepAngle / 2
        val targetItemAngle: Float = sweepAngle * (target + 1)

        return if (stopCenterOfItem) {
            270 - targetItemAngle + halfOfWheelItem
        } else {
            val maxRange: Int = sweepAngle.toInt() - 1
            val stopPosition = Random.nextInt(1, maxRange)

            270 - targetItemAngle + stopPosition
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = min(measuredWidth, measuredHeight)

        radius = width - padding * 2

        centerOfWheel = width / 2

        setMeasuredDimension(width, width)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (wheelData.isNotEmpty()) {
            drawWheelBackground(canvas)

            setupPaints()

            var startAngle = 0F
            val sweepAngle: Float = (360 / wheelData.size).toFloat()

            wheelData.forEach { item ->
                wheelItemPaint.color = item.backgroundColor

                textPaint.color = item.textColor

                canvas.drawArc(range, startAngle, sweepAngle, true, wheelItemPaint)

                if (drawItemSeparator) {
                    canvas.drawArc(range, startAngle, itemSeparatorThickness, true, wheelItemSeparatorPaint)
                }

                when (textOrientation) {
                    TextOrientation.HORIZONTAL -> {
                        drawText(canvas, startAngle, sweepAngle, item.text)
                    }
                    TextOrientation.VERTICAL -> {
                        drawTextVertically(canvas, startAngle, sweepAngle, item.text)
                    }
                    else -> {
                        drawText(canvas, startAngle, sweepAngle, item.text)
                    }
                }

                startAngle += sweepAngle
            }
        } else {
            drawWheelBackground(canvas)
        }

        if (drawCenterPoint) {
            drawCenterPoint(canvas)
        }
    }

    /**
     * this function is draw a wheel with given wheel color
     * if wheel color is not given then wheel color be white(default)
     */
    private fun drawWheelBackground(canvas: Canvas?) {
        val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = wheelColor
        }

        canvas?.drawCircle(
            centerOfWheel.toFloat(), centerOfWheel.toFloat(), centerOfWheel.toFloat(), paint
        )
    }

    /**
     * this function is draw wheel item text with given wheel data item text
     * @param startAngle is wheel item index start angel in wheel
     * @param sweepAngle is wheel item angle(wheel item width)
     */
    private fun drawText(canvas: Canvas?, startAngle: Float, sweepAngle: Float, text: String) {

        textPaint.apply {
            textSize = itemTextSize
            typeface = itemTextFont
            letterSpacing = itemTextLetterSpacing
        }

        val path = Path().apply {
            addArc(range, startAngle, sweepAngle)
        }

        if (text.contains("\n")) {
            val separatedText = text.split("\n")

            val textMeasure = textPaint.measureText(separatedText[0]) / 2

            val horizontalOffset: Float = (((radius * Math.PI) / wheelData.size / 2) - textMeasure).toFloat()
            val verticalOffset: Float = ((radius / 2 / 3) - 75).toFloat()

            canvas?.drawTextOnPath(
                separatedText[0], path, horizontalOffset, verticalOffset, textPaint
            )

            canvas?.drawTextOnPath(
                separatedText[1], path, horizontalOffset, verticalOffset + itemTextSize, textPaint
            )

        } else {
            val textMeasure = textPaint.measureText(text) / 2

            val horizontalOffset: Float = (((radius * Math.PI) / wheelData.size / 2) - textMeasure).toFloat()
            val verticalOffset: Float = ((radius / 2 / 3) - 70).toFloat()

            canvas?.drawTextOnPath(text, path, horizontalOffset, verticalOffset, textPaint)
        }
    }

    /**
     * this function is draw wheel item text with given wheel data item text
     * @param startAngle is wheel item index start angel in wheel
     * @param sweepAngle is wheel item angle(wheel item width)
     */
    private fun drawTextVertically(canvas: Canvas?, startAngle: Float, sweepAngle: Float, text: String) {

        textPaint.apply {
            textSize = itemTextSize
            typeface = itemTextFont
            letterSpacing = itemTextLetterSpacing
        }

        val path = Path().apply {
            addArc(range, startAngle, sweepAngle)
        }

        val textArray = text.toCharArray()

        val horizontalOffset: Float = (((radius * Math.PI) / wheelData.size / 2) - 10).toFloat()
        val verticalOffset: Float = ((radius / 2 / 3) - textPadding).toFloat()

        textArray.forEach { char ->
            val index = textArray.indexOf(char)

            canvas?.drawTextOnPath(
                char.toString(),
                path,
                horizontalOffset,
                verticalOffset + (index * itemTextSize),
                textPaint
            )
        }
    }

    /**
     * this function is draw a small point with given color
     * if point color is not given then point color be white(default)
     */
    private fun drawCenterPoint(canvas: Canvas?) {
        val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = centerPointColor
        }

        canvas?.drawCircle(
            centerOfWheel.toFloat(), centerOfWheel.toFloat(), centerPointRadius, paint
        )
    }
}